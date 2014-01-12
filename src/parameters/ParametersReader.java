package parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class ParametersReader extends ParametersUpdater {

	public ParametersReader(String[] args) {
		super(args);
	}

	@Override
	public String askCompute() throws IOException {
		InputStreamReader isReader = new InputStreamReader(System.in);
		BufferedReader bReader = new BufferedReader(isReader);
		//TODO
		System.out.println("Paste the Compute transaction (as a raw tx)");
		String tx = bReader.readLine();
		return tx;
	}
	
	@Override
	public List<String> askSecrets(List<String> secretsHahses) {
		//TODO!!
		List<String> ans = new LinkedList<String>();
		ans.add("ca42095840735e89283fec298e62ac2ddea9b5f34a8cbb7097ad965b87568100");
		ans.add("1b1b01dc829177da4a14551d2fc96a9db00c6501edfa12f22cd9cefd335c227f");
		return ans;
	}
}
