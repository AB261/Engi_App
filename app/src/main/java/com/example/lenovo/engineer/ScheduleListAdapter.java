package com.example.lenovo.engineer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {
    private List<Entry> entryList = new ArrayList<>();
    private Context context;
    private static SharedPreferences mPreferences;
    private static String TAG = "ScheduleListAdapter";
    private static String sharedPrefFile = "com.example.android.engineer";

    ScheduleListAdapter(Context context) {
        this.context = context;
        mPreferences = context.getApplicationContext().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fa-regular-400.ttf");
        TextView name, time, content, location;
        TextView like;


        ViewHolder(View viewHolder) {
            super(viewHolder);
            name = viewHolder.findViewById(R.id.list_item_tv_name);
            time = viewHolder.findViewById(R.id.list_item_tv_time);
            location = viewHolder.findViewById(R.id.list_item_tv_location);
            content = viewHolder.findViewById(R.id.list_item_tv_content);
            like = viewHolder.findViewById(R.id.list_item_iv_like);
            like.setTypeface(font);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.list_view_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Typeface font1 = Typeface.createFromAsset(context.getAssets(), "fa-regular-400.ttf");
        Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fa-solid-900.ttf");
        final SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        viewHolder.name.setText(entryList.get(position).getName());
        viewHolder.time.setText(entryList.get(position).getTime());
        viewHolder.location.setText(entryList.get(position).getLocation());
//        viewHolder.location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MapsFragment mapsFragment = new MapsFragment();
//                Bundle bundle = new Bundle();
//                String location = viewHolder.location.getText().toString().toLowerCase();
//                /*
//                 Add checks here to call the correct location
//
//                 * name - indicates the name displayed when user clicks on marker
//                 * location - is and int which is used to get desired LatLng object.
//                 */
//                if(location.contains("NTB")) {
//                    bundle.putString("name","NTB");
//                    bundle.putInt("location",MapsFragment.NTB);
//                } else if(location.contains("ATB")){
//                    bundle.putString("name","ATB");
//                    bundle.putInt("location",MapsFragment.ATB);
//                } else if(location.contains("ISTE Seminar Hall")
//                        || location.contains("MB")
//                        || location.contains("Main Building")
//                        || location.contains("Main Seminar Hall")){
//                    bundle.putString("name","Main Building");
//                    bundle.putInt("location",MapsFragment.MAIN_BUILDING);
//                } else if(location.contains("Pavilion")){
//                    bundle.putString("name","Pavilion");
//                    bundle.putInt("location",MapsFragment.PAVILION);
//                } else if(location.contains("CCC")){
//                    bundle.putString("name","CCC");
//                    bundle.putInt("location",MapsFragment.CCC);
//                } else if(location.contains("SJA")){
//                    bundle.putString("name","SJA");
//                    bundle.putInt("location",MapsFragment.SJA);
//                } else if(location.contains("SAC")){
//                    bundle.putString("name","SAC");
//                    bundle.putInt("location",MapsFragment.SAC);
//                } else if(location.contains("Sports Complex")){
//                    bundle.putString("name","New Sports Block");
//                    bundle.putInt("location",MapsFragment.NEW_SPORTS_BLOCK);
//                }
//                mapsFragment.setArguments(bundle);
//                ((AppCompatActivity)context).getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.main_menu_fl_container,mapsFragment)
//                        .addToBackStack("MapsFragment")
//                        .commit();
//            }
//        });
        viewHolder.content.setText(entryList.get(position).getContent());
        if (entryList.get(viewHolder.getAdapterPosition()).isLiked()) {
            viewHolder.like.setTypeface(font2);
        } else {
            viewHolder.like.setTypeface(font1);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryDialog dialogFragment =
                        EntryDialog.getInstance(entryList.get(viewHolder.getAdapterPosition()));
                dialogFragment.setCancelable(true);
                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(),
                        "entry_dialog");
            }
        });
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Typeface font1 =
                        Typeface.createFromAsset(context.getAssets(), "fa-regular-400.ttf");
                Typeface font2 =
                        Typeface.createFromAsset(context.getAssets(), "fa-solid-900.ttf");

                if (!entryList.get(viewHolder.getAdapterPosition()).isLiked()) {
                    viewHolder.like.setTypeface(font2);
                    entryList.get(viewHolder.getAdapterPosition()).setLiked(true);
                    String json = GsonHelper.getInstance().getGson()
                            .toJson(entryList.get(viewHolder.getAdapterPosition()));
                    preferencesEditor.putString(
                            String.valueOf(entryList.get(viewHolder.getAdapterPosition()).getID()),
                            json
                    );
                    preferencesEditor.apply();

                } else {
                    viewHolder.like.setTypeface(font1);
                    entryList.get(viewHolder.getAdapterPosition()).setLiked(false);
                    preferencesEditor.remove(
                            String.valueOf(entryList.get(viewHolder.getAdapterPosition()).getID()));
                    preferencesEditor.apply();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public void addEntry(Entry entry) {
        entryList.add(entry);
        notifyItemInserted(entryList.size());
    }

    public void updateList(){
        for(int i=0; i<entryList.size(); i++){
            if(mPreferences.getString(String.valueOf(entryList.get(i).getID()),"nope").equals("nope")) {
                entryList.get(i).setLiked(false);
            } else{
                entryList.get(i).setLiked(true);
            }
            notifyItemChanged(i);
        }
    }
}
