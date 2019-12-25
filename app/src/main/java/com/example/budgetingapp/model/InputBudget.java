package com.example.budgetingapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InputBudget {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "category")
    public String category;
    @ColumnInfo(name = "type")
    public String type;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "amount")
    public Double amount;
    @ColumnInfo(name = "date_from")
    public Long dateFrom;
    @ColumnInfo(name = "date_to")
    public Long dateTo;
    @ColumnInfo(name = "detail")
    public String detail;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InputBudget{");
        sb.append("id=").append(id);
        sb.append(", category='").append(category).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", dateFrom=").append(dateFrom);
        sb.append(", dateTo=").append(dateTo);
        sb.append(", detail='").append(detail).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
