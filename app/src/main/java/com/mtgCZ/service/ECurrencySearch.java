package com.mtgCZ.service;

import com.mtgCZ.model.ERateWrapper;
import com.mtgCZ.model.ScryfallCard;
import com.mtgCZ.model.ScryfallRules;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ECurrencySearch {
    @GET("/v6/latest/EUR")
    Call<ERateWrapper> getExchangeRateOfEur();

}
