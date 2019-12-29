package com.example.budgetingapp.dto;

import java.util.List;

public class MonthlyBudgetRecapDto {
    private Long timeInMilis;
    private List<BudgetRecapDto> budgetRecapDtos;

    public Long getTimeInMilis() {
        return timeInMilis;
    }

    public void setTimeInMilis(Long timeInMilis) {
        this.timeInMilis = timeInMilis;
    }

    public List<BudgetRecapDto> getBudgetRecapDtos() {
        return budgetRecapDtos;
    }

    public void setBudgetRecapDtos(List<BudgetRecapDto> budgetRecapDtos) {
        this.budgetRecapDtos = budgetRecapDtos;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MonthlyBudgetRecapDto{");
        sb.append("timeInMilis=").append(timeInMilis);
        sb.append(", budgetRecapDtos=").append(budgetRecapDtos);
        sb.append('}');
        return sb.toString();
    }
}
