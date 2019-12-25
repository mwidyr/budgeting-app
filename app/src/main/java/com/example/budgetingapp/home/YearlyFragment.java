package com.example.budgetingapp.home;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetingapp.R;
import com.example.budgetingapp.database.AppDatabase;


public class YearlyFragment extends Fragment {

    private View view;
    private Context context;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_yearly, container, false);
        return view;
    }

    public static YearlyFragment newInstance(Integer counter, Context context, AppDatabase db) {
        YearlyFragment fragment = new YearlyFragment(context, db);
        Bundle args = new Bundle();
//        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    public YearlyFragment(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
//        this.budgetList = budgetList;
    }

}
