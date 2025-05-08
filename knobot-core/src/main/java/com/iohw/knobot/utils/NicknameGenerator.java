package com.iohw.knobot.utils;

import java.util.Random;

/**
 * @author: iohw
 * @date: 2025/5/8 15:21
 * @description:
 */
public class NicknameGenerator {
    public static String generateNickname() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            randomPart.append(characters.charAt(index));
        }

        return "用户" + randomPart.toString();
    }
}
