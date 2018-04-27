package com.xchushi.fw.config;

import java.io.InputStream;

import com.xchushi.fw.common.environment.Propertie;

public class FileProperties implements Propertie {

    private java.util.Properties properties = null;

    public FileProperties(String filePath) {
        java.util.Properties ps = null;
        try {
            ps = new java.util.Properties();
            InputStream in = getClass().getClassLoader().getResourceAsStream(filePath);
            if (filePath != null && in != null) {
                ps.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ps = null;
        } finally {
            properties = ps;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> cls) {
        if (properties == null) {
            return null;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(cls)) {
            return (T) value;
        } else if (Integer.class.isAssignableFrom(cls)) {
            return (T) Integer.valueOf(value);
        } else if (Long.class.isAssignableFrom(cls)) {
            return (T) Long.valueOf(value);
        } else if (Double.class.isAssignableFrom(cls)) {
            return (T) Double.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(cls)) {
            return (T) Boolean.valueOf(value);
        }
        return null;
    }

}
