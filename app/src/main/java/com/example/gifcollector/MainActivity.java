package com.example.gifcollector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private List<String> mGifUrls;
    private RecyclerView mGifRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGifUrls = new ArrayList<>();
        mGifRecyclerView = findViewById(R.id.gifRecyclerView);
        mGifRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager gifManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mGifRecyclerView.setLayoutManager(gifManager);
        final GifsAdapter gifsAdapter = new GifsAdapter();
        mGifRecyclerView.setAdapter(gifsAdapter);

        GiphyService giphyService = GiphyServiceBuilder.build();
        for(int i = 0;i < 50;i++)
        giphyService.searchGifs(GiphyService.API_KEY).enqueue(new Callback<GiphySearch>() {
            @Override
            public void onResponse(@NonNull Call<GiphySearch> call, @NonNull Response<GiphySearch> response) {
                GiphySearch giphySearch = response.body();
                if (giphySearch != null) {
                    Log.d("aaaaaaaaaa", Objects.requireNonNull(giphySearch.data.images.get("original")).url);
                    mGifUrls.add(Objects.requireNonNull(giphySearch.data.images.get("original")).url);
                    gifsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GiphySearch> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public class GifsAdapter extends RecyclerView.Adapter<GifsAdapter.ViewHolder> {

        GifsAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.gif, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            Glide.with(getApplicationContext())
                    .load(mGifUrls.get(position))
                    .into(holder.gif);
        }

        @Override
        public int getItemCount() {
            return mGifUrls.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView gif;

            ViewHolder(View itemView) {
                super(itemView);
                gif = itemView.findViewById(R.id.gif);
            }
        }
    }
}