package com.example.gifcollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.gifcollector.giphy.GiphyRandom;
import com.example.gifcollector.giphy.GiphyService;
import com.example.gifcollector.giphy.GiphyServiceBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.gifcollector.Utils.RANDOM;
import static com.example.gifcollector.Utils.SPAN_COUNT;
import static com.example.gifcollector.Utils.SYSTEM_UI_FLAGS;

public class RandomActivity extends AppCompatActivity {
    private List<String> mUrls;
    private Adapter mAdapter;
    private GiphyService mGiphyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAGS);

        mUrls = new ArrayList<>();

        ImageButton refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGifs();
            }
        });

        Button savedButton = findViewById(R.id.saved);
        savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SavedActivity.class));
            }
        });


        RecyclerView recyclerView = findViewById(R.id.gifRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        mAdapter = new Adapter(getApplicationContext(), mUrls, RANDOM);
        recyclerView.setAdapter(mAdapter);

        final Boolean[] isScrolled = {false};
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)  && newState == RecyclerView.SCROLL_STATE_IDLE && !isScrolled[0]) {
                    loadGifs();
                }
                isScrolled[0] = false;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isScrolled[0] = true;
            }
        });

        mGiphyService = GiphyServiceBuilder.build();
        loadGifs();
    }

    public void loadGifs()
    {
        mUrls.clear();
        mAdapter.notifyDataSetChanged();

        for(int i = 0;i < 50;i++)
        {
            mGiphyService.getRandomGif(GiphyService.API_KEY).enqueue(new Callback<GiphyRandom>() {
                @Override
                public void onResponse(@NonNull Call<GiphyRandom> call, @NonNull Response<GiphyRandom> response) {
                    GiphyRandom giphyRandom = response.body();
                    if (giphyRandom != null) {
                        mUrls.add(Objects.requireNonNull(giphyRandom.data.images.get("original")).url);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GiphyRandom> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}