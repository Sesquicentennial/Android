package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.R;

/**
 * Created by haleyhinze on 2/2/16.
 */
public class MapMainFragment extends MainFragment {

    protected static LatLng CENTER_CAMPUS = new LatLng(44.460174, -93.154726);
    private double MAX_LONGITUDE = -93.141134;
    private double MIN_LONGITUDE = -93.161333;
    private double MAX_LATITUDE = 44.488045;
    private double MIN_LATITUDE = 44.458869;
    private int PROVIDER_NUMBER = 256;
    private int MAX_ZOOM_TILING = 5000;
    private int MIN_ZOOM_TILING = -5000;
    private int DEFAULT_ZOOM = 15;
    private int DEFAULT_BEARING = 0;

    private int DEFAULT_MAX_ZOOM = 13;

    private String baseURLString = " https://www.carleton.edu/global_stock/images/campus_map/tiles/base/%d_%d_%d.png";
    private String labelURLString = " https://www.carleton.edu/global_stock/images/campus_map/tiles/labels/%d_%d_%d.png";

    private int X_MIN_TILING = 15807;
    private int X_MAX_TILING = 15813;
    private int Y_MIN_TILING = 23705;
    private int Y_MAX_TILING = 23715;

    protected boolean zoomCamera = true;
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.


    //This is a variable and not a function
    public TileProvider baseTileProvider = new UrlTileProvider(PROVIDER_NUMBER, PROVIDER_NUMBER) {
        @Override
        public URL getTileUrl(int x, int y, int zoom) {

    /* Define the URL pattern for the tile images */
            String s = String.format(baseURLString,
                    zoom, x, y);

            /**if (!checkTileExists(x, y, zoom)) {
                return null;
            } **/

            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }



        /*
         * Check that the tile server supports the requested x, y and zoom.
         * Complete this stub according to the tile range you support.
         * If you support a limited range of tiles at different zoom levels, then you
         * need to define the supported x, y range at each zoom level.
         */
        private boolean checkTileExists(int x, int y, int zoom) {

            return true;
        }
    };
    //end of tileProvider

    //This is a variable and not a function
    public TileProvider labelTileProvider = new UrlTileProvider(PROVIDER_NUMBER, PROVIDER_NUMBER) {
        @Override
        public URL getTileUrl(int x, int y, int zoom) {

    /* Define the URL pattern for the tile images */
            String s = String.format(labelURLString,
                    zoom, x, y);

            /**if (!checkTileExists(x, y, zoom)) {
             return null;
             } **/

            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }



        /*
         * Check that the tile server supports the requested x, y and zoom.
         * Complete this stub according to the tile range you support.
         * If you support a limited range of tiles at different zoom levels, then you
         * need to define the supported x, y range at each zoom level.
         */
        private boolean checkTileExists(int x, int y, int zoom) {

            return true;
        }
    };
    //end of tileProvider

    public TileOverlay tileOverlay;

    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
        MainActivity mainActivity = (MainActivity) getActivity();
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.my_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                return true;
            } else {
                mainActivity.showAlertDialog(getResources().getString(R.string.unable_to_set_up_map),
                        new AlertDialog.Builder(mainActivity).create());
                return false;
            }
        }
        return true;
    }



    /**
     * Sets up the map (should only be called if mMap is null)
     * Monitors the zoom and target of the camera and changes them
     * if the user zooms out too much or scrolls map too far off campus.
     */
    protected void setUpMap() {

        //Makes it so user can't zoom out very far
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                setCamera();
                //TODO: figure out best zoom level for campus
                if (cameraPosition.zoom <= DEFAULT_MAX_ZOOM) {
                    if (cameraPosition.target == null) {
                        setCamera();
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_MAX_ZOOM));
                }

                //makes it so user can't scroll too far off campus
                //TODO: figure out best map limits
                double latitude = cameraPosition.target.latitude;
                double longitude = cameraPosition.target.longitude;
                if (cameraPosition.target.longitude > MAX_LONGITUDE) {
                    longitude = MAX_LONGITUDE;
                }
                if (cameraPosition.target.longitude < MIN_LONGITUDE) {
                    longitude = MIN_LONGITUDE;
                }
                if (cameraPosition.target.latitude > MAX_LATITUDE) {
                    latitude = MAX_LATITUDE;
                }
                if (cameraPosition.target.latitude < MIN_LATITUDE) {
                    latitude = MIN_LATITUDE;
                }

                CameraPosition newCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude))
                        .zoom(cameraPosition.zoom)
                        .bearing(cameraPosition.bearing)
                        .build();
                if(mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
                }

            }
        });

        setCamera();
    }


    /**
     * Sets the camera for the map. If we have user location, sets the camera to that location.
     * Otherwise, the camera target is the center of campus.
     */
    protected void setCamera(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null) {

            if (mainActivity.getGeofenceMonitor().currentLocation != null && zoomCamera) {
                zoomCamera = false;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mainActivity.getGeofenceMonitor().currentLocation.getLatitude(), mainActivity.getGeofenceMonitor().currentLocation.getLongitude()))
                        .zoom(DEFAULT_ZOOM)
                        .bearing(DEFAULT_BEARING)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (mainActivity.getGeofenceMonitor().currentLocation == null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(CENTER_CAMPUS.latitude, CENTER_CAMPUS.longitude))
                        .zoom(DEFAULT_ZOOM)
                        .bearing(DEFAULT_BEARING)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        drawTiles();

    }

    public void drawTiles(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.getMemoryClass() > 100) {

            if (mMap != null) {
                setUpMap();
            }
            setUpMapIfNeeded();
            if (mMap != null) {
                TileOverlay baseTileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                        .tileProvider(baseTileProvider));
                baseTileOverlay.setZIndex(0);
                TileOverlay labelTileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                        .tileProvider(labelTileProvider));
                labelTileOverlay.setZIndex(2);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMap = null;
    }
}
