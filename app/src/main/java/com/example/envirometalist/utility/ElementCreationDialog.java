package com.example.envirometalist.utility;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.envirometalist.R;
import com.example.envirometalist.model.RecycleTypes;
import com.example.envirometalist.model.Element;

import java.util.Collections;

public class ElementCreationDialog extends Dialog implements AdapterView.OnItemSelectedListener {
    private DialogListener dialogListener;

    private EditText elementName;
    private Spinner elementType;
    private CheckBox elementActive;
    private Spinner elementCapacity;
    private Button okButton;
    private Button cancelButton;
    private Element element;


    public ElementCreationDialog(@NonNull Context context, DialogListener dialogListener, Element element) {
        super(context);

        setContentView(R.layout.dialog_add_element);
        this.dialogListener = dialogListener;
        this.element = element;
        initUI();
        ArrayAdapter<RecycleTypes> binTypesArrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_selectable_list_item,
                RecycleTypes.values());
        binTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        elementType.setAdapter(binTypesArrayAdapter);

        ArrayAdapter<String> capacityAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_selectable_list_item,
                new String[]{"10", "50", "100", "800"});
        capacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        elementType.setOnItemSelectedListener(this);
        elementCapacity.setOnItemSelectedListener(this);
        elementCapacity.setAdapter(capacityAdapter);
        okButton.setOnClickListener(v -> {
            String name = elementName.getText().toString();
            if (name.isEmpty()) {
                elementName.setError("No explicit name");
                return;
            }
            element.setName(name);
            element.setActive(elementActive.isChecked());
            if (this.dialogListener != null) {
                dismiss();
                this.dialogListener.onFinish(element);
            }
            dismiss();

        });
        cancelButton.setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.typeSpinner:
                element.setType(parent.getItemAtPosition(position).toString());
                break;
            case R.id.capacitySpinner:
                element.setElementAttribute(Collections.singletonMap("Capacity", parent.getItemAtPosition(position).toString()));
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface DialogListener {
        void onFinish(Element element);
    }

    private void initUI() {
        elementName = findViewById(R.id.nameEditText);
        elementType = findViewById(R.id.typeSpinner);
        elementActive = findViewById(R.id.activeCheckBox);
        elementCapacity = findViewById(R.id.capacitySpinner);
        okButton = findViewById(R.id.createButton);
        cancelButton = findViewById(R.id.cancelButton);
    }
}
