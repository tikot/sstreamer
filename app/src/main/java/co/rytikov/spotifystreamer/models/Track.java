package co.rytikov.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Music track model with parcelable
 */
public class Track extends kaaes.spotify.webapi.android.models.Track implements Parcelable {

    public List<Imago> album_images;
    public String album_name;

    public Track(kaaes.spotify.webapi.android.models.Track track) {
        id = track.id;
        album_name = track.album.name;
        name = track.name;
        preview_url = track.preview_url;
        track_number = track.track_number;
        duration_ms = track.duration_ms;

        album_images = new ArrayList<>();
        for (Image img : track.album.images) {
            album_images.add(new Imago(img));
        }
    }

    public Track(Parcel source) {
        super();
        id = source.readString();
        album_name = source.readString();
        name = source.readString();
        preview_url = source.readString();
        track_number = source.readInt();
        duration_ms = source.readLong();
        album_images = source.createTypedArrayList(Imago.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(album_name);
        dest.writeString(name);
        dest.writeString(preview_url);
        dest.writeInt(track_number);
        dest.writeLong(duration_ms);
        dest.writeTypedList(album_images);
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {

        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
