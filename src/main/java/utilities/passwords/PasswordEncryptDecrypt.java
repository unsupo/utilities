package utilities.passwords;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * Created by jarndt on 4/4/17.
 */
public class PasswordEncryptDecrypt {
    private static final char[] CHAR_ARRAY = System.getenv("ENCRYPT_HASH").toCharArray();
    private static final byte[] SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    public static void main(String[] args) throws Exception {
        encryptReader();
//        String s = "/Users/jarndt/code_projects/qa-automation/./qa-automation-dist/src/main/config/specsV2.xml\n" +
//                "/Users/jarndt/code_projects/qa-automation/./qa-automation-dist/target/standalone/qa-automation/config/specsV2.xml\n" +
//                "/Users/jarndt/code_projects/qa-automation/./qa-automation-src/src/main/resources/specsV2.xml\n" +
//                "/Users/jarndt/code_projects/qa-automation/./qa-automation-src/target/classes/specsV2.xml\n" +
//                "/Users/jarndt/code_projects/qa-automation/./qa-automation-src/target/jarResources/specsV2.xml\n" +
//                "/Users/jarndt/code_projects/qa-automation/./testing/jarResources/specsV2.xml";
    }

    public static void encryptReader() throws IOException, GeneralSecurityException {
        Console console = System.console();
        if (console == null) {
            System.out.println("Couldn't get Console instance");
            System.out.println("WARNING  Password will not be masked");
            System.out.print("Enter Password to be encrypted: ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String line = bufferedReader.readLine();
            String v = encrypt(line);
            System.out.println("Encrypted password is: "+v);
            return;
        }

        console.printf("Testing password%n");
        char passwordArray[] = console.readPassword("Enter your secret password: ");
        console.printf("Encrypted Password is: %s%n", encrypt(new String(passwordArray)));
    }


    public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(CHAR_ARRAY));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new String(java.util.Base64.getMimeEncoder().encode(bytes),
                StandardCharsets.UTF_8);
    }

    public static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(CHAR_ARRAY));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return  java.util.Base64.getMimeDecoder().decode(property);// new BASE64Decoder().decodeBuffer(property);
    }


}