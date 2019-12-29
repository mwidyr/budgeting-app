package com.example.budgetingapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.budgetingapp.model.InputBudget;

import java.util.List;

@Dao
public interface InputBudgetDao {
    @Query("SELECT * FROM inputBudget")
    List<InputBudget> getAll();

    @Query("SELECT * FROM inputBudget where date_from = (:inputDate)")
    List<InputBudget> findByDay(Long inputDate);

    @Query("SELECT * FROM inputBudget where id = (:id)")
    InputBudget findById(Long id);

    @Query("SELECT * FROM inputBudget where date_from >= (:inputDateMin) " +
            "AND date_from <= (:inputDateMax)")
    List<InputBudget> findByMonth(Long inputDateMin, Long inputDateMax);

    @Query("SELECT * FROM inputBudget WHERE id IN (:userIds)")
    List<InputBudget> loadAllByIds(int[] userIds);

//    @Query("SELECT * FROM inputBudget WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    InputBudget findByName(String first, String last);

    @Insert
    void insertAll(InputBudget... inputBudgets);

    @Delete
    void delete(InputBudget inputBudget);

    @Update
    void update(InputBudget... inputBudgets);
}
