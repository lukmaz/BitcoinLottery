package lottery.parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lottery.control.InputVerifiers.WrongInputException;
import lottery.control.InputVerifiers.GenericVerifier;
import lottery.settings.BitcoinLotterySettings;
import lottery.transaction.ClaimTx;
import lottery.transaction.CommitTx;
import lottery.transaction.ComputeTx;
import lottery.transaction.LotteryTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;

public class StdIOHandler extends IOHandler {
	protected BufferedReader reader;

	public StdIOHandler() {
		InputStreamReader isReader = new InputStreamReader(System.in);
		reader = new BufferedReader(isReader);
	}

	protected void write(String s) {
		System.out.print(s);
	}
	
	protected void writeln(String s) {
		write(s + "\n");
	}
	
	protected String readLine() throws IOException {
		return reader.readLine(); 
	}
	
	protected <T> T readObject(GenericVerifier<T> verifier, T defaultVal) throws IOException {
		T ans = null;
		do {
			try {
				String line = readLine();
				if (line.equals("") && defaultVal != null) {
					return defaultVal;
				}
				else {
					ans = verifier.verify(line);
				}
			} catch (WrongInputException e) {
				writeln("Error: " + e.getMessage());
				writeln("Enter the value again:");
			}
		} while (ans == null);
		return ans;
	}

	@Override
	public BigInteger askFee(GenericVerifier<BigInteger> verifier) throws IOException {
		BigInteger fee = BitcoinLotterySettings.defaultFee;
		writeln("Enter the value of fee to be included in transactions (in BTC).");
		writeln("To use default value (" + Utils.bitcoinValueToFriendlyString(fee) + ") press enter");
		writeln("   (smaller fees then the default value may result in not including transactions in Blockchain).");
		return readObject(verifier, fee);
	}

	@Override
	public BigInteger askStake(GenericVerifier<BigInteger> verifier) throws IOException {
		writeln("Enter the value of stake (in BTC)");
		return readObject(verifier, null);
	}
	
	@Override
	public ComputeTx askCompute(GenericVerifier<ComputeTx> verifier) throws IOException {
		writeln("Paste the Compute transaction (as a raw tx)");
		return readObject(verifier, null);
	}

	@Override
	public OpenTx askOpen(GenericVerifier<OpenTx> verifier) throws IOException {
		writeln("Paste the Open transaction (as a raw tx)");
		return readObject(verifier, null);
	}

	@Override
	public ECKey askSK(GenericVerifier<ECKey> verifier) throws IOException {
		writeln("Enter your Bitcoin secret key to sign the transactions (will not be saved on disk)");
		return readObject(verifier, null);
	}

	@Override
	public Address askAddress(GenericVerifier<Address> verifier) throws IOException {
		writeln("Enter the Bitcoin address to which the (potential) reward should be sent:");
		return readObject(verifier, null);
	}

	@Override
	public Long askLockTime(GenericVerifier<Long> verifier) throws IOException {
		long lockTime = BitcoinLotterySettings.defaultLockTimes;
		writeln("Enter the value of lockTime to use in transactions (in minutes).");
		writeln("To use default value (" + lockTime + ") press enter");
		return readObject(verifier, lockTime);
	}

	@Override
	public Long askStartTime(long defaultTime, GenericVerifier<Long> verifier) throws IOException {
		writeln("You can enter the timestamp of the protocol start.");
		writeln("To use rounded value of actual time (" + defaultTime + ") press enter.");
		writeln("Otherwise enter the same timestamp as other users (as unix timestamp in seconds).");
		return readObject(verifier, defaultTime);
	}

	@Override
	public Long askMinLength(GenericVerifier<Long> verifier) throws IOException {
		long minLength = BitcoinLotterySettings.defaultMinLength;
		writeln("Enter the minimal length of the secrets (in bytes).");
		writeln("To use default value (" + minLength + ") press enter");
		writeln("   (smaller lengths may help the adversary to learn your secret)");
		return readObject(verifier, minLength);
	}

