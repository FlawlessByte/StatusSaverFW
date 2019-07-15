package co.realinventor.statussaverfw.Helpers;

import android.os.Environment;

public class Constants {
    public static String PATH_WHATSAPP = Environment.getExternalStorageDirectory().toString() + "/Whatsapp/Media/.Statuses/";
    public static String PATH_DOWNLOADED = Environment.getExternalStorageDirectory().toString() + "/StatusSaver/Saved/";
    public static int GRID_COUNT_IMAGE = 3;
    public static int GRID_COUNT_VIDEO = 3;
    public static int GRID_COUNT_SAVED = 3;
    public static String MEDIA_IMAGE = "media_image";
    public static String MEDIA_VIDEO = "media_video";
    public static String MEDIA_SAVED= "media_saved";
    public static String FRAG_SAVED= "fragment_saved";
    public static String FRAG_IMAGES= "fragment_images";
    public static String FRAG_VIDEOS= "fragment_videos";
    public static String FRAG_SETTINGS= "fragment_settings";
}
