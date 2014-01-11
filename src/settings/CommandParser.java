package settings;

import parameters.BitcoinLotterySettings;
import parameters.Parameters.Command;

public class CommandParser {

	public static class CommandArg {
		public CommandArg(Command command, String dir) {
			this.command = command;
			this.dir = dir;
		}
		
		public Command command;
		public String dir;
	}
	
	public static CommandArg parse(String[] args) {
		String dir = BitcoinLotterySettings.defaultDir;
		CommandArg defaultCommand = new CommandArg(Command.HELP, dir); 
		if (args.length < 1 || args.length > 2) {
			return defaultCommand;
		}
		else {
			if (args.length == 2) {
				dir = getDir(args[1]);
			}
			Command command = getCommand(args[0]);
			return new CommandArg(command, dir);
		}
	}
	
	//returns null if arg is not of the form --dir=<path>
	protected static String getDir(String arg) {
		if (arg.startsWith(BitcoinLotterySettings.argDirPrefix)) {
			return arg.substring(BitcoinLotterySettings.argDirPrefix.length());
		}
		else {
			return null;
		}
	}
	
	//returns Command.HELP if arg is not a proper command	
	protected static Command getCommand(String arg) {
		if (arg.equals(BitcoinLotterySettings.argVersion))
			return Command.VERSION;
		if (arg.equals(BitcoinLotterySettings.argGen))
			return Command.GENERATE_KEYS;
		if (arg.equals(BitcoinLotterySettings.argClaimMoney))
			return Command.CLAIM_MONEY;
		if (arg.equals(BitcoinLotterySettings.argLottery))
			return Command.LOTTERY;
		else
			return Command.HELP;
	}
}
