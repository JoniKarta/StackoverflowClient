package com.example.envirometalist.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.envirometalist.R;
import com.example.envirometalist.fragments.manager.map.FragmentMap;
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.Location;

public class CreateElementDialog  extends Dialog{
    private DialogListener dialogListener;

    private EditText elementName;
    private Spinner  elementType;
    private CheckBox elementActive;
    private EditText elementCapacity;
    private Button okButton;
    private Button cancelButton;


    public CreateElementDialog(@NonNull Context context, DialogListener listener) {
        super(context);
        dialogListener = listener;
        setContentView(R.layout.add_element_dialog);
        elementName = findViewById(R.id.name);
        elementType = findViewById(R.id.type);
        elementActive = findViewById(R.id.active);
        elementCapacity = findViewById(R.id.capacity);
        okButton = findViewById(R.id.OK);
        cancelButton = findViewById(R.id.CANCEL);
//        String[] roles = {"PLAYER", "MANAGER", "ADMIN"};
//        ArrayAdapter<String> roleArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_selectable_list_item, roles);
//        roleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        private String elementId; // In the ElementBoundary it was String
//        private String type;
//        private String name;
//        private boolean active;
//        private Date createdTimestamp;
//        private Creator createdBy; // createdBy was a Creator class
//        private Location location;
//        private Map<String, Object> elementAttribute;
//    public Element() {
        okButton.setOnClickListener(v -> dialogListener.applySetting(new Element(null,
                "PAPER",
                "paper",
                true,
                null,
                null,
                new Location(28.464800,77.221230),null)));

    }
   public interface DialogListener {
        void applySetting(Element element);
    }
}
