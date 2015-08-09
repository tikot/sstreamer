package co.rytikov.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Image;

public class Imago extends Image implements Parcelable {

    public Imago() {
        super();
    }

    public Imago(Image img) {
        width = img.width;
        height = img.height;
        url = img.url;
    }

    public Imago(Parcel source) {
        url = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }

    public static final Parcelable.Creator<Imago> CREATOR = new Parcelable.Creator<Imago>() {
        @Override
        public Imago createFromParcel(Parcel source) {
            return new Imago(source);
        }

        @Override
        public Imago[] newArray(int size) {
            return new Imago[0];
        }
    };
}
