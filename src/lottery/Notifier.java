package lottery;

import java.io.IOException;

import parameters.Parameters;

import com.google.bitcoin.core.ECKey;

public abstract class Notifier {
	public abstract void showHelp();
	
	public abstract void showVersion();

	public abstract void showKey(Parameters parameters, String session, ECKey key) throws IOException;
}
