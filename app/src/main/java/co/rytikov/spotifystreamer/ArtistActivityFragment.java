package co.rytikov.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import java.util.List;

import co.rytikov.spotifystreamer.adapter.ArtistsAdapter;
import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistActivityFragment extends Fragment implements AsyncArtistCallback {

    private View mainView;
    private ArtistsAdapter artistsAdapter;
    public ArtistActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mainView = view;
        SearchView artistSearch = (SearchView) view.findViewById(R.id.artistSearch);

        final ArtistActivityFragment that = this;

        artistSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchArtistTask getArtist = new SearchArtistTask( that );
                getArtist.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onComplete(final List<Artist> artists) {
        artistsAdapter = new ArtistsAdapter(getActivity(), 0, artists);

        ListView listView = (ListView) mainView.findViewById(R.id.artistListView);
        listView.setAdapter(artistsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = (Artist) artistsAdapter.getItem(position);

            }
        });
    }
}