package com.example.gifcollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.gifcollector.database.Database;

import java.util.List;

import static com.example.gifcollector.Utils.RANDOM;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context mContext;
    private List<String> mUrls;
    private String mMode;

    Adapter(Context context, List<String> urls, String mode) {
        mContext = context;
        mUrls = urls;
        mMode = mode;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.gif, parent, false);
        return new ViewHolder(view, mMode);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final Adapter.ViewHolder holder, final int position) {
        final Boolean[] isNotLongClicked = {true};

        Glide.with(mContext)
                .load(mUrls.get(position))
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
                if (isNotLongClicked[0])
                {
                    holder.save.setVisibility(View.INVISIBLE);
                }
                isNotLongClicked[0] = true;
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String readCountOfSaved = Database.get(mContext).readData("countOfSaved");
                int countOfSaved;
                if (readCountOfSaved.isEmpty())
                    countOfSaved = 0;
                else
                    countOfSaved = Integer.parseInt(readCountOfSaved);

                Database.get(mContext).deleteData("countOfSaved");

                if (mMode.equals(RANDOM)) {
                    Database.get(mContext).writeData("countOfSaved", String.valueOf(countOfSaved + 1));
                    Database.get(mContext).writeData(String.valueOf((countOfSaved)), mUrls.get(position));
                } else {
                    Database.get(mContext).deleteData(String.valueOf(position));
                    for(int i = position;i < countOfSaved - 1;i++)
                    {
                        Database.get(mContext).writeData(String.valueOf(i), Database.get(mContext).readData(String.valueOf(i + 1)));
                        Database.get(mContext).deleteData(String.valueOf(i + 1));
                    }
                    Database.get(mContext).writeData("countOfSaved", String.valueOf(countOfSaved - 1));

                    mUrls.remove(position);
                    notifyItemRemoved(position);
                }

                holder.save.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private ImageView gif;
        private ImageButton save;

        ViewHolder(View itemView, String mode) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progress);
            gif = itemView.findViewById(R.id.gif);
            save = itemView.findViewById(R.id.save);

            if (mode.equals(RANDOM))
                save.setBackgroundResource(R.drawable.save);
            else
                save.setBackgroundResource(R.drawable.delete);
        }
    }
}
