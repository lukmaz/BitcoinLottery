package lottery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import settings.BitcoinLotterySettings;

public class Utils {
	static public File getDir(String root, String subdir, String session, boolean testnet) throws IOException {

		Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
		permissions.add(PosixFilePermission.OWNER_READ);
		permissions.add(PosixFilePermission.OWNER_WRITE);
		permissions.add(PosixFilePermission.OWNER_EXECUTE);
		
		LinkedList<String> pathParts = new LinkedList<String>();
		pathParts.add(root);
		if (testnet) {
			pathParts.add(BitcoinLotterySettings.testnetSubdirectory);
		}
		pathParts.add(subdir);
		pathParts.add(session);
		
		File dir = new File("/");
		for (String name : pathParts) {
			dir = new File(dir, name);
			dir.mkdir();
			if (dir.isFile()) {
				throw new NotDirectoryException(dir.getAbsolutePath());
			}
			Files.setPosixFilePermissions(dir.toPath(), permissions);			
		}
		
		return dir;
	}
}
