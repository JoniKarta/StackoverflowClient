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
import com.example.envirometalist.model.Element;
import com.example.envirometalist.model.RecycleTypes;

import java.util.Collections;
import java.util.Objects;


public class ElementManagementDialog extends Dialog implements AdapterView.OnItemSelectedListener {

    // Variable for update the elements
    private EditText elementName;
    private Spinner elementTypeSpinner;
    private CheckBox elementActive;
    private Spinner elementCapacitySpinner;
    private Button updateButton;
    private Element element;

    // Listener for update element
    private OnManagerManagementCallback onManagerManagementCallback;

    // Adapters for spinners
    private ArrayAdapter<RecycleTypes> binTypesArrayAdapter;
    private ArrayAdapter<String> capacityAdapter;

    private String updateElementType;
    private String updateElementCapacity;

    public ElementManagementDialog(@NonNull Context context, Element element, OnManagerManagementCallback onManagerManagementCallback) {
        super(context);
        setContentView(R.layout.dialog_update_element);
        // This variable hold the element which going to be updated
        this.element = element;

        // Callback variable which the map registered to
        this.onManagerManagementCallback = onManagerManagementCallback;


        // Configure update elements widgets
        setElementUpdateUI();

        // Configure the bin spinner variables
        binTypesArrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_selectable_list_item,
                RecycleTypes.values());

        // Set drop down list to the spinner
        binTypesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the bin spinner
        elementTypeSpinner.setAdapter(binTypesArrayAdapter);

        // Configure the capacity spinner
        capacityAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_selectable_list_item,
                new String[]{"10", "50", "100", "800"});

        // Set dropdown list to the spinner
        capacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set listeners to the spinners
        elementTypeSpinner.setOnItemSelectedListener(this);
        elementCapacitySpinner.setOnItemSelectedListener(this);

        // Set the adapter to the capacity spinner
        elementCapacitySpinner.setAdapter(capacityAdapter);

        // Display the current state
        displayCurrentElementState();

        // Update Button to update the current element which displayed on the view
        updateButton.setOnClickListener(v -> {
            if (this.onManagerManagementCallback != null) {
                element.setName(elementName.getText().toString());
                element.setType(updateElementType);
                element.setElementAttribute(Collections.singletonMap("Capacity",updateElementCapacity));
                element.setActive(elementActive.isChecked());
                this.onManagerManagementCallback.onUpdate(element);
            }
            dismiss();


        });


    }

    private void setElementUpdateUI() {
        elementName = findViewById(R.id.managerManagement_nameEditText);
        elementTypeSpinner = findViewById(R.id.managerManagement_typeSpinner);
        elementActive = findViewById(R.id.managerManagement_activeCheckBox);
        elementCapacitySpinner = findViewById(R.id.managerManagement_capacitySpinner);
        updateButton = findViewById(R.id.managerManagement_updateButton);
    }

    private void displayCurrentElementState() {
        elementName.setText(element.getName());
        elementActive.setChecked(element.getActive());
        int position;
        position = binTypesArrayAdapter.getPosition(RecycleTypes.valueOf(element.getType()));
        elementTypeSpinner.setSelection(position);
        binTypesArrayAdapter.notifyDataSetChanged();
        if (element.getElementAttribute() != null && element.getElementAttribute().get("Capacity") != null) {
            position = capacityAdapter.getPosition(Objects.requireNonNull(element.getElementAttribute().get("Capacity")).toString());
            elementCapacitySpinner.setSelection(position);
            capacityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.managerManagement_typeSpinner:
                updateElementType = parent.getItemAtPosition(position).toString();
                break;
            case R.id.managerManagement_capacitySpinner:
                updateElementCapacity = parent.getItemAtPosition(position).toString();
                break;
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public interface OnManagerManagementCallback {
        void onUpdate(Element element);

    }
}
