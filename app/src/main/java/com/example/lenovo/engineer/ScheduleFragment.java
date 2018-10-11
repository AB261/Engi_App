package com.example.lenovo.engineer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    ScheduleListAdapter day1Adapter, day2Adapter, day3Adapter, day4Adapter, day0Adapter;
    private String sharedPrefFile = "com.example.android.engineer";
    private SharedPreferences mPreferences;
    private static int previousTab = 0;
    private RecyclerView recyclerView;

    public ScheduleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Read Shared Preference
        if(getActivity()!=null)
            mPreferences = getActivity().getApplicationContext().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        viewPager = rootView.findViewById(R.id.schedule_vp_container);
        tabLayout = rootView.findViewById(R.id.schedule_tl_tabs);
        progressBar = rootView.findViewById(R.id.schedule_list_pb_progress);
        tabLayout.setupWithViewPager(viewPager);
        //day5Adapter = new ScheduleListAdapter(getActivity());
        if(getActivity()!=null)
            getActivity().setTitle("Schedule");
        makeRequest();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        previousTab = viewPager.getCurrentItem();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        day0Adapter.updateList();
        day1Adapter.updateList();
        day2Adapter.updateList();
        day3Adapter.updateList();
        day4Adapter.updateList();
        Log.d(TAG, "OnResume");
    }

    public void makeRequest() {
        day0Adapter = new ScheduleListAdapter(getActivity());
        day1Adapter = new ScheduleListAdapter(getActivity());
        day2Adapter = new ScheduleListAdapter(getActivity());
        day3Adapter = new ScheduleListAdapter(getActivity());
        day4Adapter = new ScheduleListAdapter(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest listRequest = new JsonArrayRequest(
                Request.Method.GET,
                getString(R.string.EVENT_LIST_URL),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "listRequest success Volley : " + response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            Entry entry = new Entry();
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
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
                                                .replace("\\/", "/")
                                );
                                entry.setRegister_link(
                                        jsonObject.getString("register_link")
                                                .replace("\\/", "/")
                                );
                                if (entry.getContent().equals("null") || entry.getContent().equals("")) {
                                    entry.setContent("Engineer 2018");
                                }
                                if (mPreferences.getString(String.valueOf(entry.getID()), "b").equals("b")) {
                                    entry.setLiked(false);
                                } else {
                                    entry.setLiked(true);
                                }
                            } catch (JSONException error) {
                                Log.e(TAG, "JSON error " + error.getMessage());
                            }
                            switch (entry.getDay()) {
                                case 0:
                                    day0Adapter.addEntry(entry);
                                    break;
                                case 1:
                                    day1Adapter.addEntry(entry);
                                    break;
                                case 2:
                                    day2Adapter.addEntry(entry);
                                    break;
                                case 3:
                                    day3Adapter.addEntry(entry);
                                    break;
                                case 4:
                                    day4Adapter.addEntry(entry);
                                    break;
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        viewPager.setAdapter(new CustomPagerAdapter(getActivity()));
                        viewPager.setCurrentItem(previousTab);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "listRequest Volley error : " + error.getMessage());
                        Snackbar.make(viewPager, "Connection lost.", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        makeRequest();
                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.yellow))
                                .show();
                    }
                }
        );
        VolleyHelper.getInstance(getContext()).addToRequestQueue(listRequest);
    }

    class CustomPagerAdapter extends PagerAdapter {
        private static final int DAY0 = 0;
        private static final int DAY1 = 1;
        private static final int DAY2 = 2;
        private static final int DAY3 = 3;
        private static final int DAY4 = 4;
        private String titles[] = {"Day 0", "Day 1", "Day 2", "Day 3", "Day 4"};
        private int layouts[] = {R.layout.schedule_list_1,
                R.layout.schedule_list_2,
                R.layout.schedule_list_3,
                R.layout.schedule_list_4,
                R.layout.schedule_list_5};
        private Context context;

        CustomPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(layouts[position], container, false);
            container.addView(rootView);
            recyclerView = rootView.findViewById(R.id.schedule_list_rv);
            switch (position) {
                case DAY0:
                    recyclerView.setAdapter(day0Adapter);
                    break;
                case DAY1:
                    recyclerView.setAdapter(day1Adapter);
                    break;
                case DAY2:
                    recyclerView.setAdapter(day2Adapter);
                    break;
                case DAY3:
                    recyclerView.setAdapter(day3Adapter);
                    break;
                case DAY4:
                    recyclerView.setAdapter(day4Adapter);
                    break;
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            return rootView;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return o == view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
