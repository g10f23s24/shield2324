package com.trust.home.security.helpers;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class BundleFactory {
    private final Builder builder;

    public BundleFactory(Builder builder) {
        this.builder = builder;
    }

    private Bundle buildBundle() {
        Bundle args = new Bundle();
        for(int i = 0; i < builder.options.size(); i++) {
            Option<?> option = builder.options.get(i);
            if(option.value instanceof String) {
                args.putString(option.key, (String) option.value);
            } else if(option.value instanceof Float) {
                args.putFloat(option.key, (Float) option.value);
            } else if(option.value instanceof Boolean) {
                args.putBoolean(option.key, (Boolean) option.value);
            } else if(option.value instanceof Integer) {
                args.putInt(option.key, (Integer) option.value);
            } else if(option.value instanceof Long) {
                args.putLong(option.key, (Long) option.value);
            }
        }
        return args;
    }

    public static class Builder {
        private final List<Option<?>> options = new ArrayList<>();

         public <T> Builder put (Option<T> option) {
             this.options.add(option);
             return this;
         }

         public Bundle build() {
             return new BundleFactory(this).buildBundle();
         }
    }

    public static final class Option <T> {
        public String key;
        public T value;

        public Option(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
