package co.rytikov.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Custom artist model with parcelable
 */
public class Artiste extends Artist implements Parcelable {

    public Artiste(Artist artist) {
        id = artist.id;
        name = artist.name;
        type = artist.type;
        href = artist.href;
        images = artist.images;
        genres = artist.genres;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
