package com.github.nrudenko.spottracker.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.nrudenko.spottracker.Constants;
import com.github.nrudenko.spottracker.R;
import com.github.nrudenko.spottracker.activity.MainActivity;
import com.github.nrudenko.spottracker.model.HotSpot;
import com.github.nrudenko.spottracker.service.NotificationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Random;

import static com.github.nrudenko.spottracker.utils.CalculationUtils.getDistance;
import static com.github.nrudenko.spottracker.utils.CalculationUtils.getDistantPoint;

public class MapFragment extends SupportMapFragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final float ZOOM_LEVEL = 17;
    private LocationClient locationClient;

    private static final long LOCATION_UPDATE_INTERVAL = 500; // in mills

    private static final long LOCATION_FAST_UPDATE_INTERVAL = 100; // in mills
    private static final float POLYGON_STROKE_WIDTH = 2;

    private static final float CIRCLE_STROKE_WIDTH = 1;

    private ArrayList<HotSpot> spots = new ArrayList<HotSpot>();

    private boolean isPolygonInit;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(int sectionNumber) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                startLocationUpdate();
                return true;
            case R.id.action_simulate_notif:
                if (!spots.isEmpty()) {
                    HotSpot hotSpot = spots.get(new Random().nextInt(spots.size()));
                    notify(hotSpot);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationClient = new LocationClient(getActivity(), this, this);
        getMap().setMyLocationEnabled(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdate();
        Toast.makeText(getActivity(), getString(R.string.connected), Toast.LENGTH_SHORT).show();
    }

    private void startLocationUpdate() {
        getMap().clear();
        // Polygon must be redrawed on each locationClient connection
        isPolygonInit = false;

        locationClient.removeLocationUpdates(this);
        // Setting params for requesting location updates
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(LOCATION_FAST_UPDATE_INTERVAL);
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Start getting location updates,
        // as result will be calling callback method onLocationChanged()
        locationClient.requestLocationUpdates(locationRequest, this);

    }

    ///////////////////////////////////////////////////////////////////////////
    // Preparation methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get distant point with constant distance {{@link Constants#POLYGON_RADIUS_DISTANCE}}.
     * Just wrapper for {@link com.github.nrudenko.spottracker.utils.CalculationUtils#getDistantPoint(com.google.android.gms.maps.model.LatLng, double, double)}
     */
    private LatLng getConstDistantPoint(LatLng location, double bearing) {
        return getDistantPoint(location, Constants.POLYGON_RADIUS_DISTANCE, bearing);
    }

    /**
     * Create HotSpots which placed in rect around your location
     *
     * @param myLocation your location
     * @see #getConstDistantPoint(com.google.android.gms.maps.model.LatLng, double)
     * @see com.github.nrudenko.spottracker.utils.CalculationUtils#getDistantPoint(com.google.android.gms.maps.model.LatLng, double, double)
     */
    private void initHotSpots(LatLng myLocation) {
        spots = new ArrayList<HotSpot>();
        spots.add(new HotSpot(getConstDistantPoint(myLocation, 45), getString(R.string.hotspot_one)));
        spots.add(new HotSpot(getConstDistantPoint(myLocation, 135), getString(R.string.hotspot_two)));
        spots.add(new HotSpot(getConstDistantPoint(myLocation, 225), getString(R.string.hotspot_three)));
        spots.add(new HotSpot(getConstDistantPoint(myLocation, 315), getString(R.string.hotspot_four)));
    }

    /**
     * @return Coordinates needed for drawing polygon
     * describing by HotSpots
     * @see #initHotSpots(com.google.android.gms.maps.model.LatLng)
     */
    private LatLng[] getHotspotPoints() {
        LatLng[] polygonPoints = new LatLng[spots.size() + 1];
        for (int i = 0; i < spots.size(); i++) {
            HotSpot hotSpot = spots.get(i);
            polygonPoints[i] = hotSpot.latLng;
        }
        // polygon's drawing should "return" at the starting point
        polygonPoints[polygonPoints.length - 1] = spots.get(0).latLng;

        return polygonPoints;
    }

    /**
     * Draw circles around hotspot points, it's indicator of near zone
     *
     * @param hotspotPoints
     */
    private void drawHotspotAreas(LatLng[] hotspotPoints) {
        for (int i = 0; i < hotspotPoints.length; i++) {
            LatLng polygonPoint = hotspotPoints[i];
            CircleOptions circle = new CircleOptions()
                    .center(polygonPoint)
                    .radius(Constants.DISTANCE_INDICATE_NEAR)
                    .fillColor(getResources().getColor(R.color.transparrent_red))
                    .strokeWidth(CIRCLE_STROKE_WIDTH);
            getMap().addCircle(circle);
        }
    }

    /**
     * Adds marker for each hotspot
     */
    private void addHotspotMarkers() {
        for (int i = 0; i < spots.size(); i++) {
            HotSpot hotSpot = spots.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions
                    .position(hotSpot.latLng)
                    .title(hotSpot.name)
                    .snippet(hotSpot.name);
            getMap().addMarker(markerOptions);
        }
    }

    /**
     * Add polygon to map
     *
     * @param location location for polygon center
     * @see #getHotspotPoints()
     */
    private void drawPolygon(LatLng location) {
        LatLng[] polygonPoints = getHotspotPoints();

        drawHotspotAreas(polygonPoints);
        addHotspotMarkers();

        PolygonOptions rectOptions = new PolygonOptions()
                .add(polygonPoints);

        rectOptions.strokeColor(getResources().getColor(R.color.transparrent_red));
        rectOptions.strokeWidth(POLYGON_STROKE_WIDTH);
        rectOptions.fillColor(getResources().getColor(R.color.transparrent_blue));

        getMap().addPolygon(rectOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(ZOOM_LEVEL).build();
        getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    ///////////////////////////////////////////////////////////////////////////
    // LocationClient callbacks
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onDisconnected() {
        Toast.makeText(getActivity(), getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        tryToInitPolygon(location);
        moveToMyLocation(location);
        checkLocation(location);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Detected location methods
    ///////////////////////////////////////////////////////////////////////////

    private void moveToMyLocation(Location myLocation) {
        LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        getMap().moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
    }

    /**
     * Init and draw polygon if needed
     *
     * @param location location for polygon center
     * @see #initHotSpots(com.google.android.gms.maps.model.LatLng)
     * @see #drawPolygon(com.google.android.gms.maps.model.LatLng)
     */
    private void tryToInitPolygon(Location location) {
        if (!isPolygonInit) {
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            initHotSpots(myLocation);
            drawPolygon(myLocation);
            isPolygonInit = true;
        }
    }

    /**
     * If location is near(closer than {@link com.github.nrudenko.spottracker.Constants#DISTANCE_INDICATE_NEAR})
     * to one of HotSpot, system will be notified
     *
     * @param location location for check
     * @see #notify(com.github.nrudenko.spottracker.model.HotSpot)
     * @see com.github.nrudenko.spottracker.utils.CalculationUtils#getDistance(com.google.android.gms.maps.model.LatLng, com.google.android.gms.maps.model.LatLng)
     */
    private void checkLocation(Location location) {
        for (int i = 0; i < spots.size(); i++) {
            HotSpot hotSpot = spots.get(i);
            double distance = getDistance(new LatLng(location.getLatitude(), location.getLongitude()), hotSpot.latLng);
            if (distance < Constants.DISTANCE_INDICATE_NEAR + location.getAccuracy()) {
                notify(hotSpot);
            }
        }
    }

    /**
     * Perform notify, there are HotSpot near
     *
     * @param hotSpot nearest HotSpot
     */
    private void notify(HotSpot hotSpot) {
        NotificationService.notify(getActivity(), hotSpot);
//        Toast.makeText(getActivity(), "You are near " + hotSpot.name, Toast.LENGTH_SHORT).show();
    }
}