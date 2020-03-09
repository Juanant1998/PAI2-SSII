
package main;

import cliente.IntegrityVerifierClient;
import macCalculator.CalculatorMac;
import servidor.IntegrityVerifierServer;

public class Main {

	public static void main(final String args[]) throws Exception {
		IntegrityVerifierServer server = new IntegrityVerifierServer();
		server.clave = "1234";
		server.start();
		IntegrityVerifierClient cliente = new IntegrityVerifierClient();
		cliente.macMensaje = CalculatorMac.mac("Hola", "1234");
		cliente.mensaje = "Hola";
		cliente.runCliente();
	}
}
