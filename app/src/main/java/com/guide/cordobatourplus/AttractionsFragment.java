package com.guide.cordobatourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Attraction fragment
 */
public class AttractionsFragment extends Fragment {

    public static List<Attraction> attractions = new ArrayList<Attraction>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    private RelativeLayout rlMain;
    private ProgressBar pbBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attractions_fragment, container, false);
        rlMain = (RelativeLayout) v.findViewById(R.id.rlMain);
        pbBar = (ProgressBar) v.findViewById(R.id.pgBar);
        attractions.clear();
        getXmlData();
        Collections.sort(attractions);

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAttractionAdapter(attractions);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    /**
     * Get the xml data
     */
    public void getXmlData() {
        InputStream in_s = getResources().openRawResource(R.raw.attractions);
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

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
    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();

        Attraction attraction = new Attraction();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "name":
                            attraction.setName(parser.nextText());
                            Log.d("Name", "name " + attraction.getName());
                            break;
                        case "latitude":
                            attraction.setLatitude(parser.nextText());
                            break;
                        case "longitude":
                            attraction.setLongitude(parser.nextText());
                            //Log.d("Longitude", "longitude " + parser.nextText());
                            break;
                        case "zoom":
                            attraction.setZoom(parser.nextText());
                            //Log.d("Zoom", "zoom " + parser.nextText());
                            break;
                        case "description":
                            attraction.setDescription(parser.nextText());
                            //Log.d("Description", "description " + parser.nextText());
                            break;
                        case "image":
                            attraction.setImage(parser.nextText());
                            //Log.d("Image", "image " + parser.nextText());
                            break;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    name = parser.getName();

                    if (name.equals("attraction")) {
                        attractions.add(attraction);
                        attraction = new Attraction();
                    }
            }
            eventType = parser.next();
        }
    }

    /**
     * Attraction adapter
     */
    class MyAttractionAdapter extends RecyclerView.Adapter<MyAttractionAdapter.ViewHolder> {
        private List<Attraction> mDataset;

        public MyAttractionAdapter(List<Attraction> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAttractionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvName.setText(attractions.get(position).getName());
            final int resourceId = getResources().getIdentifier(attractions.get(position).getImage(), "drawable",
                    getActivity().getPackageName());
            holder.imgThumbnail.setImageResource(resourceId);
            holder.cvItem.setTag(position);
            Log.d("OnBindView", "OnBindViewHolder Position: " + position);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgThumbnail;
            public TextView tvName;
            public CardView cvItem;

            public ViewHolder(View v) {
                super(v);
                imgThumbnail = (ImageView) v.findViewById(R.id.img_thumbnail);
                tvName = (TextView) v.findViewById(R.id.tv_species);
                cvItem = (CardView) v.findViewById(R.id.cardViewItem);
                cvItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AttractionsDetails.class);
                        Log.d("Extra", "Extra v: " + v.getTag());
                        intent.putExtra("position", String.valueOf(v.getTag()));
                        startActivity(intent);
                    }
                });
            }
        }


    }

    @Override
    public void onDestroyView() {
        attractions.clear();
        mAdapter = null;
        mRecyclerView = null;
        mLayoutManager = null;
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
