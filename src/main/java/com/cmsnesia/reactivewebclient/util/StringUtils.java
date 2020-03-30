package com.cmsnesia.reactivewebclient.util;

public class StringUtils {

    public static boolean isEmpty(String object) {
        return object == null ? true : object.toString().isEmpty();
    }
}
