package com.guide.cordobatourplus;

import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Attraction's detail tab
 */
public class AttractionDetailTab extends Fragment {

    private EditText write;
    private Attraction attraction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attraction_detail_fragment, container, false);

        attraction = AttractionsDetails.attraction;

        ImageView ibImage = (ImageView) v.findViewById(R.id.img_thumbnail);
        ibImage.setImageResource(getResources().getIdentifier(attraction.getImage(), "drawable", getActivity().getPackageName()));

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(attraction.getDescription());

        final ImageButton ibPlay = (ImageButton) v.findViewById(R.id.ibAudio);
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AttractionsDetails.audioPlaying) {
                    ibPlay.setImageResource(R.drawable.ic_stop_green);
                    AttractionsDetails.ttobjet = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                AttractionsDetails.ttobjet.setLanguage(Locale.UK);
                                AttractionsDetails.ttobjet.speak(attraction.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
                                AttractionsDetails.audioNeverPlayed = false;
                                AttractionsDetails.audioPlaying = true;
                            }
                        }

                        @Override
                        protected void finalize() throws Throwable {
                            super.finalize();
                        }
                    });
                } else {
                    AttractionsDetails.ttobjet.stop();
                    AttractionsDetails.audioPlaying = false;
                    ibPlay.setImageResource(R.drawable.ic_play_green);
                }
            }
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
