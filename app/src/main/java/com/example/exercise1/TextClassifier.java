package com.example.exercise1;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class TextClassifier {
    private static final String TAG = "TextClassifier";
    private static final String MODEL_PATH = "text-classification.tflite";
    private static final int MAX_SEQUENCE_LENGTH = 256;

    private Interpreter tflite;
    private Map<String, Integer> wordIndex;

    public TextClassifier(Context context) {
        try {
            tflite = new Interpreter(loadModelFile(context));
            // Initialize vocabulary/word index (would need to create based on your model)
            wordIndex = new HashMap<>();
            // Load words from vocabulary file or initialize from resources
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[] classify(String text) {
        // Preprocess text (tokenize, etc)
        int[] inputSequence = tokenize(text);

        // Prepare input and output buffers
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(MAX_SEQUENCE_LENGTH * 4);
        for (int i = 0; i < MAX_SEQUENCE_LENGTH; i++) {
            if (i < inputSequence.length) {
                inputBuffer.putInt(inputSequence[i]);
            } else {
                inputBuffer.putInt(0); // padding
            }
        }
        inputBuffer.rewind();

        float[][] outputBuffer = new float[1][1]; // Adjust size based on number of categories

        // Run inference
        tflite.run(inputBuffer, outputBuffer);

        return outputBuffer[0];
    }

    private int[] tokenize(String text) {
        // Implement tokenization based on your model requirements
        // This is simplified - you would need to implement actual tokenization
        String[] words = text.toLowerCase().split("\\s+");
        int[] tokens = new int[words.length];

        for (int i = 0; i < words.length; i++) {
            Integer index = wordIndex.get(words[i]);
            tokens[i] = (index != null) ? index : 0; // Unknown token
        }

        return tokens;
    }
}