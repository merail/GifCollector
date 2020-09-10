package com.example.gifcollector;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GiphyService {

    public static final String API_KEY = "qBOHDEPHgGMxVObkgQAtQunvj5EwNoIk"; // TODO:replace with your API key

    // http://api.giphy.com/v1/gifs/search?q=cats&limit=9&api_key=dc6zaTOxFJmzC
    @GET("gifs/search")
    Call<GiphySearch> searchGifs(@Query("q") String query, @Query("limit") int limit, @Query("api_key")  String api_key);
}
