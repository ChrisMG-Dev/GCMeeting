package com.guide.cordobatourplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class EventDetails extends ActionBarActivity {

    public static Evento event;
    private int position;
    private final int SHOW = 1;
    private final int HIDE = 2;
    private int contactMenuState = SHOW;
    private int mapMenuState = SHOW;
    private int streamMenuState = SHOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        position = Integer.valueOf(getIntent().getStringExtra("position"));
        event = EventFragment.eventos.get(position);

        setTitle("Details");

        if (event.getResName().equals("") && event.getResPhone().equals("")) {
            contactMenuState = HIDE;
            invalidateOptionsMenu();
        }

        if (event.getAltitude().equals("") || event.getLongitude().equals("")) {
            mapMenuState = HIDE;
            invalidateOptionsMenu();
        }

        if (event.getStreamingUrl().equals("")) {
            streamMenuState = HIDE;
            invalidateOptionsMenu();
        }

        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(event.getDate());

        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setText(event.getTime());

        TextView tvPlace = (TextView) findViewById(R.id.tvPlace);

        String address = "";
        if (event.getPlace() != "" && event.getAddress() != "") {
            address = event.getPlace() + " - " + event.getAddress();
        } else if (event.getPlace() != "") {
            address = event.getPlace();
        } else if (event.getAddress() != "") {
            address = event.getAddress();
        }

        if (address != "")
            tvPlace.setText(address);
        else
            tvPlace.setText("Address not specified");

        TextView tvActivities = (TextView) findViewById(R.id.tvActivities);
        tvActivities.setText("");
        for(String activity: event.getActivities()) {
            tvActivities.setText(tvActivities.getText() + "-" + activity + System.getProperty("line.separator"));
        }

        Button btnVote = (Button) findViewById(R.id.btnVote);
        if (!event.isHasVoteButton()) {
            ViewGroup vg = (ViewGroup)(btnVote.getParent());
            vg.removeView(btnVote);
        } else {
            btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.vote_url))));
                }
            });
        }
    }

    /**
     * Show a contact dialog to the user
     */
    public void showContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(!event.getResName().equals("") && !event.getResPhone().equals("")) {
            builder.setMessage("Person in charge: " + event.getResName()
                    + System.getProperty("line.separator") + "Phone: " + event.getResPhone());
        }
        else if (!event.getResName().equals("")) {
            builder.setMessage("Person in charge: " + event.getResName());
        }
        else if (!event.getResPhone().equals("")) {
            builder.setMessage("Phone: " + event.getResPhone());
        }else  {
            builder.setMessage("No information provided");
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("Contact Information");
        alert.show();
    }

    /**
     * Check if internet is available
     * @return True or False
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        if (contactMenuState == HIDE) {
            menu.getItem(0).setVisible(false);
        }
        if (mapMenuState == HIDE) {
            menu.getItem(2).setVisible(false);
        }
        if (streamMenuState == HIDE) {
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_contact:
                if (event.getResName().equals("") && event.getResPhone().equals("")) {
                    Toast.makeText(this, "No contact info were provided", Toast.LENGTH_LONG).show();
                } else
                    showContact();
                return true;
            case R.id.action_stream:
                if (event.getStreamingUrl().equals("")) {
                    Toast.makeText(this, "No streaming available", Toast.LENGTH_LONG).show();
                } else
                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(event.getStreamingUrl())));
                return true;
            case R.id.action_map:
                if (isNetworkAvailable()) {
                    if (!event.getAltitude().equals("") && !event.getLongitude().equals("")) {
                        Intent intent = new Intent(this, EventDetailsMap.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "No coordinates were provided", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "An internet connection is required", Toast.LENGTH_LONG).show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
