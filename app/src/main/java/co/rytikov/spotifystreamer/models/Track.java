package co.rytikov.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Music track model with parcelable
 */
public class Track extends kaaes.spotify.webapi.android.models.Track implements Parcelable {

    public List<Image> album_images;
    public String album_name;

    public Track(kaaes.spotify.webapi.android.models.Track track) {
        id = track.id;
        album_images = track.album.images;
        album_name = track.album.name;
        name = track.name;
        preview_url = track.preview_url;
        track_number = track.track_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
