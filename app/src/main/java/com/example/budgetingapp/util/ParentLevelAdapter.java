package com.example.budgetingapp.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.budgetingapp.R;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.dto.MonthlyBudgetRecapDto;

public class ParentLevelAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "ParentLevel";
    private static final String DAY_PATTERN = "dd-MM-yyyy";
    private static final NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");

    private final Context mContext;

    private List<MonthlyBudgetRecapDto> recapDtoList = new ArrayList<>();
    private final Map<String, List<BudgetRecapDto>> mListDataMonthly_SecondLevel_Map;

    public ParentLevelAdapter(Context mContext,
                              List<MonthlyBudgetRecapDto> recapDtoList
    ) {
        this.mContext = mContext;
        this.recapDtoList = recapDtoList;
        // Init second level data
        String[] mItemHeaders;
        mListDataMonthly_SecondLevel_Map = new HashMap<>();
        int parentCount = recapDtoList.size();
        for (int i = 0; i < parentCount; i++) {
            MonthlyBudgetRecapDto content = recapDtoList.get(i);
            List<String> fillType = new ArrayList<>();
            for (BudgetRecapDto child : content.getBudgetRecapDtos()) {
                fillType.add(child.getType());
            }
            mItemHeaders = new String[fillType.size()];
            mItemHeaders = fillType.toArray(mItemHeaders);
            Log.d(TAG, "ParentLevelAdapter: put second level " + Arrays.asList(mItemHeaders));
            mListDataMonthly_SecondLevel_Map.put(recapDtoList.get(i).getTimeInMilis() + "", recapDtoList.get(i).getBudgetRecapDtos());
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final CustomExpListView secondLevelExpListView = new CustomExpListView(this.mContext);
        MonthlyBudgetRecapDto parentNode = ((MonthlyBudgetRecapDto) getGroup(groupPosition));
        Log.d(TAG, "getChildView: second map : " + mListDataMonthly_SecondLevel_Map.get(parentNode.getTimeInMilis() + ""));
        secondLevelExpListView.setAdapter(new SecondLevelAdapter(this.mContext,
                mListDataMonthly_SecondLevel_Map.get(parentNode.getTimeInMilis() + "")));

        secondLevelExpListView.setGroupIndicator(null);
        return secondLevelExpListView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.recapDtoList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.recapDtoList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        MonthlyBudgetRecapDto dto = ((MonthlyBudgetRecapDto) getGroup(groupPosition));
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_group, parent, false);
        }

        TextView titleDay = convertView
                .findViewById(R.id.lblListHeader);
        TextView amountPlanning = convertView
                .findViewById(R.id.amountPlanning);
        TextView amountIncome = convertView
                .findViewById(R.id.amountIncome);
        TextView amountExpenditure = convertView
                .findViewById(R.id.amountExpenditure);
        titleDay.setTypeface(null, Typeface.BOLD);
        titleDay.setTextColor(Color.BLACK);
        titleDay.setText(new SimpleDateFormat(DAY_PATTERN).format(new Date(dto.getTimeInMilis())));

        String INCOME = "Income : ";
        String PLANNING = "Planning : ";
        String EXPENSE = "Expense : ";

        for (BudgetRecapDto recapDto : dto.getBudgetRecapDtos()) {
            if (recapDto.getType().equalsIgnoreCase("Income")) {
                amountIncome.setText(INCOME + formatter.format(recapDto.getTotal()));
            } else if (recapDto.getType().equalsIgnoreCase("Planning")) {
                amountPlanning.setText(PLANNING + formatter.format(recapDto.getTotal()));
            } else if (recapDto.getType().equalsIgnoreCase("Expenditure")) {
                amountExpenditure.setText(EXPENSE + formatter.format(recapDto.getTotal()));
            }
        }
        if (amountIncome.getText() == null ||
                amountIncome.getText().toString().equalsIgnoreCase("")) {
            amountIncome.setVisibility(View.GONE);
        }
        if (amountPlanning.getText() == null ||
                amountPlanning.getText().toString().equalsIgnoreCase("")) {
            amountPlanning.setVisibility(View.GONE);
        }
        if (amountExpenditure.getText() == null ||
                amountExpenditure.getText().toString().equalsIgnoreCase("")) {
            amountExpenditure.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
