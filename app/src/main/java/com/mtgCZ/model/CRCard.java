package com.mtgCZ.model;

public class CRCard {
    private int price;
    private String name;
    private int stock;

    public CRCard() {
    }

    public CRCard(int price, String name, int stock) {
        this.price = price;
        this.name = name;
        this.stock = stock;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
