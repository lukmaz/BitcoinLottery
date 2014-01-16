package lottery.parameters;

import java.util.Date;

import lottery.parameters.CommandParser.CommandArg;

public class Parameters {
	protected Command command;
	protected String root;
	protected boolean testnet;
	protected String session;

	public Parameters(String[] args) {
		CommandArg commandArg = CommandParser.parse(args);
		this.command = commandArg.command;
		this.root = commandArg.root;
		this.testnet = commandArg.testnet;
		Long lDateTime = new Date().getTime();
		this.session = lDateTime.toString(); 
	}
	
	public enum Command {
		HELP,
		VERSION,
		GENERATE_KEYS,
		CLAIM_MONEY,
		OPEN,
		LOTTERY,
	}
	
	public Command getCommand() {
		return command;
	}
	public String getRoot() {
		return root;
	}
	public boolean isTestnet() {
		return testnet;
	}
	public String getSession() {
		return session;
	}
}
