package model;

public class Medicine {
    public int id;
    public String name;
    public String company;
    public int quantity;
    public double price;
    public String expiryDate;

    public Medicine(int id, String name, String company, int quantity, double price, String expiryDate) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }
}
