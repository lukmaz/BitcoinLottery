package lottery.parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import lottery.settings.BitcoinLotterySettings;
import lottery.transaction.LotteryTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.WrongNetworkException;

public class ParametersReader extends ParametersUpdater {

	public ParametersReader(String[] args) {
		super(args);
	}

	@Override
	public String askCompute() throws IOException {
		BufferedReader reader = getReader();
		//TODO
		System.out.println("Paste the Compute transaction (as a raw tx)");
		String tx = reader.readLine();
		return tx;
	}

	@Override
	public String askOpen() throws IOException {
		BufferedReader reader = getReader();
		//TODO: merge with askCompute
		System.out.println("Paste the Open transaction (as a raw tx)");
		String tx = reader.readLine();
		return tx;
	}
	
	protected BufferedReader getReader() {
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bReader = new BufferedReader(isReader);
		return bReader; //TODO: make it a class variable?
	}
	
	@Override
	public List<byte[]> askSecrets(List<byte[]> secretsHahses) throws IOException {
		BufferedReader reader = getReader();
		System.out.println("Enter the secrets for the following secretsHashes:");
		List<byte[]> secrets = new LinkedList<byte[]>();
		for (int n = 0; n < secretsHahses.size(); ++n) {
			System.out.println("   for hash " + Utils.bytesToHexString(secretsHahses.get(n)));
			String line = reader.readLine();
			secrets.add(Utils.parseAsHexOrBase58(line));
		}
		return secrets;
	}

	@Override
	public ECKey askSK(boolean testnet) throws IOException, AddressFormatException {
		BufferedReader reader = getReader();
		System.out.println("Enter your Bitcoin secret key to sign the transactions (will not be saved on disk)");
		String line = reader.readLine();
		return new DumpedPrivateKey(LotteryTx.getNetworkParameters(testnet), line).getKey();
	}

	@Override
	public Address askAddress(boolean testnet) throws IOException, WrongNetworkException, AddressFormatException {
		BufferedReader reader = getReader();
		System.out.println("Enter the Bitcoin address to which the (potential) reward should be sent:");
		String line = reader.readLine();
		return new Address(LotteryTx.getNetworkParameters(testnet), line);
	}

	@Override
	public BigInteger askFee() throws IOException {
		// TODO
		BufferedReader reader = getReader();
		BigInteger fee = BitcoinLotterySettings.defaultFee;
		System.out.println("Enter the value of fee to be included in transactions (in BTC).");
		System.out.println("To use default value (" + Utils.bitcoinValueToFriendlyString(fee) + ") press enter");
		System.out.println("   (smaller fees then the default value may result in not including transactions in Blockchain).");
		String line = reader.readLine();
		if (!line.equals("")) {
			fee = Utils.toNanoCoins(line);
		}
		
		return fee;
	}

	@Override
	public List<byte[]> askPks() throws IOException {
		// TODO
		BufferedReader reader = getReader();
		System.out.println("Enter number of players");
		int n = Integer.parseInt(reader.readLine());
		if (n < 2) {
			throw new RuntimeException("wrong number of players"); //TODO: change exception
		}
		List<byte[]> pks = new LinkedList<byte[]>();
		System.out.println("Enter public keys of each players (as it will be used in the lottery) in players order");
		for (int k = 0; k < n; ++k) {
			pks.add(Utils.parseAsHexOrBase58(reader.readLine()));
		}

		return pks;
	}

	@Override
	public BigInteger askStake() throws IOException {
		// TODO
		BufferedReader reader = getReader();
		BigInteger stake;
		System.out.println("Enter the value of stake (in BTC)");
		String line = reader.readLine();
		stake = Utils.toNanoCoins(line);
		
		return stake;
	}

	@Override
	public long askLockTime() throws IOException {
		// TODO 
		BufferedReader reader = getReader();
		long lockTime = BitcoinLotterySettings.defaultLockTimes;
		System.out.println("Enter the value of lockTime to use in transactions (in minutes).");
		System.out.println("To use default value (" + lockTime + ") press enter");
		String line = reader.readLine();
		if (!line.equals("")) {
			lockTime = Long.parseLong(line);
		}
		
		return lockTime;
	}

	@Override
	public int askMinLength() throws IOException {
		// TODO 
		BufferedReader reader = getReader();
		int minLength = BitcoinLotterySettings.defaultMinLength;
		System.out.println("Enter the minimal length of the secrets (in bytes).");
		System.out.println("To use default value (" + minLength + ") press enter");
		System.out.println("   (smaller lengths may help the adversary to learn your secret)");
		String line = reader.readLine();
		if (!line.equals("")) {
			minLength = Integer.parseInt(line);	//TODO: where check for errors?
		}
		
		return minLength;
	}

	@Override
	public byte[] askSecret(int minLength, int noPlayers) throws IOException {
		// TODO 
		BufferedReader reader = getReader();
		byte[] secret = null;
		System.out.println("You can enter the secret to use in your commitments.");
		System.out.println("However, it is recomended to let the program to sample your secret. To do it press enter");
		System.out.println("Otherwise enter the secret in hex, not shorter than " + minLength + " bytes " +
							"and not longer than " + (minLength + noPlayers -1) + " bytes.");
		String line = reader.readLine();
		if (!line.equals("")) {
			secret = Utils.parseAsHexOrBase58(line);	//TODO: where check for errors?
		}
		
		return secret;
	}

	@Override
	public TransactionOutput askOutput(BigInteger stake, boolean testnet) throws IOException, ProtocolException {
		// TODO
		System.out.println("Enter a raw transaction to use as an input.");
		System.out.println("Its output should have value exactly " + Utils.bitcoinValueToFriendlyString(stake) + " BTC.");
		BufferedReader reader = getReader();
		Transaction tx = new Transaction(LotteryTx.getNetworkParameters(testnet), Utils.parseAsHexOrBase58(reader.readLine()));
		for (int k = 0; k < tx.getOutputs().size(); ++k) {
			if (tx.getOutput(k).getValue().equals(stake)) { //TODO: what if there is more than one?
				System.out.println("The output number " + k + " will be used.");
				return tx.getOutput(k);
			}
		}
		
		throw new ProtocolException("Bad transaction values.");
	}

	@Override
	public long askStartTime(long defaultTime) throws IOException {
		// TODO 
		BufferedReader reader = getReader();
		System.out.println("Every party using the protocol should set the same protocol start timestamp.");
		System.out.println("To use rounded value of actual time (" + defaultTime + ") press enter.");
		System.out.println("Otherwise enter the same timestamp as other users (as unix timestamp).");
		String line = reader.readLine();
		if (!line.equals("")) {
			defaultTime = Long.parseLong(line);
		}
		
		return defaultTime;
	}
}
