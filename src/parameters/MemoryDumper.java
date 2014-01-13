package parameters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import parameters.Parameters.Command;
import settings.BitcoinLotterySettings;
import logic.ClaimTx;
import logic.ComputeTx;
import logic.LotteryTx;
import logic.PutMoneyTx;
import lottery.LotteryUtils;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;

public class MemoryDumper extends MemoryStorage {

	@Override
	public void saveKey(Parameters parameters, String session, ECKey key) throws IOException {
		String subdir = BitcoinLotterySettings.keySubdirectory;
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, session};
		File dir = LotteryUtils.getDir(pathParts);
		File sk = new File(dir, BitcoinLotterySettings.skFilename);
		File pk = new File(dir, BitcoinLotterySettings.pkFilename);
		NetworkParameters params = getNetworkParams(parameters.isTestnet());
		
		PrintWriter skWriter = new PrintWriter(sk.getAbsolutePath());
		skWriter.println(key.getPrivateKeyEncoded(params));
		skWriter.close();
		
		PrintWriter pkWriter = new PrintWriter(pk.getAbsolutePath());
		pkWriter.println(key.toAddress(params));
		pkWriter.close();
	}

	protected NetworkParameters getNetworkParams(boolean testnet) {
		//TODO: move to Parameters?
		return testnet ? TestNet3Params.get() : MainNetParams.get();
	}

	@Override
	public void saveTransaction(Parameters parameters, String session, LotteryTx tx) throws IOException {
		String txFilename = getTxFilename(tx.getClass());
		//TODO: extract below lines? where? 
		String subdir = getSubdir(parameters.getCommand());
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, session};
		File dir = LotteryUtils.getDir(pathParts);
		File txFile = new File(dir, txFilename);
		
		PrintWriter writer = new PrintWriter(txFile.getAbsolutePath());
		writer.println(Utils.bytesToHexString(tx.toRaw()));
		writer.close();
	}

	protected String chainName(boolean testnet) {
		//TODO: move to Parameters? 
		return testnet ? BitcoinLotterySettings.testnetSubdirectory : "";
	}

	protected String getSubdir(Command command) {
		switch (command) {
			case CLAIM_MONEY:
				return BitcoinLotterySettings.claimSubdirectory;
			case LOTTERY:
				return BitcoinLotterySettings.lotterySubdirectory;
			default:
				throw new RuntimeException("BitcoinLottery: Illegal command.");
		}
	}

	protected String getTxFilename(Class<?> lotteryClass) {
		//TODO
		if (lotteryClass == ClaimTx.class)
			return BitcoinLotterySettings.txClaimMoneyFilename;
//		else if (lotteryClass == .class)
//			return BitcoinLotterySettings.txCommitFilename;
		else if (lotteryClass == ComputeTx.class)
			return BitcoinLotterySettings.txComputeFilename;
//		else if (lotteryClass == .class)
//			return BitcoinLotterySettings.txOpenFilename;
//		else if (lotteryClass == .class)
//			return BitcoinLotterySettings.txPayDepositFilename;
		else if (lotteryClass == PutMoneyTx.class)
			return BitcoinLotterySettings.txPutMoneyFilename;
		else
			throw new RuntimeException("Illegal transaction type.");
	}

	@Override
	public void saveSecrets(Parameters parameters, String session,
			List<String> secrets) throws IOException {
		String subdir = getSubdir(parameters.getCommand());
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, session};
		File dir = LotteryUtils.getDir(pathParts);
		File secretsFile = new File(dir, BitcoinLotterySettings.secretsFilename);
		
		PrintWriter writer = new PrintWriter(secretsFile.getAbsolutePath());
		for (String s : secrets) {
			writer.println(s);
		}
		writer.close();
	}
}
