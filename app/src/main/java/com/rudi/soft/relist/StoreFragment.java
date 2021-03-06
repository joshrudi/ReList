package com.rudi.soft.relist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView mMapView;  // mapbox mapview
    private String[] mItems = new String[]{"New York"};  // stores
    private double lat = 0, lng = 0;  // coordinates
    private OnFragmentInteractionListener mListener;

    public StoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // my user access token to initiate mapbox
        Mapbox.getInstance(getActivity(), "pk.eyJ1Ijoiam9zaHJ1ZGkiLCJhIjoiY2pvZHJkcmMwMHprbjNsamQxejEyem1icyJ9.7OkO7hr3eszZfv4ly1h24Q");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        // Inflate the layout for this fragment

        // member var mapview setup
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        // dropbox box / spinner containing stores
        Spinner spinner = v.findViewById(R.id.spinner);

        // call update on stores
        updateItems();

        // set adapter for spinner with dropdown items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_text_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                // update map with selected item from spinner
                updateMap(mItems[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

                //do nothing
            }

        });

        // navigate to selected location FAB
        FloatingActionButton myFab = (FloatingActionButton) v.findViewById(R.id.fab_start_direction);
        myFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // get direction to currently selected location
                getDirections();
            }
        });

        return v;
    }

    private void updateMap(String address) {  // update maps currently selected location

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                // use geocoder to convert string location to actual lat and long coordinates
                Geocoder gc = new Geocoder(getActivity());
                List<Address> list = new ArrayList<>();
                if(gc.isPresent()){
                    try {

                        // convert string to lat and long coordinates
                        list = gc.getFromLocationName(address, 1);
                        Address address = list.get(0);
                        lat = address.getLatitude();
                        lng = address.getLongitude();
                    }
                    catch (Exception e) {  // if all else fails, defaults to new york lat/long

                        //new york
                        lat = 40.7128;
                        lng = -74.0060;
                    }
                }

                mapboxMap.removeAnnotations();  // clears old marker
                mapboxMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));  // add new marker

                // creates viewing 'box' to view derived lat/long values by +/- to the coordinates
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(lat+.005, lng+.005))
                        .include(new LatLng(lat-.005, lng-.005))
                        .build();

                // zoom over to newly generated location
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));
            }
        });
    }

    private void updateItems() {  // update store list

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "stores", Context.MODE_PRIVATE);

        String items = prefs.getString("stores", "get");
        int lastIndex = 0;
        List<String> places = new ArrayList<String>();

        for (int i = 0; i < items.length(); i++) {  // parse

            if (items.charAt(i) == '\n') { places.add(items.substring(lastIndex, i)); lastIndex = i+1; }
        }

        if (places.size() > 0) {  // if we have at least one place, update default mItems with parsed values

            mItems = new String[places.size()];
            mItems = places.toArray(mItems);
        }
    }

    public void getDirections() {

        // convert lat/long coordinates to a url for google maps
        // this will also launch google maps app if installed
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://maps.google.com/maps?saddr=My Location&daddr="+lat+","+lng+""));
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();  // call mapview onstart
    }

    @Override
    public void onResume() {
        super.onResume();

        // update theme
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                boolean isDark;

                try {
                    SharedPreferences prefs = getActivity().getSharedPreferences(
                            "theme", Context.MODE_PRIVATE);
                    isDark = prefs.getBoolean("theme", false);  // grab value
                } catch (Exception e) {
                    isDark = false;  // by default assume false
                }

                if (isDark) {  // if dark enabled, use dark theme

                    // mapbox uses online styles which can be managed from account,
                    // my account is "joshrudi" as seen below
                    // this url in particular is for a dark style map
                    mapboxMap.setStyleUrl("mapbox://styles/joshrudi/cjohxeb2l2l8e2rpcoexccvwc");
                } else {

                    // this map style is for a light theme
                    mapboxMap.setStyleUrl("mapbox://styles/joshrudi/cjoeyvytq4sfy2qnx2j13xzj6");
                }
            }
        });

        updateItems();  // update store items
        mMapView.onResume();  // call mapview resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();  // mapview pause
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();  // mapview stop
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();  // mapview low mem
    }

    @Override
    public void onDestroyView() {  // need to call onDestroyView because of the way mapbox handles destroy calls
        super.onDestroyView();
        mMapView.onDestroy();  // mapview destroy
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);  // mapview onSaveInstanceState
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
