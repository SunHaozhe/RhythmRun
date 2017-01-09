package com.example.raphaelattali.rythmrun.activities.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.raphaelattali.rythmrun.R;

import layout.ItineraryFragment;
import layout.PaceFragment;
import layout.MusicFragment;

public class NewRunActivity extends AppCompatActivity {

    public static final String EXTRA_DISTANCE="distance";
    public static final String EXTRA_PACE="pace";
    public static final String EXTRA_MUSIC="music";

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getActionBar().setDisplayHomeAsUpEnabled(true);*/

        viewPager = (ViewPager) findViewById(R.id.vpNewRun);
        tabLayout = (TabLayout) findViewById(R.id.tlNewRun);

        // Fragment manager to add fragment in viewpager we will pass object of Fragment manager to adpater class.
        FragmentManager manager=getSupportFragmentManager();

        //object of PagerAdapter passing fragment manager object as a parameter of constructor of PagerAdapter class.
        final PagerAdapter adapter=new PagerAdapter(manager);

        //set Adapter to view pager
        viewPager.setAdapter(adapter);

        //set tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager);

        // adding functionality to tab and viewpager to manage each other when a page is changed or when a tab is selected
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Setting tabs from adpater
        tabLayout.setTabsFromPagerAdapter(adapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabNewRun);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecapActivity.class);
                intent.putExtra(EXTRA_DISTANCE, adapter.getItineraryFragment().getDistance());
                intent.putExtra(EXTRA_PACE,adapter.getPaceFragment().getPace());
                intent.putExtra(EXTRA_MUSIC,adapter.getMusicFragment().getMusicStyle());
                startActivity(intent);
            }
        });

    }

    public class PagerAdapter extends FragmentStatePagerAdapter{

        private ItineraryFragment itineraryFragment;
        private PaceFragment paceFragment;
        private MusicFragment musicFragment;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment frag=null;
            switch (position){
                case 0:
                    itineraryFragment = new ItineraryFragment();
                    return itineraryFragment;
                case 1:
                    paceFragment = new PaceFragment();
                    return paceFragment;
                case 2:
                    musicFragment = new MusicFragment();
                    return musicFragment;
            }
            return null;
        }

        public ItineraryFragment getItineraryFragment() {
            return itineraryFragment;
        }

        public PaceFragment getPaceFragment() {
            return paceFragment;
        }

        public MusicFragment getMusicFragment() {
            return musicFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title=" ";
            switch (position){
                case 0:
                    title="Itinerary";
                    break;
                case 1:
                    title="Pace";
                    break;
                case 2:
                    title="Music";
                    break;
            }
            return title;
        }
    }

}