	@Override
	public byte[] askSecret(int minLength, int noPlayers, GenericVerifier<byte[]> verifier) throws IOException {
		writeln("You can enter the secret to use in your commitments.");
		writeln("However, it is recomended to let the program to sample your secret. To do it press enter");
		writeln("Otherwise enter the secret in hex, not shorter than " + minLength + " bytes " +
							"and not longer than " + (minLength + noPlayers -1) + " bytes.");
		return readObject(verifier, null);
	}

	@Override
	public TransactionOutput askOutput(BigInteger stake, GenericVerifier<TransactionOutput> verifier) throws IOException {
		writeln("Enter a raw transaction to use as an input.");
		writeln("Its output should have value exactly " + Utils.bitcoinValueToFriendlyString(stake) + " BTC.");
		return readObject(verifier, null); //TODO: print output number (or ask for it if more than one suits)
	}
	
	@Override
	public Long askNoPlayers(GenericVerifier<Long> verifier) throws IOException {
		writeln("Enter number of players.");
		return readObject(verifier, null);
	}
	
	@Override
	public List<byte[]> askPks(int noPlayers, GenericVerifier<byte[]> verifier) throws IOException {
		List<byte[]> pks = new LinkedList<byte[]>();
		writeln("Enter public keys of each players (as they will be used in the lottery) in players order");
		for (int k = 0; k < noPlayers; ++k) {
			pks.add(readObject(verifier, null));
		}

		return pks;
	}
	
	@Override
	public List<byte[]> askSecrets(List<byte[]> secretsHahses, GenericVerifier<byte[]> verifier) throws IOException {
		writeln("Enter the secrets corresponding for the following hashes:");
		List<byte[]> secrets = new LinkedList<byte[]>();
		for (int n = 0; n < secretsHahses.size(); ++n) {
			writeln("   for hash " + Utils.bytesToHexString(secretsHahses.get(n)));
			secrets.add(readObject(verifier, null));
		}
		
		return secrets;
	}

	@Override
	public List<CommitTx> askOthersCommits(int position,
			GenericVerifier<CommitTx> verifier) throws IOException {
		// TODO !!!
		return null;
	}

	@Override
	public List<PayDepositTx> askOthersPayDeposits(int position,
			GenericVerifier<PayDepositTx> verifier) throws IOException {
		// TODO !!!
		return null;
	}
	
	

	@Override
	public void showHelp() {
		// TODO
		StringBuilder usage = new StringBuilder();
		char separator = '|';
		usage.append("bitcoinlottery {")
				 .append(BitcoinLotterySettings.argHelp).append(separator)
				 .append(BitcoinLotterySettings.argVersion).append(separator)
				 .append(BitcoinLotterySettings.argGen).append(separator)
				 .append(BitcoinLotterySettings.argClaimMoney).append(separator)
				 .append(BitcoinLotterySettings.argOpen).append(separator)
				 .append(BitcoinLotterySettings.argLottery)
				 .append("} [")
				 .append(BitcoinLotterySettings.argDirPrefix)
				 .append("<path>] [")
				 .append(BitcoinLotterySettings.argTestnet)
				 .append("]");
		writeln("BitcoinLottery " + BitcoinLotterySettings.version + ". Usage:");
		writeln(usage.toString());
		printArgDetails(BitcoinLotterySettings.argHelp,       "prints this help");
		printArgDetails(BitcoinLotterySettings.argVersion,    "prints version and general information");
		printArgDetails(BitcoinLotterySettings.argGen,        "generates fresh pair <public key, secret key>");
		printArgDetails(BitcoinLotterySettings.argClaimMoney, "returns the transaction ClaimMoney when provided "
																+ "transaction Compute and secrets s_i");
		printArgDetails(BitcoinLotterySettings.argOpen,       "pulls out the secret from the Open transaction");
		printArgDetails(BitcoinLotterySettings.argLottery,    "generates the transactions to perform the lottery");
		printArgDetails(BitcoinLotterySettings.argDirPrefix + "<path>",
				"Every value (e.g. keys, transactions) generated or received by the program " +
				"is stored under the <path> location (by default it is " + BitcoinLotterySettings.defaultDir + ")");
		printArgDetails(BitcoinLotterySettings.argTestnet,
				"use this option to use the testnet network instead of the main Bitcoin chain");
	}
	
