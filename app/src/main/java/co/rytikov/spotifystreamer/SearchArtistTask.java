package co.rytikov.spotifystreamer;

import android.os.AsyncTask;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class SearchArtistTask extends AsyncTask<String, Void, List<Artist>> {

    private AsyncArtistCallback callback;

    public SearchArtistTask(AsyncArtistCallback callback) {
        this.callback = callback;
    }

    @Override
    protected List<Artist> doInBackground(String... params) {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        ArtistsPager result = spotify.searchArtists(params[0]);
        return result.artists.items;
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        super.onPostExecute(artists);
        callback.onComplete(artists);
    }

}