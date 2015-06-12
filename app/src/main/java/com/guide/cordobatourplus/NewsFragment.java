package com.guide.cordobatourplus;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class NewsFragment extends Fragment {

    public static List<Noticia> news = new ArrayList<Noticia>();
    Noticia noticia;
    SimpleDateFormat syncFormat = new SimpleDateFormat("kk:mm:ss dd/MM/yyyy");
    String fechaSync = "";
    String minDate = null;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.news_fragment, container, false);
        news.clear();
        getXmlData();
        Collections.sort(news);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyNewsAdapter(news);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onDestroyView() {
        news = new ArrayList<Noticia>();
        mAdapter = null;
        mRecyclerView = null;
        mLayoutManager = null;
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_calendary) {

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Get the XML Data
     */
    public void getXmlData() {
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
            parseXML(parser);

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse the XML
     * @param parser The parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        int eventType = parser.getEventType();

        String fecha = "";
        String hora = "";
        String texto = "";
        String autor = "";

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        switch (name) {
                            case "noticia":

                                break;
                            //Log.d("Noticia", "Soy una noticia");
                            case "fecha":
                                fecha = parser.nextText();
                                //Log.d("Fecha", "Soy una Fecha:" + parser.nextText());
                                break;
                            case "hora":
                                hora = parser.nextText();
                                //Log.d("hora", "Soy una Hora " + parser.nextText());
                                break;
                            case "texto":
                                texto = parser.nextText();
                                //Log.d("texto", "texto: " + parser.nextText());
                                break;
                            case "autor":
                                autor = parser.nextText();

                                //Log.d("Autor", "Soy un autor " + parser.nextText());
                                break;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        name = parser.getName();

                        switch (name) {
                            case "noticia":
                                if (this.minDate == null) {
                                    Log.d("No hay mindate", "No hay mindate: " + minDate);
                                    news.add(new Noticia(fecha, hora, texto, autor));
                                }
                                else {
                                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        Date mMinDate = df.parse(minDate);
                                        Date newsDate = df.parse(fecha);

                                        if (newsDate.after(mMinDate)) {
                                            Log.d("Va despues","Va desppues");
                                            news.add(new Noticia(fecha, hora, texto, autor));
                                            mAdapter.notifyDataSetChanged();
                                        } else if (!newsDate.after(mMinDate) && !newsDate.before(mMinDate)) {
                                            news.add(new Noticia(fecha, hora, texto, autor));
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } catch (ParseException e) {
                                        Log.e("Formato fecha error", e.getMessage());
                                    }
                                }
                        }
                }
            eventType = parser.next();
        }
    }

    /**
     * News adapter
     */
    class MyNewsAdapter extends RecyclerView.Adapter<MyNewsAdapter.ViewHolder> {
        private List<Noticia> mDataset;


        public MyNewsAdapter(List<Noticia> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyNewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_row, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvDate.setText(news.get(position).getDate());
            holder.tvTime.setText(news.get(position).getTime());
            holder.tvContent.setText(news.get(position).getInfo());
            holder.tvAuthor.setText(news.get(position).getAuthor());

        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView tvDate;
            public TextView tvTime;
            public TextView tvContent;
            public TextView tvAuthor;

            public ViewHolder(View v) {
                super(v);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                tvTime = (TextView) v.findViewById(R.id.tvTime);
                tvContent = (TextView) v.findViewById(R.id.tvContent);
                tvAuthor = (TextView) v.findViewById(R.id.tvAuthor);
            }
        }


    }
}
