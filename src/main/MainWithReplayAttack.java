
package main;

import java.math.BigDecimal;

import cliente.ReplayAttackVerifierClient;
import servidor.ReplayAttackVerifierServer;

public class MainWithReplayAttack {

	public static void main(final String[] args) throws Exception {
		ReplayAttackVerifierServer server = new ReplayAttackVerifierServer(new BigDecimal("123"));
		server.start();
		ReplayAttackVerifierClient cliente = new ReplayAttackVerifierClient(new BigDecimal("541"), new BigDecimal("855"), new BigDecimal("978"), "Test message v2");
		cliente.runCliente();
		System.exit(0);

	}

}
