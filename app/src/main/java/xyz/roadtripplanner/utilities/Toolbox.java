package xyz.roadtripplanner.utilities;

import android.util.Log;

/**
 * Various methods
 *
 * @author xyz
 */
public class Toolbox {

    //flag for debugging logs
    public static final boolean DEBUG = true;

    /**
     * Return file extension from file URL
     *
     * @param filePath - url of the file
     * @return - text of extension
     */
    public static String getFileExtension(String filePath){

        String extension = "";
        try {
            extension = filePath.substring(filePath.lastIndexOf('.'));
        } catch (Exception e){
            Log.e("IMAGE PARSING", e.getMessage());
        }

        return extension;
    }

}
