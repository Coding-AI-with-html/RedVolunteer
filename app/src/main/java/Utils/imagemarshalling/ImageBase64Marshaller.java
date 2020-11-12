package Utils.imagemarshalling;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageBase64Marshaller {


    /**
     * Generate bitmap image from a BASE64 string (serialized)
     * @param encodedImg
     * @return
     */
    public static Bitmap decode64BitmapString(String encodedImg){
        byte[] decodedString = Base64.decode(encodedImg, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

/**
 * Generate a serialized string of the image (BASE 64)
 */

public static String encodedBase64BitmapString(Bitmap imageToEncode) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    imageToEncode.compress(Bitmap.CompressFormat.PNG, 100, baos);
    return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

}
private ImageBase64Marshaller(){

}



}
