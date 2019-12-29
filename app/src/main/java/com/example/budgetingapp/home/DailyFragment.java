package com.example.budgetingapp.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetingapp.InputBudgetActivity;
import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;
import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;
import com.example.budgetingapp.util.CustomExpandableListAdapter;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "DailyFragment";
    private static final String EXTRA_ID = "idInputBudget";
    private Context context;
    private AppDatabase db;
    View view;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    private ImageView btnLeft, btnRight;
    private TextView datePicker;
    private Calendar calendar;
    private static final String DATE_PATTERN = "dd MMMM yyyy";
    private static final Integer YEAR_THRESHOLD = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_daily, container, false);
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
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
                refreshLayout();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
                refreshLayout();
            }
        });


        expandableListView = view.findViewById(R.id.expandableListView);
        refreshLayout();
        return view;
    }

    public void refreshLayout(){
        ((MainActivity)getActivity()).setCalendar(calendar);
        //use db with calendar configuration
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTime(calendar.getTime());
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE,0);
        calendarMin.set(Calendar.SECOND,0);
        calendarMin.set(Calendar.MILLISECOND,0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTime(calendar.getTime());
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE,59);
        calendarMax.set(Calendar.SECOND,59);
        calendarMax.set(Calendar.MILLISECOND,0);
        List<InputBudget> budgetList = db.inputBudgetDao()
                .findByMonth(calendarMin.getTimeInMillis(), calendarMax.getTimeInMillis());
        final List<BudgetRecapDto> budgetRecapDtos = new ArrayList<>();
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

        expandableListAdapter = new CustomExpandableListAdapter(getActivity(),
                budgetRecapDtos);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity(),
                        budgetRecapDtos.get(groupPosition).getType() + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        budgetRecapDtos.get(groupPosition).getType() + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        budgetRecapDtos.get(groupPosition).getType()
                                + " -> "
                                + budgetRecapDtos.get(groupPosition).getInputBudgetList().get(childPosition),
                        Toast.LENGTH_SHORT
                ).show();
//                Intent intent = new Intent(getActivity(), InputBudgetActivity.class);
//                intent.putExtra(EXTRA_ID,budgetRecapDtos.get(groupPosition).getInputBudgetList().get(childPosition).id);
////                intent.putString("key1", budgetRecapDtos.get(groupPosition).getInputBudgetList().get(childPosition).id+"");// if its string type
////                Intent.putExtra("key2", var2);// if its int type
//                startActivity(intent);
                return false;
            }
        });
    }

    public static DailyFragment newInstance(Integer counter, Context context, AppDatabase db) {
        DailyFragment fragment = new DailyFragment(context, db);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public DailyFragment(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
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
