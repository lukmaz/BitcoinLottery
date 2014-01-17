package lottery.control;

import java.io.File;
import java.io.IOException;

import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.IOHandler;

import com.google.bitcoin.core.ECKey;

public class KeyGenerator {
	protected IOHandler ioHandler;
	protected MemoryStorage memoryStorage;
	protected Parameters parameters;
	
	
	public KeyGenerator(IOHandler ioHandler, Parameters parameters,
			MemoryStorage memoryStorage) {
		super();
		this.ioHandler = ioHandler;
		this.memoryStorage = memoryStorage;
		this.parameters = parameters;
	}

	public void generateKeys() throws IOException {
		ECKey key = new ECKey();
		File[] keyFiles = memoryStorage.saveKey(parameters, key);
		String dir = keyFiles[0].getParent();
		ioHandler.showKey(key, dir, parameters.isTestnet());
	}
}
