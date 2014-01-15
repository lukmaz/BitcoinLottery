package lottery.control;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.ClaimTx;
import lottery.transaction.ComputeTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.core.WrongNetworkException;

public class MoneyClaimer {
	protected IOHandler parametersUpdater;
	protected MemoryStorage memoryStorage;
	protected String session;
	
	
	public MoneyClaimer(IOHandler parametersUpdater, String session, 
			MemoryStorage memoryStorage) {
		super();
		this.parametersUpdater = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
	}
	
	public void claimMoney() throws IOException {
		Parameters parameters = parametersUpdater.getParameters();
		String txString = null;
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
				parametersUpdater.showWinner(winner);
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
				parametersUpdater.showClaimMoney(parameters, session, claimMoneyTx);
			}
			else {
				parametersUpdater.showWrongSecrets(computeTx.findBadSecrets(secrets));
			}
		}
	}
}
