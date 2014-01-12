package parameters;

public class Parameters {
	public enum Command {
		HELP,
		VERSION,
		GENERATE_KEYS,
		CLAIM_MONEY,
		LOTTERY,
	}
	
	protected Command command;
	protected String dir;
	protected boolean testnet;
	
	public Command getCommand() {
		return command;
	}
	public void setCommand(Command command) {
		this.command = command;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		// TODO: check if it's a directory (?)
		this.dir = dir;
	}
	public void setTestnet(boolean testnet) {
		this.testnet = testnet;
	}
	public boolean isTestnet() {
		return testnet;
	}
}
