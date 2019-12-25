package com.example.budgetingapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetingapp.MainActivity;
import com.example.budgetingapp.R;

import com.example.budgetingapp.model.InputBudget;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InputBudgetAdapter extends ArrayAdapter<InputBudget> {
    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView amount;
        TextView date;
        ImageView edit;
        ImageView delete;
    }

    public InputBudgetAdapter(Context context, List<InputBudget> users) {
        super(context, R.layout.activity_listview, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final InputBudget inputBudget = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_listview, parent, false);
            viewHolder.title = convertView.findViewById(R.id.budget_title);
            viewHolder.amount =  convertView.findViewById(R.id.budget_amount);
            viewHolder.date = convertView.findViewById(R.id.budget_date);
            viewHolder.edit =  convertView.findViewById(R.id.budget_edit);
            viewHolder.delete =  convertView.findViewById(R.id.budget_delete);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        NumberFormat formatter = new DecimalFormat("'Rp' #,###,###,###");
        String amountToText = formatter.format(inputBudget.amount);
        viewHolder.title.setText(inputBudget.title);
        viewHolder.amount.setText(amountToText);
        String dateToText = new SimpleDateFormat("dd-MMM-yyyy").format(new Date(inputBudget.dateFrom));
        viewHolder.date.setText(dateToText);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), inputBudget.title, Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "click "+inputBudget.title, Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete "+inputBudget.title+" ?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                MainActivity activity = ((MainActivity)getContext());
                                activity.getDb().inputBudgetDao().delete(inputBudget);
                                activity.refreshAdapter();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
}
