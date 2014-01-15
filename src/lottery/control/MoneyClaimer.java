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
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.VerificationException;

public class MoneyClaimer {
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected String session;
	
	
	public MoneyClaimer(IOHandler parametersUpdater, String session, 
			MemoryStorage memoryStorage) {
		super();
		this.ioHandler = parametersUpdater;
		this.memoryStorage = memoryStorage;
		this.session = session;
	}
	
	public void claimMoney() throws IOException {
		Parameters parameters = ioHandler.getParameters();
		boolean testnet = parameters.isTestnet();
		ComputeTx computeTx = ioHandler.askCompute(new InputVerifiers.ComputeTxVerifier());
		memoryStorage.saveTransaction(parameters, session, computeTx);

		List<byte[]> secrets = null;
		boolean secretsCorrect = false;
		while (!secretsCorrect) { //TODO !!! change
			secrets = ioHandler.askSecrets(computeTx.getSecretsHashes(), new InputVerifiers.SecretListVerifier());
			memoryStorage.saveSecrets(parameters, session, secrets);
			
			secretsCorrect = computeTx.checkSecrets(secrets);
			if (secretsCorrect) {
				int winner = 0;
				try {
					winner = computeTx.getWinner(secrets);
				} catch (VerificationException e1) {// can not happen
					e1.printStackTrace();
				}
				ioHandler.showWinner(winner);
				ECKey sk = null;
				Address address = null;
				sk = ioHandler.askSK(new InputVerifiers.SkVerifier(testnet));
				address = ioHandler.askAddress(new InputVerifiers.AddressVerifier(testnet));
				BigInteger fee = ioHandler.askFee(new InputVerifiers.FeeVerifier());
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
				ioHandler.showClaimMoney(parameters, session, claimMoneyTx);
			}
			else {
				ioHandler.showWrongSecrets(computeTx.findBadSecrets(secrets));
			}
		}
	}
}
