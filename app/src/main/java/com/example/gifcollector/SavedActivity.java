package com.example.gifcollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class SavedActivity extends AppCompatActivity {
    private List<String> mGifUrls;
    private GifsAdapter mGifsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mGifUrls = new ArrayList<>();

        String readCountOfSaved = Database.get(getApplicationContext()).readData("countOfSaved");
        int countOfSaved;
        if (readCountOfSaved.isEmpty())
            countOfSaved = 0;
        else
            countOfSaved = Integer.parseInt(readCountOfSaved);

        for(int i = 0;i < countOfSaved;i++)
            mGifUrls.add(Database.get(getApplicationContext()).readData(String.valueOf(i)));

        RecyclerView mGifRecyclerView = findViewById(R.id.gifRecyclerView);
        mGifRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager gifManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mGifRecyclerView.setLayoutManager(gifManager);
        mGifsAdapter = new GifsAdapter();
        mGifRecyclerView.setAdapter(mGifsAdapter);
    }

    public class GifsAdapter extends RecyclerView.Adapter<GifsAdapter.ViewHolder> {

        GifsAdapter() {
        }

        @NonNull
        @Override
        public GifsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.gif, parent, false);
            return new GifsAdapter.ViewHolder(view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull final GifsAdapter.ViewHolder holder, final int position) {
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

                    Database.get(getApplicationContext()).writeData("savedGifs", (countOfSaved + 1) + ":" + mGifUrls.get(position));
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
}