package lottery;

import java.util.Arrays;

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
							" [" + BitcoinLotterySettings.argDirPrefix + "path]");
		printArgDetails(BitcoinLotterySettings.argHelp,       "prints this help");
		printArgDetails(BitcoinLotterySettings.argVersion,    "prints version and general information");
		printArgDetails(BitcoinLotterySettings.argGen,        "generates fresh pair <public key, secret key>");
		printArgDetails(BitcoinLotterySettings.argClaimMoney, "returns the transaction ClaimMoney when provided "
																+ "transaction Compute and secrets s_i");
		printArgDetails(BitcoinLotterySettings.argLottery,    "generates the transactions to perform the lottery");
		System.out.println("Every value (e.g. keys, transactions) generated or received by the program " +
							"is stored by default under the " + BitcoinLotterySettings.defaultDir + " location.");
		System.out.println("This can be changed by using the option " + BitcoinLotterySettings.argDirPrefix);
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

}
