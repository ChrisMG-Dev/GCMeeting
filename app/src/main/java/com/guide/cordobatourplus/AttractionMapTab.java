package com.guide.cordobatourplus;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Attraction map tab
 */
public class AttractionMapTab extends Fragment {

    private GoogleMap map;
    private MapView mMapView;
    private LatLng localizacion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attraction_map, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map = mMapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(AttractionsDetails.attraction.getLatitude()), Double.valueOf(AttractionsDetails.attraction.getLongitude())));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
        localizacion = new LatLng(Double.valueOf(AttractionsDetails.attraction.getLatitude()), Double.valueOf(AttractionsDetails.attraction.getLongitude()));
        map.addMarker(new MarkerOptions().position(localizacion).title(AttractionsDetails.attraction.getName()));
        map.moveCamera(center);
        map.animateCamera(zoom);
        return v;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
