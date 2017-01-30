package com.example.raphaelattali.rythmrun;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raphaelattali.rythmrun.activities.gui.LibraryActivity;
import com.example.raphaelattali.rythmrun.music.Song;

import java.util.ArrayList;
import java.util.List;

public class HistoriqueActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ListView mListView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        mListView = (ListView) findViewById(R.id.listView);
        List<HistoricItem> tweets = genererHistorics();

        HistoricAdapter adapter = new HistoricAdapter(HistoriqueActivity.this, tweets);
        mListView.setAdapter(adapter);
    }

    private List<HistoricItem> genererHistorics()
    {
        List<HistoricItem> tweets = new ArrayList<HistoricItem>();
        tweets.add(new HistoricItem(Color.BLACK, "01-01-2017", "24km"));
        tweets.add(new HistoricItem(Color.BLUE,"01-02-2017", "50km"));
        tweets.add(new HistoricItem(Color.BLUE,"30-01-2015", "10km"));
        tweets.add(new HistoricItem(Color.RED,"30-05-2015", "12km"));
        return tweets;
    }

    public class HistoricAdapter extends ArrayAdapter<HistoricItem> {

        HistoricAdapter(Context context, List<HistoricItem> historic) {
            super(context, 0, historic);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_historique, parent, false);
            }

            HistoricViewHolder viewHolder = (HistoricViewHolder) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new HistoricViewHolder();
                viewHolder.date = (TextView) convertView.findViewById(R.id.date);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            HistoricItem historic = getItem(position);

            //il ne reste plus qu'à remplir notre vue
            viewHolder.date.setText(historic.getDate());
            Log.d("historic","Date: "+historic.getDate());
            viewHolder.distance.setText(historic.getDistance());
            viewHolder.image.setImageDrawable(new ColorDrawable(historic.getColor()));

            return convertView;

        }

        private class HistoricViewHolder {
            public TextView date;
            public TextView distance;
            public ImageView image;
        }
    }
}

