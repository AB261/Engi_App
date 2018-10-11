package com.example.lenovo.engineer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FavouriteScheduleFragment extends Fragment {
    private static String TAG = "FavouriteScheduleFragment";
    private RecyclerView mRecyclerView;
    private FavListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mSharedPrefFile = "com.example.android.engineer";

    public FavouriteScheduleFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(getActivity()!=null){
            getActivity().setTitle("Favourites");
        }
        View rootView = inflater.inflate(R.layout.fragment_favourite,container,false);
        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FavListAdapter(getActivity());
        SharedPreferences mPreferences = getActivity().getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE);
        Map<String, ?> keys = mPreferences.getAll();
        if (keys.size() != 0) {
            rootView.findViewById(R.id.favourites_check).setVisibility(View.GONE);
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
        return rootView;
    }
}
