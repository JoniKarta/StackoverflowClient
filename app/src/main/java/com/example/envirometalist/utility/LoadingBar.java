package com.example.envirometalist.utility;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;

public final class LoadingBar {
    private Context context;
    private SweetAlertDialog pDialog;

    public LoadingBar(Context context) {
        this.context = context;
    }

    public void showLoadingDialog() {
        hidePDialog();
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btnParams.leftMargin = 5;
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.showCancelButton(true);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelText("Cancel");
        pDialog.setCancelClickListener(sDialog -> hidePDialog());
        pDialog.show();
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setHeight(85);
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(btnParams);
    }

    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismissWithAnimation();
            pDialog = null;
        }
    }
}

