package com.example.budgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.dto.InputBudgetDto;
import com.example.budgetingapp.model.InputBudget;
import com.example.budgetingapp.util.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MainActivity";
    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    AlertDialog dialog;
    LayoutInflater inflater;
//    View dialogView;
    EditText et_title, et_amount, date_from, date_to, et_details;
    TextView tv_balance_amount;
    InputBudgetDto inputBudgetDto;
    boolean dateFromClicked;
    private static final String simpleDateFormatPattern = "dd-MM-yyyy";
    private static final Integer DEFAULT_DAY = 1;
    private static final Integer DEFAULT_MONTH = 0;
    private static final Integer DEFAULT_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private AppDatabase db;

    public AppDatabase getDb() {
        return db;
    }

    public void setDb(AppDatabase db) {
        this.db = db;
    }

    public void refreshAdapter(){
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(Calendar.DAY_OF_MONTH,0);
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE,0);
        calendarMin.set(Calendar.SECOND,0);
        calendarMin.set(Calendar.MILLISECOND,0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.set(Calendar.DAY_OF_MONTH,30);
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE,59);
        calendarMax.set(Calendar.SECOND,59);
        calendarMax.set(Calendar.MILLISECOND,999);
        List<InputBudget> budgetList = db.inputBudgetDao()
                .findByMonth((new Date(calendarMin.getTimeInMillis())).getTime()
                        ,(new Date(calendarMax.getTimeInMillis())).getTime());

        Double amountIncome= 0.0;
        Double amountExpenditure = 0.0;
        List<InputBudget> budgetIncome = new ArrayList<>();
        List<InputBudget> budgetExpenditure = new ArrayList<>();
        for(InputBudget budget:budgetList){
            if(budget.type.equalsIgnoreCase("Income")){
                budgetIncome.add(budget);
                amountIncome += budget.amount;
            }else if(budget.type.equalsIgnoreCase("Expenditure")){
                budgetExpenditure.add(budget);
                amountExpenditure += budget.amount;
            }
        }
        viewPager.setAdapter(createCardAdapter(this));
         NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
        String totalAmountString = formatter.format(amountIncome-amountExpenditure);
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
        refreshAdapter();
//
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch(position){
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
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this,
                        InputBudgetActivity.class));
//                dialogForm();
            }
        });
//

    }

    private ViewPagerAdapter createCardAdapter(Context context) {
        return new ViewPagerAdapter(this, context, db);
    }

    // untuk mengosongi edittext
    private void kosong() {
        et_title.setText(null);
        et_amount.setText(null);
        date_from.setText(null);
        date_to.setText(null);
        inputBudgetDto = new InputBudgetDto();
    }

    @SuppressLint("InflateParams")
//    private void dialogForm() {
//        inflater = getLayoutInflater();
//        dialogView = inflater.inflate(R.layout.form_data, null);
//        dialog = new AlertDialog.Builder(MainActivity.this)
//                .setView(dialogView)
//                .setCancelable(false)
//                .setIcon(R.mipmap.ic_launcher)
//                .setTitle("Input InputBudget")
//                .setPositiveButton("SUBMIT", null)
//                .setNegativeButton("CANCEL", null)
//                .create();
//
//        et_title = dialogView.findViewById(R.id.et_budget_title);
//        et_amount = dialogView.findViewById(R.id.et_budget_amount);
//        date_from = dialogView.findViewById(R.id.et_budget_date_from);
//        date_to = dialogView.findViewById(R.id.et_budget_date_to);
//        et_details = dialogView.findViewById(R.id.et_budget_detail);
//
//        kosong();
//
//        date_from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    dateFromClicked = true;
//                    datePick();
//                    date_from.clearFocus();
//                }
//            }
//        });
//
//
//        date_to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    dateFromClicked = false;
//                    datePick();
//                    date_to.clearFocus();
//                }
//            }
//        });
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//
//                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                button.setOnClickListener(new View.OnClickListener() {
//
//                    @SuppressLint("SimpleDateFormat")
//                    @Override
//                    public void onClick(View view) {
//                        boolean statusValidation = true;
//                        if (et_title.getText().toString().equalsIgnoreCase("")) {
//                            statusValidation = false;
//                            et_title.setError("title is blank");
//                        }
//                        if (et_amount.getText().toString().equalsIgnoreCase("")) {
//                            statusValidation = false;
//                            et_amount.setError("amount is blank");
//                        }
//                        if (date_from.getText().toString().equalsIgnoreCase("")) {
//                            statusValidation = false;
//                            date_from.setError("date is blank");
//                        }
//                        if (date_to.getText().toString().equalsIgnoreCase("")) {
//                            statusValidation = false;
//                            date_to.setError("date is blank");
//                        }
//                        if (statusValidation) {
//                            inputBudgetDto.setTitle(et_title.getText().toString());
//                            inputBudgetDto.setAmount(Double.valueOf(et_amount.getText().toString()));
//                            try {
//                                Calendar.getInstance().getTimeInMillis();
//                                inputBudgetDto.setDateFrom(new SimpleDateFormat(simpleDateFormatPattern).parse(date_from.getText().toString()).getTime());
//                                inputBudgetDto.setDateTo(new SimpleDateFormat(simpleDateFormatPattern).parse(date_to.getText().toString()).getTime());
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            inputBudgetDto.setDetail(et_details.getText().toString());
//                            InputBudget saveEntity = new InputBudget();
//                            saveEntity.title = inputBudgetDto.getTitle();
//                            saveEntity.amount = inputBudgetDto.getAmount();
//                            saveEntity.dateFrom = inputBudgetDto.getDateFrom();
//                            saveEntity.dateTo = inputBudgetDto.getDateTo();
//                            saveEntity.detail = inputBudgetDto.getDetail();
//                            db.inputBudgetDao().insertAll(saveEntity);
//                            Toast.makeText(MainActivity.this, inputBudgetDto.toString(), Toast.LENGTH_LONG).show();
//                            refreshAdapter();
//                            dialog.dismiss();
//                        }
//                    }
//                });
//            }
//        });
//        dialog.show();
//
//
//    }

