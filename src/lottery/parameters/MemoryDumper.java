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

	protected abstract class Showable<T> {
		public abstract String show(T val);
	}
	
	protected <T> T getNotEmptyElement(List<T> values) {
		for (T value : values) {
			if (value != null) {
				return value;
			}
		}
		throw new NullPointerException();
	}
	
	protected <T> File saveValues(Parameters parameters, String filename, 
					List<T> values, Showable<T> show) throws IOException {
		if (values.size() > 1) {
			filename = "listof" + filename; //TODO
		}
		String subdir = getSubdir(parameters.getCommand());
		String chain = chainName(parameters.isTestnet());
		String[] pathParts = {parameters.getRoot(), chain, subdir, parameters.getSession()};
		File dir = LotteryUtils.getDir(pathParts);
		File file = new File(dir, filename);
		
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(), true)));
		for (T value : values) {
			if (show != null) {
				writer.println(show.show(value));
			}
			else {
				writer.println(value);
			}
		}
		writer.close();
		return file;
	}

	
	@Override
	public File[] saveKey(Parameters parameters, ECKey key) throws IOException {
		NetworkParameters params = LotteryTx.getNetworkParameters(parameters.isTestnet());
		File[] files = {saveValues(parameters, BitcoinLotterySettings.pkFilename, 
							LotteryUtils.singleton(key.toAddress(params)), null),
						saveValues(parameters, BitcoinLotterySettings.skFilename, 
							LotteryUtils.singleton(key.getPrivateKeyEncoded(params)), null)};
		return files;
	}
	
	@Override
	public <T extends LotteryTx> File saveTransactions(Parameters parameters, List<T> txs) throws IOException {
		String filename = getTxFilename(getNotEmptyElement(txs).getClass());
		return saveValues(parameters, filename, txs, 
									new Showable<T>() {
										@Override
										public String show(T val) {
											return Utils.bytesToHexString(val.toRaw());
									}});
	}
	
	@Override
	public File saveSecrets(Parameters parameters, List<byte[]> secrets) throws IOException {
		return saveValues(parameters, BitcoinLotterySettings.secretsFilename, secrets, 
									new Showable<byte[]>() {
										@Override
										public String show(byte[] val) {
											return Utils.bytesToHexString(val);
									}});
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
			case GENERATE_KEYS:
				return BitcoinLotterySettings.keySubdirectory;
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
}
