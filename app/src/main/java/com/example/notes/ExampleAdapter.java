package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> implements Filterable {

    private ArrayList<exampleItem> mExampleList;
    private ArrayList<exampleItem> mExampleListFull;
    private OnItemClickListener mListener;

    int mLayout;

    public interface OnItemClickListener {
        public void OnItemClick(int position);
        public void OnDeleteItem(int position);
        public void isBoxSelected(int position, Boolean checked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTitleText;
        public TextView mSubText;
        public ImageView mDeleteImage;
        public CheckBox mCheckBox;

        public void selectCheckBox() {
            mCheckBox.setChecked(true);
        }

        public void unSelectCheckBox() {
            mCheckBox.setChecked(false);
        }

        public ExampleViewHolder(@NonNull View itemView, final OnItemClickListener listener, int layout) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTitleText = itemView.findViewById(R.id.titleText);
            mSubText = itemView.findViewById(R.id.subText);
            if(layout == R.layout.example_item) {
                mDeleteImage = itemView.findViewById(R.id.trashcan);
                mDeleteImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null) {
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION) {
                                listener.OnDeleteItem(position);
                            }
                        }
                    }
                });
            } else {
                mCheckBox = itemView.findViewById(R.id.selectButton);
                mCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null) {
                            Boolean checked = mCheckBox.isChecked();
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION) {
                                listener.isBoxSelected(position, checked);
                            }
                        }
                    }
                });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public ExampleAdapter(ArrayList<exampleItem> exampleList, int layout) {
        mLayout = layout;
        mExampleList = exampleList;
        mExampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public ExampleAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener, mLayout);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleAdapter.ExampleViewHolder holder, int position) {
        exampleItem currentItem = mExampleList.get(position);

        holder.mTitleText.setText(currentItem.getTitleText());
        holder.mSubText.setText(currentItem.getSubText());
        if(mLayout == R.layout.example_item_selected) {
            holder.mCheckBox.setChecked(currentItem.getChecked());
        }
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<exampleItem> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(mExampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(exampleItem item : mExampleListFull) {
                    if(item.getTitleText().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results  = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mExampleList.clear();
            mExampleList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
