package carleton150.edu.carleton.carleton150;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by haleyhinze on 3/3/16.
 */
public class Constants {

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public final static String QUEST_PREFERENCES_KEY = "QuestPreferences";
    // Location updates intervals in milliseconds
    public static int UPDATE_INTERVAL = 30000; // 30 sec
    public static int FASTEST_INTERVAL = 10000; // 10 sec
    public static int DISPLACEMENT = 10; // 10 meters

    public static final String isFirstHistoryRunStr = "isFirstHistoryRun";
    public static final String isFirstQuestRunStr = "isFirstQuestRun";

    public static final LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);
    public static final double MAX_LONGITUDE = -93.141134;
    public static final double MIN_LONGITUDE = -93.161333;
    public static final double MAX_LATITUDE = 44.488045;
    public static final double MIN_LATITUDE = 44.458869;
    public static final int PROVIDER_NUMBER = 256;
    public static final int DEFAULT_ZOOM = 15;
    public static final int DEFAULT_BEARING = 0;
    public static final int DEFAULT_MAX_ZOOM = 13;
    public static final int PLACEHOLDER_IMAGE_DIMENSIONS = 10;
    public static final float DEFAULT_SCALE_FROM = .5f;

    public static final String INFO_ENDPOINT = "https://carl150.carleton.edu/info";
    public static final String GEOFENCES_ENDPOINT = "https://carl150.carleton.edu/geofences";
    public static final String EVENTS_ENDPOINT = "https://carl150.carleton.edu/events";
    public static final String QUESTS_ENDPOINT = "https://carl150.carleton.edu/quest";
    public static final String MEMORIES_ENDPOINT = "https://carl150.carleton.edu/memories_fetch";
    public static final String ADD_MEMORY_ENDPOINT = "https://carl150.carleton.edu/memories_add";

    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";

    public static final String TAG = "GeofenceTransitionsIS";

    public static final String baseURLString = " https://www.carleton.edu/global_stock/images/campus_map/tiles/base/%d_%d_%d.png";
    public static final String labelURLString = " https://www.carleton.edu/global_stock/images/campus_map/tiles/labels/%d_%d_%d.png";

}
