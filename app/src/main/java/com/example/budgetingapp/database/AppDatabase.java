package com.example.budgetingapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.budgetingapp.dao.InputBudgetDao;
import com.example.budgetingapp.model.InputBudget;

@Database(entities = {InputBudget.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InputBudgetDao inputBudgetDao();
}
