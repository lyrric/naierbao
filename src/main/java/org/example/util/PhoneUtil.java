package org.example.util;

import java.util.Random;

public class PhoneUtil {


    public static String generateRandomPhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("186");
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int digit = random.nextInt(10); // 生成0到9之间的随机数字
            phoneNumber.append(digit);
        }
        return phoneNumber.toString();
    }
}
