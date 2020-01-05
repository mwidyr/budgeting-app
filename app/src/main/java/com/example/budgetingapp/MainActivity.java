package com.example.budgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.model.InputBudget;
import com.example.budgetingapp.util.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MainActivity";
    private static final String EXTRA_TIMEINMILIS = "extraTimeInMilis";
    private static final String BALANCE_THIS_MONTH = "Balance this ";
    private static final Integer DEFAULT_DAY = 1;
    private static final Integer DEFAULT_MONTH = 0;
    private static final Integer DEFAULT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private static final String SHOW_CHART_FOR = "Show Chart for ";

    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TextView tv_balance_amount, tv_balance_thisMonth;
    ImageView iv_menu, iv_filter_search, iv_balance_home_more;

    private static final NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
    private TabLayout.Tab tabSelected;

    private AppDatabase db;

    public AppDatabase getDb() {
        return db;
    }

    public void setDb(AppDatabase db) {
        this.db = db;
    }

    private Calendar calendar;

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void refreshAdapter() {
        setBalanceThisMonth();
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTimeInMillis(calendar.getTimeInMillis());
        calendarMin.set(Calendar.DAY_OF_MONTH, 0);
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE, 0);
        calendarMin.set(Calendar.SECOND, 0);
        calendarMin.set(Calendar.MILLISECOND, 0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTimeInMillis(calendar.getTimeInMillis());
        calendarMax.set(Calendar.DAY_OF_MONTH, 30);
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE, 59);
        calendarMax.set(Calendar.SECOND, 59);
        calendarMax.set(Calendar.MILLISECOND, 999);
        List<InputBudget> budgetList = db.inputBudgetDao()
                .findByMonth((new Date(calendarMin.getTimeInMillis())).getTime()
                        , (new Date(calendarMax.getTimeInMillis())).getTime());

        Double amountIncome = 0.0;
        Double amountExpenditure = 0.0;
        List<InputBudget> budgetIncome = new ArrayList<>();
        List<InputBudget> budgetExpenditure = new ArrayList<>();
        for (InputBudget budget : budgetList) {
            if (budget.type.equalsIgnoreCase("Income")) {
                budgetIncome.add(budget);
                amountIncome += budget.amount;
            } else if (budget.type.equalsIgnoreCase("Expenditure")) {
                budgetExpenditure.add(budget);
                amountExpenditure += budget.amount;
            }
        }
//        viewPager.setAdapter(createCardAdapter(this));
        TabLayout.Tab tabTemp = tabSelected;
        if (tabTemp != null) {
            Log.d(TAG, "refreshAdapter: tabTemp " + tabTemp.getPosition());
        }
        if (viewPager.getAdapter() != null) {
            Log.d(TAG, "refreshAdapter: adapter " + viewPager.getAdapter().getClass());
            viewPager.setAdapter(createCardAdapter(this));
            if (tabTemp != null) {
                Log.d(TAG, "refreshAdapter: pager and tabTemp " + tabTemp.getPosition());
                viewPager.setCurrentItem(tabTemp.getPosition());
            }
        }

        String totalAmountString = formatter.format(amountIncome - amountExpenditure);
        tv_balance_amount.setText(totalAmountString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDb(Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build());

        fab = findViewById(R.id.btn_addBudget);
        tv_balance_amount = findViewById(R.id.tv_balance_amount);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        iv_menu = findViewById(R.id.btn_home_more);
        iv_filter_search = findViewById(R.id.btn_menu_filter);
        iv_balance_home_more = findViewById(R.id.balance_home_more);
        tv_balance_thisMonth = findViewById(R.id.tv_balance);

        setCalendar(Calendar.getInstance());

        setBalanceThisMonth();

        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "More is Selected", Toast.LENGTH_SHORT).show();
            }
        });

        iv_filter_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Menu Filter is Selected", Toast.LENGTH_SHORT).show();
            }
        });

        iv_balance_home_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Balance Home is Selected", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_home_more, null);
                dialogBuilder.setView(dialogView);
                TextView tvChangeCalendar = dialogView.findViewById(R.id.change_calendar);
                TextView tvShowChart = dialogView.findViewById(R.id.show_chart);
                tvShowChart.setText(SHOW_CHART_FOR+getMonthString());
                final AlertDialog alertDialog = dialogBuilder.create();
                tvChangeCalendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, "Change Calendar", Toast.LENGTH_SHORT).show();
                        datePick();
                        alertDialog.cancel();
                    }
                });

                tvShowChart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, SHOW_CHART_FOR+getMonthString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                        Log.d(TAG, "onClick: timeInMilis " + getCalendar().getTimeInMillis());
                        Long timeInMilis = getCalendar().getTimeInMillis();
                        intent.putExtra(EXTRA_TIMEINMILIS, timeInMilis);
                        startActivity(intent);;
                    }
                });

                alertDialog.show();

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabSelected != null) {
                    Log.d(TAG, "onTabSelected: " + tabSelected.getPosition());
                }
                tabSelected = tab;
                Log.d(TAG, "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        refreshAdapter();
        viewPager.setAdapter(createCardAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("DAILY");
                                break;
                            case 1:
                                tab.setText("MONTHLY");
                                break;
                            case 2:
                                tab.setText("YEARLY");
                                break;
                            default:
                                tab.setText("DAILY");
                                break;
                        }

                    }
                }).attach();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputBudgetActivity.class);
                Log.d(TAG, "onClick: timeInMilis " + getCalendar().getTimeInMillis());
                Long timeInMilis = getCalendar().getTimeInMillis();
                intent.putExtra(EXTRA_TIMEINMILIS, timeInMilis);
                startActivity(intent);
            }
        });

    }

    private ViewPagerAdapter createCardAdapter(Context context) {
        return new ViewPagerAdapter(this, context, db);
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        refreshAdapter();
    }

    public void onResumeFromOutside() {
        onResume();
    }

    private void datePick() {
        Log.d(TAG, "datePick: ");
        String dateInput;
        String dateMin = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR - 10);
        String dateMax = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR + 10);
        dateInput = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.YEAR);
        Log.d(TAG, "datePick: " + dateInput);
        showDatePicker(dateInput, dateMin, dateMax);
    }


    private void showDatePicker(String dateInput, String dateMin, String dateMax) {
        Log.d(TAG, "showDatePicker: dateInput " + dateInput);
        new SpinnerDatePickerDialogBuilder()
                .context(MainActivity.this)
                .callback(MainActivity.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(Integer.valueOf(dateInput.split("-")[2]),
                        Integer.valueOf(dateInput.split("-")[1]),
                        Integer.valueOf(dateInput.split("-")[0]))
                .minDate(Integer.valueOf(dateMin.split("-")[2]),
                        Integer.valueOf(dateMin.split("-")[1]),
                        Integer.valueOf(dateMin.split("-")[0]))
                .maxDate(Integer.valueOf(dateMax.split("-")[2]),
                        Integer.valueOf(dateMax.split("-")[1]),
                        Integer.valueOf(dateMax.split("-")[0]))
                .build()
                .show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        if (dateFromClicked) dateFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setCalendar(cal);
        setBalanceThisMonth();
        refreshAdapter();
    }

    private void setBalanceThisMonth() {
        tv_balance_thisMonth.setText(BALANCE_THIS_MONTH + getMonthString());
    }

    private String getMonthString() {
        return new SimpleDateFormat("MMMM")
                .format(calendar.getTime());
    }

}
