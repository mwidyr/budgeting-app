package com.example.budgetingapp.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;
import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;
import com.example.budgetingapp.util.CustomExpandableListAdapter;
import com.google.android.material.datepicker.MaterialCalendar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DailyFragment extends Fragment
//        implements DatePickerDialog.OnDateSetListener
{
    private static final String TAG = "DailyFragment";
    private static final String EXTRA_ID = "idInputBudget";
    private Context context;
    private AppDatabase db;
    View view;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    //    private ImageView btnLeft, btnRight;
//    private TextView datePicker;
    private Calendar calendar;
    private static final String DATE_PATTERN = "dd MMMM yyyy";
    private static final Integer YEAR_THRESHOLD = 10;
    MaterialCalendarView simpleCalendarView;
    CalendarDay calendarDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_daily, container, false);
        if (getActivity() != null && ((MainActivity) getActivity()).getCalendar() != null) {
            Log.d(TAG, "onCreateView: " + ((MainActivity) getActivity()).getCalendar().get(Calendar.MONTH));
            calendar = ((MainActivity) getActivity()).getCalendar();
        } else {
            calendar = Calendar.getInstance();
        }
        simpleCalendarView = view.findViewById(R.id.simpleCalendarView);
        calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        simpleCalendarView.setCurrentDate(calendarDay);
        simpleCalendarView.setSelectedDate(calendarDay.getDate());
//        btnLeft = view.findViewById(R.id.btn_left_date);
//        btnRight = view.findViewById(R.id.btn_right_date);
//        datePicker = view.findViewById(R.id.tv_date_picker);
//        datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
//        datePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDatePicker();
//            }
//        });

//        btnLeft.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)-1);
//                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
//                simpleCalendarView.setDate(calendar.getTimeInMillis());
//
//                refreshLayout();
//            }
//        });

//        btnRight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
//                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
//                simpleCalendarView.setDate(calendar.getTimeInMillis());
//                refreshLayout();
//            }
//        });
        simpleCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                // check if weekday is sunday
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, day.getYear());
                calendar.set(Calendar.MONTH, day.getMonth() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day.getDay());
                return calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                // add red foreground span
                view.addSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(getActivity(), R.color.white)));
            }
        });

        simpleCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                // check if weekday is sunday
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, day.getYear());
                calendar.set(Calendar.MONTH, day.getMonth() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day.getDay());
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            }

            @Override
            public void decorate(DayViewFacade view) {
                // add red foreground span
                view.addSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(getActivity(), R.color.red_100)));
            }
        });

        expandableListView = view.findViewById(R.id.expandableListView);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        simpleCalendarView.setTileWidth(width / 7);


        simpleCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                setCalendarByDDMMYYYY(date.getYear(), date.getMonth() - 1, date.getDay());
                calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                simpleCalendarView.setCurrentDate(calendarDay);
                Log.d(TAG, "onSelectedDayChange: " + date.getDate());
