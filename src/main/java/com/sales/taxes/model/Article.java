package com.sales.taxes.model;

public class Article {

    private int quantity;

    private String name;

    private double price;

    private double priceWithTaxes;

    private boolean basicTaxes;

    private boolean importedTaxes;

    public Article (int quantity, String name, double price, boolean basicTaxes, boolean importedTaxes) {
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.basicTaxes = basicTaxes;
        this.importedTaxes = importedTaxes;
        this.priceWithTaxes = price + calculateImportedTaxes(price, importedTaxes) + calculateBasicTaxes(price, basicTaxes);
    }

    private double calculateImportedTaxes (double price, boolean importedTaxes) {
        double result = 0.0;
        if (importedTaxes){
            result = (price/100)*5;
        }
        return result;
    }

    private double calculateBasicTaxes (double price, boolean basicTaxes) {
        double result = 0.0;
        if (basicTaxes){
            result = (price/100)*10;
        }
        return result;
    }

    public int getQuantity () {
        return quantity;
    }

    public void setQuantity (int quantity) {
        this.quantity = quantity;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public double getPrice () {
        return price;
    }

    public void setPrice (double price) {
        this.price = price;
    }

    public boolean isBasicTaxes () {
        return basicTaxes;
    }

    public void setBasicTaxes (boolean basicTaxes) {
        this.basicTaxes = basicTaxes;
    }

    public boolean isImportedTaxes () {
        return importedTaxes;
    }

    public void setImportedTaxes (boolean importedTaxes) {
        this.importedTaxes = importedTaxes;
    }

    public double getPriceWithTaxes () {
        return priceWithTaxes;
    }

    public void setPriceWithTaxes (double priceWithTaxes) {
        this.priceWithTaxes = priceWithTaxes;
    }

    @Override
    public String toString () {
        return  quantity + " " + name + ": " + priceWithTaxes;
    }
}
