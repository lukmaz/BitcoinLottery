package lottery.control;

import java.math.BigInteger;

import com.google.bitcoin.core.Utils;

import lottery.parameters.IOHandler;


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
	
	public static class BtcAmountVerifier implements GenericVerifier<BigInteger> {
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
}
