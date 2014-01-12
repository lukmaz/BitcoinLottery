package parameters;

import java.io.IOException;
import java.util.List;

import parameters.CommandParser.CommandArg;

public abstract class ParametersUpdater {
	protected Parameters parameters;

	public ParametersUpdater(String[] args) {
		parameters = new Parameters();
		CommandArg commandArg = CommandParser.parse(args);
		parameters.setRoot(commandArg.dir);
		parameters.setCommand(commandArg.command);
		parameters.setTestnet(commandArg.testnet);
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public abstract String askCompute() throws IOException;

	public abstract List<String> askSecrets(List<String> secretsHahses);
	
}
