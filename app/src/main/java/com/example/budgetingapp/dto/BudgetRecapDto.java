package com.example.budgetingapp.dto;

import com.example.budgetingapp.model.InputBudget;

import java.util.List;

public class BudgetRecapDto {
    private String type;
    private Double total;
    private List<InputBudget> inputBudgetList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<InputBudget> getInputBudgetList() {
        return inputBudgetList;
    }

    public void setInputBudgetList(List<InputBudget> inputBudgetList) {
        this.inputBudgetList = inputBudgetList;
    }

    public BudgetRecapDto() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BudgetRecapDto{");
        sb.append("type='").append(type).append('\'');
        sb.append(", total=").append(total);
        sb.append(", inputBudgetList=").append(inputBudgetList);
        sb.append('}');
        return sb.toString();
    }
}
