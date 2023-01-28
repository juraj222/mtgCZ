package com.mtgCZ.model;

public class ScryfallCard {
    private String name;
    private String id;
    private Price prices;

    public ScryfallCard() {
    }

    public ScryfallCard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Price getPrices() {
        return prices;
    }

    public void setPrices(Price prices) {
        this.prices = prices;
    }

    public class Price {
        private String eur;

        public Price() {
        }

        public Price(String eur) {
            this.eur = eur;
        }

        public String getEur() {
            return eur;
        }

        public void setEur(String eur) {
            this.eur = eur;
        }
    }
}
