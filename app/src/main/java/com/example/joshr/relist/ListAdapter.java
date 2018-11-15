package com.example.joshr.relist;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private List<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public FrameLayout mFrameLayout;
        public MyViewHolder(FrameLayout v) {
            super(v);
            //mTextView = v;
            mFrameLayout = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(List<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_text, parent, false);
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_text, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //holder.mFrameLayout.setText(mDataset[position]);

        TextView textView = (TextView) holder.mFrameLayout.findViewById(R.id.item_name);
        textView.setText(mDataset.get(position));
        ImageView imageView = (ImageView) holder.mFrameLayout.findViewById(R.id.remove_item);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mDataset.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
        CheckBox checkBox = (CheckBox) holder.mFrameLayout.findViewById(R.id.checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //do nothing as of yet
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}