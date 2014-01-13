package lottery;

import java.io.IOException;
import java.util.Collection;

import logic.ClaimTx;
import parameters.Parameters;

import com.google.bitcoin.core.ECKey;

public abstract class Notifier {
	public abstract void showHelp();
	
	public abstract void showVersion();

	public abstract void showKey(Parameters parameters, String session, ECKey key) throws IOException;

	public abstract void showWinner(int winner);

	public abstract void showClaimMoney(Parameters parameters, String session, ClaimTx claimMoneyTx)  throws IOException;

	public abstract void showWrongSecrets(Collection<Integer> collection);
}
