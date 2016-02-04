package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

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
    protected boolean zoomCamera = true;
    protected GoogleMap mMap; // Might be null if Google Play services APK is not available.

    //This is a variable and not a function
    public TileProvider tileProvider = new UrlTileProvider(256, 256) {
        @Override
        public URL getTileUrl(int x, int y, int zoom) {

    /* Define the URL pattern for the tile images */
            String s = String.format(" https://www.carleton.edu/global_stock/images/campus_map/tiles/base/16_%d_%d.png",
                    x, y);

            if (!checkTileExists(x, y, zoom)) {
                return null;
            }

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
            int minZoom = -5000;
            int maxZoom = 5000;

            //here we'll put the range and domain of the tiles
            if (x<15807 || x>15813 || y>23713 || y<23713){
                return false;
            }

            if ((zoom < minZoom || zoom > maxZoom)) {
                return false;
            }

            return true;
        }
    };
    //end of tileProvider

    public TileOverlay tileOverlay;

    /***** Sets up the map if it is possible to do so *****/
    public boolean setUpMapIfNeeded() {
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
                if (cameraPosition.zoom <= 13) {
                    if (cameraPosition.target == null) {
                        setCamera();
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
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
        if(mainActivity.getGeofenceMonitor().currentLocation != null && zoomCamera) {
            zoomCamera = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mainActivity.getGeofenceMonitor().currentLocation.getLatitude(), mainActivity.getGeofenceMonitor().currentLocation.getLongitude()))
                    .zoom(15)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }if(mainActivity.getGeofenceMonitor().currentLocation == null){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(CENTER_CAMPUS.latitude, CENTER_CAMPUS.longitude))
                    .zoom(15)
                    .bearing(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}