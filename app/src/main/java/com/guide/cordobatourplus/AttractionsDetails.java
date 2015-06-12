package com.guide.cordobatourplus;

import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

/**
 * Attraction details
 */
public class AttractionsDetails extends ActionBarActivity {

    public static int position;
    public static Attraction attraction;
    private FragmentTabHost mTabHost;

    public static TextToSpeech ttobjet = null;
    public static boolean audioNeverPlayed = true;
    public static boolean audioPlaying = false;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attractions_details);
        position = Integer.valueOf(getIntent().getStringExtra("position"));
        attraction = AttractionsFragment.attractions.get(position);
        setTitle(AttractionsFragment.attractions.get(position).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("Details", null),
                AttractionDetailTab.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("Map", null),
                AttractionMapTab.class, null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (!audioNeverPlayed) {
                    ttobjet.shutdown();
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_attractions_details, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!audioNeverPlayed)
            ttobjet.shutdown();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                if(!audioNeverPlayed)
                    ttobjet.shutdown();
                onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStop()
    {
        if(!audioNeverPlayed) {
            audioPlaying = false;
            ttobjet.shutdown();
        }
        super.onStop();
    }

}
