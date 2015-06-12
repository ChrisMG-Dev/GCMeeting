package com.guide.cordobatourplus;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class EventDetailsMap extends ActionBarActivity {

    private GoogleMap map;
    private MapView mMapView;
    private LatLng localizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(EventDetails.event.getPlace() + " map");
        setContentView(R.layout.event_details_map);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        map = mMapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(getApplicationContext());
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(EventDetails.event.getAltitude()), Double.valueOf(EventDetails.event.getLongitude())));
        Log.d("Latitude", "Latitude: " + Double.valueOf(EventDetails.event.getAltitude()));
        Log.d("Longitude", "Longitude: " + Double.valueOf(EventDetails.event.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
        localizacion = new LatLng(Double.valueOf(EventDetails.event.getAltitude()), Double.valueOf(EventDetails.event.getLongitude()));
        map.addMarker(new MarkerOptions().position(localizacion).title(EventDetails.event.getPlace()));
        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
