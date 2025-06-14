package com.example.electricitybillestimator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BillDetailActivity extends AppCompatActivity {

    EditText editMonth, editUnits, editRebate;
    Button buttonDelete, buttonEdit;
    int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        // Initialize UI components
        editMonth = findViewById(R.id.editMonth);
        editUnits = findViewById(R.id.editUnits);
        editRebate = findViewById(R.id.editRebate);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonEdit = findViewById(R.id.buttonEdit);

        // Get data from intent
        recordId = getIntent().getIntExtra("id", -1);
        if (recordId == -1) {
            Toast.makeText(this, "Invalid record ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate fields
        editMonth.setText(getIntent().getStringExtra("month"));
        editUnits.setText(String.valueOf(getIntent().getIntExtra("units", 0)));
        editRebate.setText(String.valueOf(getIntent().getDoubleExtra("rebate", 0.0)));

        // Delete record
        buttonDelete.setOnClickListener(v -> {
            BillDatabase db = BillDatabase.getInstance(this);
            db.billDao().deleteById(recordId);
            Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Edit and update record
        buttonEdit.setOnClickListener(v -> {
            String month = editMonth.getText().toString().trim();
            String unitStr = editUnits.getText().toString().trim();
            String rebateStr = editRebate.getText().toString().trim();

            // Validate input
            if (month.isEmpty() || unitStr.isEmpty() || rebateStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int units = Integer.parseInt(unitStr);
                double rebate = Double.parseDouble(rebateStr);

                double totalCharges = calculateCharges(units);
                double finalCost = totalCharges - (totalCharges * rebate);

                BillDatabase db = BillDatabase.getInstance(this);
                db.billDao().updateBill(recordId, month, units, totalCharges, rebate, finalCost);

                Toast.makeText(this, "Record updated", Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateCharges(int units) {
        double total = 0;
        if (units <= 200) {
            total = units * 0.218;
        } else if (units <= 300) {
            total = (200 * 0.218) + ((units - 200) * 0.334);
        } else if (units <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((units - 300) * 0.516);
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((units - 600) * 0.546);
        }
        return total;
    }
}
