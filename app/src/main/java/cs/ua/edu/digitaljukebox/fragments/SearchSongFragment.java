package cs.ua.edu.digitaljukebox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.activities.BaseFragmentActivity;
import cs.ua.edu.digitaljukebox.activities.SearchSongActivity;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.services.HttpHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;
import cs.ua.edu.digitaljukebox.views.PlaylistViews.PlaylistAdapter;

public class SearchSongFragment extends BaseFragment implements PlaylistAdapter.OnOptionListener {

  @BindView(R2.id.search_fragment_recycleview)
  RecyclerView mRecyclerView;

  @BindView(R2.id.search_fragment_no_song)
  TextView mTextView;

  @BindView(R2.id.search_fragment_edittext)
  EditText searchSongEdt;

  @BindView(R2.id.search_fragment_add_btn)
  Button searchSongAddBtn;

  public Context context;

  private Unbinder mUnbinder;
  private String songName;

  private DBHandler dbHandler;
  private HttpHandler httpHandler;
  private SharedPreferences sharedPreferences;

  public static PlaylistAdapter adapter;

  private String accessToken;
  private String playlistId;

  public static SearchSongFragment newInstance() {
    return new SearchSongFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_search_song, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    sharedPreferences = getContext().getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
    httpHandler = new HttpHandler(getContext());
    dbHandler = new DBHandler(
        sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null),
        getContext()
    );

    adapter = new PlaylistAdapter((BaseFragmentActivity) getActivity(), this, getContext(), adapter,
        mRecyclerView, mTextView);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    dbHandler.getPlaylistId();

    accessToken = sharedPreferences.getString(Constants.USER_TOKEN, null);
    playlistId = sharedPreferences.getString(Constants.PLAYLIST_ID, null);

    System.out.println("Playlist: " + playlistId);

    httpHandler.getTracksFromPlaylist(playlistId, accessToken, adapter , mRecyclerView, mTextView);

    System.out.println("Token: " + accessToken);

    ((SearchSongActivity)context).runOnUiThread(new Runnable() {
      @Override
      public void run() {
        dbHandler.newTrackListener(adapter , mRecyclerView, mTextView, new HashSet<>());

        dbHandler.scoreChangeListener(adapter , mRecyclerView, mTextView);

        mRecyclerView.setAdapter(adapter);
      }
    });

    return rootView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @OnClick(R2.id.search_fragment_add_btn)
  public void setSearchSongAddBtn() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        songName = searchSongEdt.getText().toString();
        dbHandler.addSongToQueue(songName);
      }
    }).start();

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

  @Override
  public void OnOptionClicked(Track track) {

  }
}
