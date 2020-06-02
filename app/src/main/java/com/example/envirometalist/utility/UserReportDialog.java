package com.example.envirometalist.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.envirometalist.R;
import com.example.envirometalist.fragments.player.map.PlayerFragmentMap;
import com.example.envirometalist.model.Element;
import com.squareup.picasso.Picasso;

public class UserReportDialog extends Dialog {
    private Activity activity;
    private Element element;
    private PlayerFragmentMap playerFragmentMap;
    private Button confirmButt;
    private RadioGroup rGroup;
    private EditText otherText;
    private Button uploadButt;

    private int chosenReportRadio;

    public UserReportDialog(Activity activity, Element element, PlayerFragmentMap playerFragmentMap) {
        super(activity);
        setContentView(R.layout.dialog_player_report);
        this.activity = activity;
        this.element = element;
        this.playerFragmentMap = playerFragmentMap;
        confirmButt = findViewById(R.id.confirmButt);
        otherText = findViewById(R.id.other_text);
        uploadButt = findViewById(R.id.uploadButt);

        setCanceledOnTouchOutside(true);
        uploadButtListener();
        confirmButtListener();
        radioGroupListener();
    }

    private void uploadButtListener() {
        uploadButt.setOnClickListener(v -> {
          selectImage();
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 1);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



    private void confirmButtListener() {
        confirmButt.setOnClickListener(v -> {
            int radioButtonID = rGroup.getCheckedRadioButtonId();
            View radioButton = rGroup.findViewById(radioButtonID);
            int position = rGroup.indexOfChild(radioButton);

            switch (position){
                case 0:
                    // Send to server report on bin full
                    break;
                case 1:
                    // Send to server report on bin broken
                    break;
                case 2:
                    // Send to server report on bin missing
                    break;
                case 3:
                    // Other - get text from editText & send to server
                    break;
            }
        });
    }

    private void radioGroupListener() {
        rGroup = findViewById(R.id.radioGroup);
        rGroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked) {
                chosenReportRadio = rGroup.indexOfChild(checkedRadioButton);
                if(chosenReportRadio == 3/*Other radio butt*/){
                    otherText.setEnabled(true);
                }else
                    otherText.setEnabled(false);
                Toast.makeText(activity, "CHECKED!" + checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
