package lottery.control;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.WrongNetworkException;

import lottery.transaction.LotteryTx;


public class InputVerifiers {

	public static class WrongInputException extends Exception {
		private static final long serialVersionUID = 1L;
		public WrongInputException(String string) {
			super(string);
		}

	}
	
	public static interface GenericVerifier<T> {
		public T verify(String input) throws WrongInputException;
	}
	
	protected static class BtcAmountVerifier implements GenericVerifier<BigInteger> {
		protected String type;
		
		public BtcAmountVerifier(String type) {
			this.type = type;
		}
		
		public BigInteger verify(String input) throws WrongInputException {
			try {
				BigInteger fee = Utils.toNanoCoins(input);
				return fee;
			} catch (NumberFormatException e) {
				throw new WrongInputException("Wrong format of the " + type + ".");
			} catch (ArithmeticException e) {
				throw new WrongInputException("Wrong value of the " + type + ".");
			}
        }
    }

	public static class FeeVerifier extends BtcAmountVerifier {
		public FeeVerifier() {
			super("fee");
		}
	}

	public static class StakeVerifier extends BtcAmountVerifier {
		public StakeVerifier() {
			super("stake");
		}
	}
	
	public static class SkVerifier implements GenericVerifier<ECKey> {
		protected NetworkParameters params;
		
		public SkVerifier(boolean testnet) {
			params = LotteryTx.getNetworkParameters(testnet);
		}
		
		public ECKey verify(String input) throws WrongInputException {
			try {
				ECKey sk = new DumpedPrivateKey(params, input).getKey();
				return sk;
			} catch (AddressFormatException e) {
				throw new WrongInputException("Wrong format of the secret key.");
			} 
        }
    }
	
	public static class AddressVerifier implements GenericVerifier<Address> {
		protected NetworkParameters params;
		
		public AddressVerifier(boolean testnet) {
			params = LotteryTx.getNetworkParameters(testnet);
		}
		
		public Address verify(String input) throws WrongInputException {
			try {
				Address address = new Address(params, input);
				return address;
			} catch (WrongNetworkException e) {
				throw new WrongInputException("Provided key corresponds to a different chain.");
			} catch (AddressFormatException e) {
				throw new WrongInputException("Wrong format of the address.");
			} 
        }
    }
	
	protected static class NumberVerifier implements GenericVerifier<Long> {
		protected long min, max;
		protected String type;
		
		public NumberVerifier(long min, long max, String type) {
			this.min = min;
			this.max = max;
			this.type = type;
		}

		public Long verify(String input) throws WrongInputException {
			try {
				Long value = Long.parseLong(input);
				if (value < min) {
					throw new WrongInputException(type + " has to be bigger than " + min + ".");
				}
				if (value > max) {
					throw new WrongInputException(type + " has to be smaller than " + max + ".");
				}
				return value;
			} catch (NumberFormatException e) {
				throw new WrongInputException("Wrong format of the " + type + ".");
			}
        }
    }
	
	public static class LockTimeVerifier extends NumberVerifier {
		public LockTimeVerifier() {
			super(1, 7 * 60 * 24, "lock time");
		}
    }
	
	public static class StartTimeVerifier extends NumberVerifier {
		public StartTimeVerifier() {
			super(Transaction.LOCKTIME_THRESHOLD, Long.MAX_VALUE, "start time");
		}
    }
	
	public static class MinLengthVerifier extends NumberVerifier {
		public MinLengthVerifier() {
			super(0, 512, "min length");
		}
    }
	
	public static class NoPlayersVerifier extends NumberVerifier {
		public NoPlayersVerifier() {
			super(2, 100, "number of players");
		}
    }
	
	protected static class NewSecretVerifier implements GenericVerifier<byte[]> {
		protected int minLength, noPlayers;
		
		public NewSecretVerifier(int minLength, int noPlayers) {
			this.minLength = minLength;
			this.noPlayers = noPlayers;
		}

		public byte[] verify(String input) throws WrongInputException {
			if (input.equals("")) {
				return sampleSecret();
			}
			else {
				byte[] secret = Utils.parseAsHexOrBase58(input);
				if (secret == null) {
					throw new WrongInputException("Wrong format of the secret.");
				}
				if (secret.length < minLength) {
					throw new WrongInputException("The secret is to short.");
				}
				else if (secret.length >= minLength + noPlayers) {
					throw new WrongInputException("The secret is to long.");
				}
			}
			return null;
        }

		protected byte[] sampleSecret() {
		    SecureRandom random = new SecureRandom();
		    int n = random.nextInt(noPlayers); 	//TODO: is it secure?
		    byte[] secret = new byte[minLength + n];
			random.nextBytes(secret);
			return secret;
		}
    }
	
	
}
