package com.example.budgetingapp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.budgetingapp.InputBudgetActivity;
import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;
import com.example.budgetingapp.dto.BudgetRecapDto;
import com.example.budgetingapp.model.InputBudget;

public class ExpandableMonthlyAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CustomExpandable";
    private static final NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
    private static final String EXTRA_ID = "idInputBudget";
    private static final String EXTRA_TIMEINMILIS = "extraTimeInMilis";
    private Context context;
    private HashMap<String, List<InputBudget>> recapDtoDetail;
    private List<BudgetRecapDto> recapDtos;

    public ExpandableMonthlyAdapter(Context context,
                                       List<BudgetRecapDto> recapDtos) {
        this.context = context;
        this.recapDtos = recapDtos;
        recapDtoDetail = new HashMap<>();
        for(BudgetRecapDto budget : recapDtos){
            recapDtoDetail.put(budget.getType(),budget.getInputBudgetList());
        }
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
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
            TextView childTitle = convertView
                    .findViewById(R.id.childTitle);
            TextView chilAmount = convertView
                    .findViewById(R.id.childAmount);
            childTitle.setText(inputBudget.title);
            chilAmount.setText(formatter.format(inputBudget.amount));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, InputBudgetActivity.class);
                    intent.putExtra(EXTRA_ID,inputBudget.id);
                    intent.putExtra(EXTRA_TIMEINMILIS,((MainActivity)context).getCalendar().getTimeInMillis());
//                intent.putString("key1", budgetRecapDtos.get(groupPosition).getInputBudgetList().get(childPosition).id+"");// if its string type
//                Intent.putExtra("key2", var2);// if its int type
                    context.startActivity(intent);
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

        }else{
            Log.d(TAG, "getChildView: lebih dari sama dengan size");
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.recapDtoDetail.get(this.recapDtos.get(listPosition).getType())
                .size();
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

    private void showDialogDelete(final InputBudget inputBudget){
        Log.d(TAG, "showDialogDelete: ");
        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry ("+inputBudget.title+")?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        ((MainActivity) context).getDb().inputBudgetDao().delete(inputBudget);
                        ((MainActivity) context).onResumeFromOutside();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
