package com.mtgCZ.service;

import com.mtgCZ.model.CRCard;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface MtgSearchService {
    @GET("/mtg/{cardName}")
    Call<List<CRCard>> getCard(@Path("cardName") String CardName);
}
