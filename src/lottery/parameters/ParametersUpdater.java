package lottery.parameters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import lottery.parameters.CommandParser.CommandArg;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.WrongNetworkException;

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
	
	public abstract String askOpen() throws IOException;

	public abstract List<byte[]> askSecrets(List<byte[]> hashes) throws IOException;

	public abstract ECKey askSK(boolean testnet) throws IOException, AddressFormatException;

	public abstract Address askAddress(boolean testnet) throws IOException, WrongNetworkException, AddressFormatException;

	public abstract BigInteger askFee() throws IOException;

	public abstract List<byte[]> askPks() throws IOException;

	public abstract BigInteger askStake() throws IOException;

	public abstract long askLockTime() throws IOException;
	
	public abstract int askMinLength() throws IOException;

	//returns null if secret should be (properly) randomly chosen
	public abstract byte[] askSecret(int minLength, int noPlayers) throws IOException;

}
