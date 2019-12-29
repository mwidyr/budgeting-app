package com.example.budgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String EXTRA_TIMEINMILIS = "extraTimeInMilis";
    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    TextView tv_balance_amount;
    ImageView iv_menu,iv_filter_search,iv_balance_home_more;

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
//        viewPager.setAdapter(createCardAdapter(this));
        TabLayout.Tab tabTemp = tabSelected;
        if(tabTemp!=null){
            Log.d(TAG, "refreshAdapter: tabTemp "+tabTemp.getPosition());
        }
        if(viewPager.getAdapter()!=null){
            Log.d(TAG, "refreshAdapter: adapter "+viewPager.getAdapter().getClass());
            viewPager.setAdapter(createCardAdapter(this));
            if(tabTemp!=null){
                Log.d(TAG, "refreshAdapter: pager and tabTemp "+tabTemp.getPosition());
                viewPager.setCurrentItem(tabTemp.getPosition());
            }
        }

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
        iv_menu = findViewById(R.id.btn_home_more);
        iv_filter_search = findViewById(R.id.btn_menu_filter);
        iv_balance_home_more = findViewById(R.id.balance_home_more);

        setCalendar(Calendar.getInstance());

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
                Toast.makeText(MainActivity.this, "Balance Home is Selected", Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabSelected!=null){
                    Log.d(TAG, "onTabSelected: "+tabSelected.getPosition());
                }
                tabSelected = tab;
                Log.d(TAG, "onTabSelected: "+tab.getPosition());
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
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, InputBudgetActivity.class);
                Log.d(TAG, "onClick: timeInMilis "+getCalendar().getTimeInMillis());
                Long timeInMilis = getCalendar().getTimeInMillis();
                intent.putExtra(EXTRA_TIMEINMILIS,timeInMilis);
//                intent.putExtra(EXTRA_ID,inputBudget.id);
//                intent.putString("key1", budgetRecapDtos.get(groupPosition).getInputBudgetList().get(childPosition).id+"");// if its string type
//                Intent.putExtra("key2", var2);// if its int type
                startActivity(intent);
//                startActivity(new Intent(MainActivity.this,
//                        InputBudgetActivity.class));
//                dialogForm();
            }
        });
//

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

    public void onResumeFromOutside(){
        onResume();
    }
}
