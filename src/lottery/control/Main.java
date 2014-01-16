package lottery.control;

import lottery.parameters.MemoryDumper;
import lottery.parameters.MemoryStorage;
import lottery.parameters.Parameters;
import lottery.parameters.StdIOHandler;
import lottery.parameters.IOHandler;

public class Main {

	public static void main(String[] args) {
		IOHandler ioHandler = new StdIOHandler();
		Parameters parameters = new Parameters(args);
		MemoryStorage memoryStorage = new MemoryDumper();
		Controller controller = new Controller(ioHandler, parameters, memoryStorage);
		controller.run();
	}
}
