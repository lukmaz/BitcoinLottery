package lottery.control;

import lottery.parameters.MemoryDumper;
import lottery.parameters.MemoryStorage;
import lottery.parameters.ParametersReader;
import lottery.parameters.ParametersUpdater;

public class Main {

	public static void main(String[] args) {
		ParametersUpdater parametersUpdater = new ParametersReader(args);
		MemoryStorage memoryStorage = new MemoryDumper();
		Notifier notifier = new NotifierText();
		Controller controller = new Controller(parametersUpdater, memoryStorage, notifier);
		controller.run();
	}
}
