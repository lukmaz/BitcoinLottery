package lottery;

import parameters.ParametersUpdater;

public class Controller {

	public void run(ParametersUpdater parametersUpdater, Notifier notifier) {
		switch (parametersUpdater.getParameters().getCommand()) {
			case VERSION:
				showVersion(notifier);
				break;
			case HELP:
				showHelp(notifier);
				break;
			case GENERATE_KEYS:
				generateKeys(parametersUpdater);
				break;
			case CLAIM_MONEY:
				claimMoney(parametersUpdater);
				break;
			case LOTTERY:
				lottery(parametersUpdater);
				break;
			default:
				throw new RuntimeException("BitcoinLottery: Illegal command.");
		}
		
	}

	private void claimMoney(ParametersUpdater parametersUpdater) {
		// TODO Auto-generated method stub
		
	}

	private void lottery(ParametersUpdater parametersUpdater) {
		// TODO Auto-generated method stub
		
	}

	private void generateKeys(ParametersUpdater parametersUpdater) {
		// TODO Auto-generated method stub
		
	}

	private void showHelp(Notifier notifier) {
		notifier.showHelp();
	}

	private void showVersion(Notifier notifier) {
		notifier.showVersion();
	}

}
