package parameters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.WrongNetworkException;

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

	public abstract List<byte[]> askSecrets(List<byte[]> hashes) throws IOException;

	public abstract ECKey askSK(boolean testnet) throws IOException, AddressFormatException;

	public abstract Address askAddress(boolean testnet) throws IOException, WrongNetworkException, AddressFormatException;

	public abstract BigInteger askFee() throws IOException;
	
}
