package lottery;

import parameters.MemoryDumper;
import parameters.MemoryStorage;
import parameters.ParametersReader;
import parameters.ParametersUpdater;

public class Main {

	public static void main(String[] args) {
		ParametersUpdater parametersUpdater = new ParametersReader(args);
		MemoryStorage memoryStorage = new MemoryDumper();
		Notifier notifier = new NotifierText();
		Controller controller = new Controller(parametersUpdater, memoryStorage, notifier);
		controller.run();
	}

}
