package com.example.budgetingapp.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;
import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.dto.MonthlyBudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;
import com.example.budgetingapp.util.MonthlyBudgetAdapter;
import com.example.budgetingapp.util.ParentLevelAdapter;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MonthlyFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "MonthlyFrag";

    private View view;
    private Context context;
    private AppDatabase db;

    private List<MonthlyBudgetRecapDto> recapDtoList = new ArrayList<>();
    ExpandableListView mExpandableListView;
    private MonthlyBudgetAdapter mAdapter;

    private ImageView btnLeft, btnRight;
    private TextView datePicker;
    private Calendar calendar;
    private static final String DATE_PATTERN = "MMMM yyyy";
    private static final Integer YEAR_THRESHOLD = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_monthly, container, false);

        if(getActivity()!=null&&((MainActivity)getActivity()).getCalendar()!=null){
            calendar = ((MainActivity)getActivity()).getCalendar();
        }else{
            calendar = Calendar.getInstance();
        }
        btnLeft = view.findViewById(R.id.btn_left_date);
        btnRight = view.findViewById(R.id.btn_right_date);
        datePicker = view.findViewById(R.id.tv_date_picker);
        datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
//                recapDtoList = new ArrayList<>();
                refreshLayout();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
//                recapDtoList = new ArrayList<>();
                refreshLayout();
            }
        });

        mExpandableListView = view.findViewById(R.id.expandableListView_Parent);
        if (mExpandableListView != null) {
            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(context, recapDtoList);
            mExpandableListView.setAdapter(parentLevelAdapter);
        }

        refreshLayout();
        return view;
    }

    private void refreshLayout(){
        ((MainActivity)getActivity()).setCalendar(calendar);
        recapDtoList = new ArrayList<>();
//        mAdapter.notifyDataSetChanged();
        for(int i = 1;i<=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
            Log.d(TAG, "refreshLayout: month "+calendar.get(Calendar.MONTH));
            Calendar calendarMin = Calendar.getInstance();
            calendarMin.setTime(calendar.getTime());
            calendarMin.set(Calendar.DAY_OF_MONTH,i);
            calendarMin.set(Calendar.HOUR_OF_DAY, 0);
            calendarMin.set(Calendar.MINUTE,0);
            calendarMin.set(Calendar.SECOND,0);
            calendarMin.set(Calendar.MILLISECOND,0);
            Calendar calendarMax = Calendar.getInstance();
            calendarMax.setTime(calendar.getTime());
            calendarMax.set(Calendar.DAY_OF_MONTH, i);
            calendarMax.set(Calendar.HOUR_OF_DAY, 23);
            calendarMax.set(Calendar.MINUTE,59);
            calendarMax.set(Calendar.SECOND,59);
            calendarMax.set(Calendar.MILLISECOND,0);


            List<InputBudget> budgetList = db.inputBudgetDao()
                    .findByMonth(calendarMin.getTimeInMillis(), calendarMax.getTimeInMillis());
            if(budgetList.size()>0){
                Log.d(TAG, "refreshLayout: not empty");
                MonthlyBudgetRecapDto monthlyBudgetRecapDto = new MonthlyBudgetRecapDto();
                List<BudgetRecapDto> budgetRecapDtos = new ArrayList<>();
                Double amountIncome= 0.0;
                Double amountPlanning = 0.0;
                Double amountExpenditure = 0.0;
                List<InputBudget> budgetIncome = new ArrayList<>();
                List<InputBudget> budgetPlanning = new ArrayList<>();
                List<InputBudget> budgetExpenditure = new ArrayList<>();
                for(InputBudget budget:budgetList){
                    if(budget.type.equalsIgnoreCase("Income")){
                        budgetIncome.add(budget);
                        amountIncome += budget.amount;
                    }else if(budget.type.equalsIgnoreCase("Planning")){
                        budgetPlanning.add(budget);
                        amountPlanning += budget.amount;
                    }else if(budget.type.equalsIgnoreCase("Expenditure")){
                        budgetExpenditure.add(budget);
                        amountExpenditure += budget.amount;
                    }
                }
                // income
                BudgetRecapDto recapDto = new BudgetRecapDto();
                if(budgetIncome.size()>0){
                    recapDto.setType("Income");
                    recapDto.setTotal(amountIncome);
                    recapDto.setInputBudgetList(budgetIncome);
                    budgetRecapDtos.add(recapDto);
                }


                //planning
                if(budgetPlanning.size()>0){
                    recapDto = new BudgetRecapDto();
                    recapDto.setType("Planning");
                    recapDto.setTotal(amountPlanning);
                    recapDto.setInputBudgetList(budgetPlanning);
                    budgetRecapDtos.add(recapDto);
                }


                //expenditure
                if(budgetExpenditure.size()>0){
                    recapDto = new BudgetRecapDto();
                    recapDto.setType("Expenditure");
                    recapDto.setTotal(amountExpenditure);
                    recapDto.setInputBudgetList(budgetExpenditure);
                    budgetRecapDtos.add(recapDto);
                }
                monthlyBudgetRecapDto.setTimeInMilis(calendarMin.getTimeInMillis());
                monthlyBudgetRecapDto.setBudgetRecapDtos(budgetRecapDtos);
                recapDtoList.add(monthlyBudgetRecapDto);
            }
        }
        Log.d(TAG, "refreshLayout: "+ recapDtoList);
//        mAdapter = new MonthlyBudgetAdapter(recapDtoList,context);
//        recyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
        ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(context, recapDtoList);
        mExpandableListView.setAdapter(parentLevelAdapter);
    }

    public static MonthlyFragment newInstance(Integer counter, Context context, AppDatabase db) {
        MonthlyFragment fragment = new MonthlyFragment(context, db);
        Bundle args = new Bundle();
//        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    public MonthlyFragment(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
//        this.budgetList = budgetList;
    }

    private void showDatePicker() {
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                .minDate(calendar.get(Calendar.YEAR)-YEAR_THRESHOLD,
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                .maxDate(calendar.get(Calendar.YEAR)+YEAR_THRESHOLD,
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                .build()
                .show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.YEAR, year);
        datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
        refreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout();
    }

}
