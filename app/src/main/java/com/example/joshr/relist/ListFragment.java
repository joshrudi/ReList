package com.example.joshr.relist;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.design.widget.FloatingActionButton;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final List<String> dataSet = new ArrayList<String>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private OnFragmentInteractionListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
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
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ListAdapter(dataSet, this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    boolean isCleared;

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        //adapter.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        //return true;
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                        if (swipeDir == ItemTouchHelper.LEFT | swipeDir == ItemTouchHelper.RIGHT) {

                            dataSet.remove(viewHolder.getAdapterPosition());
                            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            checkEmpty();
                        }
                    }

                    @Override
                    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                    }

                    @Override
                    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                        super.clearView(recyclerView, viewHolder);
                        viewHolder.itemView.setElevation(0);
                        isCleared = true;
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (isCleared) {
                            isCleared = false;
                        }
                        else {
                            viewHolder.itemView.setElevation(10);
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {

                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (!editText.getText().toString().matches("")) {

                        dataSet.add(editText.getText().toString());
                        mAdapter.notifyItemInserted(dataSet.size() - 1);
                        editText.getText().clear();
                        checkEmpty();
                    }
                    return true;
                }
                return false;
            }
        });

        ImageView mic = (ImageView) view.findViewById(R.id.microphone);
        mic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                startVoiceRecording();
            }
        });

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab_add_item);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                StringBuilder sb = new StringBuilder();
                sb.append("Hi!  I'm sharing my Shopping List with you via the ReList App!\n");

                for (int i = 0; i < dataSet.size(); i++) {
                    sb.append("\n");
                    sb.append("* ");
                    sb.append(dataSet.get(i));
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share Through:"));
            }
        });

        Button setDefault = (Button) view.findViewById(R.id.fill_with_default);
        setDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataSet.addAll(Arrays.asList(getDefault()));
                mAdapter.notifyItemInserted(dataSet.size() - 1);
                checkEmpty();
            }
        });

        return view;
    }

    private void startVoiceRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Say Item Name\nSeparate individual items with an 'and' in between names");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void saveListState() {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "items", Context.MODE_PRIVATE);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataSet.size(); i++) {
            sb.append(dataSet.get(i)).append("\n");
        }
        prefs.edit().putString("items", sb.toString()).apply();
    }

    private void loadListState() {

        SharedPreferences prefs = getActivity().getSharedPreferences(
                "items", Context.MODE_PRIVATE);

        String items = prefs.getString("items", "get");
        int lastIndex = 0;
        dataSet.clear();

        for (int i = 0; i < items.length(); i++) {

            if (items.charAt(i) == '\n') { dataSet.add(items.substring(lastIndex, i)); lastIndex = i+1; }
        }

        checkEmpty();  //check to see if we display "default" button
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    //EditText editText = (EditText) getActivity().findViewById(R.id.editText);
                    //editText.getText().clear();
                    //editText.setText(result.get(0));

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String items = result.get(0);
                    for (int i = 0; i < items.length(); i++) {

                        if (items.length() >= i+5 && items.substring(i, i+5).equals(" and ")) {

                            dataSet.add(items.substring(0, i));
                            items = items.substring(i+5, items.length());
                            i = 0;
                        }
                    }

                    if (items.length() > 0) dataSet.add(items);  // if there was something after the last "and", add it
                    mAdapter.notifyItemInserted(dataSet.size() - 1);
                    saveListState();
                }
                break;
            }
        }
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
    public void onResume() {
        super.onResume();

        loadListState();
    }

    @Override
    public void onPause() {
        super.onPause();

        saveListState();
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

    public void checkEmpty() {

        View v = getView();

        TextView listEmptyText = (TextView) v.findViewById(R.id.list_is_empty_prompt);
        ImageView receiptImage = (ImageView) v.findViewById(R.id.receipt_background);
        Button fillDefaultButton = (Button) v.findViewById(R.id.fill_with_default);

        if (mAdapter.getItemCount() == 0) {

            listEmptyText.setVisibility(View.VISIBLE);
            receiptImage.setVisibility(View.VISIBLE);
            fillDefaultButton.setVisibility(View.VISIBLE);
            fillDefaultButton.setClickable(true);
            fillDefaultButton.setFocusable(true);
        } else {

            listEmptyText.setVisibility(View.INVISIBLE);
            receiptImage.setVisibility(View.INVISIBLE);
            fillDefaultButton.setVisibility(View.INVISIBLE);
            fillDefaultButton.setClickable(false);
            fillDefaultButton.setFocusable(false);
        }
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
}
