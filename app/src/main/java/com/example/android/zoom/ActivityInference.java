package com.example.android.zoom;


import android.content.Context;
import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class ActivityInference {
    private static ActivityInference activityInferenceInstance;
    private TensorFlowInferenceInterface inferenceInterface;
    // Graph serialization: Invalid GraphDef
    //private static final String MODEL_FILE = "file:///android_asset/c-dcscnn.dlc";
    private static final String MODEL_FILE=  "file:///android_asset/frozen_compact_x2.pb";
    private static final String INPUT_NODE = "input";
    private static final String[] OUTPUT_NODES = {"output_tensor"};
    private static final String OUTPUT_NODE = "output_tensor";
    private static final long[] INPUT_SIZE = {500,500,1};
    private static final int OUTPUT_SIZE = 1000;
    private static AssetManager assetManager;

    public static ActivityInference getInstance(final Context context)
    {
        if (activityInferenceInstance == null)
        {
            activityInferenceInstance = new ActivityInference(context);
        }
        return activityInferenceInstance;
    }

    public ActivityInference(final Context context) {
        this.assetManager = context.getAssets();
        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    }

    public int[] getActivityProb(int[] input_signal)
    {
        //Si va mal probar con float
        float[] input = new float[500*500];
        for (int i = 0; i < input_signal.length; i++){
            input[i] = (float)input_signal[i];
        }

        float[] result= new float[OUTPUT_SIZE * OUTPUT_SIZE];
        int[] result_int= new int[OUTPUT_SIZE * OUTPUT_SIZE];

        inferenceInterface.feed(INPUT_NODE,input,INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE,result);


        for (int i = 0; i < result.length; i++){
            result_int[i] = (int)result[i];
        }
        return result_int;

    }
}
