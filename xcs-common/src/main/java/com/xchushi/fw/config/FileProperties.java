package com.xchushi.fw.config;

import java.io.IOException;

import com.xchushi.fw.common.environment.Propertie;

public class FileProperties implements Propertie {
    
    private java.util.Properties properties = null;
    
    public FileProperties(String filePath) throws IOException{
        this.properties = new java.util.Properties();
        this.properties.load(XcsConfigure.class.getClassLoader().getResourceAsStream(filePath));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> cls) {
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
