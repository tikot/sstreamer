package co.rytikov.spotifystreamer.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.rytikov.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;

public class ArtistsAdapter extends ArrayAdapter {
     
    public List<Artist> list;
    private Artist artist;
    private Context context;

    public ArtistsAdapter(Context context, int resource, List<Artist> artists) {
        super(context, resource, (List) artists);
        this.context = context;
        list = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        artist = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        String url = null;
        if (artist.images.size() > 0) {
            if (artist.images.get(0).url != null) {
                url = artist.images.get(0).url;
            }
        }
        else {
            Log.d("artist_img", "error getting image url");
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.ArtistImage);
        Picasso.with(this.context).load(url).error(R.drawable.ic_album_black).into(image);

        TextView versionNameView = (TextView) convertView.findViewById(R.id.ArtistName);
        versionNameView.setText(artist.name);

        return convertView;
    }
}
