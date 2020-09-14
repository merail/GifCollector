package com.example.gifcollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.gifcollector.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class MainActivity extends AppCompatActivity {
    private List<String> mGifUrls;
    private GifsAdapter mGifsAdapter;
    private GiphyService mGiphyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

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


        mGifUrls = new ArrayList<>();
        RecyclerView mGifRecyclerView = findViewById(R.id.gifRecyclerView);
        mGifRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager gifManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mGifRecyclerView.setLayoutManager(gifManager);
        mGifsAdapter = new GifsAdapter();
        mGifRecyclerView.setAdapter(mGifsAdapter);

        final Boolean[] isScrolled = {false};
        mGifRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            final Boolean[] isNotLongClicked = {true};

            Glide.with(getApplicationContext())
                    .load(mGifUrls.get(position))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.gif);

            holder.gif.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.save.setVisibility(View.VISIBLE);
                    isNotLongClicked[0] = false;
                    return false;
                }
            });

            holder.gif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNotLongClicked[0])
                        holder.save.setVisibility(View.INVISIBLE);
                    isNotLongClicked[0] = true;
                }
            });

            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String readCountOfSaved = Database.get(getApplicationContext()).readData("countOfSaved");
                    int countOfSaved;
                    if (readCountOfSaved.isEmpty())
                        countOfSaved = 0;
                    else
                        countOfSaved = Integer.parseInt(readCountOfSaved);

                    Database.get(getApplicationContext()).deleteData("countOfSaved");

                    Database.get(getApplicationContext()).writeData("countOfSaved", String.valueOf(countOfSaved + 1));

                    Database.get(getApplicationContext()).writeData(String.valueOf((countOfSaved + 1)), mGifUrls.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mGifUrls.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ProgressBar progressBar;
            private ImageView gif;
            private ImageButton save;

            ViewHolder(View itemView) {
                super(itemView);
                progressBar = itemView.findViewById(R.id.progress);
                gif = itemView.findViewById(R.id.gif);
                save = itemView.findViewById(R.id.save);
            }
        }
    }

    public void loadGifs()
    {
        mGifUrls.clear();
        mGifsAdapter.notifyDataSetChanged();

        for(int i = 0;i < 50;i++)
        {
            mGiphyService.searchGifs(GiphyService.API_KEY).enqueue(new Callback<GiphySearch>() {
                @Override
                public void onResponse(@NonNull Call<GiphySearch> call, @NonNull Response<GiphySearch> response) {
                    GiphySearch giphySearch = response.body();
                    if (giphySearch != null) {
                        mGifUrls.add(Objects.requireNonNull(giphySearch.data.images.get("original")).url);
                        mGifsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GiphySearch> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}