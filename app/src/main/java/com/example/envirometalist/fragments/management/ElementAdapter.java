package com.example.envirometalist.fragments.management;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envirometalist.R;
import com.example.envirometalist.model.Element;

import java.util.List;


public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder>  {
    private List<Element> elementList;
    private OnElementClickListener onElementClickListener;

    public ElementAdapter(List<Element> elementList,OnElementClickListener onElementClickListener) {
        this.elementList = elementList;
        this.onElementClickListener = onElementClickListener;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_item, parent, false);
        return new ElementViewHolder(v,onElementClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        Element currentElement = elementList.get(position);
        // TODO need to check how to set images in the view holder
        holder.elementTypeTextView.setText(String.format("Type:  %s", currentElement.getType()));
        holder.elementNameTextView.setText(String.format("Name: %s",currentElement.getName()));
        holder.elementActiveTextView.setText(String.format("Active: %s", currentElement.getActive()));
        holder.elementCreator.setText(String.format("Creator: %s",currentElement.getCreatedBy().getUserEmail()));
        holder.elementDateCreation.setText(String.format("Creation Date: %s", currentElement.getCreatedTimestamp()));
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public static class ElementViewHolder extends RecyclerView.ViewHolder {
        private ImageView elementImageView;
        private TextView elementTypeTextView;
        private TextView elementNameTextView;
        private TextView elementActiveTextView;
        private TextView elementCreator;
        private TextView elementDateCreation;
        public ElementViewHolder(@NonNull View itemView, OnElementClickListener onElementListener) {
            super(itemView);
            elementImageView = itemView.findViewById(R.id.recycleBinImageView);
            elementTypeTextView = itemView.findViewById(R.id.elementTypeTextView);
            elementNameTextView = itemView.findViewById(R.id.elementNameTextView);
            elementActiveTextView = itemView.findViewById(R.id.elementActiveTextView);
            elementCreator = itemView.findViewById(R.id.elementCreatorTextView);
            elementDateCreation = itemView.findViewById(R.id.elementCreationTextView);
            itemView.setOnClickListener(v -> {
                if(onElementListener != null)
                    onElementListener.onClick(getAdapterPosition());
            });
        }
    }
    interface OnElementClickListener {
        void onClick(int position);
    }
}
