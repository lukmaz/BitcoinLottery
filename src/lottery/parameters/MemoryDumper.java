package lottery.parameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import lottery.control.LotteryUtils;
import lottery.parameters.Parameters.Command;
import lottery.settings.BitcoinLotterySettings;
import lottery.transaction.ClaimTx;
import lottery.transaction.CommitTx;
import lottery.transaction.ComputeTx;
import lottery.transaction.LotteryTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;
import lottery.transaction.PutMoneyTx;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Utils;

public class MemoryDumper extends MemoryStorage {

	@Override
	public void saveKey(Parameters parameters, ECKey key) throws IOException {
		String subdir = BitcoinLotterySettings.keySubdirectory;
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, parameters.getSession()};
		File dir = LotteryUtils.getDir(pathParts);
		File sk = new File(dir, BitcoinLotterySettings.skFilename);
		File pk = new File(dir, BitcoinLotterySettings.pkFilename);
		NetworkParameters params = LotteryTx.getNetworkParameters(parameters.isTestnet());
		
		PrintWriter skWriter = new PrintWriter(sk.getAbsolutePath());
		skWriter.println(key.getPrivateKeyEncoded(params));
		skWriter.close();
		
		PrintWriter pkWriter = new PrintWriter(pk.getAbsolutePath());
		pkWriter.println(key.toAddress(params));
		pkWriter.close();
	}

	@Override
	public void saveTransaction(Parameters parameters, LotteryTx tx) throws IOException {
		String txFilename = getTxFilename(tx.getClass());
		//TODO: extract below lines? where? 
		String subdir = getSubdir(parameters.getCommand());
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, parameters.getSession()};
		File dir = LotteryUtils.getDir(pathParts);
		File txFile = new File(dir, txFilename);
		
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(txFile.getAbsolutePath(), true)));
		writer.println(Utils.bytesToHexString(tx.toRaw()));
		writer.close();
	}

	protected String chainName(boolean testnet) {
		//TODO: move to Parameters? 
		return testnet ? BitcoinLotterySettings.testnetSubdirectory : "";
	}

	public static String getSubdir(Command command) { //TODO: move to LotteryUtils?
		switch (command) {
			case CLAIM_MONEY:
				return BitcoinLotterySettings.claimSubdirectory;
			case OPEN:
				return BitcoinLotterySettings.openSubdirectory;
			case LOTTERY:
				return BitcoinLotterySettings.lotterySubdirectory;
			default:
				throw new RuntimeException("BitcoinLottery: Illegal command.");
		}
	}

	protected String getTxFilename(Class<?> lotteryClass) {
		if (lotteryClass == ClaimTx.class)
			return BitcoinLotterySettings.txClaimMoneyFilename;
		else if (lotteryClass == CommitTx.class)
			return BitcoinLotterySettings.txCommitFilename;
		else if (lotteryClass == ComputeTx.class)
			return BitcoinLotterySettings.txComputeFilename;
		else if (lotteryClass == OpenTx.class)
			return BitcoinLotterySettings.txOpenFilename;
		else if (lotteryClass == PayDepositTx.class)
			return BitcoinLotterySettings.txPayDepositFilename;
		else if (lotteryClass == PutMoneyTx.class)
			return BitcoinLotterySettings.txPutMoneyFilename;
		else
			throw new RuntimeException("Illegal transaction type.");
	}

	@Override
	public void saveSecrets(Parameters parameters, List<byte[]> secrets) throws IOException {
		String subdir = getSubdir(parameters.getCommand());
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, parameters.getSession()};
		File dir = LotteryUtils.getDir(pathParts);
		File secretsFile = new File(dir, BitcoinLotterySettings.secretsFilename);
		
		PrintWriter writer = new PrintWriter(secretsFile.getAbsolutePath());
		for (byte[] s : secrets) {
			writer.println(Utils.bytesToHexString(s));
		}
		writer.close();
	}
}
