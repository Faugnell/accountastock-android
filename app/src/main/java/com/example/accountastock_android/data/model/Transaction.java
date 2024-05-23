package com.example.accountastock_android.data.model;

public class Transaction {
    private int idTransaction;
    private String title;
    private String note;
    private double amount;
    private String date;
    private Integer fkIdProfit;
    private Integer fkIdExpense;
    private Integer fkIdUser;

    public Transaction(int idTransaction, String title, String note, double amount, String date, Integer fkIdProfit, Integer fkIdExpense, Integer fkIdUser) {
        this.idTransaction = idTransaction;
        this.title = title;
        this.note = note;
        this.amount = amount;
        this.date = date;
        this.fkIdProfit = fkIdProfit;
        this.fkIdExpense = fkIdExpense;
        this.fkIdUser = fkIdUser;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "idTransaction=" + idTransaction +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", fkIdProfit=" + fkIdProfit +
                ", fkIdExpense=" + fkIdExpense +
                ", fkIdUser=" + fkIdUser +
                '}';
    }

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getFkIdProfit() {
        return fkIdProfit;
    }

    public void setFkIdProfit(Integer fkIdProfit) {
        this.fkIdProfit = fkIdProfit;
    }

    public Integer getFkIdExpense() {
        return fkIdExpense;
    }

    public void setFkIdExpense(Integer fkIdExpense) {
        this.fkIdExpense = fkIdExpense;
    }
    public Integer getFkIdUser() {
        return fkIdUser;
    }

    public void setFkIdUser(Integer fkIdUser) {
        this.fkIdUser = fkIdUser;
    }
}
