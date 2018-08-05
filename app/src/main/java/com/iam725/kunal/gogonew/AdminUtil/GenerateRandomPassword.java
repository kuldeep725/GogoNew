package com.iam725.kunal.gogonew.AdminUtil;

import java.util.Random;
import java.util.UUID;

public class GenerateRandomPassword {
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random RANDOM = new Random();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString()+ UUID.randomUUID().toString().substring(0, 7);
    }
}
