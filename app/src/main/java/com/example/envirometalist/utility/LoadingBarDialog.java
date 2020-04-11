package com.example.envirometalist.utility;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoadingBarDialog implements CustomDialog {
    private SweetAlertDialog pDialog;

    public LoadingBarDialog(Context context) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        setCustomConfiguration();
    }

    @Override
    public SweetAlertDialog setNewConfiguration() {
        return pDialog;
    }

    @Override
    public void dismissDialog() {
        pDialog.dismissWithAnimation();
    }

    @Override
    public void showDialog() {
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.leftMargin = 5;

        pDialog.show();
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setHeight(85);
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(btnParams);
    }


    private void setCustomConfiguration(){
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.showCancelButton(true);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelText("Cancel");
        pDialog.setCancelClickListener(sDialog -> dismissDialog());

    }
}
