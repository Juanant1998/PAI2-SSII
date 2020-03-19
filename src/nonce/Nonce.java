
package nonce;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Nonce {

	public static Long generarNonce() throws NoSuchAlgorithmException {
		Long numeroAleatorio = new SecureRandom().nextLong();
		while (numeroAleatorio <= 0) {
			numeroAleatorio = new SecureRandom().nextLong();
		}
		return numeroAleatorio;
	}
}
