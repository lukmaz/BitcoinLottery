package parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import logic.LotteryTx;
import settings.BitcoinLotterySettings;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
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
		return bReader;
	}
	
	@Override
	public List<byte[]> askSecrets(List<byte[]> secretsHahses) throws IOException {
		BufferedReader reader = getReader();
		System.out.println("Provide the secrets for the following secretsHashes:");
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
		System.out.println("Provide your Bitcoin secret key to sign the transactions (will not be saved on disk)");
		String line = reader.readLine();
		return new DumpedPrivateKey(LotteryTx.getNetworkParameters(testnet), line).getKey();
	}

	@Override
	public Address askAddress(boolean testnet) throws IOException, WrongNetworkException, AddressFormatException {
		BufferedReader reader = getReader();
		System.out.println("Provide the Bitcoin address to which the (potential) reward should be sent:");
		String line = reader.readLine();
		return new Address(LotteryTx.getNetworkParameters(testnet), line);
	}

	@Override
	public BigInteger askFee() throws IOException {
		// TODO
		BufferedReader reader = getReader();
		BigInteger fee = BitcoinLotterySettings.defaultFee;
		System.out.println("Provide the value of fee to be included in transactions (in BTC).");
		System.out.println("To use default value (" + Utils.bitcoinValueToFriendlyString(fee) + ") press enter");
		System.out.println("   (smaller fees then the default value may result in not including transactions in Blockchain).");
		String line = reader.readLine();
		if (!line.equals("")) {
			fee = Utils.toNanoCoins(line);
		}
		
		return fee;
	}
}
