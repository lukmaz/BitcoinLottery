package lottery;

import java.io.IOException;
import java.util.Arrays;

import parameters.Parameters;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;

import settings.BitcoinLotterySettings;

public class NotifierText extends Notifier {

	@Override
	public void showHelp() {
		// TODO
		StringBuilder arguments = new StringBuilder();
		char separator = '|';
		arguments.append(BitcoinLotterySettings.argHelp).append(separator)
				 .append(BitcoinLotterySettings.argVersion).append(separator)
				 .append(BitcoinLotterySettings.argGen).append(separator)
				 .append(BitcoinLotterySettings.argClaimMoney).append(separator)
				 .append(BitcoinLotterySettings.argLottery);
		System.out.println("BitcoinLottery " + BitcoinLotterySettings.version + ". Usage:\n" +
							"bitcoinlottery {" + arguments + "}" + 
							" [" + BitcoinLotterySettings.argDirPrefix + "<path>]" +
							" [" + BitcoinLotterySettings.argTestnet + "]");
		printArgDetails(BitcoinLotterySettings.argHelp,       "prints this help");
		printArgDetails(BitcoinLotterySettings.argVersion,    "prints version and general information");
		printArgDetails(BitcoinLotterySettings.argGen,        "generates fresh pair <public key, secret key>");
		printArgDetails(BitcoinLotterySettings.argClaimMoney, "returns the transaction ClaimMoney when provided "
																+ "transaction Compute and secrets s_i");
		printArgDetails(BitcoinLotterySettings.argLottery,    "generates the transactions to perform the lottery");
		printArgDetails(BitcoinLotterySettings.argDirPrefix + "<path>",
				"Every value (e.g. keys, transactions) generated or received by the program " +
				"is stored under the <path> location (by default it is " + BitcoinLotterySettings.defaultDir);
		printArgDetails(BitcoinLotterySettings.argTestnet,
				"use this option to use the testnet network instead of the main Bitcoin chain");

	}
	
	protected void printArgDetails(String arg, String details) {
		StringBuilder line = new StringBuilder();
		int maxTab = 15;
		char[] tab = new char[maxTab - arg.length()];
		Arrays.fill(tab, ' ');
		line.append("  ")
			.append(arg)
			.append(tab)
			.append(details);
		System.out.println(line);
	}

	@Override
	public void showVersion() {
		//TODO
		System.out.println("BitcoinLottery protocol implementation\n"
							+ "version: " + BitcoinLotterySettings.version + "\n" +
							"In this version all broadcasts and most of validations must be performed manually\n" +
							"For more info about the protocol see te paper: " + BitcoinLotterySettings.paper + "\n" + 
							"To report bugs contact " + BitcoinLotterySettings.author + 
							" at: " + BitcoinLotterySettings.email + "\n" +
							"This application comes with no warranty");
	}

	@Override
	public void showKey(Parameters parameters, String session, ECKey key) throws IOException {
		//TODO
		String subdir = BitcoinLotterySettings.keySubdirectory;
		boolean testnet = parameters.isTestnet();
		String chain = parameters.isTestnet() ? BitcoinLotterySettings.testnetSubdirectory : "";
		String[] pathParts = {parameters.getRoot(), chain, subdir, session};
		String dir = LotteryUtils.getDir(pathParts).getAbsolutePath();
		NetworkParameters params = testnet ? TestNet3Params.get() : MainNetParams.get();
		System.out.println("Generated new <public key, secret key> pair" + (testnet ? " (for the testnet)" : ""));			
		System.out.println("They were saved under the " + dir + " directory");
		System.out.println("The public key and the private key are:");
		System.out.println(key.toAddress(params));
		System.out.println(key.getPrivateKeyEncoded(params));
	}
}