//    private String fillDateInput(String dateFromText) {
//        String dateFill = simpleDateFormatPattern;
//        dateFill = dateFill.replaceAll("dd", dateFromText.split("-")[0]);
//        dateFill = dateFill.replaceAll("MM", (Integer.valueOf(dateFromText.split("-")[1]) - 1) + "");
//        dateFill = dateFill.replaceAll("yyyy", dateFromText.split("-")[2]);
//        return dateFill;
//    }

//    private void datePick() {
//        String dateInput;
//        String dateMin = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR - 10);
//        String dateMax = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR + 10);
//        if (dateFromClicked) {
//            if (date_from.getText() != null
//                    && !date_from.getText().toString().equalsIgnoreCase("")) {
//                dateInput = fillDateInput(date_from.getText().toString());
//            } else {
//                dateInput = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + DEFAULT_YEAR;
//            }
//            if (date_to.getText() != null
//                    && !date_to.getText().toString().equalsIgnoreCase("")) {
//                dateMax = fillDateInput(date_to.getText().toString());
//            }
//            date_from.setError(null);
//        } else {
//            if (date_to.getText() != null
//                    && !date_to.getText().toString().equalsIgnoreCase("")) {
//                dateInput = fillDateInput(date_to.getText().toString());
//            } else {
//                dateInput = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR + 1);
//            }
//            if (date_from.getText() != null
//                    && !date_from.getText().toString().equalsIgnoreCase("")) {
//                dateMin = fillDateInput(date_from.getText().toString());
//            }
//            date_to.setError(null);
//        }
//        showDatePicker(dateInput, dateMin, dateMax);
//    }

//    private void showDatePicker(String dateInput, String dateMin, String dateMax) {
//        new SpinnerDatePickerDialogBuilder()
//                .context(MainActivity.this)
//                .callback(MainActivity.this)
//                .spinnerTheme(R.style.NumberPickerStyle)
//                .showTitle(true)
//                .showDaySpinner(true)
//                .defaultDate(Integer.valueOf(dateInput.split("-")[2]),
//                        Integer.valueOf(dateInput.split("-")[1]),
//                        Integer.valueOf(dateInput.split("-")[0]))
//                .minDate(Integer.valueOf(dateMin.split("-")[2]),
//                        Integer.valueOf(dateMin.split("-")[1]),
//                        Integer.valueOf(dateMin.split("-")[0]))
//                .maxDate(Integer.valueOf(dateMax.split("-")[2]),
//                        Integer.valueOf(dateMax.split("-")[1]),
//                        Integer.valueOf(dateMax.split("-")[0]))
//                .build()
//                .show();
//    }

//    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (dateFromClicked) date_from.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
        else date_to.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAdapter();
    }
}
