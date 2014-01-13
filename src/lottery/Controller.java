package lottery;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import logic.ClaimTx;
import logic.ComputeTx;
import logic.KeyGenerator;
import logic.OpenTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.core.WrongNetworkException;

import parameters.MemoryStorage;
import parameters.Parameters;
import parameters.ParametersUpdater;

public class Controller {

	protected ParametersUpdater parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected Notifier notifier;
	protected String session;
		
	public Controller(ParametersUpdater parametersUpdater,
			MemoryStorage memoryStorage, Notifier notifier) {
		Long lDateTime = new Date().getTime();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.notifier = notifier;
		this.session = lDateTime.toString(); 
	}

	public void run() {
		switch (parametersUpdater.getParameters().getCommand()) {
			case VERSION:
				showVersion();
				break;
			case HELP:
				showHelp();
				break;
			case GENERATE_KEYS:
				generateKeys();
				break;
			case CLAIM_MONEY:
				claimMoney();
				break;
			case OPEN:
				open();
				break;
			case LOTTERY:
				lottery();
				break;
			default:
				throw new RuntimeException("BitcoinLottery: Illegal command.");
		}
		
	}

	protected void claimMoney() {
		Parameters parameters = parametersUpdater.getParameters();
		String txString = null;
		try {
			txString = parametersUpdater.askCompute();
			ComputeTx computeTx = null;
			try {
				computeTx = new ComputeTx(Utils.parseAsHexOrBase58(txString), parameters.isTestnet());
			} catch (ProtocolException e) {
				// TODO
				e.printStackTrace();
			} catch (VerificationException e) {
				// TODO
				e.printStackTrace();
			}
			memoryStorage.saveTransaction(parameters, session, computeTx);
	
			List<byte[]> secrets = null;
			boolean secretsCorrect = false;
			while (!secretsCorrect) {
				secrets = parametersUpdater.askSecrets(computeTx.getSecretsHashes());
				memoryStorage.saveSecrets(parameters, session, secrets);
				
				secretsCorrect = computeTx.checkSecrets(secrets);
				if (secretsCorrect) {
					int winner = 0;
					try {
						winner = computeTx.getWinner(secrets);
					} catch (VerificationException e1) {// can not happen
						e1.printStackTrace();
					}
					notifier.showWinner(winner);
					ECKey sk = null;
					Address address = null;
					try {
						sk = parametersUpdater.askSK(parameters.isTestnet());
						address = parametersUpdater.askAddress(parameters.isTestnet());
					} catch (WrongNetworkException e) {
						// TODO
						e.printStackTrace();
						System.exit(1);
					} catch (AddressFormatException e) {
						// TODO
						e.printStackTrace();
						System.exit(1);
					}
					BigInteger fee = parametersUpdater.askFee();
					ClaimTx claimMoneyTx = new ClaimTx(computeTx, address, fee, parameters.isTestnet());
					try {
						claimMoneyTx.addSecrets(secrets);
						claimMoneyTx.setSignature(sk);
					} catch (VerificationException e1) {
						// TODO 
						e1.printStackTrace();
						System.exit(1);
					}
					memoryStorage.saveTransaction(parameters, session, claimMoneyTx);
					notifier.showClaimMoney(parameters, session, claimMoneyTx);
				}
				else {
					notifier.showWrongSecrets(computeTx.findBadSecrets(secrets));
				}
			}
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}

	protected void open() {
		Parameters parameters = parametersUpdater.getParameters();
		String txString = null;
		try {
			txString = parametersUpdater.askOpen();
			OpenTx openTx = null;
			try {
				openTx = new OpenTx(Utils.parseAsHexOrBase58(txString), parameters.isTestnet());
			} catch (ProtocolException e) {
				// TODO
				e.printStackTrace();
			} catch (VerificationException e) {
				// TODO
				e.printStackTrace();
			}
			
			memoryStorage.saveTransaction(parameters, session, openTx);
			byte[] secret = openTx.getSecret();
			List<byte[]> secrets = new LinkedList<byte[]>();
			secrets.add(secret);
			memoryStorage.saveSecrets(parameters, session, secrets);
			notifier.showSecret(parameters, session, secret);
			
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}

	protected void lottery() {
		// TODO Auto-generated method stub
		
	}

	protected void generateKeys() {
		Parameters parameters = parametersUpdater.getParameters();
		ECKey key = new KeyGenerator().generate();
		try {
			memoryStorage.saveKey(parameters, session, key);
			notifier.showKey(parameters, session, key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void showHelp() {
		notifier.showHelp();
	}

	protected void showVersion() {
		notifier.showVersion();
	}

	
}
