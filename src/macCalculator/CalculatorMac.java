
package macCalculator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CalculatorMac {

	public static String mac(final String mensaje, final String clave) throws InvalidKeyException, NoSuchAlgorithmException {
		Mac mac1 = Mac.getInstance("HmacSHA256");
		byte[] decodedKey = Base64.getDecoder().decode(clave);
		SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		mac1.init(key);
		mac1.update(mensaje.getBytes());
		byte[] b = mac1.doFinal();
		String s = javax.xml.bind.DatatypeConverter.printHexBinary(b);
		s.toUpperCase();
		return s;
	}
}
