package com.codezjx.aidllib;

/**
 * Created by codezjx on 2017/10/8.<br/>
 */
public class StringUtils {

    private StringUtils() {

    }

    public static boolean isBlank(String str) {
        boolean isBlank = false;
        if (null == str || str.trim().length() == 0) {
            isBlank = true;
        }
        return isBlank;
    }
    
}
