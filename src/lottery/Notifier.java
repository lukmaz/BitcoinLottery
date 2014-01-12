package lottery;

import com.google.bitcoin.core.ECKey;

public abstract class Notifier {
	public abstract void showHelp();
	
	public abstract void showVersion();

	public abstract void showKey(String root, String subdir, String session, ECKey key);
}
