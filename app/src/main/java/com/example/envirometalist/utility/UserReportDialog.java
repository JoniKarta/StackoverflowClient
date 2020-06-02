package com.example.envirometalist.utility;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.envirometalist.R;
import com.example.envirometalist.fragments.player.map.PlayerFragmentMap;
import com.example.envirometalist.model.Element;

public class UserReportDialog extends Dialog {
    private Context context;
    private Element element;
    private PlayerFragmentMap playerFragmentMap;
    private Button confirmButt;
    private RadioGroup rGroup;
    private int chosenReportId;

    public UserReportDialog(@NonNull Context context, Element element, PlayerFragmentMap playerFragmentMap) {
        super(context);
        setContentView(R.layout.dialog_player_report);
        this.context = context;
        this.element = element;
        this.playerFragmentMap = playerFragmentMap;
        confirmButt = findViewById(R.id.confirmButt);
        confirmButtListener();
        radioGroupListener();
    }

    private void confirmButtListener() {
        confirmButt.setOnClickListener(v -> {

        });
    }

    private void radioGroupListener() {
        rGroup = findViewById(R.id.radioGroup);
        // This will get the radiobutton in the radiogroup that is checked
        rGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // This will get the radiobutton that has changed in its check state
            RadioButton checkedRadioButton1 = group.findViewById(checkedId);
            // This puts the value (true/false) into the variable
            boolean isChecked = checkedRadioButton1.isChecked();
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked) {
                chosenReportId = rGroup.indexOfChild(checkedRadioButton1);
                Toast.makeText(context, "CHECKED!" + checkedRadioButton1.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
