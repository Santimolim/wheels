package wheels.datos;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Cifrado de contrasenas con PBKDF2 (HMAC-SHA256 + sal aleatoria).
 * Formato guardado en users.password:  pbkdf2$iteraciones$sal$hash
 * Las contrasenas antiguas en texto plano se aceptan una vez y se
 * cifran automaticamente en el siguiente inicio de sesion.
 */
public final class Seguridad {

    private static final String PREFIJO = "pbkdf2";
    private static final int ITERACIONES = 120_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom AZAR = new SecureRandom();

    private Seguridad() { }

    public static String cifrar(String clave) {
        byte[] sal = new byte[BYTES_SAL];
        AZAR.nextBytes(sal);
        byte[] hash = pbkdf2(clave.toCharArray(), sal, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder().withoutPadding();
        return PREFIJO + "$" + ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    /** Compara la clave escrita con la guardada (cifrada o, si es antigua, en texto plano). */
    public static boolean verificar(String clave, String guardada) {
        if (guardada == null) return false;
        if (!estaCifrada(guardada)) {
            return MessageDigest.isEqual(clave.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    guardada.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        try {
            String[] p = guardada.split("\\$");
            int iter = Integer.parseInt(p[1]);
            byte[] sal = Base64.getDecoder().decode(p[2]);
            byte[] esperado = Base64.getDecoder().decode(p[3]);
            return MessageDigest.isEqual(esperado, pbkdf2(clave.toCharArray(), sal, iter));
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean estaCifrada(String guardada) {
        return guardada != null && guardada.startsWith(PREFIJO + "$") && guardada.split("\\$").length == 4;
    }

    private static byte[] pbkdf2(char[] clave, byte[] sal, int iteraciones) {
        try {
            KeySpec spec = new PBEKeySpec(clave, sal, iteraciones, BITS_HASH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 no disponible en esta JVM", e);
        }
    }
}
