package co.rytikov.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.rytikov.spotifystreamer.adapter.TracksAdapter;
import co.rytikov.spotifystreamer.models.Track;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTrackActivityFragment extends Fragment {

    private View rootView;
    private String[] artist;
    private ArrayList<Track> tracks;

    public TopTrackActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("the_tracks", tracks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_top_track, container, false);

        if (savedInstanceState != null ) {
            tracks = savedInstanceState.getParcelableArrayList("the_tracks");
            onCompete();
        }
        else {
            tracks = new ArrayList<>();
        }

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT) && tracks.size() == 0) {
            artist = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            if (isConnected())
                new TopTrackTask().execute(artist[1]);
        }

        return rootView;
    }

    public void onCompete() {
        if (tracks.size() == 0) {
            Toast.makeText(getActivity(), "No tracks found.", Toast.LENGTH_SHORT).show();
        }

        TracksAdapter tracksAdapter = new TracksAdapter(getActivity(), 0, tracks);


        ListView listView = (ListView) rootView.findViewById(R.id.top_track_list_view);
        listView.setAdapter(tracksAdapter);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(getActivity(), "Check your connection.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class TopTrackTask extends AsyncTask<String, Void, ArrayList<Track>> {

        @Override
        protected ArrayList<Track> doInBackground(String... params) {

            Map<String, Object> endpoint = new HashMap<String, Object>();
            endpoint.put("country", "US");

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Tracks results = spotify.getArtistTopTrack(params[0], endpoint);

            for (kaaes.spotify.webapi.android.models.Track spotify_track : results.tracks) {
                tracks.add(new Track(spotify_track));
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(ArrayList<Track> tracks) {
            onCompete();
        }
    }
}
