package com.trust.home.security.utils;

import com.google.gson.annotations.SerializedName;

public class FloatArray2D {
    @SerializedName("data")
    public float[][] data;

    public FloatArray2D() {
    }

    public FloatArray2D(float[][] data) {
        this.data = data;
    }

    public float[][] getData() {
        return data;
    }

    public void setData(float[][] data) {
        this.data = data;
    }
}