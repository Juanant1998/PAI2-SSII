
package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;

import macCalculator.CalculatorMac;
import nonce.Nonce;

public class ReplayAttackVerifierServer extends Thread {

	public String			mensajeOK			= "OK. Mensaje enviado íntegro.";
	public String			mensajeFalloMitM	= "FALLO. Mensaje enviado no íntegro.";
	public String			mensajeFalloReplay	= "FALLO. Intento de ataque de replay.";
	private ServerSocket	serverSocket;
	public BigDecimal		b					= new BigDecimal("15");
	public BigDecimal		clientP, clientG, clientA, B;
	public BigDecimal		Bdash;
	String					Bstr;
	public List<String>		nonceUsados			= new ArrayList<String>();
	public boolean			execute				= true;


	// Constructor del Servidor
	public ReplayAttackVerifierServer(final BigDecimal bkey) throws Exception {
		// ServerSocketFactory para construir los ServerSockets
		ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
		// Creación de un objeto ServerSocket escuchando peticiones en el puerto 7070
		this.serverSocket = socketFactory.createServerSocket(7070);
		this.b = bkey;
	}
	// Ejecución del servidor para escuchar peticiones de los clientes
	public void runServer() {
		while (this.execute) {
			// Espera las peticiones del cliente para comprobar mensaje/MAC
			try {
				System.err.println("[SERVER] Esperando conexiones de clientes...");
				Socket socket = this.serverSocket.accept();

				System.out.println("[SERVER] Connected to client; Private Key = " + this.b);
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

				this.clientP = new BigDecimal(input.readLine());
				this.clientG = new BigDecimal(input.readLine());
				this.clientA = new BigDecimal(input.readLine());

				System.out.println("[SERVER] Client p = " + this.clientP);
				System.out.println("[SERVER] Client g = " + this.clientG);
				System.out.println("[SERVER] Client A = " + this.clientA);

				this.B = this.clientG.pow(this.b.intValue()).remainder(this.clientP);
				this.Bstr = this.B.toString();

				output.println(this.Bstr);

				output.flush();
				this.Bdash = this.clientA.pow(this.b.intValue()).remainder(this.clientP);

				String DHKey = Integer.toString(this.Bdash.intValue());

				// Nonce del servidor
				String nonceServ = Long.toString(Nonce.generarNonce());

				System.out.println("[SERVER] Secret Key = " + DHKey);

				// Leemos el nonce enviado por el cliente y comprobamos si no se ha usado anteriormente.
				String nonce = input.readLine();
				// SIMULAMOS ATAQUE DE REPLAY. AÑADIMOS EL NONCE A LA LISTA ANTES DE LEERLO COMO SI YA SE
				// HUBIESE USADO.
				this.nonceUsados.add(nonce);
				System.out.println("[SERVER] Nonce received: " + nonce);
				if (!this.nonceUsados.contains(nonce)) {
					this.nonceUsados.add(nonce);
					String mensaje = input.readLine();
					// A continuación habría que calcular el mac del MensajeEnviado que podría ser
					String macdelMensajeEnviado = input.readLine();
					//mac del MensajeCalculado
					System.out.println("[SERVER] Message received: " + mensaje + ", Message MAC: " + macdelMensajeEnviado);

					if (macdelMensajeEnviado.equals(CalculatorMac.mac(mensaje, nonce, DHKey))) {
						String macMensaje = CalculatorMac.mac(this.mensajeOK, nonceServ, DHKey);
						System.out.println("[SERVER] Message MAC: " + macMensaje);
						output.println(nonceServ);
						output.println(this.mensajeOK);
						output.println(macMensaje);
						output.flush();
					} else {
						String macMensaje = CalculatorMac.mac(this.mensajeFalloMitM, nonceServ, DHKey);
						System.out.println("[SERVER] Message MAC: " + macMensaje);
						output.println(nonceServ);
						output.println(this.mensajeFalloMitM);
						output.println(macMensaje);
						output.flush();
					}
				} else {
					String macMensaje = CalculatorMac.mac(this.mensajeFalloReplay, nonceServ, DHKey);
					System.out.println("[SERVER] Message MAC: " + macMensaje);
					output.println(nonceServ);
					output.println(this.mensajeFalloReplay);
					output.println(macMensaje);
					output.flush();
				}

				System.out.println("[SERVER] Nonce generado: " + nonceServ);

				System.out.println("[SERVER] Se termina la conexion");
				input.close();
				output.close();
				socket.close();

			} catch (IOException ioException) {
				System.out.println("Esto peta");
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				System.out.println("Esto peta");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				System.out.println("Esto peta");
			}
		}
	}
	@Override
	public void run() {
		this.runServer();
	}
	//	public static void main( String args[] ) throws Exception
	//	 {
	//	 IntegrityVerifierServer server = new IntegrityVerifierServer(3);
	//	 server.run();
	//	 }
}
