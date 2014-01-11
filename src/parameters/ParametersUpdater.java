package parameters;

import parameters.CommandParser.CommandArg;

public abstract class ParametersUpdater {
	protected Parameters parameters;

	public ParametersUpdater(String[] args) {
		parameters = new Parameters();
		CommandArg commandArg = CommandParser.parse(args);
		parameters.setDir(commandArg.dir);
		parameters.setCommand(commandArg.command);
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}
	
}
