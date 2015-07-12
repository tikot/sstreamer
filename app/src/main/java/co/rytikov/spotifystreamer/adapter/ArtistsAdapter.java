package co.rytikov.spotifystreamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.rytikov.spotifystreamer.R;
import co.rytikov.spotifystreamer.models.Artiste;

public class ArtistsAdapter extends ArrayAdapter {

    public ArrayList<Artiste> list;
    private Artiste artiste;
    private Context context;

    public ArtistsAdapter(Context context, int resource, ArrayList<Artiste> artistes) {
        super(context, resource, (List) artistes);
        this.context = context;
        list = artistes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        artiste = list.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.ArtistImage);
        if (artiste.images.size() > 0) {
            if (artiste.images.get(0).url != null) {
                Picasso.with(this.context).load(artiste.images.get(0).url).into(image);
            }
        }

        TextView versionNameView = (TextView) convertView.findViewById(R.id.ArtistName);
        versionNameView.setText(artiste.name);

        return convertView;
    }
}
