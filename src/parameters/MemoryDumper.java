package parameters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import settings.BitcoinLotterySettings;
import lottery.Utils;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;

public class MemoryDumper extends MemoryStorage {

	@Override
	public void saveKey(Parameters parameters, String session, ECKey key) throws IOException {
		String subdir = BitcoinLotterySettings.keySubdirectory;
		File dir = Utils.getDir(parameters.getRoot(), subdir, session, parameters.isTestnet());
		File sk = new File(dir, BitcoinLotterySettings.skFilename);
		File pk = new File(dir, BitcoinLotterySettings.pkFilename);
		NetworkParameters params = parameters.isTestnet() ? TestNet3Params.get() : MainNetParams.get();
		
		PrintWriter skWriter = new PrintWriter(sk.getAbsolutePath());
		skWriter.println(key.getPrivateKeyEncoded(params));
		skWriter.close();
		
		PrintWriter pkWriter = new PrintWriter(pk.getAbsolutePath());
		pkWriter.println(key.toAddress(params));
		pkWriter.close();
	}
}
