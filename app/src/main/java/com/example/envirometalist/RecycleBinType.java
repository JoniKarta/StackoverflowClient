package com.example.envirometalist;


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
