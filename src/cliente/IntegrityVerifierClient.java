
package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.swing.JOptionPane;

public class IntegrityVerifierClient {

	public String	macMensaje	= "";
	public String	mensaje		= "";

	private Socket	socket;


	public IntegrityVerifierClient() throws Exception {
		SocketFactory socketFactory = SocketFactory.getDefault();
		this.socket = socketFactory.createSocket("localhost", 7070);
	}

	// Constructor que abre una conexión Socket para enviar mensaje/MAC al servidor
	public void runCliente() {
		try {
			// Crea un PrintWriter para enviar mensaje/MAC al servidor
			PrintWriter output = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			// Envío del mensaje al servidor
			output.println(this.mensaje);
			// Habría que calcular el correspondiente MAC con la clave compartida por servidor/cliente
			output.println(this.macMensaje);
			// Importante para que el mensaje se envíe
			output.flush();
			// Crea un objeto BufferedReader para leer la respuesta del servidor
			BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			// Lee la respuesta del servidor
			String respuesta = input.readLine();
			// Muestra la respuesta al cliente
			JOptionPane.showMessageDialog(null, respuesta);
			// Se cierra la conexion
			output.close();
			input.close();
			this.socket.close();
		} // end try
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
		// Salida de la aplicacion
		finally {
			System.exit(0);
		}
	}
}
