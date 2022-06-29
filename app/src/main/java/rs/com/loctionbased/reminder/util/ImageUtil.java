package rs.com.loctionbased.reminder.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageUtil {

    public static Bitmap getBitmap(byte[] imgInBytes) {
        //TODO: Check NullPointerException bug here.
        int length = imgInBytes.length;
        return BitmapFactory.decodeByteArray(imgInBytes, 0, length);
    }

    public static Bitmap getBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static Bitmap getBitmap(Uri uri, AppCompatActivity activity) throws IOException {
        return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
    }

    public static byte[] toCompressedByteArray(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public static void saveBitmapAsJpeg(File file, Bitmap bitmapToSave, int quality) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(toCompressedByteArray(bitmapToSave, quality));
        fos.close();
    }

    public static Bitmap scaleBitmap(Bitmap image, int largerScaledDimension) {

        if (image == null || image.getWidth() == 0 || image.getHeight() == 0)
            return image;

        if (image.getHeight() <= largerScaledDimension && image.getWidth() <= largerScaledDimension)
            return image;

        boolean heightLargerThanWidth = (image.getHeight() > image.getWidth());
        float aspectRatio = (heightLargerThanWidth ? (float)image.getHeight() / (float)image.getWidth() : (float)image.getWidth() / (float)image.getHeight());
        int smallerScaledDimension = (int) (largerScaledDimension / aspectRatio);
        int scaledWidth = (heightLargerThanWidth ? smallerScaledDimension : largerScaledDimension);
        int scaledHeight = (heightLargerThanWidth ? largerScaledDimension : smallerScaledDimension);

        return Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, true);
    }

}
