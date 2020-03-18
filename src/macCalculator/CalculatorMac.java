
package macCalculator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CalculatorMac {

	public static String mac(final String mensaje, final String clave) throws InvalidKeyException, NoSuchAlgorithmException {
		Mac mac1 = Mac.getInstance("HmacSHA256");
		byte[] decodedKey = Base64.getDecoder().decode(clave);
		SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		mac1.init(key);
		String mensajeNonce = mensaje + CalculatorMac.generarNonce();
		mac1.update(mensajeNonce.getBytes());
		byte[] b = mac1.doFinal();
		String s = javax.xml.bind.DatatypeConverter.printHexBinary(b);
		s.toUpperCase();
		return s;

	}

	private static Integer generarNonce() {
		Random random = new Random();
		Integer nonce = random.nextInt(100000000);
		return nonce;
	}
}
