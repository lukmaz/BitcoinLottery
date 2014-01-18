package lottery.control;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;
import lottery.transaction.ClaimTx;
import lottery.transaction.ComputeTx;
import lottery.transaction.LotteryTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.VerificationException;

public class MoneyClaimer {
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected Parameters parameters;
	
	
	public MoneyClaimer(IOHandler ioHandler, Parameters parameters, 
			MemoryStorage memoryStorage) {
		super();
		this.ioHandler = ioHandler;
		this.memoryStorage = memoryStorage;
		this.parameters = parameters;
	}
	
	public void claimMoney() throws IOException {
		boolean testnet = parameters.isTestnet();
		ComputeTx computeTx = ioHandler.askCompute(new InputVerifiers.ComputeTxVerifier(testnet));
		memoryStorage.saveTransaction(parameters, computeTx);
		
		List<byte[]> hashes = computeTx.getSecretsHashes();
		int minLength = computeTx.getMinLength();
		List<byte[]> secrets = ioHandler.askSecrets(hashes, 
				new InputVerifiers.SecretListVerifier(hashes, minLength));
		memoryStorage.saveSecrets(parameters, secrets);
		
		int winner = 0;
		byte[] winersAddress = null;
		try {
			winner = computeTx.getWinner(secrets);
			winersAddress = computeTx.getAddress(winner);
		} catch (VerificationException e1) {// can not happen
			e1.printStackTrace();
		}
		ioHandler.showWinner(winner, winersAddress);
		byte[] pkHash = computeTx.getPkHash(winner);
		NetworkParameters params = LotteryTx.getNetworkParameters(testnet);
		ECKey sk = ioHandler.askSK(new InputVerifiers.SkVerifier(pkHash, testnet));
		Address address = ioHandler.askAddress(sk.toAddress(params), new InputVerifiers.AddressVerifier(testnet));
		BigInteger fee = ioHandler.askFee(new InputVerifiers.FeeVerifier(computeTx.getValue(0)));
		ClaimTx claimMoneyTx = new ClaimTx(computeTx, address, fee, parameters.isTestnet());
		try {
			claimMoneyTx.addSecrets(secrets);
			claimMoneyTx.setSignature(sk);
		} catch (VerificationException e1) {
			// TODO 
			e1.printStackTrace();
			System.exit(1);
		}
		File claimMoneyFile = memoryStorage.saveTransaction(parameters, claimMoneyTx);
		ioHandler.showClaimMoney(claimMoneyTx, claimMoneyFile.getParent());
	}
}
