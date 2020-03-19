
package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.net.SocketFactory;
import javax.swing.JOptionPane;

import macCalculator.CalculatorMac;

// Este cliente simula un man in the middle que modifica el mensaje
public class NoIntegrityVerifierClient {

	public String		macMensaje	= "";
	public String		mensaje		= "";
	public Integer		nonce;
	public BigDecimal	p;
	public BigDecimal	g;
	public BigDecimal	a;
	BigDecimal			Adash;
	public Double		serverB;

	private Socket		socket;


	public NoIntegrityVerifierClient(final BigDecimal p, final BigDecimal g, final BigDecimal a, final String mensaje) throws Exception {
		SocketFactory socketFactory = SocketFactory.getDefault();
		this.socket = socketFactory.createSocket("localhost", 7070);
		this.p = p;
		this.g = g;
		this.a = a;
		this.mensaje = mensaje;
	}

	// Constructor que abre una conexión Socket para enviar mensaje/MAC al servidor
	public void runCliente() {
		try {
			// Crea un PrintWriter para enviar mensaje/MAC al servidor
			BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			PrintWriter output = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));

			output.println(this.p);

			output.println(this.g);

			BigDecimal A = this.g.pow(this.a.intValue()).remainder(this.p);
			String Astr = A.toString();

			output.println(Astr);

			output.flush();

			System.out.println("[CLIENT] Private Key = " + this.a);
			BigDecimal serverB = new BigDecimal(input.readLine());

			this.Adash = serverB.pow(this.a.intValue()).remainder(this.p);

			String DHKey = Integer.toString(this.Adash.intValue());

			System.out.println("[CLIENT] Secret Key = " + DHKey);

			this.nonce = CalculatorMac.generarNonce();

			String nonceStr = Integer.toString(this.nonce);

			System.out.println("[CLIENT] Nonce: " + nonceStr);

			this.macMensaje = CalculatorMac.mac(this.mensaje, nonceStr, DHKey);

			System.out.println("[CLIENT] Message MAC: " + this.macMensaje);

			//Envío del nonce al servidor
			output.println(nonceStr);
			//MAN IN THE MIDDLE modifica el mensaje
			this.mensaje = "Este mensaje ha sido hackeado";
			// Envío del mensaje al servidor
			output.println(this.mensaje);
			// Habría que calcular el correspondiente MAC con la clave compartida por servidor/cliente
			output.println(this.macMensaje);
			// Importante para que el mensaje se envíe
			output.flush();
			// Crea un objeto BufferedReader para leer la respuesta del servidor
			// Lee la respuesta del servidor
			String respuesta = input.readLine();
			// Muestra la respuesta al cliente
			JOptionPane.showMessageDialog(null, respuesta);

			output.close();
			input.close();
			this.socket.close();

			System.out.println("[CLIENT] Se termina la conexion");

		} // end try
		catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			System.out.println("Esto peta");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Salida de la aplicacion
		finally {
			//System.exit(0);
		}
	}

	//	 public static void main( String args[]) throws Exception{
	//		 new IntegrityVerifierClient(23,9,6);
	//		 }
}
