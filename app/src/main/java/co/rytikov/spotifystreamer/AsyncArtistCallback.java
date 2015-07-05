package co.rytikov.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public interface AsyncArtistCallback {
    void onComplete(List<Artist> artists);
}
