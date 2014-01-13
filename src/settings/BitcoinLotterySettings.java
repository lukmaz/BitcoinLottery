package settings;

public class BitcoinLotterySettings {

	public static final String version = "0.0";
	public static final String author = "Daniel Malinowski";
	public static final String email = "--"; //TODO
	public static final String homepage = "--"; //TODO
	public static final String paper = "http://eprint.iacr.org/2013/784.pdf";

	public static final String argDirPrefix = "--dir=";
	public static final String argTestnet = "--testnet";
	public static final String argHelp = "help";
	public static final String argVersion = "version";
	public static final String argGen = "gen";
	public static final String argClaimMoney = "claim";
	public static final String argLottery = "lottery";
	
	public static final String defaultDir = ".bitcoinlottery";
	public static final String testnetSubdirectory = "testnet";
	public static final String keySubdirectory = "keys";
	public static final String claimSubdirectory = "claimmoney";
	public static final String lotterySubdirectory = "lottery";
	public static final String skFilename = "bitcoinkey";
	public static final String pkFilename = "bitcoinkey.pub";
	public static final String txCommitFilename = "commit.tx";
	public static final String txOpenFilename = "open.tx";
	public static final String txPayDepositFilename = "paydeposit.tx";
	public static final String txPutMoneyFilename = "putmoney.tx";
	public static final String txComputeFilename = "compute.tx";
	public static final String txClaimMoneyFilename = "claimmoney.tx";
	public static final String secretsFilename = "secrets.tx";
}
