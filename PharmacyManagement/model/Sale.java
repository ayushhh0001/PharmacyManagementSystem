package model;

public class Sale {
    public int medicineId;
    public int quantitySold;
    public String saleDate;

    public Sale(int medicineId, int quantitySold, String saleDate) {
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
    }
}
