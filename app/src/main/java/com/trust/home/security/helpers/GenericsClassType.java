package com.trust.home.security.helpers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsClassType <T> {
    private final Class<T> type;

    public GenericsClassType() {
        Type t = getClass().getGenericSuperclass();
        if(t instanceof Class) {
            this.type = (Class<T>) t;
        } else if(t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            this.type = (Class<T>) pt.getActualTypeArguments()[0];
        } else this.type = null;
    }

    public T createInstance() {
        try {
//            return type.newInstance();
            return (T) Class.forName(type.getPackage().getName()).newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
