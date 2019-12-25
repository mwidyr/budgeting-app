package com.example.budgetingapp.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.room.Database;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;

public class DailyFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
//    List<BudgetRecapDto> budgetList = new ArrayList<>();
    private static final String TAG = "DailyFragment";
    private Context context;
    private AppDatabase db;
    View view;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    private ImageView btnLeft, btnRight;
    private TextView datePicker;
    private Calendar calendar;
    private static final String DATE_PATTERN = "dd MMMM yyyy";
    private static final Integer YEAR_THRESHOLD = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_daily, container, false);
        calendar = Calendar.getInstance();
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
        expandableListDetail = getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

        refreshLayout();
//        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle,
//                expandableListDetail,budgetList);
//        expandableListView.setAdapter(expandableListAdapter);
//        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getActivity(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                ).show();
//                return false;
//            }
//        });
        return view;
    }

    private void refreshLayout(){
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

        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle,
                expandableListDetail,budgetRecapDtos);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<>();
        cricket.add("India");
        cricket.add("Pakistan");
        cricket.add("Australia");
        cricket.add("England");
        cricket.add("South Africa");

        List<String> football = new ArrayList<String>();
        football.add("Brazil");
        football.add("Spain");
        football.add("Germany");
        football.add("Netherlands");
        football.add("Italy");

        List<String> basketball = new ArrayList<String>();
        basketball.add("United States");
        basketball.add("Spain");
        basketball.add("Argentina");
        basketball.add("France");
        basketball.add("Russia");

        expandableListDetail.put("CRICKET TEAMS", cricket);
        expandableListDetail.put("FOOTBALL TEAMS", football);
        expandableListDetail.put("BASKETBALL TEAMS", basketball);
        return expandableListDetail;
    }

    public static DailyFragment newInstance(Integer counter, Context context, AppDatabase db) {
        DailyFragment fragment = new DailyFragment(context, db);
        Bundle args = new Bundle();
//        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }
    public DailyFragment(Context context, AppDatabase db) {
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
