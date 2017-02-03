package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = (ListView) findViewById(R.id.listView);
        List<HistoryItem> samples = generateSample();

        HistoryAdapter adapter = new HistoryAdapter(HistoryActivity.this, samples);
        listView.setAdapter(adapter);
    }

    private List<HistoryItem> generateSample()
    {
        List<HistoryItem> samples = new ArrayList<>();
        samples.add(new HistoryItem("01/02/2017 19h30","Paris","10 km","47:18",null));
        samples.add(new HistoryItem("30/01/2017 07h44","Paris","9.5 km","43:56",null));
        samples.add(new HistoryItem("27/01/2017 12h34","Paris","16.7 km","1:23:45",null));
        return samples;
    }

    public class HistoryAdapter extends ArrayAdapter<HistoryItem> {

        HistoryAdapter(Context context, List<HistoryItem> history) {
            super(context, 0, history);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_history, parent, false);
            }

            HistoricViewHolder viewHolder = (HistoricViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new HistoricViewHolder();
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                viewHolder.place = (TextView) convertView.findViewById(R.id.place);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                //viewHolder.mapFragment = (SimpleMapFragment) getSupportFragmentManager().findFragmentById(R.id.historyMap);
                convertView.setTag(viewHolder);
            }

            HistoryItem history = getItem(position);

            viewHolder.date.setText(history.getDate());
            viewHolder.distance.setText(history.getDistance());
            viewHolder.place.setText("- "+history.getPlace());
            viewHolder.time.setText(history.getTime());
            /*if(history.getRoute() != null){
                viewHolder.mapFragment.drawnPolyline(history.getRoute().getPolylineOptions());
                viewHolder.mapFragment.waitToAnimateCamera(history.getRoute().getBounds());
            }*/
            return convertView;

        }

        private class HistoricViewHolder {
            public TextView date;
            public TextView distance;
            public TextView place;
            public TextView time;
            public SimpleMapFragment mapFragment;
        }
    }
}

