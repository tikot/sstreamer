package co.rytikov.spotifystreamer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import co.rytikov.spotifystreamer.models.Track;


public class PlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        if (intent != null && savedInstanceState == null) {

            int position = intent.getIntExtra("position", 0);
            String artist_name = intent.getStringExtra("artist_name");
            ArrayList<Track> tracks = intent.getParcelableArrayListExtra("tracks");
            boolean saved = intent.getBooleanExtra("saved_state", false);

            PlayerActivityFragment playerFragment = PlayerActivityFragment.newInstance(position,
                    artist_name, tracks, saved);

            getFragmentManager().beginTransaction()
                    .replace(R.id.top_track_layout, playerFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
