package com.techelevator.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {
    
    public static String getResponseErrorMessage(String exceptionMessageString) {
        Pattern pattern = Pattern.compile("^.*\"message\":\"([^\"]+)\".*$");
        Matcher matcher = pattern.matcher(exceptionMessageString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return exceptionMessageString;
    }

}
