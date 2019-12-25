package com.example.budgetingapp.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.model.InputBudget;
import com.example.budgetingapp.util.InputBudgetAdapter;

import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment {
    // Array of strings...
    ListView simpleList;
    List<InputBudget> budgetList = new ArrayList<>();
    private Context context;
    private static final String TAG = "CardFrg";

    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private int[] COLOR_MAP = {
            R.color.red_100, R.color.red_300, R.color.red_500, R.color.red_700, R.color.blue_100,
            R.color.blue_300, R.color.blue_500, R.color.blue_700, R.color.green_100, R.color.green_300,
            R.color.green_500, R.color.green_700
    };

    public CardFragment(Context context, List<InputBudget> budgetList) {
        this.context = context;
        this.budgetList = budgetList;
    }

    public static CardFragment newInstance(Integer counter, Context context, List<InputBudget> budgetList) {
        CardFragment fragment = new CardFragment(context, budgetList);
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        simpleList = view.findViewById(R.id.simpleListView);
        List<InputBudget> listString = new ArrayList<>();
        Log.d(TAG, "onCreateView: "+ budgetList);
        for(int i = 0; i< budgetList.size()-counter; i++){
            listString.add(budgetList.get(i));
        }
//        String[] itemsArray = new String[listString.size()];
//        itemsArray = listString.toArray(itemsArray);
//        ArrayAdapter<InputBudget> arrayAdapter =
//                new ArrayAdapter<>(context, R.layout.activity_listview, R.id.budget_title, listString);
        InputBudgetAdapter arrayAdapter = new InputBudgetAdapter(context, listString);
        simpleList.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        view.setBackgroundColor(ContextCompat.getColor(getContext(), COLOR_MAP[counter]));
        TextView textViewCounter = view.findViewById(R.id.tv_counter);
        textViewCounter.setText("Today" + (counter + 1));
    }
}