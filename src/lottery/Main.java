package lottery;

import parameters.ParametersReader;
import parameters.ParametersUpdater;

public class Main {

	public static void main(String[] args) {
		ParametersUpdater parametersUpdater = new ParametersReader(args);
		Notifier notifier = new NotifierText();
		Controller controller = new Controller();
		controller.run(parametersUpdater, notifier);
	}

}