//                datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
                refreshLayout();
            }
        });

        simpleCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String LOADING = "Loading Data...";
                ProgressDialog loading = null;
                loading = new ProgressDialog(getActivity());
                loading.setCancelable(false);
                loading.setMessage(LOADING);
                loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                loading.show();
                Log.d(TAG, "onMonthChanged: " + date.getDate());
                setCalendarByDDMMYYYY(date.getYear(), date.getMonth() - 1, date.getDay());
                refreshLayoutMonth();
                loading.dismiss();
            }
        });
        refreshLayoutMonth();
        return view;
    }

    public void refreshLayout() {
        Log.d(TAG, "refreshLayout: ");
        ((MainActivity) getActivity()).setCalendar(calendar);
        //use db with calendar configuration
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTime(calendar.getTime());
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE, 0);
        calendarMin.set(Calendar.SECOND, 0);
        calendarMin.set(Calendar.MILLISECOND, 0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTime(calendar.getTime());
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE, 59);
        calendarMax.set(Calendar.SECOND, 59);
        calendarMax.set(Calendar.MILLISECOND, 0);
        List<InputBudget> budgetList = db.inputBudgetDao()
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
        // income
        BudgetRecapDto recapDto = new BudgetRecapDto();
        if (budgetIncome.size() > 0) {
            recapDto.setType("Income");
            recapDto.setTotal(amountIncome);
            recapDto.setInputBudgetList(budgetIncome);
            budgetRecapDtos.add(recapDto);
        }


        //planning
        if (budgetPlanning.size() > 0) {
            recapDto = new BudgetRecapDto();
            recapDto.setType("Planning");
            recapDto.setTotal(amountPlanning);
            recapDto.setInputBudgetList(budgetPlanning);
            budgetRecapDtos.add(recapDto);
        }


        //expenditure
        if (budgetExpenditure.size() > 0) {
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

    public void refreshLayoutMonth() {
        Log.d(TAG, "refreshLayoutMonth: ");


        ((MainActivity) getActivity()).setCalendar(calendar);
        //use db with calendar configuration
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendarMin.setTime(calendar.getTime());
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE, 0);
        calendarMin.set(Calendar.SECOND, 0);
        calendarMin.set(Calendar.MILLISECOND, 0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTime(calendar.getTime());
        calendarMax.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE, 59);
        calendarMax.set(Calendar.SECOND, 59);
        calendarMax.set(Calendar.MILLISECOND, 0);
        List<InputBudget> budgetList = db.inputBudgetDao()
                .findByMonth(calendarMin.getTimeInMillis(), calendarMax.getTimeInMillis());
        final List<BudgetRecapDto> budgetRecapDtos = new ArrayList<>();
        Double amountIncome = 0.0;
        Double amountPlanning = 0.0;
        Double amountExpenditure = 0.0;
        List<InputBudget> budgetIncome = new ArrayList<>();
        List<InputBudget> budgetPlanning = new ArrayList<>();
        List<InputBudget> budgetExpenditure = new ArrayList<>();

        for (final InputBudget budget : budgetList) {
            addDotSpan(budget.dateFrom);

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

        // income
        BudgetRecapDto recapDto = new BudgetRecapDto();
        if (budgetIncome.size() > 0) {
            recapDto.setType("Income");
            recapDto.setTotal(amountIncome);
            recapDto.setInputBudgetList(budgetIncome);
            budgetRecapDtos.add(recapDto);
        }


        //planning
        if (budgetPlanning.size() > 0) {
            recapDto = new BudgetRecapDto();
            recapDto.setType("Planning");
            recapDto.setTotal(amountPlanning);
            recapDto.setInputBudgetList(budgetPlanning);
            budgetRecapDtos.add(recapDto);
        }


        //expenditure
        if (budgetExpenditure.size() > 0) {
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

//    private void showDatePicker() {
//        new SpinnerDatePickerDialogBuilder()
//                .context(getActivity())
//                .callback(this)
//                .spinnerTheme(R.style.NumberPickerStyle)
//                .showTitle(true)
//                .showDaySpinner(true)
//                .defaultDate(calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH))
//                .minDate(calendar.get(Calendar.YEAR)-YEAR_THRESHOLD,
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH))
//                .maxDate(calendar.get(Calendar.YEAR)+YEAR_THRESHOLD,
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH))
//                .build()
//                .show();
//    }

//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        setCalendarByDDMMYYYY(year, monthOfYear, dayOfMonth);
////        datePicker.setText(new SimpleDateFormat(DATE_PATTERN).format(new Date(calendar.getTimeInMillis())));
//        simpleCalendarView.setDate(calendar.getTimeInMillis());
//        refreshLayout();
//    }

    private void setCalendarByDDMMYYYY(int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.YEAR, year);
        ((MainActivity) getActivity()).setCalendar(calendar);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout();
    }

    private void addDotSpan(final Long timeInMilis) {
        Log.d(TAG, "addDotSpan: " + timeInMilis);
        simpleCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                // check if weekday is sunday
                Calendar calendarMin = Calendar.getInstance();
                calendarMin.set(Calendar.YEAR, day.getYear());
                calendarMin.set(Calendar.MONTH, day.getMonth() - 1);
                calendarMin.set(Calendar.DAY_OF_MONTH, day.getDay());
                calendarMin.set(Calendar.HOUR_OF_DAY, 0);
                calendarMin.set(Calendar.MINUTE, 0);
                calendarMin.set(Calendar.SECOND, 0);
                calendarMin.set(Calendar.MILLISECOND, 0);
//                Log.d(TAG, "shouldDecorate: min >> " + calendarMin.getTimeInMillis());
                Calendar calendarMax = Calendar.getInstance();
                calendarMax.setTimeInMillis(calendarMin.getTimeInMillis());
                calendarMax.set(Calendar.HOUR_OF_DAY, 23);
                calendarMax.set(Calendar.MINUTE, 59);
                calendarMax.set(Calendar.SECOND, 59);
                calendarMax.set(Calendar.MILLISECOND, 0);
//                Log.d(TAG, "shouldDecorate: max >> " + calendarMax.getTimeInMillis());
                return (calendarMax.getTimeInMillis() >= timeInMilis
                        && timeInMilis >= calendarMin.getTimeInMillis());
            }

            @Override
            public void decorate(DayViewFacade view) {
                // add red foreground span
                view.addSpan(new DotSpan(5, R.color.black));
            }
        });
    }
}
