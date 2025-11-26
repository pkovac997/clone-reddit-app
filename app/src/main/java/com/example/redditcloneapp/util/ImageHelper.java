package com.example.redditcloneapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ImageHelper {

    public static Uri saveBitmapToAppStorage(@NonNull Bitmap bitmap, Context context) {
        try {
            java.io.File imagesDir = new java.io.File(context.getFilesDir(), "images");
            if (!imagesDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                imagesDir.mkdirs();
            }

            String fileName = "post_cam_" + System.currentTimeMillis() + ".jpg";
            java.io.File outFile = new java.io.File(imagesDir, fileName);

            try (java.io.FileOutputStream out = new java.io.FileOutputStream(outFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
            }

            return Uri.fromFile(outFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save camera image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static Uri copyImageToAppStorage(@NonNull Uri sourceUri, Context context) {
        try {
            java.io.File imagesDir = new java.io.File(context.getFilesDir(), "images");

            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            String fileName = "post_" + System.currentTimeMillis() + ".jpg";
            java.io.File outFile = new java.io.File(imagesDir, fileName);

            try (java.io.InputStream in = context.getContentResolver().openInputStream(sourceUri);
                 java.io.OutputStream out = new java.io.FileOutputStream(outFile)) {
                if (in == null) {
                    return null;
                }
                byte[] buffer = new byte[8 * 1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }

            return Uri.fromFile(outFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to copy image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
