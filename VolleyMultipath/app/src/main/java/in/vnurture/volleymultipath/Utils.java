package in.vnurture.volleymultipath;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Utils {


    public static String setImage(Bitmap path){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        path.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString.trim();
    }
}
