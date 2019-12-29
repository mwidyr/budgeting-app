package com.example.budgetingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.dto.InputBudgetDto;
import com.example.budgetingapp.model.InputBudget;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class InputBudgetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "InputBudget";
    private static final String EXTRA_ID = "idInputBudget";
    private static final String EDIT = "CONFIRM EDIT";
    private static final String PLANNING = "Planning";
    private static final String EXTRA_TIMEINMILIS = "extraTimeInMilis";

    private static final NumberFormat formatter = new DecimalFormat("#,###,###,###");

    EditText etTitle, etAmount, dateFrom, etDetails;
    private Spinner budgetCategory, budgetType;
    private Button btnConfirm;
    boolean dateFromClicked;

    private static final String simpleDateFormatPattern = "dd-MM-yyyy";
    private static final Integer DEFAULT_DAY = 1;
    private static final Integer DEFAULT_MONTH = 0;
    private static final Integer DEFAULT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private AppDatabase db;
    private InputBudget model;
    private Long timeInMilis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_budget);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();

        etTitle = findViewById(R.id.et_budget_title);
        etAmount = findViewById(R.id.et_budget_amount);
        dateFrom = findViewById(R.id.et_budget_date_from);
        etDetails = findViewById(R.id.et_budget_detail);

        budgetCategory = findViewById(R.id.budget_category);
        budgetType = findViewById(R.id.budget_type);
        btnConfirm = findViewById(R.id.button_confirm);

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            private String current = "";
            private String CURRENCY = "RP";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && !s.toString().equalsIgnoreCase("")
                &&!s.toString().equals(current)){
                    etAmount.removeTextChangedListener(this);
                    Log.d(TAG, "onTextChanged: s = "+s);
                    s = s.toString().split("\\.")[0];
                    String cleanString = s.toString().replaceAll("[$,.]", "");
                    Log.d(TAG, "onTextChanged: cleanString = "+cleanString);

                    Long parsed = Long.parseLong(cleanString);
                    Log.d(TAG, "onTextChanged: parsed = "+parsed);
                    String formatted = formatter.format((parsed));
                    Log.d(TAG, "onTextChanged: formatted = "+formatted);

                    current = formatted;
                    etAmount.setText(formatted);
                    etAmount.setSelection(formatted.length());

                    etAmount.addTextChangedListener(this);
                }else{
                    etAmount.removeTextChangedListener(this);
                    current = "0";
                    etAmount.setText("0");
                    etAmount.setSelection("0".length());

                    etAmount.addTextChangedListener(this);
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        if (getIntent().getExtras() != null) {
            Log.d(TAG, "onCreate: " + getIntent().getExtras());
            Long extraId = getIntent().getLongExtra(EXTRA_ID, 0);
            timeInMilis = getIntent().getLongExtra(EXTRA_TIMEINMILIS, 0);
            if (extraId != 0) {
                Long idInputBudget = Long.valueOf(extraId);
                model = db.inputBudgetDao().findById(idInputBudget);
                fillModel(model);
                btnConfirm.setText(EDIT);
            } else if (timeInMilis != 0) {
                //do nothing
            } else {
                finish();
            }
        }

        dateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    dateFromClicked = true;
                    datePick();
                    dateFrom.clearFocus();
                }
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: submit");
                boolean statusValidation = true;
                if (etTitle.getText().toString().equalsIgnoreCase("")) {
                    statusValidation = false;
                    etTitle.setError("title is blank");
                }
                if (etAmount.getText().toString().equalsIgnoreCase("")) {
                    statusValidation = false;
                    etAmount.setError("amount is blank");
                }
                if (dateFrom.getText().toString().equalsIgnoreCase("")) {
                    statusValidation = false;
                    dateFrom.setError("date is blank");
                }
                if (statusValidation) {
                    InputBudgetDto inputBudgetDto = new InputBudgetDto();
                    inputBudgetDto.setTitle(etTitle.getText().toString());
                    String amountInput = etAmount.getText().toString().replaceAll(",","");
                    inputBudgetDto.setAmount(Double.valueOf(amountInput));
                    try {
                        Calendar.getInstance().getTimeInMillis();
                        inputBudgetDto.setDateFrom(new SimpleDateFormat(simpleDateFormatPattern).parse(dateFrom.getText().toString()).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    inputBudgetDto.setDetail(etDetails.getText().toString());
                    Log.d(TAG, "onClick: save " + inputBudgetDto.toString());
                    InputBudget saveEntity = new InputBudget();

                    saveEntity.title = inputBudgetDto.getTitle();
                    saveEntity.amount = inputBudgetDto.getAmount();
                    saveEntity.dateFrom = inputBudgetDto.getDateFrom();
                    saveEntity.dateTo = inputBudgetDto.getDateTo();
                    saveEntity.detail = inputBudgetDto.getDetail();
                    saveEntity.category = String.valueOf(budgetCategory.getSelectedItem());
                    saveEntity.type = String.valueOf(budgetType.getSelectedItem());
                    Log.d(TAG, "onClick: save " + saveEntity.toString());
                    if (model != null && model.id != null) {
                        saveEntity.id = model.id;
                        db.inputBudgetDao().update(saveEntity);
                    } else {
                        if (saveEntity.type.equalsIgnoreCase(PLANNING)) {
                            Log.d(TAG, "onClick: planning");
                            saveToCalendar(saveEntity);
                        }
                        db.inputBudgetDao().insertAll(saveEntity);
                    }
                    Toast.makeText(InputBudgetActivity.this, inputBudgetDto.toString(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void fillModel(InputBudget model) {
        etTitle.setText(model.title);
        etAmount.setText(model.amount.toString());
        etDetails.setText(model.detail);
        dateFrom.setText(new SimpleDateFormat(simpleDateFormatPattern).format(new Date(model.dateFrom)));

        String[] category = getResources().getStringArray(R.array.budget_category);
        for (int i = 0; i < category.length; i++) {
            if (category[i].trim().equalsIgnoreCase(model.category.trim())) {
                budgetCategory.setSelection(i);
            }
        }

        String[] type = getResources().getStringArray(R.array.budget_type);
        for (int i = 0; i < type.length; i++) {
            if (type[i].trim().equalsIgnoreCase(model.type.trim())) {
                budgetType.setSelection(i);
            }
        }
    }

    private void datePick() {
        Log.d(TAG, "datePick: ");
        String dateInput;
        String dateMin = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR - 10);
        String dateMax = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + (DEFAULT_YEAR + 10);
        if (dateFrom.getText() != null
                && !dateFrom.getText().toString().equalsIgnoreCase("")) {
            dateInput = fillDateInput(dateFrom.getText().toString());
        } else {
            if (timeInMilis != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timeInMilis);
                dateInput = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH)) + "-" +
                        calendar.get(Calendar.YEAR);
                Log.d(TAG, "datePick: YEAR = "+calendar.get(Calendar.YEAR));
            } else {
                dateInput = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + DEFAULT_YEAR;
            }
        }
        dateFrom.setError(null);
        Log.d(TAG, "datePick: " + dateInput);
        showDatePicker(dateInput, dateMin, dateMax);
    }

    private void showDatePicker(String dateInput, String dateMin, String dateMax) {
        Log.d(TAG, "showDatePicker: dateInput "+dateInput);
        new SpinnerDatePickerDialogBuilder()
                .context(InputBudgetActivity.this)
                .callback(InputBudgetActivity.this)
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
        if (dateFromClicked) dateFrom.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
    }

    private String fillDateInput(String dateFromText) {
        String dateFill = simpleDateFormatPattern;
        dateFill = dateFill.replaceAll("dd", dateFromText.split("-")[0]);
        dateFill = dateFill.replaceAll("MM", (Integer.valueOf(dateFromText.split("-")[1]) - 1) + "");
        dateFill = dateFill.replaceAll("yyyy", dateFromText.split("-")[2]);
        return dateFill;
    }

    ContentResolver cr;
    ContentValues values;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveToCalendar(InputBudget saveEntity) {
        Log.d(TAG, "saveToCalendar: ");
        Calendar dtStart = Calendar.getInstance();
        dtStart.setTimeInMillis(saveEntity.dateFrom);
//        dtstart.set(Calendar.DAY_OF_MONTH, 1);
        dtStart.set(Calendar.HOUR_OF_DAY, 7);
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        Calendar dtUntil = Calendar.getInstance();

        cr = this.getContentResolver();
        values = new ContentValues();

// Where untilDate is a date instance of your choice, for example 30/01/2012
//        dt.setTime();
        dtUntil.setTime(dtStart.getTime());
// If you want the event until 30/01/2012, you must add one day from our day because UNTIL in RRule sets events before the last day
        dtUntil.add(Calendar.DATE, dtStart.get(Calendar.DAY_OF_MONTH));
        dtUntil.set(Calendar.HOUR_OF_DAY, 19);
        String dtUntilString = yyyyMMdd.format(dtUntil.getTime());


        values.put(CalendarContract.Events.DTSTART, dtStart.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, saveEntity.type + " - " + saveEntity.title);
        values.put(CalendarContract.Events.DESCRIPTION, saveEntity.type + " - " + saveEntity.detail);

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

// Default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
                + dtUntilString);
// Set Period for 1 Hour
        values.put(CalendarContract.Events.DURATION, "+P1H");

        values.put(CalendarContract.Events.HAS_ALARM, 1);

// Insert event to calendar
        if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "saveToCalendar: ");
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    1);
            return;
        }
        Uri uri = insertCalendar(cr, values);
    }


    @SuppressLint("MissingPermission")
    private Uri insertCalendar(ContentResolver cr, ContentValues values) {
        return cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: ");

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Uri uri = insertCalendar(cr, values);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
