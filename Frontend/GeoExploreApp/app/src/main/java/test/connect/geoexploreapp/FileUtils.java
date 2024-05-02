package test.connect.geoexploreapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String createCopyFromUri(Context context, Uri contentUri) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        String filePath = null;

        try {
            // Create a temporary file to copy the URI content
            File tempFile = File.createTempFile("upload", ".jpg", context.getExternalFilesDir(null));
            filePath = tempFile.getAbsolutePath();

            inputStream = context.getContentResolver().openInputStream(contentUri);
            if (inputStream == null) {
                Log.e("FileUtils", "Unable to obtain input stream from URI");
                return null;
            }

            outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

        } catch (IOException e) {
            Log.e("FileUtils", "Failed to create a copy from URI", e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e("FileUtils", "Error closing IO resources", e);
            }
        }

        return filePath;
    }
}
