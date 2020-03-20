
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
import java.util.ArrayList;
import java.util.List;

import javax.net.SocketFactory;
import javax.swing.JOptionPane;

import macCalculator.CalculatorMac;
import nonce.Nonce;

public class ReplayAttackVerifierClient {

	public String		macMensaje	= "";
	public String		mensaje		= "";
	public Long			nonce;
	public BigDecimal	p;
	public BigDecimal	g;
	public BigDecimal	a;
	BigDecimal			Adash;
	public Double		serverB;
	public List<String>	nonceUsados	= new ArrayList<String>();

	private Socket		socket;


	public ReplayAttackVerifierClient(final BigDecimal p, final BigDecimal g, final BigDecimal a, final String mensaje) throws Exception {
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

			String nonce = Long.toString(Nonce.generarNonce());

			System.out.println("[CLIENT] Nonce: " + nonce);

			this.macMensaje = CalculatorMac.mac(this.mensaje, nonce, DHKey);

			System.out.println("[CLIENT] Message MAC: " + this.macMensaje);

			//Envío del nonce al servidor
			output.println(nonce);
			// Envío del mensaje al servidor
			output.println(this.mensaje);
			// Habría que calcular el correspondiente MAC con la clave compartida por servidor/cliente
			output.println(this.macMensaje);
			// Importante para que el mensaje se envíe
			output.flush();

			//ATAQUE DE REPLAY
			output.println(nonce);
			output.println(this.mensaje);
			output.println(this.macMensaje);
			output.flush();

			// Leemos el nonce enviado por el servidor y comprobamos si no se ha usado anteriormente.
			String nonceRecib = input.readLine();
			System.out.println("[CLIENT] Nonce received: " + nonceRecib);
			if (!this.nonceUsados.contains(nonceRecib)) {
				this.nonceUsados.add(nonceRecib);
				String respuesta = input.readLine();
				String macdelMensajeRecib = input.readLine();
				System.out.println("[CLIENT] Message received: " + respuesta + ", Message MAC: " + macdelMensajeRecib);
				if (macdelMensajeRecib.equals(CalculatorMac.mac(respuesta, nonceRecib, DHKey))) {
					JOptionPane.showMessageDialog(null, respuesta);
				} else {
					JOptionPane.showMessageDialog(null, "FALLO. Respuesta del servidor no íntegra.");
				}
			} else {
				JOptionPane.showMessageDialog(null, "FALLO. Respuesta del servidor replicada.");
			}

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
