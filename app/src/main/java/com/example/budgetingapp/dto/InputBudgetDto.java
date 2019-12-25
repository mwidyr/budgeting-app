package com.example.budgetingapp.dto;

import java.sql.Timestamp;

public class InputBudgetDto {
    private Long id;
    private String title;
    private Double amount;
    private Long dateFrom;
    private Long dateTo;
    private String detail;

    public InputBudgetDto() {
    }

    public InputBudgetDto(Long id, String title, Double amount, Long dateFrom, Long dateTo, String detail) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.detail = detail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Long getDateTo() {
        return dateTo;
    }

    public void setDateTo(Long dateTo) {
        this.dateTo = dateTo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InputBudgetDto{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", dateFrom=").append(dateFrom);
        sb.append(", dateTo=").append(dateTo);
        sb.append(", detail='").append(detail).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
