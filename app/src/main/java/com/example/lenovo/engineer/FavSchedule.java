package com.example.lenovo.engineer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FavSchedule extends AppCompatActivity {
    private static String TAG = "Fav_Schedule";
    private RecyclerView mRecyclerView;
    private FavListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mSharedPrefFile = "com.example.android.engineer";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_sc);
        Toolbar toolbar = findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.showOverflowMenu();
        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FavListAdapter(this);
        SharedPreferences mPreferences = getSharedPreferences(mSharedPrefFile, MODE_PRIVATE);
        Map<String, ?> keys = mPreferences.getAll();
        if (keys.size() != 0) {
            findViewById(R.id.favourites_check).setVisibility(View.GONE);
        }
        for (Map.Entry<String, ?> entry1 : keys.entrySet()) {
            Log.d("map values", entry1.getKey() + ": " +
                    entry1.getValue().toString());
            Entry entry = new Entry();
            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(entry1.getValue()));
                Log.d(TAG, jsonObject.toString());
                entry.setDay(jsonObject.getInt("Day"));
                entry.setID(jsonObject.getInt("ID"));
                entry.setContent(jsonObject.getString("Content"));
                entry.setLocation(jsonObject.getString("Location"));
                entry.setTime(jsonObject.getString("Time"));
                entry.setRegister_event(jsonObject.getInt("register_event"));
                entry.setCommittee(jsonObject.getString("Committee"));
                entry.setName(jsonObject.getString("Name"));
                entry.setImage(
                        jsonObject.getString("Image")
                                .replace("\\/","/")
                );
                entry.setRegister_link(
                        jsonObject.getString("register_link")
                                .replace("\\/","/")
                );
                entry.setLiked(true);
            } catch (JSONException error) {
                Log.e(TAG, "JSON error " + error.getMessage());
            }
            mAdapter.addEntry(entry);
        }

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
