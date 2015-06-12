package com.guide.cordobatourplus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Event Fragment that shows all events
 */
public class EventFragment extends Fragment {

    public static List<Evento> eventos = new ArrayList<Evento>();
    public List<Evento> allEvents = new ArrayList<Evento>();;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Calendar c = Calendar.getInstance();
    private boolean hasEvents = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.event_fragment, container, false);
        eventos.clear();
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        getXmlData();
        if (hasEvents) {
            Collections.sort(eventos);
            allEvents = new ArrayList<>(eventos);
            mAdapter = new MyEventAdapter(eventos);
            mRecyclerView.setAdapter(mAdapter);
            ((MyEventAdapter)mAdapter).setDateFilter(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
        } else {
            Toast.makeText(getActivity(),"No events available",Toast.LENGTH_LONG).show();
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        //eventos = new ArrayList<Evento>();
        eventos.clear();
        allEvents.clear();
        c = null;
        mAdapter = null;
        mRecyclerView = null;
        mLayoutManager = null;
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_events, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_calendary) {
            if(!hasEvents) {
                Toast.makeText(getActivity(),"No events available",Toast.LENGTH_LONG).show();
            } else {
                openDatePicker();
            }

        } else if (id == R.id.action_all) {
            if(!hasEvents) {
                Toast.makeText(getActivity(),"No events available",Toast.LENGTH_LONG).show();
            } else {
                ((MyEventAdapter) mRecyclerView.getAdapter()).setAllData();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Does put leading zeros on numbers
     * @param number The number which you want to add leading zeros
     * @return The number
     */
    public String putLeadingZeros(int number) {
        return number<=9?"0"+number:String.valueOf(number);
    }

    /**
     * Open a date picker
     */
    public void openDatePicker() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        ((MyEventAdapter) mRecyclerView.getAdapter()).setDateFilter(putLeadingZeros(dayOfMonth) + "/" + putLeadingZeros(monthOfYear + 1)
                                + "/" + putLeadingZeros(year));
                    }
                }, mYear, mMonth, mDay);
        //dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.show();
    }

    /**
     * Get the fragment data
     */
    public void getXmlData() {
        String filePath = getActivity().getFilesDir() + "/" + "eventos.xml";
        File file = new File(filePath);
        if(file.exists()) {
            hasEvents = true;
            InputStream in_s = null;
            try {
                in_s = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //InputStream in_s = getResources().openRawResource(R.raw.eventos);
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
        } else {
            hasEvents = false;
        }
    }

    /**
     * Parse de XML
     * @param parser The XmlPullParser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();

        Evento evento = new Evento();
        ArrayList<String> activities = new ArrayList<>();


        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    switch (name) {
                        case "event":
                            //Log.d("Evento", "Evento");
                            break;
                        case "activity":
                            activities.add(parser.nextText());
                            break;
                        case "latitude":
                            evento.setAltitude(parser.nextText());
                            break;
                        case "longitude":
                            evento.setLongitude(parser.nextText());
                            break;
                        case "zoom":
                            evento.setZoom(parser.nextText());
                            break;
                        case "date":
                            evento.setDate(parser.nextText());
                            break;
                        case "time":
                            evento.setTime(parser.nextText());
                            break;
                        case "place":
                            evento.setPlace(parser.nextText());
                            break;
                        case "address":
                            evento.setAddress(parser.nextText());
                            break;
                        case "name":
                            evento.setResName(parser.nextText());
                            break;
                        case "phone":
                            evento.setResPhone(parser.nextText());
                            break;
                        case "streamingURL":
                            evento.setStreamingUrl(parser.nextText());
                            break;
                        case "vota":
                            evento.setHasVoteButton(Boolean.valueOf(parser.nextText()));
                            break;
                    }
                    break;

                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equals("activities")) {
                        if (activities.size() > 0) {
                            evento.setActivities(
                                    activities.toArray(new String[activities.size()])
                            );
                            activities.clear();
                        }
                    } else if (name.equals("event")) {
                        eventos.add(evento);
                        evento = new Evento();
                    }
            }
            eventType = parser.next();
        }
    }

    /**
     * Event adapter
     */
    class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.ViewHolder> {
        private List<Evento> mDataset;

        public MyEventAdapter(List<Evento> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyEventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_recycle_row, parent, false);

            return new ViewHolder(v);
        }

        public void setDateFilter(String dateFilter) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            mDataset.clear();
            notifyDataSetChanged();
            int j = 0;
            for (int i = 0; i < allEvents.size(); i++) {
                try {
                    Date minDate = df.parse(dateFilter);
                    Date eventDate = df.parse(allEvents.get(i).getDate());

                    if (eventDate.before(minDate)) {

                    } else if (eventDate.after(minDate) || !eventDate.after(minDate) && !eventDate.before(minDate)) {
                        mDataset.add(allEvents.get(i));
                        notifyItemInserted(j);
                        j++;
                    }
                } catch (ParseException e) {
                    Log.e("Formato fecha error", e.getMessage());
                }
            }
        }

        /**
         * Set all the new data to the dataset and notify to the adapter
         */
        public void setAllData() {
            mDataset.clear();
            notifyDataSetChanged();
            int j = 0;
            for (int i = 0; i < allEvents.size(); i++) {
                mDataset.add(allEvents.get(i));
                notifyItemInserted(j);
                j++;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvDate.setText(eventos.get(position).getDate());
            holder.tvTime.setText(eventos.get(position).getTime());
            StringBuilder activities = new StringBuilder("");
            String[] currentActivities = eventos.get(position).getActivities();
            for (String currentActivity : currentActivities) {
                activities.append("-").append(currentActivity).append(System.getProperty("line.separator"));
            }
            holder.tvContent.setText(activities.toString());
            holder.rlEvent.setTag(position);
            holder.rlEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EventDetails.class);
                    intent.putExtra("position", v.getTag().toString());
                    startActivity(intent);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView tvDate;
            public TextView tvTime;
            public TextView tvContent;
            public RelativeLayout rlEvent;

            public ViewHolder(View v) {
                super(v);
                tvDate = (TextView) v.findViewById(R.id.tvDate);
                tvTime = (TextView) v.findViewById(R.id.tvTime);
                tvContent = (TextView) v.findViewById(R.id.tvContent);
                rlEvent = (RelativeLayout) v.findViewById(R.id.rlEvent);
            }
        }


    }
}
