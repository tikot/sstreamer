package co.rytikov.spotifystreamer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.util.ArrayList;

import co.rytikov.spotifystreamer.models.Track;

/**
 * make a media service :)
 *
 */
public class MediaPlayerService extends Service {

    private boolean amRunning = false;
    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList tracks;

    public static String MEDIA_PLAYER_TIME = "media-player-time";
    public static String CURRENT_TRACK = "current-track";
    public static String MEDIA_CONTROLLERS = "media-controllers";

    /**
     * for broadcast
     */
    private Intent currentTrack;
    private Intent timeIntent;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {

        initializeMediaPlayer();

        currentTrack = new Intent(CURRENT_TRACK);
        timeIntent = new Intent(MEDIA_PLAYER_TIME);

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(MediaPlayerReceiver, new IntentFilter(MEDIA_CONTROLLERS));

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra("position") && intent.hasExtra("tracks")) {
            position = intent.getIntExtra("position", 0);
            tracks = intent.getParcelableArrayListExtra("tracks");
        }

        if (mediaPlayer != null && !intent.hasExtra("saved_state")) {
            updateMedia();
            amRunning = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateMedia() {
        Track track = (Track) tracks.get(position);

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(track.preview_url);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeMediaPlayer() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int mDuration = mediaPlayer.getDuration();

                currentTrack.putExtra("duration", mDuration);
                currentTrack.putExtra("position", position);
                LocalBroadcastManager.getInstance(getApplicationContext())
                        .sendBroadcast(currentTrack);

                handler.postDelayed(seekRun, 100);

                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });
    }

    private Runnable seekRun = new Runnable() {
        @Override
        public void run() {
            int current = mediaPlayer.getCurrentPosition();
            boolean isPlaying = mediaPlayer.isPlaying();

            timeIntent.putExtra("current_time", current);
            timeIntent.putExtra("is_playing", isPlaying);

            LocalBroadcastManager.getInstance(getApplicationContext())
                    .sendBroadcast(timeIntent);

            if (isPlaying) {
                handler.postDelayed(this, 100);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) mediaPlayer.release();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(MediaPlayerReceiver);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    private BroadcastReceiver MediaPlayerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final int countTrack = tracks.size() -1;

            switch (intent.getStringExtra("media-player")){
                case "seek":
                    int progress = intent.getIntExtra("progress", 0);
                    mediaPlayer.seekTo(progress);
                    break;
                case "next":
                    if (position < countTrack) {
                        position++;
                        updateMedia();
                    }
                    break;
                case "previous":
                    if (position > 0) {
                        position--;
                        updateMedia();
                    }
                    break;
                case "pause-play":
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    else {
                        mediaPlayer.start();
                        handler.postDelayed(seekRun, 100);
                    }
                    break;
            }
        }
    };
}
