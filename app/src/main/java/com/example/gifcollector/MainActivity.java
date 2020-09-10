package com.example.gifcollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GiphyService giphyService = GiphyServiceBuilder.build();
        giphyService.searchGifs("cat", 9, GiphyService.API_KEY).enqueue(new Callback<GiphySearch>() {
            @Override
            public void onResponse(@NonNull Call<GiphySearch> call, @NonNull Response<GiphySearch> response) {
                GiphySearch giphySearch = response.body();
                if (giphySearch != null) {
                    Log.d("aaaaaaaaaa", String.valueOf(Objects.requireNonNull(giphySearch.data.get(0).images.get("original")).url));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GiphySearch> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}