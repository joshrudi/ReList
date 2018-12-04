package com.example.joshr.relist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DefaultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DefaultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DefaultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DefaultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DefaultsFragment newInstance(String param1, String param2) {
        DefaultsFragment fragment = new DefaultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_defaults, container, false);
        // Inflate the layout for this fragment

        String[] currentDefault = getDefault();
        String[] defaultStoreList = getStores();

        ListView lists, stores;

        lists = (ListView) v.findViewById(R.id.listView_lists);
        stores = (ListView) v.findViewById(R.id.listView_stores);

        ArrayAdapter<String> listsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, currentDefault);
        ArrayAdapter<String> storesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, defaultStoreList);

        lists.setAdapter(listsAdapter);
        stores.setAdapter(storesAdapter);

        stores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Remove Location?")
                        .setMessage("By Selecting 'Yes', you will delete '" + getStores()[arg2] + "' from your list of stores.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                saveStores(getStores(), arg2);
                                String[] updatedLoc = getStores();
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, updatedLoc);
                                stores.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Ok, do nothing then
                            }
                        })
                        .setIcon(R.drawable.round_warning_24)
                        .show();
            }

        });



        ImageView infoListBtn = (ImageView) v.findViewById(R.id.info_img_default_list);
        infoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Default List")
                        .setMessage("The default list is a customizable list which contains your most commonly purchased items.  When your list is empty on the 'Lists' tab, a button allowing you to automatically propagate all of the items in the default list will appear.  Replacing the Current default list will overwrite it's contents with what you have currently added on the 'Lists' page.")
                        .setIcon(R.drawable.round_help_24)
                        .show();
            }
        });

        ImageView infoStoreBtn = (ImageView) v.findViewById(R.id.info_img_default_stores);
        infoStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Default Stores")
                        .setMessage("The Default Stores list is a customizable list which contains your favorite stores!  You can add a location (please be specific with the address) by selecting the 'Add' button, and remove a location by selecting the location from the list.")
                        .setIcon(R.drawable.round_help_24)
                        .show();
            }
        });

        TextView replaceList = (TextView) v.findViewById(R.id.replaceText);
        replaceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Replace Default?")
                        .setMessage("By Selecting 'Yes', you will overwrite your previous default list with your current list.\nThis cannot be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // access the 0th index of the array and set it as the default (get the raw data)
                                setDefault(getCurList(true)[0]);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getCurList(false));
                                lists.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Ok, do nothing then
                            }
                        })
                        .setIcon(R.drawable.round_warning_24)
                        .show();
            }
        });

        TextView addStore = (TextView) v.findViewById(R.id.add_store);
        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Enter Address")
                        .setMessage("Please Enter the full address of the store you would like to add to your default stores list.")
                        .setView(input)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String[] curStores = getStores();
                                String[] updatedStores = new String[curStores.length+1];
                                for (int i = 0; i < curStores.length; i++) updatedStores[i] = curStores[i];
                                updatedStores[curStores.length] = input.getText().toString();
                                saveStores(updatedStores, -1);  // -1 so we don't remove any value
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getStores());
                                stores.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Ok, do nothing then
                            }
                        })
                        .setIcon(R.drawable.round_add_location_24)
                        .show();
            }
        });

        FloatingActionButton settingsCog = (FloatingActionButton) v.findViewById(R.id.fab_settings);
        settingsCog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), Settings.class));
                getActivity().finish();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private String[] getDefault() {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "default", Context.MODE_PRIVATE);

        String items = prefs.getString("default", "get");
        int lastIndex = 0;
        List<String> dataSet = new ArrayList<String>();

        for (int i = 0; i < items.length(); i++) {

            if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
        }

        return dataSet.toArray(new String[dataSet.size()]);
    }

    private void setDefault(String newDefault) {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "default", Context.MODE_PRIVATE);

        prefs.edit().putString("default", newDefault).apply();
    }

    public String[] getCurList(boolean isRaw) {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "items", Context.MODE_PRIVATE);

        String items = prefs.getString("items", "get");

        if (!isRaw) {

            int lastIndex = 0;
            List<String> dataSet = new ArrayList<String>();

            for (int i = 0; i < items.length(); i++) {

                if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
            }

            return dataSet.toArray(new String[0]);
        }

        return new String[] {items};
    }

    private String[] getStores() {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "stores", Context.MODE_PRIVATE);

        String items = prefs.getString("stores", "New York, New York\n ");
        int lastIndex = 0;
        List<String> dataSet = new ArrayList<String>();

        for (int i = 0; i < items.length(); i++) {

            if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
        }

        return dataSet.toArray(new String[dataSet.size()]);
    }

    private void saveStores(String[] stores, int valToRemove) {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "stores", Context.MODE_PRIVATE);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < stores.length; i++) {

            if (i != valToRemove) {

                sb.append(stores[i]);
                sb.append("\n");
            }
        }

        prefs.edit().putString("stores", sb.toString()).apply();
    }
}
