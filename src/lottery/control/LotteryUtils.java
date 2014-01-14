package lottery.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class LotteryUtils {
	static public File getDir(String[] pathParts) throws IOException {
		Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
		permissions.add(PosixFilePermission.OWNER_READ);
		permissions.add(PosixFilePermission.OWNER_WRITE);
		permissions.add(PosixFilePermission.OWNER_EXECUTE);
		
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

	public static long minutesToMiliseconds(long m) {
		return m * 60 * 1000;
	}
}
