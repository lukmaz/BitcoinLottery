package lottery.control;

import lottery.parameters.MemoryDumper;
import lottery.parameters.MemoryStorage;
import lottery.parameters.StdIOHandler;
import lottery.parameters.IOHandler;

public class Main {

	public static void main(String[] args) {
		IOHandler parametersUpdater = new StdIOHandler(args);
		MemoryStorage memoryStorage = new MemoryDumper();
		Controller controller = new Controller(parametersUpdater, memoryStorage);
		controller.run();
	}
}
