package com.example.budgetingapp.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.home.CardFragment;
import com.example.budgetingapp.home.DailyFragment;
import com.example.budgetingapp.home.MonthlyFragment;
import com.example.budgetingapp.home.YearlyFragment;
import com.example.budgetingapp.model.InputBudget;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 3;
    private Context context;
    private List<BudgetRecapDto> budgetList;
    private AppDatabase db;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, AppDatabase db) {
        super(fragmentActivity);
        this.context = context;
//        this.budgetList = budgetList;
        this.db = db;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return DailyFragment.newInstance(position,context,db);
            case 1:
                return MonthlyFragment.newInstance(position,context,db);
            case 2:
                return YearlyFragment.newInstance(position,context,db);
            default:
                return DailyFragment.newInstance(position,context,db);

        }
//        return CardFragment.newInstance(position, context, budgetList);
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
