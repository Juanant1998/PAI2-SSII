
package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.net.ServerSocketFactory;

import macCalculator.CalculatorMac;

public class IntegrityVerifierServer extends Thread {

	private ServerSocket	serverSocket;
	public String			clave	= "1234";


	// Constructor del Servidor
	public IntegrityVerifierServer() throws Exception {
		// ServerSocketFactory para construir los ServerSockets
		ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
		// Creación de un objeto ServerSocket escuchando peticiones en el puerto 7070
		this.serverSocket = socketFactory.createServerSocket(7070);
	}
	// Ejecución del servidor para escuchar peticiones de los clientes
	public void runServer() {
		while (true) {
			// Espera las peticiones del cliente para comprobar mensaje/MAC
			try {
				System.err.println("Esperando conexiones de clientes...");
				Socket socket = this.serverSocket.accept();
				// Abre un BufferedReader para leer los datos del cliente
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// Abre un PrintWriter para enviar datos al cliente
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				// Se lee del cliente el mensaje y el macdelMensajeEnviado
				String mensaje = input.readLine();
				// A continuación habría que calcular el mac del MensajeEnviado que podría ser
				String macdelMensajeEnviado = input.readLine();
				//mac del MensajeCalculado
				System.out.println(mensaje + " " + macdelMensajeEnviado);
				if (macdelMensajeEnviado.equals(CalculatorMac.mac(mensaje, this.clave))) {
					output.println("Mensaje enviado integro ");
				} else {
					output.println("Mensaje enviado no integro.");
				}
				output.close();
				input.close();
				socket.close();
			} catch (IOException | InvalidKeyException | NoSuchAlgorithmException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	@Override
	public void run() {
		this.runServer();
	}
}
