package com.example.budgetingapp.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.budgetingapp.home.CardFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 3;
    private Context context;
    private List<String> stringList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, List<String> stringList) {
        super(fragmentActivity);
        this.context = context;
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CardFragment.newInstance(position, context, stringList);
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
