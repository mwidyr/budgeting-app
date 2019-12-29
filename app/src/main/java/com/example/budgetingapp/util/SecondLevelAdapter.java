package com.example.budgetingapp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.budgetingapp.InputBudgetActivity;
import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;

public class SecondLevelAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "SecondLevel";
    private static final NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
    private static final String EXTRA_ID = "idInputBudget";
    private static final String EXTRA_TIMEINMILIS = "extraTimeInMilis";

    private final Context mContext;
    private final List<BudgetRecapDto> mListDataHeader;
    private final Map<String, List<InputBudget>> mListDataChild;
    public SecondLevelAdapter(Context mContext, List<BudgetRecapDto> mListDataHeader) {
        this.mContext = mContext;
        this.mListDataHeader = mListDataHeader;
//        this.mListDataChild = mListDataChild;
        this.mListDataChild = new HashMap<>();
        for(BudgetRecapDto input : mListDataHeader){
            mListDataChild.put(input.getType(), input.getInputBudgetList());
        }

    }
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        Log.d(TAG, "getChild: ");
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition).getType())
                .get(childPosition);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        final InputBudget inputBudget = (InputBudget) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, parent, false);
        }
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        TextView txtAmount = (TextView) convertView
                .findViewById(R.id.amount);
        txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        txtListChild.setTextColor(Color.BLACK);
        txtListChild.setText(inputBudget.title);
        txtAmount.setText(formatter.format(inputBudget.amount));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, InputBudgetActivity.class);
                intent.putExtra(EXTRA_ID,inputBudget.id);
                intent.putExtra(EXTRA_TIMEINMILIS,((MainActivity)mContext).getCalendar().getTimeInMillis());
                mContext.startActivity(intent);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: ");
                showDialogDelete(inputBudget);
                return false;
            }
        });
        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition)
    {
        try {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition).getType()).size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGroup(int groupPosition)
    {
        return this.mListDataHeader.get(groupPosition);
    }
    @Override
    public int getGroupCount()
    {
        return this.mListDataHeader.size();
    }
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        BudgetRecapDto headerTitle = (BudgetRecapDto) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_group_second, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle.getType());
        lblListHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        lblListHeader.setTextColor(Color.GRAY);
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
    private void showDialogDelete(final InputBudget inputBudget){
        Log.d(TAG, "showDialogDelete: ");
        new AlertDialog.Builder(mContext)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry ("+inputBudget.title+")?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        ((MainActivity) mContext).getDb().inputBudgetDao().delete(inputBudget);
                        ((MainActivity) mContext).onResumeFromOutside();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