	protected void printArgDetails(String arg, String details) {
		StringBuilder line = new StringBuilder();
		int maxTab = 15;
		char[] tab = new char[maxTab - arg.length()];
		Arrays.fill(tab, ' ');
		line.append("   ")
			.append(arg)
			.append(tab)
			.append(details);
		writeln(line.toString());
	}

	@Override
	public void showVersion() {
		//TODO
		writeln("BitcoinLottery protocol implementation");
		writeln("version: " + BitcoinLotterySettings.version);
		writeln("In this version all broadcasts and most of validations must be performed manually");
		writeln("For more info about the protocol see te paper: " + BitcoinLotterySettings.paper); 
		writeln("To report bugs contact " + BitcoinLotterySettings.author + " at: " + BitcoinLotterySettings.email);
		writeln("This application comes with no warranty");
	}

	@Override
	public void showKey(ECKey key, String dir, boolean testnet) throws IOException {
		//TODO
		NetworkParameters params = LotteryTx.getNetworkParameters(testnet);
		writeln("Generated new <public key, secret key> pair" + (testnet ? " (for the testnet)" : ""));			
		writeln("They were saved under the " + dir + " directory");
		writeln("The public key and the private key are:");
		writeln(key.toAddress(params).toString());
		writeln(key.getPrivateKeyEncoded(params).toString());
	}

	@Override
	public void showWinner(int winner) {
		//TODO: show winners pk
		writeln("The winner is the player number " + winner);		
		writeln("    (numerating starts with 1).");		
		writeln("If you are not the winner press Ctrl+c to exit.");
	}

	@Override
	public void showClaimMoney(ClaimTx claimMoneyTx, String dir) throws IOException {
		//TODO
		writeln("Congratulation, you are the winner!");
		writeln("The provided Compute transaction, secrets and created ClaimMoney transaction " +
							"were save under the " + dir + " directory");
		BigInteger reward = claimMoneyTx.getValue(0);
		writeln("To get your reward (" + Utils.bitcoinValueToFriendlyString(reward) + " BTC), broadcast the transaction:");
		writeln(Utils.bytesToHexString(claimMoneyTx.toRaw()));
	}

	@Override
	public void showOpenedSecret(byte[] secret, String file) throws IOException {
		writeln("The provided Open transaction and its secret " +
							"were save in the " + file + " file");
		writeln("And the secret is:");
		writeln(Utils.bytesToHexString(secret));
	}
	
	@Override
	public void showSecret(byte[] secret, String file) throws IOException {
		writeln("The secret was saved in the " + file + " file");
		writeln("Your secret is:");
		writeln(Utils.bytesToHexString(secret));
	}

	@Override
	public void showHash(byte[] hash) throws IOException {
		// TODO 
		writeln("And the hash is:");
		writeln(Utils.bytesToHexString(hash));
	}

	@Override
	public void showCommitmentScheme(LotteryTx commitTx,
			OpenTx openTx, List<PayDepositTx> payTxs, String dir) throws IOException {
		//TODO
		writeln("The Commit transaction, Open transaction and partial PayDeposit transaction " +
							"were save under the " + dir + " directory.");
		writeln("The Commit transaction is (you should broadcast it now):");
		writeln(Utils.bytesToHexString(commitTx.toRaw()));
		writeln("The Open transaction is (you should not broadcast it before the Compute transaction is final):");
		writeln(Utils.bytesToHexString(openTx.toRaw()));
		writeln("The partial PayDeposit transactions are (you should send them to other players):");
		for (int k = 0; k < payTxs.size(); ++k) {
			if (payTxs.get(k) != null) {
				write("For player nr. " + (k+1) + ": ");
				writeln(Utils.bytesToHexString(payTxs.get(k).toRaw()));
			}
		}
	}

	public void showEndOfCommitmentPhase(Parameters parameters) {
		//TODO !!!
	}
}
