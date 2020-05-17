package com.example.envirometalist.fragments.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.envirometalist.R;
import com.example.envirometalist.model.Element;

import java.util.ArrayList;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder>  {
    private ArrayList<Element> elementList;
    public ElementAdapter(ArrayList<Element> elementList){
        this.elementList = elementList;
    }
    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_item,parent,false);
        ElementViewHolder evh = new ElementViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        Element currentElement = elementList.get(position);

    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public static class ElementViewHolder extends  RecyclerView.ViewHolder{
        private ImageView imageView;
        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycleBinImageView);
        }
    }
}
