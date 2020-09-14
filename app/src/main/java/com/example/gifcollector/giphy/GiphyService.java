package com.example.gifcollector.giphy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyService {

    String API_KEY = "qBOHDEPHgGMxVObkgQAtQunvj5EwNoIk";

    @GET("gifs/random")
    Call<GiphyRandom> getRandomGif(@Query("api_key") String api_key);
}
