package com.example.gifcollector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gifcollector.database.Database;

import java.util.ArrayList;
import java.util.List;

import static com.example.gifcollector.Utils.SAVED;
import static com.example.gifcollector.Utils.SPAN_COUNT;
import static com.example.gifcollector.Utils.SYSTEM_UI_FLAGS;

public class SavedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAGS);

        ImageButton randomButton = findViewById(R.id.random);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        List<String> mUrls = new ArrayList<>();

        String readCountOfSaved = Database.get(getApplicationContext()).readData("countOfSaved");
        int countOfSaved;
        if (readCountOfSaved.isEmpty())
            countOfSaved = 0;
        else
            countOfSaved = Integer.parseInt(readCountOfSaved);

        for (int i = 0; i < countOfSaved; i++)
            mUrls.add(Database.get(getApplicationContext()).readData(String.valueOf(i)));
        RecyclerView recyclerView = findViewById(R.id.savedRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        Adapter adapter = new Adapter(getApplicationContext(), mUrls, SAVED);
        recyclerView.setAdapter(adapter);
    }
}