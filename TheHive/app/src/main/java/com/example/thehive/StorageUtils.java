package com.example.thehive;

import android.content.Context;
import org.json.JSONArray;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class StorageUtils {

    public static JSONArray readJsonArray(Context context, String filename) {
        try {
            FileInputStream fis = context.openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            String jsonStr = new String(buffer, "UTF-8");
            return new JSONArray(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void writeJsonArray(Context context, String filename, JSONArray jsonArray) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes("UTF-8"));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
