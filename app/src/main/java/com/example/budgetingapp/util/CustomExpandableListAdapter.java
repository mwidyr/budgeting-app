package com.example.budgetingapp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CustomExpandable";

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private HashMap<String, List<InputBudget>> recapDtoDetail;
    private List<BudgetRecapDto> recapDtos;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<String>> expandableListDetail, List<BudgetRecapDto> recapDtos) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.recapDtos = recapDtos;
        recapDtoDetail = new HashMap<>();
        for(BudgetRecapDto budget : recapDtos){
            recapDtoDetail.put(budget.getType(),budget.getInputBudgetList());
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
//        Log.d(TAG, "getChild: title "+this.expandableListTitle.get(listPosition));
//        Log.d(TAG, "getChild: position "+expandedListPosition);
//        Log.d(TAG, "getChild: detail "+this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .get(expandedListPosition));
//        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .get(expandedListPosition);
        Log.d(TAG, "getChild: title "+this.recapDtos.get(listPosition));
        Log.d(TAG, "getChild: position "+(expandedListPosition));
        Log.d(TAG, "getChild: detail "+this.recapDtoDetail.get(this.recapDtos.get(listPosition).getType())
                .get(expandedListPosition));
        return this.recapDtoDetail.get(this.recapDtos.get(listPosition).getType())
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d(TAG, "getChildView: "+expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        if(this.recapDtoDetail.get(this.recapDtos.get(listPosition).getType()).size()>expandedListPosition){
            Log.d(TAG, "getChildView: kurang dari size");
            final InputBudget inputBudget = (InputBudget) getChild(listPosition, expandedListPosition);
            TextView expandedListTextView = convertView
                    .findViewById(R.id.expandedListItem);
            expandedListTextView.setText(inputBudget.title);

        }else{
            Log.d(TAG, "getChildView: lebih dari sama dengan size");
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        Log.d(TAG, "getChildrenCount: detail "+this.recapDtoDetail.get(this.recapDtos.get(listPosition).getType())
                .size());
        return this.recapDtoDetail.get(this.recapDtos.get(listPosition).getType())
                .size();
//        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
//                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.recapDtos.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.recapDtos.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = ((BudgetRecapDto) getGroup(listPosition)).getType();
        Double listTotal = ((BudgetRecapDto) getGroup(listPosition)).getTotal();
        NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
        String totalAmountString = formatter.format(listTotal);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.listTitle);
        TextView listTotalTextView = convertView
                .findViewById(R.id.tv_listTotal);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        listTotalTextView.setText(totalAmountString);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
