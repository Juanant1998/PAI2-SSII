
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

import javax.net.ServerSocketFactory;

import macCalculator.CalculatorMac;

public class IntegrityVerifierServer extends Thread {

	private ServerSocket	serverSocket;
	public BigDecimal b = new BigDecimal("15");
	public BigDecimal clientP, clientG, clientA, B;
	public BigDecimal Bdash;
	String Bstr;

	// Constructor del Servidor
	public IntegrityVerifierServer(BigDecimal bkey) throws Exception {
		// ServerSocketFactory para construir los ServerSockets
		ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
		// Creación de un objeto ServerSocket escuchando peticiones en el puerto 7070
		this.serverSocket = socketFactory.createServerSocket(7070);
		this.b = bkey;
	}
	// Ejecución del servidor para escuchar peticiones de los clientes
	public void runServer() {
		while (true) {
			// Espera las peticiones del cliente para comprobar mensaje/MAC
			try {
				System.err.println("[SERVER] Esperando conexiones de clientes...");
				Socket socket = this.serverSocket.accept();

				System.out.println("[SERVER] Connected to client; Private Key = " + b); 
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));


				this.clientP = new BigDecimal(input.readLine());
				this.clientG = new BigDecimal(input.readLine());
				this.clientA = new BigDecimal(input.readLine());
				
				System.out.println("[SERVER] Client p = " + clientP); 
				System.out.println("[SERVER] Client g = " + clientG); 
				System.out.println("[SERVER] Client A = " + clientA); 


				B = clientG.pow(b.intValue()).remainder(clientP);
	            Bstr = B.toString();

				output.println(Bstr);

				output.flush();
				Bdash = clientA.pow(b.intValue()).remainder(clientP);
				  

				String DHKey = Integer.toString(Bdash.intValue());
				
	            System.out.println("[SERVER] Secret Key = "
                        + DHKey); 
	            
				String mensaje = input.readLine();
				// A continuación habría que calcular el mac del MensajeEnviado que podría ser
				String macdelMensajeEnviado = input.readLine();
				//mac del MensajeCalculado
				System.out.println("[SERVER] Message received: " + mensaje + ", Message MAC: " + macdelMensajeEnviado);
				if (macdelMensajeEnviado.equals(CalculatorMac.mac(mensaje, DHKey))) {
					output.println("Mensaje enviado integro");
				} else {
					output.println("Mensaje enviado no integro.");
				}
				output.flush();

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
