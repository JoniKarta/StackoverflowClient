package com.example.envirometalist.fragments.manager.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.envirometalist.R;
import com.example.envirometalist.model.Element;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

enum RecycleType {
    PAPER, GLASS, TRASH, BOTTLE
}

public class RecycleBinClusterMarker implements ClusterItem {
    private String snippet;
    private Element element;
    private LatLng position;
    private int iconPicture;

    public RecycleBinClusterMarker() {

    }

    public RecycleBinClusterMarker(String snippet, Element element) {
        this.snippet = snippet;
        this.element = element;
        position = new LatLng(element.getLocation().getLat(), element.getLocation().getLng());
        setImageMarker();
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return element.getName();
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    private void setImageMarker() {
        RecycleType recycleType = RecycleType.valueOf(element.getType());
        switch (recycleType) {
            case PAPER:
                setIconPicture(R.drawable.paper);
                break;
            case GLASS:
                setIconPicture(R.drawable.glass);
                break;
            case TRASH:
                setIconPicture(R.drawable.trash);
                break;
            case BOTTLE:
                setIconPicture(R.drawable.bottle);
                break;
        }
    }

}
