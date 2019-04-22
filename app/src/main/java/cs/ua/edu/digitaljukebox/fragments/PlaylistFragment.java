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
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.activities.BaseFragmentActivity;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.services.HttpHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;
import cs.ua.edu.digitaljukebox.views.PlaylistViews.PlaylistAdapter;

public class PlaylistFragment extends BaseFragment implements PlaylistAdapter.OnOptionListener {

  @BindView(R2.id.fragment_playlist_recylerView)
  RecyclerView mRecyclerView;

  @BindView(R2.id.fragment_playlist_song_name)
  TextView mTextView;

  @BindView(R2.id.bottomBar)
  BottomBar mBottomBar;

  private Unbinder mUnbinder;

  private HttpHandler httpHandler;

  private String accessToken;
  private String playlistId;

  private SharedPreferences sharedPreferences;

  public static PlaylistAdapter adapter;

  private DBHandler dbHandler;


  public static PlaylistFragment newInstance() {
    return new PlaylistFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_playlist_list, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    mBottomBar.selectTabWithId(R.id.tab_playlist);
    setUpBottomBar(mBottomBar, 3);

    adapter = new PlaylistAdapter((BaseFragmentActivity) getActivity(), this, getContext(),
        adapter, mRecyclerView, mTextView);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    accessToken = sharedPreferences.getString(Constants.USER_TOKEN, null);
    playlistId = sharedPreferences.getString(Constants.PLAYLIST_ID, null);


    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        httpHandler.getTracksFromPlaylist(playlistId, accessToken, adapter , mRecyclerView, mTextView);

        dbHandler.newTrackListener(adapter , mRecyclerView, mTextView, new HashSet<>());

        dbHandler.scoreChangeListener(adapter , mRecyclerView, mTextView);
      }
    });

    mRecyclerView.setAdapter(adapter);

    return rootView;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mUnbinder.unbind();
  }

  @Override
  public void OnOptionClicked(Track track) {

  }
}


