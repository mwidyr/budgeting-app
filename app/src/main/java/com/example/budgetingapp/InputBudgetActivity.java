package com.example.budgetingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputBudgetActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "InputBudget";

    EditText etTitle, etAmount, dateFrom, etDetails;
    private Spinner budgetCategory, budgetType;
    private Button btnConfirm;
    boolean dateFromClicked;

    private static final String simpleDateFormatPattern = "dd-MM-yyyy";
    private static final Integer DEFAULT_DAY = 1;
    private static final Integer DEFAULT_MONTH = 0;
    private static final Integer DEFAULT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    private AppDatabase db;

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
                    inputBudgetDto.setAmount(Double.valueOf(etAmount.getText().toString()));
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
                    db.inputBudgetDao().insertAll(saveEntity);
                    Toast.makeText(InputBudgetActivity.this, inputBudgetDto.toString(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
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
            dateInput = DEFAULT_DAY + "-" + DEFAULT_MONTH + "-" + DEFAULT_YEAR;
        }
        dateFrom.setError(null);
        showDatePicker(dateInput, dateMin, dateMax);
    }

    private void showDatePicker(String dateInput, String dateMin, String dateMax) {
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
}
