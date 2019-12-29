package com.example.budgetingapp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.dto.MonthlyBudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthlyBudgetAdapter extends RecyclerView.Adapter<MonthlyBudgetAdapter.MyViewHolder> {
    private static final String TAG = "MonthlyBudAdap";
    private static final NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
    private static final String DATE_PATTERN = "dd-MM-yyyy";

    private List<MonthlyBudgetRecapDto> MonthlyBudgetRecapDtosList;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView title, amountPlanning, amountIncome, amountExpenditure;
        ExpandableListView expandableListView;

        MyViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.title);
            amountIncome = view.findViewById(R.id.amountIncome);
            amountPlanning = view.findViewById(R.id.amountPlanning);
            amountExpenditure = view.findViewById(R.id.amountExpenditure);
            expandableListView = view.findViewById(R.id.expandableListView);
        }
    }


    public MonthlyBudgetAdapter(List<MonthlyBudgetRecapDto> MonthlyBudgetRecapDtosList, Context context) {
        this.MonthlyBudgetRecapDtosList = MonthlyBudgetRecapDtosList;
        this.context = context;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String INCOME = "Income : ";
        String PLANNING = "Planning : ";
        String EXPENSE = "Expense : ";
        MonthlyBudgetRecapDto monthlyBudgetRecapDto = MonthlyBudgetRecapDtosList.get(position);
        holder.title.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(monthlyBudgetRecapDto.getTimeInMilis())));
        for (BudgetRecapDto recapDto : monthlyBudgetRecapDto.getBudgetRecapDtos()) {
            Boolean notEmpty = false;
            if (recapDto.getType().equalsIgnoreCase("Income")) {
                notEmpty = true;
                holder.amountIncome.setText(INCOME + formatter.format(recapDto.getTotal()));
            } else if (recapDto.getType().equalsIgnoreCase("Planning")) {
                notEmpty = true;
                holder.amountPlanning.setText(PLANNING + formatter.format(recapDto.getTotal()));
            } else if (recapDto.getType().equalsIgnoreCase("Expenditure")) {
                notEmpty = true;
                holder.amountExpenditure.setText(EXPENSE + formatter.format(recapDto.getTotal()));
            }
            if (notEmpty) {
                refreshLayout(holder.view, holder.expandableListView, monthlyBudgetRecapDto.getTimeInMilis());
            }
        }

    }

    @Override
    public int getItemCount() {
        return MonthlyBudgetRecapDtosList.size();
    }

    public void refreshLayout(final View view, final ExpandableListView expandableListView, Long getTimeInMilis) {
        Log.d(TAG, "refreshLayout: view height "+view.getLayoutParams().height);
        //use db with calendar configuration
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTimeInMillis(getTimeInMilis);
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE, 0);
        calendarMin.set(Calendar.SECOND, 0);
        calendarMin.set(Calendar.MILLISECOND, 0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTimeInMillis(getTimeInMilis);
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE, 59);
        calendarMax.set(Calendar.SECOND, 59);
        calendarMax.set(Calendar.MILLISECOND, 0);
        List<InputBudget> budgetList = ((MainActivity) context).getDb().inputBudgetDao()
                .findByMonth(calendarMin.getTimeInMillis(), calendarMax.getTimeInMillis());
        final List<BudgetRecapDto> budgetRecapDtos = new ArrayList<>();
        Double amountIncome = 0.0;
        Double amountPlanning = 0.0;
        Double amountExpenditure = 0.0;
        List<InputBudget> budgetIncome = new ArrayList<>();
        List<InputBudget> budgetPlanning = new ArrayList<>();
        List<InputBudget> budgetExpenditure = new ArrayList<>();
        for (InputBudget budget : budgetList) {
            if (budget.type.equalsIgnoreCase("Income")) {
                budgetIncome.add(budget);
                amountIncome += budget.amount;
            } else if (budget.type.equalsIgnoreCase("Planning")) {
                budgetPlanning.add(budget);
                amountPlanning += budget.amount;
            } else if (budget.type.equalsIgnoreCase("Expenditure")) {
                budgetExpenditure.add(budget);
                amountExpenditure += budget.amount;
            }
        }
        Integer heightExpandable = 0;

        // income
        BudgetRecapDto recapDto = new BudgetRecapDto();
        if (budgetIncome.size() > 0) {
            recapDto.setType("Income");
            recapDto.setTotal(amountIncome);
            recapDto.setInputBudgetList(budgetIncome);
            budgetRecapDtos.add(recapDto);
            heightExpandable += budgetIncome.size();
        }


        //planning
        if (budgetPlanning.size() > 0) {
            recapDto = new BudgetRecapDto();
            recapDto.setType("Planning");
            recapDto.setTotal(amountPlanning);
            recapDto.setInputBudgetList(budgetPlanning);
            budgetRecapDtos.add(recapDto);
            heightExpandable += budgetPlanning.size();
        }


        //expenditure
        if (budgetExpenditure.size() > 0) {
            recapDto = new BudgetRecapDto();
            recapDto.setType("Expenditure");
            recapDto.setTotal(amountExpenditure);
            recapDto.setInputBudgetList(budgetExpenditure);
            budgetRecapDtos.add(recapDto);
            heightExpandable += budgetExpenditure.size();

        }

        final ExpandableListAdapter expandableListAdapter = new CustomExpandableListAdapter(context,
                budgetRecapDtos);
        expandableListView.setAdapter(expandableListAdapter);




        final ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
//        params.height = heightExpandable*500;
        expandableListView.setLayoutParams(params);
        final Integer realHeight = params.height;
//        params.height = realHeight*500;
//        view.setLayoutParams(params);
        final Integer finalHeightExpandable = heightExpandable;
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                Toast.makeText(context,
                        budgetRecapDtos.get(groupPosition).getType() + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onGroupExpand: group "+budgetRecapDtos.get(groupPosition));
                Log.d(TAG, "onGroupExpand: size "+budgetRecapDtos.get(groupPosition).getInputBudgetList().size());
                Log.d(TAG, "onGroupExpand: "+expandableListView.getHeight());
                params.height = (finalHeightExpandable+1) * (expandableListView.getHeight()-10);
                expandableListView.setLayoutParams(params);
                expandableListView.requestLayout();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(context,
                        budgetRecapDtos.get(groupPosition).getType() + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();
                params.height = realHeight;
                expandableListView.setLayoutParams(params);
                expandableListView.requestLayout();
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        context,
                        budgetRecapDtos.get(groupPosition).getType()
                                + " -> "
                                + budgetRecapDtos.get(groupPosition).getInputBudgetList().get(childPosition),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }
}
