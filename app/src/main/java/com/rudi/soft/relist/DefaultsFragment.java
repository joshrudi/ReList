package com.rudi.soft.relist;

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

        String[] currentDefault = getDefault();  //loads the current default shopping list saved
        String[] defaultStoreList = getStores();  //loads the current list of added stores

        /* vvv Creates two lists, one for the default shopping list and one for the saved stores */
        ListView lists, stores;

        lists = (ListView) v.findViewById(R.id.listView_lists);
        stores = (ListView) v.findViewById(R.id.listView_stores);

        ArrayAdapter<String> listsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, currentDefault);
        ArrayAdapter<String> storesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, defaultStoreList);

        lists.setAdapter(listsAdapter);
        stores.setAdapter(storesAdapter);
        /* ^^^ Creates two lists, one for the default shopping list and one for the saved stores */

        //if a store in the list is clicked, see if the user wants to delete THAT specific store
        stores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // alert dialog prompting user
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Remove Location?")
                        .setMessage("By Selecting 'Yes', you will delete '" + getStores()[arg2] + "' from your list of stores.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // if user selected 'yes' then delete store

                                saveStores(getStores(), arg2);  // overwrite currently store list (delete index 'arg2')
                                String[] updatedLoc = getStores();  // load new stores

                                // update adapter with new stores list
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


        // infoListBtn is a question-mark-button on the bottom cardview
        // if selected, explain how replacing list works to user
        ImageView infoListBtn = (ImageView) v.findViewById(R.id.info_img_default_list);
        infoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create alert dialog and explain to user how default list works
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Default List")
                        .setMessage("The default list is a customizable list which contains your most commonly purchased items.  When your list is empty on the 'Lists' tab, a button allowing you to automatically propagate all of the items in the default list will appear.  Replacing the Current default list will overwrite it's contents with what you have currently added on the 'Lists' page.")
                        .setIcon(R.drawable.round_help_24)
                        .show();
            }
        });

        // infoStoreBtn is a question-mark-button on the top cardview
        // if selected, explain how the store list works
        ImageView infoStoreBtn = (ImageView) v.findViewById(R.id.info_img_default_stores);
        infoStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create alert dialog and explain to user how the store list works
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Default Stores")
                        .setMessage("The Default Stores list is a customizable list which contains your favorite stores!  You can add a location (please be specific with the address) by selecting the 'Add' button, and remove a location by selecting the location from the list.")
                        .setIcon(R.drawable.round_help_24)
                        .show();
            }
        });

        // button which triggers the user prompt to update/overwrite the current default shopping list
        TextView replaceList = (TextView) v.findViewById(R.id.replaceText);
        replaceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if user selects 'yes' then overwrite the default shopping list
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Replace Default?")
                        .setMessage("By Selecting 'Yes', you will overwrite your previous default list with your current list.\nThis cannot be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // access the 0th index of the array (getCurList) and set it as the default with 'setDefault()' (get the raw data and set it)
                                setDefault(getCurList(true)[0]);

                                // update the default list with the new default list
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

        // button which triggers the user prompt to add to the store list
        TextView addStore = (TextView) v.findViewById(R.id.add_store);
        addStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create an editText to put in the dialog (also set up layout for the editText)
                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(layoutParams);

                // user enters address and if 'Add' is selected, add the text to the shopping list
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Enter Address")
                        .setMessage("Please Enter the full address of the store you would like to add to your default stores list.")
                        .setView(input)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String[] curStores = getStores();  // get the current list of stores
                                String[] updatedStores = new String[curStores.length+1];  // create new array with space for one more store
                                for (int i = 0; i < curStores.length; i++) updatedStores[i] = curStores[i];  // copy old list into new list

                                // grab the user input string from editText and put it in the last index of the new arra
                                updatedStores[curStores.length] = input.getText().toString();

                                saveStores(updatedStores, -1);  // -1 so we don't remove any value

                                // update the stores
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

        // settings cog FAB; if selected opens settings activity
        FloatingActionButton settingsCog = (FloatingActionButton) v.findViewById(R.id.fab_settings);
        settingsCog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open settings
                startActivity(new Intent(getActivity(), Settings.class));

                /*
                    finish current activity because when dark theme is toggled,
                    will need to effectively perform a recreate() on the main activity to update colors
                */
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

    private String[] getDefault() {  // gets the current default shopping list

        // access saved default from prefs
        SharedPreferences prefs = getActivity().getSharedPreferences(
                "default", Context.MODE_PRIVATE);

        String items = prefs.getString("default", "get");

        // keep track of last index
        int lastIndex = 0;

        List<String> dataSet = new ArrayList<String>();  // stores \n separated values

        for (int i = 0; i < items.length(); i++) {

            // if we find a '\n' marker, add the item inbetween lastIndex and the current location, then update lastIndex
            if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
        }

        return dataSet.toArray(new String[dataSet.size()]);  // convert to string array and return
    }

    private void setDefault(String newDefault) {  // takes in raw \n separated string and stores it as default

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "default", Context.MODE_PRIVATE);

        prefs.edit().putString("default", newDefault).apply();
    }

    // return the current shopping list as String[]; if isRaw is true, return raw \n separated string in 0th index
    public String[] getCurList(boolean isRaw) {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "items", Context.MODE_PRIVATE);

        String items = prefs.getString("items", "get");

        if (!isRaw) {  // if isRaw is false, parse the string and return String[] for each separated item

            int lastIndex = 0;
            List<String> dataSet = new ArrayList<String>();

            for (int i = 0; i < items.length(); i++) {  // parse data

                if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
            }

            return dataSet.toArray(new String[0]);  // convert to string array and return
        }

        return new String[] {items};  // if isRaw is true, return raw \n separated string in 0th index
    }

    private String[] getStores() {  // loads current saved stores list

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "stores", Context.MODE_PRIVATE);

        String items = prefs.getString("stores", "New York, New York\n ");
        int lastIndex = 0;
        List<String> dataSet = new ArrayList<String>();

        for (int i = 0; i < items.length(); i++) {  // parse data

            if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
        }

        return dataSet.toArray(new String[dataSet.size()]);  // convert to string array and return
    }

    private void saveStores(String[] stores, int valToRemove) {  // saves String[] input as new stores list

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "stores", Context.MODE_PRIVATE);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < stores.length; i++) {

            if (i != valToRemove) {  // if index is valid, remove value at index in saved list

                sb.append(stores[i]);
                sb.append("\n");
            }
        }

        prefs.edit().putString("stores", sb.toString()).apply(); // save
    }
}
