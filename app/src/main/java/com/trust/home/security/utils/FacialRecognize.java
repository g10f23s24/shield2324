package com.trust.home.security.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.trust.home.security.HomeSecurityApplication;
import com.trust.home.security.R;
import com.trust.home.security.database.entity.Face;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;

public class FacialRecognize {
    private static final float IMAGE_MEAN = 128.0f;
    private static final float IMAGE_STD = 128.0f;
    private static final int INPUT_SIZE = 112;
    private static final int OUTPUT_SIZE = 192;

    private Interpreter tfLite;
    private static FacialRecognize INSTANCE;
    private final Gson gson = new Gson();
    private final Context context;

    private FacialRecognize(Context context) {
        this.context = context;
    }

    public static synchronized FacialRecognize getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new FacialRecognize(context);
            INSTANCE.loadModel();
        }
        return INSTANCE;
    }

    public void training(long userId, String userName, Bitmap bmp) {
        FloatArray2D floatArray2D = new FloatArray2D(getEmbeddingsFace(bmp));
        Face face = new Face(null, userId, userName, gson.toJson(floatArray2D));
        HomeSecurityApplication.database.checkUserIsRegisteredFace(userName, isRegistered -> {
            if(!isRegistered) {
                HomeSecurityApplication.database.insertFace(face);
            }
            return Unit.INSTANCE;
        });
    }

    public void recognize(String userName, Bitmap bmp, RecognitionListener listener) {
        HomeSecurityApplication.database.selectFaceWhere(userName, face -> {
            if(face == null) {
                listener.userHasNotRegistration();
            } else {
                float distance = findNearest(stringToArray(face.getFaceData()), getEmbeddingsFace(bmp)[0]);

                Log.d("DISTANCE", String.valueOf(distance));

                if(distance < 1.000f) {
                    listener.recognizeSuccess();
                } else listener.recognizeFailure();
            }
            return Unit.INSTANCE;
        });
    }

    private float[][] stringToArray(String input) {
        return gson.fromJson(input, FloatArray2D.class).getData();
    }

    private float[][] getEmbeddingsFace(Bitmap bmp) {
        Map<Integer, Object> outputMap = new HashMap<>();
        float[][] embeddings = new float[1][OUTPUT_SIZE];
        outputMap.put(0, embeddings);
        tfLite.runForMultipleInputsOutputs(createInputArray(bmp), outputMap);
        return embeddings;
    }

    private float findNearest(float[][] registered, float[] emb) {
        final float[] knownEmb = ((float[][]) registered)[0];

        float distance = 0;
        for (int i = 0; i < emb.length; i++) {
            float diff = emb[i] - knownEmb[i];
            distance += diff*diff;
        }

        return (float) Math.sqrt(distance);
    }

    private Object[] createInputArray(Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                int pixelValue = intValues[i * INPUT_SIZE + j];
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            }
        }

        return new Object[]{imgData};
    }

    /** Model loader */
    private void loadModel() {
        try {
            tfLite = new Interpreter(getModel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer getModel() throws IOException {
        InputStream modelInputStream = context.getResources().openRawResource(R.raw.mobile_face_data);

        byte[] modelData = new byte[modelInputStream.available()];
        modelInputStream.read(modelData);
        modelInputStream.close();

        ByteBuffer modelByteBuffer = ByteBuffer.allocateDirect(modelData.length);
        modelByteBuffer.put(modelData);
        modelByteBuffer.rewind();
        return modelByteBuffer;
    }

    public interface RecognitionListener {
        void recognizeSuccess();
        void recognizeFailure();
        void userHasNotRegistration();
    }
}
