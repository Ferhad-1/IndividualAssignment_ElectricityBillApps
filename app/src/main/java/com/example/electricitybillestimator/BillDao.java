package com.example.electricitybillestimator;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BillDao {

    @Insert
    void insertBill(BillRecord bill);

    @Query("SELECT * FROM BillRecord ORDER BY id DESC")
    List<BillRecord> getAllBills();

    @Query("DELETE FROM BillRecord WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE BillRecord SET month = :month, unitsUsed = :units, totalCharges = :total, rebatePercent = :rebate, finalCost = :finalCost WHERE id = :id")
    void updateBill(int id, String month, int units, double total, double rebate, double finalCost);

}
