package com.guide.cordobatourplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pushbots.push.Pushbots;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    /**
     * Getter for HOME
     * @return HOME
     */
    public int getHOME() {
        return HOME;
    }

    /**
     * Getter for EVENT
     * @return EVENT
     */
    public int getEVENT() {
        return EVENT;
    }

    /**
     * Getter for NEWS
     * @return NEWS
     */
    public int getNEWS() {
        return NEWS;
    }

    /**
     * Getter for ATTRACTIONS
     * @return ATTRACTIONS
     */
    public int getATTRACTIONS() {
        return ATTRACTIONS;
    }

    /**
     * HOME position in the drawer
     */
    private final int HOME = 0;

    /**
     * EVENT position in the drawer
     */
    private final int EVENT = 1;

    /**
     * NEWS position in the drawer
     */
    private final int NEWS = 2;

    /**
     * ATTRACTIONS position in the drawer
     */
    private final int ATTRACTIONS = 3;

    /**
     * Progress bar for the initial loading
     */
    public ProgressDialog progress;

    /**
     * The toggle listener
     */
    public ActionBarDrawerToggle drawerListener;

    /**
     * Default fragment
     */
    Fragment fragment = new HomeFragment();

    /**
     * Fragment Manager used to configure the behaviour of the framgments
     */
    FragmentManager fragmentManager = getSupportFragmentManager();

    /**
     * The drawer layout used to display the main menu at a side of the screen
     */
    private DrawerLayout drawerLayout;

    /**
     * ListView used to display the menu options on the NavigationDrawer
     */
    private ListView listView;

    /**
     * Menu options
     */
    private String[] elementos = {
            "Home",
            "Events",
            "News",
            "Tourist Attractions"
    };

    /**
     * Custom adapter used by the listview
     */
    private MyAdapter myAdapter;

    /**
     * An AsyncTask class that fetchs the initial necesary data for the program
     */
    private AppDataFetcher dataFetcher;

    /**
     * MainActivity's context so it can be used on fragments if getActivity method isn't available
     */
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Pushbots.sharedInstance().init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadResources();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.drawerList);
        myAdapter = new MyAdapter(this);
        setListViewConfig();
        setActionBarDrawerToggle();
        setDrawerUpButton();
        drawerLayout.setVisibility(View.INVISIBLE);
        setTitle(getResources().getString(R.string.home));
        dataFetcher = new AppDataFetcher();
        dataFetcher.execute();
    }

    private void loadResources() {
        elementos[0] = getResources().getString(R.string.home);
        elementos[1] = getResources().getString(R.string.events);
        elementos[2] = getResources().getString(R.string.news);
        elementos[3] = getResources().getString(R.string.attractions);
    }

    /**
     * AsynTask custom class used to fetch the app initial data
     */
    class AppDataFetcher extends AsyncTask<String, Void, String> {
        public AppDataFetcher() {
            progress = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if(isNetworkAvailable()) {
                    downloadXML(getResources().getString(R.string.news_url), "news.xml");
                    downloadXML(getResources().getString(R.string.events_url), "eventos.xml");
                    downloadXML(getResources().getString(R.string.config_url),"config.xml");
                }
            }catch(Exception ex) {}
            return "ok";
        }

        @Override
        protected void onPreExecute() {
            progress.setMessage(getResources().getString(R.string.load_dialog_text));
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.dismiss();
            }
            drawerLayout.setVisibility(View.VISIBLE);
            setDefaultFragment();
        }
    }

    /**
     * Sets a toggle for the action bar drawer
     */
    private void setActionBarDrawerToggle() {
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                //Toast.makeText(MainActivity.this, "Menu abierto", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Toast.makeText(MainActivity.this,"Menu cerrado", Toast.LENGTH_LONG).show();
            }
        };

        drawerLayout.setDrawerListener(drawerListener);
        drawerListener.syncState();
    }

    /**
     * Confiures the ListView's configuration
     */
    private void setListViewConfig() {
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);
        listView.setDividerHeight(0);
    }

    /**
     * Enables the hamburguer icon on the drawer action bar
     */
    private void setDrawerUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Sets the default fragment of the app
     */
    public void setDefaultFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment).commit();
        listView.setItemChecked(0, true);
        setTitle(elementos[0]);
        drawerLayout.closeDrawer(listView);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    /**
     * Selects an item on the drawer based on a position
     * @param position Item position
     */
    public void selectItem(int position) {
        Intent intent;
        Fragment eventFragment = new EventFragment();
        Fragment homeFrament = new HomeFragment();
        Fragment newsFragment = new NewsFragment();
        Fragment attractionFragment = new AttractionsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case HOME:
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, homeFrament).commit();
                UIDrawerTransition(position);
                break;
            case EVENT:
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, eventFragment).commit();
                UIDrawerTransition(position);
                break;
            case NEWS:
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, newsFragment).commit();
                UIDrawerTransition(position);
                break;
            case ATTRACTIONS:
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, attractionFragment).commit();
                UIDrawerTransition(position);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    /**
     * Does change the selected drawer option menu and set the adecuate title
     * @param position
     */
    public void UIDrawerTransition(int position) {
        listView.setItemChecked(position, true);
        setTitle(elementos[position]);
        drawerLayout.closeDrawer(listView);
    }

    /**
     * Open the Event Fragment
     * @param v Clicked view
     */
    public void openEventFragment(View v) {
        selectItem(EVENT);
    }

    /**
     * Open the Attraction Fragment
     * @param v Clicked view
     */
    public void openAttractionsFragment(View v) {selectItem(ATTRACTIONS);}

    /**
     * Set action bar's title
     * @param title
     */
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Change the drawer configuration for a new one
     * @param newConfig The new configuration to take changes from
     */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Download an XML file properly with a 5 seconds connect timeout and 15 seconds read item out
     * @param fileUrl The file URL
     * @param filename The name you want to give to the downloaded XML
     */
    public void downloadXML(String fileUrl, String filename) {
        try {
            URL url = new URL(fileUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(15000);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            urlConnection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder("");
            while ((inputLine = in.readLine()) != null)
                content.append(inputLine);
            in.close();

            File file = new File(filename);
            String strContent = content.toString();
            FileOutputStream outputStream;
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(strContent.getBytes());
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getResources().getString(R.string.exit_title))
                .setMessage(getResources().getString(R.string.exit_text))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return true;
            }
        })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Custom adapter needed to display the ListView properly on the navigation drawer
     */
    class MyAdapter extends BaseAdapter {
        String[] contenido = {
                getResources().getString(R.string.home),
                getResources().getString(R.string.events),
                getResources().getString(R.string.news),
                getResources().getString(R.string.attractions)
        };
        int[] iconos = {
                R.drawable.ic_action,
                R.drawable.ic_event,
                R.drawable.ic_news,
                R.drawable.ic_attractions
        };
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return contenido.length;
        }

        @Override
        public Object getItem(int position) {
            return contenido[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.custom_row, parent, false);
            } else {
                row = convertView;
            }

            ImageView iv = (ImageView) row.findViewById(R.id.icono);
            TextView tv = (TextView) row.findViewById(R.id.texto);

            tv.setText(contenido[position]);
            iv.setImageResource(iconos[position]);
            return row;
        }
    }

    /**
     * Checks whether or not the user has an internet conexion available
     * @return True if internet is available or False if it isn't
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
