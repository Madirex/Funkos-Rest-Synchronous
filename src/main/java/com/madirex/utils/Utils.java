package com.madirex.utils;

public class Utils {

    private static Utils utilsInstance;

    private Utils() {
    }

    public static Utils getInstance() {
        if (utilsInstance == null) {
            utilsInstance = new Utils();
        }
        return utilsInstance;
    }

    public String doubleToESLocal(double dbl){
        return String.format("%,.2f", dbl).replace(".", ",");
    }
}
