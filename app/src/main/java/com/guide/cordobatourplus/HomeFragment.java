package com.guide.cordobatourplus;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * This is the default fragment of the application
 */
public class HomeFragment extends Fragment {
    Noticia noticia;
    SimpleDateFormat syncFormat = new SimpleDateFormat("kk:mm:ss dd/MM/yyyy");
    String fechaSync = "";
    ImageView ivLogo;
    private String cName = "";
    private String cLogo = "";
    GetInitialData mTask;
    View v;

    RelativeLayout rlContent;
    ProgressBar pgBar;
    public ProgressDialog progress;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        v = inflater.inflate(R.layout.home_fragment, container, false);
        context = getActivity();
        ivLogo = (ImageView) v.findViewById(R.id.imageView);
        rlContent = (RelativeLayout) v.findViewById(R.id.container);
        pgBar = (ProgressBar) v.findViewById(R.id.pgBar);
        mTask = new GetInitialData(this);
        mTask.execute();

        return v;
    }

    /**
     * Fetches the initial data
     */
    class GetInitialData extends AsyncTask<String, Void, String> {
        private HomeFragment container;
        public GetInitialData(HomeFragment f) {
            container = f;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                getXmlNewsData();
                getXmlConfigData();

            }catch(Exception ex) {}
            return "ok";
        }

        @Override
        protected void onPreExecute() {
            rlContent.setVisibility(View.INVISIBLE);
            pgBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            adaptFragmentViews(v);
            if(cLogo != "") {
                ivLogo.setImageURI(Uri.parse(getActivity().getFilesDir() + "/logo.jpg"));
            }
            rlContent.setVisibility(View.VISIBLE);
            pgBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Download an XML file
     * @param fileUrl URL
     * @param filename Output file name
     */
    public void downloadXML(String fileUrl, String filename) {
        try {
            URL url = new URL(fileUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setConnectTimeout(8000);
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
            outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(strContent.getBytes());
            outputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        if (cLogo != "" )
            ivLogo.setImageURI(null);
        noticia = null;
        syncFormat = null;
        fechaSync = null;
        cName = null;
        cLogo = null;
        super.onDestroyView();
    }

    /**
     * Does change the home layout based on the existance of news data
     * @param v The inflated view
     */
    private void adaptFragmentViews(View v) {
        String filePath = context.getFilesDir() + "/" + "news.xml";
        File file = new File(filePath);
        if(file.exists())
            setViewsData(v);
        else {
            removeNonDefaultViews(v);
        }
    }

    /**
     * Removes some of the views that only makes sense to appear if there is data available
     * @param v The inflated layout
     */
    private void removeNonDefaultViews(View v) {
        CardView cView = (CardView) v.findViewById(R.id.cView);
        ViewGroup vg = (ViewGroup)(cView.getParent());
        vg.removeView(cView);
        TextView tvSeeMore = (TextView) v.findViewById(R.id.tvSeeMore);
        vg = (ViewGroup)(tvSeeMore.getParent());
        vg.removeView(tvSeeMore);

        TextView tvLastNews = (TextView) v.findViewById(R.id.tvLastNews);
        tvLastNews.setText("No news at the moment!");

        ImageView ivLogo = (ImageView) v.findViewById(R.id.imageView);
        vg = (ViewGroup) ivLogo.getParent();
        vg.removeView(ivLogo);

        View vHr = v.findViewById(R.id.vHr);
        vg = (ViewGroup) vHr.getParent();
        vg.removeView(vHr);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh) {
            if (isNetworkAvailable()) {
                ResfreshAppDataFetcher dataFetcher = new ResfreshAppDataFetcher();
                dataFetcher.execute();

                mTask = new GetInitialData(this);
                mTask.execute();
            } else {
                Toast.makeText(getActivity(), "Enable your internet conexion first",Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes the whole app by downloading all necesary files once again
     */
    class ResfreshAppDataFetcher extends AsyncTask<String, Void, String> {
        public ResfreshAppDataFetcher() {
            progress = new ProgressDialog(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if(isNetworkAvailable()) {
                    downloadXML(context.getResources().getString(R.string.news_url), "news.xml");
                    downloadXML(context.getResources().getString(R.string.events_url), "eventos.xml");
                    downloadXML(context.getResources().getString(R.string.config_url),"config.xml");
                }
            }catch(Exception ex) {}
            return "ok";
        }

        @Override
        protected void onPreExecute() {
            progress.setMessage(context.getResources().getString(R.string.load_dialog_text));
            progress.show();
            super.onPreExecute();
            //container.showProgressBar();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.dismiss();
            }
            // The activity can be null if it is thrown out by Android while task is running!
        }
    }


    /**
     * Check if internet is available on the device
     * @return True if internet is available or False if it isn't
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Set the layout views with the news data
     * @param v The inflated view
     */
    private void setViewsData(View v) {
        TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvDate.setText(noticia.getDate());
        TextView tvTime = (TextView) v.findViewById(R.id.tvTime);
        tvTime.setText(noticia.getTime());
        TextView tvContent = (TextView) v.findViewById(R.id.tvContent);
        tvContent.setText(noticia.getInfo());
        TextView tvAuthor = (TextView) v.findViewById(R.id.tvAuthor);
        tvAuthor.setText(noticia.getAuthor());
        TextView tvSeeMore = (TextView) v.findViewById(R.id.tvSeeMore);
        tvSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = getActivity().getFilesDir() + "/" + "news.xml";
                File file = new File(filePath);
                if(file.exists()) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.mainContent, new NewsFragment()).commit();
                    ((MainActivity)getActivity()).UIDrawerTransition(((MainActivity)getActivity()).getNEWS());
                } else {
                    Toast.makeText(getActivity(), "No news found, perhaps you never opened the app with an internet conexion?",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Load the configuration data
     */
    public void getXmlConfigData() {
        String filePath = getActivity().getFilesDir() + "/" + "config.xml";
        File file = new File(filePath);
        String ultMod = syncFormat.format(file.lastModified());
        fechaSync = ultMod;

        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s= new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            parseConfigXML(parser);

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load news data
     */
    public void getXmlNewsData() {
        String filePath = getActivity().getFilesDir() + "/" + "news.xml";
        File file = new File(filePath);
        String ultMod = syncFormat.format(file.lastModified());
        fechaSync = ultMod;

        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s= new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            parseNewsXML(parser);

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse the configuration data
     * @param parser The XmlPullParser
     * @throws XmlPullParserException If the XML file has errors
     * @throws IOException If reading fails
     */
    private void parseConfigXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "name":
                            cName = parser.nextText();
                            break;
                        case "logo":
                            cLogo = parser.nextText();
                            break;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    name = parser.getName();
            }
            eventType = parser.next();
        }
        URL url = new URL (cLogo);
        InputStream input = url.openStream();
        try {
            FileOutputStream output = getActivity().openFileOutput("logo.jpg", Context.MODE_PRIVATE);
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                output.close();
            }
        } finally {
            input.close();
        }

    }

    /**
     * Parse the news data
     * @param parser The XmlPullParser
     * @throws XmlPullParserException If the XML file has errors
     * @throws IOException If reading fails
     */
    private void parseNewsXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        int eventType = parser.getEventType();

        String fecha = "";
        String hora = "";
        String texto = "";
        String autor = "";

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            Boolean once = false;
            if (!once) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        switch (name) {
                            case "noticia":
                                break;
                            case "fecha":
                                fecha = parser.nextText();
                                break;
                            case "hora":
                                hora = parser.nextText();
                                break;
                            case "texto":
                                texto = parser.nextText();
                                break;
                            case "autor":
                                autor = parser.nextText();
                                noticia = new Noticia(fecha, hora, texto, autor);
                                once = true;
                                break;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                }
            }
            if (!once)
                eventType = parser.next();
        }
    }


}