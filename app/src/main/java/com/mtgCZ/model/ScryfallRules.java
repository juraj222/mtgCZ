package com.mtgCZ.model;

import java.util.ArrayList;
import java.util.List;

public class ScryfallRules {
    private List<ScryfallRule> data = new ArrayList<>();

    public ScryfallRules() {
    }

    public ScryfallRules(List<ScryfallRule> data) {
        this.data = data;
    }

    public List<ScryfallRule> getData() {
        return data;
    }

    public void setData(List<ScryfallRule> data) {
        this.data = data;
    }
}
