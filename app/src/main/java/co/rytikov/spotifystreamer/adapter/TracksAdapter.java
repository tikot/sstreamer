package co.rytikov.spotifystreamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.rytikov.spotifystreamer.R;
import co.rytikov.spotifystreamer.models.Track;


public class TracksAdapter extends ArrayAdapter<Track> {

    public ArrayList<Track> list;
    private Track track;
    private Context context;

    public TracksAdapter(Context context, int resource, ArrayList<Track> tracks) {
        super(context, resource, tracks);
        this.context = context;
        list = tracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        track = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_top_track, parent, false);
        }

        String url = null;
        if (track.album_images.size() > 0) {
            if (track.album_images.get(0).url != null) {
                url = track.album_images.get(0).url;
            }
        }
        else {
            Log.d("artist_img", "error getting image url");
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.album_art);
        Picasso.with(this.context).load(url).into(image);

        TextView trackName = (TextView) convertView.findViewById(R.id.track_name);
        trackName.setText(track.name);

        TextView albumName = (TextView) convertView.findViewById(R.id.album_name);
        albumName.setText(track.album_name);

        return convertView;
    }
}
