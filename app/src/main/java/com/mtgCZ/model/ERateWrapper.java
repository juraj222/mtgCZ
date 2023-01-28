package com.mtgCZ.model;

public class ERateWrapper {
    private ERate rates;

    public ERateWrapper() {
    }

    public ERateWrapper(ERate rates) {
        this.rates = rates;
    }

    public ERate getRates() {
        return rates;
    }

    public void setRates(ERate rates) {
        this.rates = rates;
    }
}
