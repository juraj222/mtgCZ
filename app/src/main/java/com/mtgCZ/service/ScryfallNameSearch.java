package com.mtgCZ.service;

import com.mtgCZ.model.CRCard;
import com.mtgCZ.model.ScryfallCard;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface ScryfallNameSearch {
    @GET("/cards/named")
    Call<ScryfallCard> findCardNameFromFuzzyText(@Query("fuzzy") String cardName);
}
