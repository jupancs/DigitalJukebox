package cs.ua.edu.digitaljukebox.views.PlaylistViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.activities.BaseFragmentActivity;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.services.HttpHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;

/**
 * Created by jupan on 6/16/17.
 */

public class PlaylistAdapter extends RecyclerView.Adapter {

  private BaseFragmentActivity mActivity;
  private LayoutInflater mInflater;
  private List<Track> mTrack;
  private OnOptionListener mListener;
  private Context context;

  private DBHandler dbHandler;
  private HttpHandler httpHandler;
  private SharedPreferences sharedPreferences;

  private PlaylistAdapter adapter;
  private RecyclerView mRecyclerView;
  private TextView mTextView;

  public PlaylistAdapter(BaseFragmentActivity mActivity, OnOptionListener mListener, Context context,
                         PlaylistAdapter adapter, RecyclerView mRecyclerView, TextView mTextView) {
    this.mActivity = mActivity;
    this.mListener = mListener;
    mInflater = mActivity.getLayoutInflater();
    mTrack = new ArrayList<>();
    this.context = context;
    this.adapter = adapter;
    this.mRecyclerView = mRecyclerView;
    this.mTextView = mTextView;

    sharedPreferences = context.getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
    httpHandler = new HttpHandler(context);
    dbHandler = new DBHandler(
        sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null),
        context
    );
  }

  public void setmTracks(List<Track> tracks) {
    mTrack.clear();
    mTrack.addAll(tracks);
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.list_playlist, parent, false);
    final PlaylistViewHolder friendRequestViewHolder = new PlaylistViewHolder(view);
    friendRequestViewHolder.upvoteTrack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Track track = (Track) friendRequestViewHolder.itemView.getTag();
        System.out.println("Track's name is: " + track.getTrackName());
        dbHandler.updateTrackScore(track, context);
//        httpHandler.getTracksFromPlaylist(sharedPreferences.getString(Constants.PLAYLIST_ID, null),
//            sharedPreferences.getString(Constants.USER_TOKEN, null), adapter , mRecyclerView, mTextView);
        mListener.OnOptionClicked(track);
      }
    });

    return friendRequestViewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((PlaylistViewHolder) holder).populate(mActivity, mTrack.get(position));
  }

  @Override
  public int getItemCount() {
    return mTrack.size();
  }

  public interface OnOptionListener {
    void OnOptionClicked(Track track);
  }
}

