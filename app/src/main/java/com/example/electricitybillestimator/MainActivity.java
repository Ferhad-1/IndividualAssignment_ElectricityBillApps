package com.example.electricitybillestimator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth, spinnerRebate;
    EditText editTextUnits;
    Button buttonCalculate, buttonViewBills, buttonAbout;
    TextView textTotalCharge, textFinalCost;

    String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    String[] rebateOptions = {"0%", "1%", "2%", "3%", "4%", "5%"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerRebate = findViewById(R.id.spinnerRebate);
        editTextUnits = findViewById(R.id.editTextUnits);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonViewBills = findViewById(R.id.buttonViewBills);
        buttonAbout = findViewById(R.id.buttonAbout);
        textTotalCharge = findViewById(R.id.textTotalCharge);
        textFinalCost = findViewById(R.id.textFinalCost);

        // Setup spinners
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        ArrayAdapter<String> rebateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rebateOptions);
        rebateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRebate.setAdapter(rebateAdapter);

        // Handle Calculate button
        buttonCalculate.setOnClickListener(v -> calculateBill());

        // Navigate to Bill List
        buttonViewBills.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BillListActivity.class);
            startActivity(intent);
        });

        // Navigate to About Page
        buttonAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void calculateBill() {
        String unitText = editTextUnits.getText().toString();

        if (unitText.isEmpty()) {
            editTextUnits.setError("Please enter the number of units");
            return;
        }

        int units = Integer.parseInt(unitText);
        String selectedRebate = spinnerRebate.getSelectedItem().toString();
        double rebatePercent = Double.parseDouble(selectedRebate.replace("%", "")) / 100.0;

        double totalCharges = 0;

        if (units <= 200) {
            totalCharges = units * 0.218;
        } else if (units <= 300) {
            totalCharges = (200 * 0.218) + ((units - 200) * 0.334);
        } else if (units <= 600) {
            totalCharges = (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
        } else {
            totalCharges = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);
        }

        double finalCost = totalCharges - (totalCharges * rebatePercent);

        // Display results
        textTotalCharge.setText(String.format("Total Charges: RM %.2f", totalCharges));
        textFinalCost.setText(String.format("Final Cost After Rebate: RM %.2f", finalCost));

        // Save to Room database
        BillRecord bill = new BillRecord(
                spinnerMonth.getSelectedItem().toString(),
                units,
                totalCharges,
                rebatePercent,
                finalCost
        );

        BillDatabase db = BillDatabase.getInstance(this);
        db.billDao().insertBill(bill);
        Toast.makeText(this, "Bill saved!", Toast.LENGTH_SHORT).show();
    }
}