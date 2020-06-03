package com.example.envirometalist.utility;


import com.example.envirometalist.R;
import com.example.envirometalist.model.RecycleTypes;

/** This is a helper class which handle conversion of recycle bin to it's relevant drawable view*/
public final class RecycleBinType {

    public static int getRecycleBinImage(RecycleTypes recycleType) {
        if (recycleType.equals(RecycleTypes.PAPER)) {
            return R.drawable.paper;
        } else if (recycleType.equals(RecycleTypes.GLASS)) {
            return R.drawable.glass;
        } else if (recycleType.equals(RecycleTypes.TRASH)) {
            return R.drawable.trash;
        } else {
            return R.drawable.bottle;
        }
    }
}
