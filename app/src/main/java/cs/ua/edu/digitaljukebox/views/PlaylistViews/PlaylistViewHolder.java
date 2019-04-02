package cs.ua.edu.digitaljukebox.views.PlaylistViews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.R2;

/**
 * Created by jupan on 6/15/17.
 */

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

  @BindView(R2.id.list_playlist_song_name)
  TextView trackName;

  @BindView(R2.id.list_song_name_upvote)
  ImageView upvoteTrack;

  public PlaylistViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void populate(Context context, Track track) {
    itemView.setTag(track);
    trackName.setText(track.getTrackName());
  }
}
