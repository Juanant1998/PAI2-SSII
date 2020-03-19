
package main;

import java.math.BigDecimal;

import cliente.NoIntegrityVerifierClient;
import servidor.IntegrityVerifierServer;

public class MainWithManInTheMiddle {

	public static void main(final String[] args) throws Exception {
		IntegrityVerifierServer server = new IntegrityVerifierServer(new BigDecimal("123"));
		server.start();
		NoIntegrityVerifierClient cliente = new NoIntegrityVerifierClient(new BigDecimal("541"), new BigDecimal("855"), new BigDecimal("978"), "Test message v2");
		cliente.runCliente();
		System.exit(0);
	}

}
