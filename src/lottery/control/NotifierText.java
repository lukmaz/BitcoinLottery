package lottery.control;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import lottery.parameters.MemoryDumper;
import lottery.parameters.Parameters;
import lottery.parameters.Parameters.Command;
import lottery.settings.BitcoinLotterySettings;
import lottery.transaction.ClaimTx;
import lottery.transaction.LotteryTx;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Utils;

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

	protected String getDir(String subdir, Parameters parameters, String session) throws IOException {
		String chain = parameters.isTestnet() ? BitcoinLotterySettings.testnetSubdirectory : "";
		String[] pathParts = {parameters.getRoot(), chain, subdir, session};
		return LotteryUtils.getDir(pathParts).getAbsolutePath();
	}
	
	@Override
	public void showKey(Parameters parameters, String session, ECKey key) throws IOException {
		//TODO
		String dir = getDir(BitcoinLotterySettings.keySubdirectory, parameters, session);
		NetworkParameters params = LotteryTx.getNetworkParameters(parameters.isTestnet());
		System.out.println("Generated new <public key, secret key> pair" + (parameters.isTestnet() ? " (for the testnet)" : ""));			
		System.out.println("They were saved under the " + dir + " directory");
		System.out.println("The public key and the private key are:");
		System.out.println(key.toAddress(params));
		System.out.println(key.getPrivateKeyEncoded(params));
	}

	@Override
	public void showWinner(int winner) {
		//TODO
		//TODO: show winners pk
		System.out.println("The winner is the player number " + winner);		
		System.out.println("    (numerating starts with 1).");		
		System.out.println("If you are the winner press enter to continue.");		
		System.out.println("Otherwise press Ctrl+c to exit.");
		//TODO: wait for enter ?
	}

	@Override
	public void showClaimMoney(Parameters parameters, String session, ClaimTx claimMoneyTx) throws IOException {
		//TODO
		String dir = getDir(BitcoinLotterySettings.claimSubdirectory, parameters, session);
		System.out.println("Congratulation, you are the winner!");
		System.out.println("The provided Compute transaction, secrets and created ClaimMoney transaction " +
							"were save under the " + dir + " directory");
		BigInteger reward = claimMoneyTx.getValue(0);
		System.out.println("To get your reward (" + Utils.bitcoinValueToFriendlyString(reward) + " BTC), broadcast the transaction:");
		System.out.println(Utils.bytesToHexString(claimMoneyTx.toRaw()));
	}

	@Override
	public void showWrongSecrets(Collection<Integer> collection) {
		// TODO
		System.out.println("The secrets you have provided are not correct");
		System.out.print("The errors are on the positions: ");
		Iterator<Integer> it = collection.iterator();
		while (it.hasNext()) {
			System.out.print(it.next()+1 + " ");
		}
		System.out.println();
		System.out.println("If you want to provide them again, press enter to continue.");		
		System.out.println("Otherwise press Ctrl+c to exit.");
		//TODO: wait for enter ?
	}

	@Override
	public void showSecret(Parameters parameters, String session, byte[] secret) throws IOException {
		Command command = parameters.getCommand();
		String dir = getDir(MemoryDumper.getSubdir(command), parameters, session);
		if (command == Command.OPEN) {
			System.out.println("The provided Open transaction and its secret " +
								"were save under the " + dir + " directory");
			System.out.println("And the secret is:");
		}
		else if (command == Command.LOTTERY) {
			System.out.println("The secret was saved under the " + dir + " directory");
			System.out.println("Your secret is:");
		}
		System.out.println(Utils.bytesToHexString(secret));
	}
}
