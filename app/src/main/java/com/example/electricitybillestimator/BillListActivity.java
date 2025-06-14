package com.example.electricitybillestimator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BillListActivity extends AppCompatActivity {

    ListView listView;
    List<BillRecord> billRecords;
    ArrayAdapter<String> adapter;
    List<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        listView = findViewById(R.id.listViewBills);

        // Load list for the first time
        loadBillList();

        // Set click listener
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            BillRecord record = billRecords.get(i);
            Intent intent = new Intent(BillListActivity.this, BillDetailActivity.class);
            intent.putExtra("id", record.id);
            intent.putExtra("month", record.month);
            intent.putExtra("units", record.unitsUsed);
            intent.putExtra("total", record.totalCharges);
            intent.putExtra("rebate", record.rebatePercent);
            intent.putExtra("final", record.finalCost);
            startActivity(intent);
        });
    }

    // Refresh the list every time the screen comes back
    @Override
    protected void onResume() {
        super.onResume();
        loadBillList(); // Reload from database
    }

    private void loadBillList() {
        BillDatabase db = BillDatabase.getInstance(this);
        billRecords = db.billDao().getAllBills();

        displayList = new ArrayList<>();
        for (BillRecord record : billRecords) {
            displayList.add(record.month + "\nRM " + String.format("%.2f", record.finalCost));
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(18);
                textView.setPadding(20, 30, 20, 30);
                return view;
            }
        };

        listView.setAdapter(adapter);
    }
}
