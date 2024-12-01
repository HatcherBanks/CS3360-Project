package cs3360;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    private static final String SALT = "SomeUniquePasswordManagerSalt"; // In a real app, use a more secure salt generation
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

    // Generate a secret key from the master password
    public static SecretKey generateSecretKey(String password) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), 
            SALT.getBytes(), 
            ITERATION_COUNT, 
            KEY_LENGTH
        );
        
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Encrypt the master password
    public static String encrypt(String password) throws Exception {
        SecretKey key = generateSecretKey(password);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Save encrypted password to file in Downloads/masterPasswords folder
    public static void saveEncryptedPassword(String encryptedPassword) throws IOException {
        // Get the user's home directory
        String userHome = System.getProperty("user.home");
        
        // Create path to Downloads/masterPasswords folder
        Path masterPasswordsDir = Paths.get(userHome, "Downloads", "masterPasswords");
        
        // Create the directory if it doesn't exist
        Files.createDirectories(masterPasswordsDir);
        
        // Create the file path
        Path passwordFilePath = masterPasswordsDir.resolve("masterPassword.txt");
        
        // Write the encrypted password
        try (FileWriter writer = new FileWriter(passwordFilePath.toFile())) {
            writer.write(encryptedPassword);
        }
    }

    // Check if the master password already exists
    public static boolean masterPasswordExists() throws IOException {
        // Get the path to the master password file
        String userHome = System.getProperty("user.home");
        Path passwordFilePath = Paths.get(userHome, "Downloads", "masterPasswords", "masterPassword.txt");

        // Check if the file exists
        return Files.exists(passwordFilePath);
    }
}
