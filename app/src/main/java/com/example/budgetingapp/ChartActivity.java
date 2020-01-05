package com.example.budgetingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.budgetingapp.database.AppDatabase;
import com.example.budgetingapp.model.InputBudget;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChartActivity extends AppCompatActivity {
    private static final String TAG = "ChartActivity";
    private static final String TITLE_MONTH = "Expense Chart in ";
    private static final NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
    private static final float PERCENTAGE_DIVIDER = 100f;

    private TextView tvTitleMonth;

    private List<Float> yData = Arrays.asList(25.31f, 10.6f, 66.75f, 44.32f, 46.01f, 16.89f, 23.9f, 44.32f, 46.01f, 16.89f, 23.9f);
    private List<String> xData = Arrays.asList("Test1", "Test2", "Test3", "Test4", "Test5", "Test6", "Test7", "Test8", "Test9", "Test10", "Test11");
    private static final String EXTRA_TIMEINMILIS = "extraTimeInMilis";
    PieChart pieChart;

    private AppDatabase db;

    public AppDatabase getDb() {
        return db;
    }

    public void setDb(AppDatabase db) {
        this.db = db;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        setDb(Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build());

        Long timeInMilis = getIntent().getLongExtra(EXTRA_TIMEINMILIS, 0);

        if (timeInMilis == 0) timeInMilis = Calendar.getInstance().getTimeInMillis();

        Calendar calendarMin = Calendar.getInstance();
        calendarMin.setTimeInMillis(timeInMilis);
        calendarMin.set(Calendar.DAY_OF_MONTH, 0);
        calendarMin.set(Calendar.HOUR_OF_DAY, 0);
        calendarMin.set(Calendar.MINUTE, 0);
        calendarMin.set(Calendar.SECOND, 0);
        calendarMin.set(Calendar.MILLISECOND, 0);
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.setTimeInMillis(timeInMilis);
        calendarMax.set(Calendar.DAY_OF_MONTH, 30);
        calendarMax.set(Calendar.HOUR_OF_DAY, 23);
        calendarMax.set(Calendar.MINUTE, 59);
        calendarMax.set(Calendar.SECOND, 59);
        calendarMax.set(Calendar.MILLISECOND, 999);

        List<InputBudget> budgetList = db.inputBudgetDao()
                .findByMonth((new Date(calendarMin.getTimeInMillis())).getTime()
                        , (new Date(calendarMax.getTimeInMillis())).getTime());
        Float maxAmountIncome = 0.0f;
        Float maxAmountExpense = 0.0f;
        HashMap<String, Double> budgetByCategory = new HashMap<>();
        for (InputBudget budget : budgetList) {
            if (budget.type.equalsIgnoreCase("Expenditure")) {
                if (budgetByCategory.containsKey(budget.category)) {
                    Double amount = budgetByCategory.get(budget.category) + budget.amount;
                    budgetByCategory.replace(budget.category, amount);
                } else {
                    budgetByCategory.put(budget.category, budget.amount);
                }
                maxAmountExpense += budget.amount.floatValue();
            }
            if (budget.type.equalsIgnoreCase("Income")) {
                maxAmountIncome += budget.amount.floatValue();
            }
        }

        Log.d(TAG, "onCreate: hashmap = " + budgetByCategory);
        Log.d(TAG, "onCreate: maxAmount = " + maxAmountIncome);

        yData = new ArrayList<>();
        xData = new ArrayList<>();
        for (Map.Entry me : budgetByCategory.entrySet()) {
            Log.d(TAG,"Key: " + me.getKey()
                    + " & Value: " + ((((Double) me.getValue()).floatValue() / maxAmountIncome)*PERCENTAGE_DIVIDER));
            yData.add(((((Double) me.getValue()).floatValue() / maxAmountIncome)*PERCENTAGE_DIVIDER));
            xData.add(me.getKey()+"");
        }

        pieChart = findViewById(R.id.pie_chart_balance);
        tvTitleMonth = findViewById(R.id.chart_in_month);

        String month = new SimpleDateFormat("MMMM").format(new Date(timeInMilis));
        tvTitleMonth.setText(TITLE_MONTH+ month);

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("> Income = "+formatter.format(maxAmountIncome)
                +"> Expense = "+formatter.format(maxAmountExpense));
        pieChart.setCenterTextSize(15f);
        pieChart.setUsePercentValues(true);


        addDataSet();
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "onValueSelected: e = " + e.toString());
                Log.d(TAG, "onValueSelected: h = " + h.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet: ");
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();

        for (int i = 0; i < yData.size(); i++) {
            yEntries.add(new PieEntry(yData.get(i), xData.get(i)));
        }

        for (int i = 0; i < xData.size(); i++) {
            xEntries.add(xData.get(i));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntries, "Employee Sales");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //create color
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i = 0; i<xData.size();i++){
            colors.add(getRandomColor());
        }

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setFormSize(10f);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);


        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
        legend.setYOffset(5f);

        List<LegendEntry> entries = new ArrayList<>();

        for (int i = 0; i < yData.size(); i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colors.get(i);
            entry.label = xData.get(i);
            entry.formSize = 10;
            entries.add(entry);
            Log.d(TAG, "addDataSet: " + entry.toString());
        }

        legend.setCustom(entries);

        legend.setEnabled(true);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.getLegend().setTextColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
