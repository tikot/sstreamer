package co.rytikov.spotifystreamer;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import co.rytikov.spotifystreamer.models.Artiste;
import co.rytikov.spotifystreamer.adapter.ArtistsAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistActivityFragment extends Fragment {

    private View mainView;
    private SearchView artistSearch;
    private ArtistsAdapter artistsAdapter;
    private ArrayList<Artiste> artistes;

    public interface Callback {

        void onItemSelected(String[] data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null ) {
            artistes = savedInstanceState.getParcelableArrayList("the_artistes");
        }
        else {
            artistes = new ArrayList<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("the_artistes", artistes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mainView = view;
        artistSearch = (SearchView) view.findViewById(R.id.artistSearch);

        if (artistes.size() != 0) {
            onComplete();
        }

        artistSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isConnected()) {
                    SearchArtistTask getArtist = new SearchArtistTask();
                    getArtist.execute(query);
                }
                artistSearch.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    /**
     * load a list view after async task and if has a saved instance
     */
    public void onComplete() {
        if (artistes.size() == 0) {
            Toast.makeText(getActivity(), R.string.no_results, Toast.LENGTH_SHORT).show();
        }
        artistsAdapter = new ArtistsAdapter(getActivity(), 0, artistes);

        ListView listView = (ListView) mainView.findViewById(R.id.artistListView);
        listView.setAdapter(artistsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artiste artiste = (Artiste) artistsAdapter.getItem(position);
                String[] data = new String[]{artiste.name, artiste.id};
                ((Callback) getActivity()).onItemSelected(data);
            }
        });

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(getActivity(), R.string.check_connection, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class SearchArtistTask extends AsyncTask<String, Void, ArrayList<Artiste>> {

        @Override
        protected ArrayList<Artiste> doInBackground(String... params) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(params[0]);

            artistes = null;
            artistes = new ArrayList<>();
            for (Artist artist : results.artists.items) {
                artistes.add(new Artiste(artist));
            }

            return artistes;
        }

        @Override
        protected void onPostExecute(ArrayList<Artiste> artistes) {
            onComplete();
        }
    }
}