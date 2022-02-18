package domain.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHandler {
    public static String createPassword(final String passwordToHash) {
        return BCrypt.hashpw(passwordToHash, BCrypt.gensalt());
    }

    public static boolean checkPassword(final String candidate, final String encryptedPassword) {
        return BCrypt.checkpw(candidate, encryptedPassword);
    }
}
