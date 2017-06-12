package br.tiagohm.chatuniversidade.common.utils;

import android.support.annotation.NonNull;

import java.security.MessageDigest;

public class Utils {

    private Utils() {
    }

    @NonNull
    private static String gerarHash(String text) {
        try {
            byte[] hashBytes = MessageDigest.getInstance("MD5").digest(text.getBytes());
            StringBuilder sb = new StringBuilder(hashBytes.length * 2);
            for (byte hashByte : hashBytes)
                sb.append(String.format("%02X", hashByte));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
