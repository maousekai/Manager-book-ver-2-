package com.bookstore.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
    public static void main(String[] args) {
        String hash = "$2a$10$9N.yq.7v1V.q/aZ9s.C.O.G.N2.q.11UR2.1/0.6r/2.u";
        String testPlain = "thử-plain-nào-đó"; 
        System.out.println(checkPassword(testPlain, hash));
    }
}