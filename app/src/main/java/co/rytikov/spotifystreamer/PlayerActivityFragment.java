package co.rytikov.spotifystreamer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import co.rytikov.spotifystreamer.models.Track;
import co.rytikov.spotifystreamer.service.MediaPlayerService;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends DialogFragment {

    public View listView;
    private String artist_name;
    private ArrayList<Track> tracks;

    /**
     * sendBroadcast intent
     */
    private Intent sendIntent;

    private TextView durationView;
    private TextView currentTime;
    private ImageButton playButton;
    private TextView albumName;
    private TextView trackName;
    private ImageView image;

    private SeekBar seekbar;
    private int position = 0;
    private int maxDuration;
    private boolean is_playing;

    /**
     * get new instance of this frament
     * @param position int
     * @param artistName String
     * @param tracks ArrayList
     * @return liveView
     */
    public static PlayerActivityFragment newInstance(int position, String artistName,
                                                     ArrayList<Track> tracks) {
        return newInstance(position, artistName, tracks, false);
    }

    public static PlayerActivityFragment newInstance(int position, String artistName,
                                                     ArrayList<Track> tracks, boolean saved) {
        PlayerActivityFragment fragment = new PlayerActivityFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("artist_name", artistName);
        args.putParcelableArrayList("tracks", tracks);
        args.putBoolean("saved_state", saved);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        Bundle bundle = getArguments();
        Boolean saved = false;

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
            artist_name = savedInstanceState.getString("artist_name");
            tracks = savedInstanceState.getParcelableArrayList("tracks");

            maxDuration = savedInstanceState.getInt("max_duration");

            saved = true;
        }
        else {
            if (bundle != null) {
                position = bundle.getInt("position");
                artist_name = bundle.getString("artist_name");
                tracks = bundle.getParcelableArrayList("tracks");
                saved = bundle.getBoolean("saved_state");
            }
        }

        if (saved) {
            serviceIntent.putExtra("saved_state", true);
        }

        // setup and start Media Service
        serviceIntent.putExtra("position", position);
        serviceIntent.putExtra("artist_name", artist_name);
        serviceIntent.putParcelableArrayListExtra("tracks", tracks);

        getActivity().startService(serviceIntent);

        sendIntent = new Intent(MediaPlayerService.MEDIA_CONTROLLERS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
        outState.putString("artist_name", artist_name);
        outState.putParcelableArrayList("tracks", tracks);

        outState.putInt("max_duration", maxDuration);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listView = inflater.inflate(R.layout.fragment_player, container, false);

        TextView artistName = (TextView) listView.findViewById(R.id.artist_name);
        artistName.setText(artist_name);

        albumName = (TextView) listView.findViewById(R.id.album_name);
        trackName = (TextView) listView.findViewById(R.id.track_name);
        image = (ImageView) listView.findViewById(R.id.album_art);

        durationView = (TextView) listView.findViewById(R.id.track_duration);
        durationView.setText("--:--");

        currentTime = (TextView) listView.findViewById(R.id.track_time);
        currentTime.setText("--:--");

        seekbar = (SeekBar) listView.findViewById(R.id.seek_track);
        playButton = (ImageButton) listView.findViewById(R.id.play_button);

        //Set update view things
        setUpView();
        setDuration();
        updateView();

        LocalBroadcastManager.getInstance(listView.getContext())
                .registerReceiver(CurrentTrackReceiver,
                        new IntentFilter(MediaPlayerService.CURRENT_TRACK));

        LocalBroadcastManager.getInstance(listView.getContext())
                .registerReceiver(SeekBarReceiver,
                        new IntentFilter(MediaPlayerService.MEDIA_PLAYER_TIME));

        return listView;
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(listView.getContext())
                .unregisterReceiver(CurrentTrackReceiver);

        LocalBroadcastManager.getInstance(listView.getContext())
                .unregisterReceiver(SeekBarReceiver);

        super.onDestroyView();
    }

    private BroadcastReceiver CurrentTrackReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            maxDuration = intent.getIntExtra("duration", 0);
            position = intent.getIntExtra("position", 0);

            updateView();

            // change the play button
            changeToPause();
            setDuration();
        }
    };

    private BroadcastReceiver SeekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getIntExtra("current_time", 0);
            is_playing = intent.getBooleanExtra("is_playing", true);

            seekbar.setProgress(current);
            currentTime.setText(getTimeFormat(current));

            if (!is_playing) {
                changeToPlay();
            }
        }

    };

    private String getTimeFormat(int time) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1));
    }

    private void setDuration() {
        seekbar.setMax(maxDuration);
        durationView.setText(getTimeFormat(maxDuration));
    }

    public void updateView() {
        Track track = tracks.get(position);

        albumName.setText(track.album_name);
        trackName.setText(track.name);

        String url = null;
        if (track.album_images.size() > 0) {
            if (track.album_images.get(0).url != null) {
                url = track.album_images.get(0).url;
            }
        }
        else {
            Log.d("artist_img", "error getting image url");
        }

        Picasso.with(listView.getContext()).load(url).into(image);
    }

    public void setUpView() {

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sendIntent.putExtra("media-player", "seek");
                    sendIntent.putExtra("progress", progress);
                    LocalBroadcastManager.getInstance(listView.getContext())
                            .sendBroadcast(sendIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent.putExtra("media-player", "pause-play");
                LocalBroadcastManager.getInstance(listView.getContext())
                        .sendBroadcast(sendIntent);
                if (is_playing) {
                    changeToPlay();
                }
                else {
                    changeToPause();
                }
            }
        });

        ImageButton nextButton = (ImageButton) listView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent.putExtra("media-player", "next");
                LocalBroadcastManager.getInstance(listView.getContext())
                        .sendBroadcast(sendIntent);

                changeToPause();
            }
        });

        ImageButton prevButton = (ImageButton) listView.findViewById(R.id.previous_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent.putExtra("media-player", "previous");
                LocalBroadcastManager.getInstance(listView.getContext())
                        .sendBroadcast(sendIntent);

                changeToPause();
            }
        });
    }

    private void changeToPause() {
        playButton.setImageResource(R.drawable.ic_pause_circle_48dp);
    }

    private void changeToPlay() {
        playButton.setImageResource(R.drawable.ic_play_circle_48dp);
    }
}
