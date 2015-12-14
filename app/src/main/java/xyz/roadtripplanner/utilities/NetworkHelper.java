package xyz.roadtripplanner.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Methods for the network
 *
 * @author xyz
 */
public class NetworkHelper {

    public static final int BUFFER_SIZE = 1500;
    public static final String TAG = NetworkHelper.class.getSimpleName();

    /**
     * Check if device is connected to the web
     *
     * @param context - activity's context
     * @return true if connected to the internet
     */
    public static boolean checkConnection(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    /**
     * Download image from web to internal storage
     *
     * @param imageUrl - web image url
     * @param fileName - file name to save image as
     * @param overwrite - if true existing files will be updated, otherwise - skipped
     * @return status - true if downloaded (or no loading needed)
     */
    public static boolean downloadImage(Context context, String imageUrl, String fileName, boolean overwrite){

        boolean status = false;

        if ((imageUrl.equals("")) || (fileName.equals(""))) {
            return false;
        }
        try{
            URL url = new URL(imageUrl);

            //File storagePath = Environment.getExternalStorageDirectory();
            File storagePath = context.getFilesDir();
            boolean fileExists = false;
            String outputFilePath = storagePath + "/" + fileName + Toolbox.getFileExtension(imageUrl);
            Log.d(TAG, "Save to: " + outputFilePath);
            //File outPutFile = new File(context.getFilesDir(), fileName);
//            try {
                fileExists = (new File(outputFilePath)).exists();
//            } catch (Exception e) {
//                //Log.e(TAG, "Cannot check file: "+ outputFilePath);
//            }

            if (overwrite || (!fileExists)) {
                InputStream input = url.openStream();
                // output
                OutputStream output = new FileOutputStream(outputFilePath);
                //OutputStream output = context.openFileOutput(fileName, Context.MODE_PRIVATE);

                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;
                    // write data to file
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                    status = true;
                } finally {
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                }
                input.close();

            } else {
                status = true;
            }

        } catch (Exception e) {
            //Log.e("Error: ", e.getMessage());
            status = false;
        }

        return status;
    }

}
