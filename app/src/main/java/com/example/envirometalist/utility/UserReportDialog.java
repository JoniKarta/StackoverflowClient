package com.example.envirometalist.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.envirometalist.LoginActivity;
import com.example.envirometalist.PlayerActivity;
import com.example.envirometalist.R;
import com.example.envirometalist.model.Action;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.ElementId;
import com.example.envirometalist.model.Invoker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.UUID;

public class UserReportDialog extends Dialog implements PlayerActivity.ImageTakenListener {
    private Activity activity;
    private Element element;
    private Button confirmButt;
    private RadioGroup rGroup;
    private EditText otherText;
    private Button uploadButt;
    private ImageView imageTaken;
    private OnReportReadyListener onReportReadyListener;
    private int chosenReportRadio;
    private StorageReference mStorageRef;
    private Bitmap uploadBitmap;


    public UserReportDialog(Activity activity, Element element, OnReportReadyListener onReportReadyListener) {
        super(activity);
        setContentView(R.layout.dialog_player_report);
        this.activity = activity;
        this.element = element;
        this.onReportReadyListener = onReportReadyListener;
        confirmButt = findViewById(R.id.confirmButt);
        otherText = findViewById(R.id.other_text);
        uploadButt = findViewById(R.id.uploadButt);
        imageTaken = findViewById(R.id.uploadedPic);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ((PlayerActivity) activity).setImageTakenListener(this);
        setCanceledOnTouchOutside(true);
        uploadButtListener();
        confirmButtListener();
        radioGroupListener();
    }

    private void uploadButtListener() {
        uploadButt.setOnClickListener(v -> selectImage());
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 1);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(gallery, "Select Picture"), 0);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void confirmButtListener() {
        confirmButt.setOnClickListener(v -> {
            int radioButtonID = rGroup.getCheckedRadioButtonId();
            RadioButton radioButton = rGroup.findViewById(radioButtonID);
            int position = rGroup.indexOfChild(radioButton);
            if (radioButton == null) {
                Toast.makeText(activity,
                        "Please choose report type", Toast.LENGTH_LONG).show();
                return;
            }
            String txtReport;
            if (position != 3) {
                txtReport = radioButton.getText().toString();
            } else {
                txtReport = otherText.getText().toString();
                if (txtReport.isEmpty()) {
                    Toast.makeText(activity, "Please type an other issue",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            onReportReadyListener.onFinishReport(new Action(element.getType(),
                    null, new ElementId(element.getElementId()),
                    null, new Invoker("Joni@gmail.com"),
                    Collections.singletonMap("Report", txtReport)));

            uploadFile(uploadBitmap);
            dismiss();
        });

    }

    private void radioGroupListener() {
        rGroup = findViewById(R.id.radioGroup);
        rGroup.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked) {
                chosenReportRadio = rGroup.indexOfChild(checkedRadioButton);
                if (chosenReportRadio == 3/*Other radio butt*/) {
                    otherText.setEnabled(true);
                } else {
                    otherText.setInputType(InputType.TYPE_NULL);
                    otherText.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onFinishProcImage(Bitmap bitmap) {
        uploadBitmap = bitmap;
        imageTaken.setImageBitmap(uploadBitmap);
    }

    public interface OnReportReadyListener {
        void onFinishReport(Action action);
    }

    /**
     * Uploads report's image to FireStorage
     */
    private void uploadFile(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mStorageRef.child("photos")
                    .child(LoginActivity.user.getEmail())
                    .child(UUID.randomUUID().toString()).putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
            }).addOnSuccessListener(taskSnapshot -> {
            });
        }
    }
}
