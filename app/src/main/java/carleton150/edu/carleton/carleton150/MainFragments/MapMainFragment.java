package carleton150.edu.carleton.carleton150.MainFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

import carleton150.edu.carleton.carleton150.MainActivity;
import carleton150.edu.carleton.carleton150.R;

/**
 * MainFragment that contains a map. Extended by HistoryFragment and QuestInProgressFragment
 */
public class MapMainFragment extends MainFragment {


    protected boolean zoomCamera = true;
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.


    //TileProvider for Carleton map tiling
    public TileProvider baseTileProvider = new UrlTileProvider(constants.PROVIDER_NUMBER, constants.PROVIDER_NUMBER) {
        @Override
        public URL getTileUrl(int x, int y, int zoom) {

         /* Define the URL pattern for the tile images */
            String s = String.format(constants.baseURLString,
                    zoom, x, y);
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }
    };


    //TileProvider for Carleton label tiling
    public TileProvider labelTileProvider = new UrlTileProvider(constants.PROVIDER_NUMBER, constants.PROVIDER_NUMBER) {
        @Override
        public URL getTileUrl(int x, int y, int zoom) {

        /* Define the URL pattern for the tile images */
            String s = String.format(constants.labelURLString,
                    zoom, x, y);
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }
    };


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
                if (cameraPosition.zoom <= constants.DEFAULT_MAX_ZOOM) {
                    if (cameraPosition.target == null) {
                        setCamera();
                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(constants.DEFAULT_MAX_ZOOM));
                }

                //makes it so user can't scroll too far off campus
                double latitude = cameraPosition.target.latitude;
                double longitude = cameraPosition.target.longitude;
                if (cameraPosition.target.longitude > constants.MAX_LONGITUDE) {
                    longitude = constants.MAX_LONGITUDE;
                }
                if (cameraPosition.target.longitude < constants.MIN_LONGITUDE) {
                    longitude = constants.MIN_LONGITUDE;
                }
                if (cameraPosition.target.latitude > constants.MAX_LATITUDE) {
                    latitude = constants.MAX_LATITUDE;
                }
                if (cameraPosition.target.latitude < constants.MIN_LATITUDE) {
                    latitude = constants.MIN_LATITUDE;
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
                        .zoom(constants.DEFAULT_ZOOM)
                        .bearing(constants.DEFAULT_BEARING)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (mainActivity.getGeofenceMonitor().currentLocation == null) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(constants.CENTER_CAMPUS.latitude, constants.CENTER_CAMPUS.longitude))
                        .zoom(constants.DEFAULT_ZOOM)
                        .bearing(constants.DEFAULT_BEARING)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        drawTiles();

    }

    /**
     * Draws map tiling for campus
     */
    public void drawTiles(){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity.getMemoryClass() > 80) {
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
