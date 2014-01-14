package lottery.control;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import lottery.parameters.Parameters;
import lottery.transaction.ClaimTx;
import lottery.transaction.CommitTx;
import lottery.transaction.OpenTx;
import lottery.transaction.PayDepositTx;

import com.google.bitcoin.core.ECKey;

public abstract class Notifier {
	public abstract void showHelp();
	
	public abstract void showVersion();

	public abstract void showKey(Parameters parameters, String session, ECKey key) throws IOException;

	public abstract void showWinner(int winner);

	public abstract void showClaimMoney(Parameters parameters, String session, ClaimTx claimMoneyTx)  throws IOException;

	public abstract void showWrongSecrets(Collection<Integer> collection);

	public abstract void showSecret(Parameters parameters, String session, byte[] secret) throws IOException;

	public abstract void showHash(byte[] hash) throws IOException;

	public abstract void showCommitmentScheme(Parameters parameters, String session, CommitTx commitTx,
			OpenTx openTx, List<PayDepositTx> payTxs) throws IOException;

}
