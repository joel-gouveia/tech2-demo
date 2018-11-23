package tech2.demo.com.demo.common;

import java.util.Random;

/**
 * Created by Joel on 03-Mar-16.
 */
public class StringUtils {

    private static final int MAX_LENGTH = 15;

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
