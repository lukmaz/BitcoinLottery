package lottery.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
	
	static byte[] calcHash(byte[] secret) {
		MessageDigest SHA256 = null; 
		try {
			SHA256 = MessageDigest.getInstance("SHA-256"); //TODO: global settings for hash function
		} catch (NoSuchAlgorithmException e) {
			// TODO
			e.printStackTrace();
		}
		SHA256.update(secret);
		return SHA256.digest();
	}
	
	public static <T> List<T> singleton(T elem) {
		List<T> list = new LinkedList<T>();
		list.add(elem);
		return list;
	}
}
