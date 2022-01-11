package manu.apps.lucemtrader.classes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class Crypto2 {

    @Exclude
    private String id;
    private String title;
    private double value;
    private String name;
    private double amount;
    private String date;
    @PropertyName("isProfit")
    private boolean isProfit;
    private double profitLossPercentage;

    public Crypto2() {
    }

    public Crypto2(String id, String title, double value, String name, double amount, String date, boolean isProfit, double profitLossPercentage) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.isProfit = isProfit;
        this.profitLossPercentage = profitLossPercentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isProfit() {
        return isProfit;
    }

    public void setProfit(boolean profit) {
        isProfit = profit;
    }

    public double getProfitLossPercentage() {
        return profitLossPercentage;
    }

    public void setProfitLossPercentage(double profitLossPercentage) {
        this.profitLossPercentage = profitLossPercentage;
    }
}
