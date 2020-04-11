package com.example.envirometalist.utility;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import cn.pedant.SweetAlert.SweetAlertDialog;

public final class AlertDialog implements CustomDialog {
    private SweetAlertDialog pDialog;

    public AlertDialog(Context context) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);


    }

    @Override
    public SweetAlertDialog setNewConfiguration() {
        return pDialog;
    }

    @Override
    public void dismissDialog() {
        pDialog.dismissWithAnimation();

    }

    public void showDialog(){
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.leftMargin = 5;

        pDialog.show();
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setHeight(85);
        pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(btnParams);
    }


}
// TODO check why this code make problem to my phone
//    LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        btnParams.leftMargin = 5;
//
//                pDialog.show();
//                pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setHeight(85);
//                pDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setLayoutParams(btnParams);