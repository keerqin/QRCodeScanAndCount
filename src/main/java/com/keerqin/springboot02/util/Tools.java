package com.keerqin.springboot02.util;

public class Tools {
    public static String filterNull(final Object str) {
        String rs = str == null ? "" : str.toString().trim();
        return rs.equals("null") ? "" : rs;
    }
}
