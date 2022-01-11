package manu.apps.lucemtrader.classes;

public class Crypto {

    int id;
    String title;
    double value;
    String name;
    double amount;
    String date;
    int cryptoImage;
    boolean isProfit;
    double profitLossPercentage;

    public Crypto() {
    }

    public Crypto(int id, String title, double value,
                  String name, double amount,
                  String date, int cryptoImage) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.cryptoImage = cryptoImage;
    }

    public Crypto(int id, String title, double value, String name,
                  double amount, String date, int cryptoImage,
                  boolean isProfit, double profitLossPercentage) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.cryptoImage = cryptoImage;
        this.isProfit = isProfit;
        this.profitLossPercentage = profitLossPercentage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getCryptoImage() {
        return cryptoImage;
    }

    public void setCryptoImage(int cryptoImage) {
        this.cryptoImage = cryptoImage;
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
