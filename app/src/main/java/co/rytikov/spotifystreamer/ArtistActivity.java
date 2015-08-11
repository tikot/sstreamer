package co.rytikov.spotifystreamer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import co.rytikov.spotifystreamer.models.Track;
import co.rytikov.spotifystreamer.service.MediaPlayerService;


public class ArtistActivity extends Activity implements ArtistActivityFragment.Callback {

    private boolean mDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        if (findViewById(R.id.top_track_layout) != null) {
            mDualPane = true;
        }
        //Find a better way to restore view
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(RestoreView,
                new IntentFilter(MediaPlayerService.RECOVER_PLAYER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.now_playing) {
            openPlayer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPlayer() {
        Intent intent = new Intent(this, MediaPlayerService.class)
                .putExtra("recovery", true);
        startService(intent);
    }

    private BroadcastReceiver RestoreView = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", 0);
            String artist_name = intent.getStringExtra("artist_name");
            ArrayList<Track> tracks = intent.getParcelableArrayListExtra("tracks");

            if (intent.getBooleanExtra("not-running", false)) {
                Toast.makeText(context, R.string.no_now_playing, Toast.LENGTH_SHORT).show();
                return;
            }

            if (mDualPane) {
                PlayerActivityFragment newFragment = PlayerActivityFragment
                        .newInstance(position, artist_name, tracks, true);
                newFragment.show(getFragmentManager(), "dialog");
            }
            else {
                Intent playerIntent = new Intent(context, PlayerActivity.class)
                        .putExtra("position", position)
                        .putExtra("artist_name", artist_name)
                        .putExtra("tracks", tracks)
                        .putExtra("saved_state", true);
                startActivity(playerIntent);
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(RestoreView);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String[] data) {

        if (mDualPane) {

            TopTrackActivityFragment newFragment = TopTrackActivityFragment.newInstance(data);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.top_track_layout, newFragment);
            transaction.commit();
        }
        else {
            Intent intent = new Intent(this, TopTrackActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, data)
                    .putExtra("dual_pane", mDualPane);
            startActivity(intent);
        }
    }
}
