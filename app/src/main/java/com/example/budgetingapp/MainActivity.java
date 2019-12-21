package com.example.budgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.budgetingapp.util.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stringList = Arrays.asList(new String[]{"India", "China", "australia", "Portugle", "America", "NewZealand"});
        fab = findViewById(R.id.btn_addBudget);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter(this, stringList));

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText("Tab " + (position + 1));
                    }
                }).attach();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private ViewPagerAdapter createCardAdapter(Context context, List<String> stringList) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, context, stringList);
        return adapter;
    }
}
