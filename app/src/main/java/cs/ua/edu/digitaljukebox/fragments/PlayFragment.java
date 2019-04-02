package cs.ua.edu.digitaljukebox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.services.HttpHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class PlayFragment extends BaseFragment {

  @BindView(R2.id.bottomBar)
  BottomBar mBottomBar;

  @BindView(R2.id.fragment_play_play_btn)
  Button playBtn;

  @BindView(R2.id.fragment_play_next_btn)
  Button nextBtn;

  @BindView(R2.id.fragment_play_pause_btn)
  Button pauseBtn;

  @BindView(R2.id.fragment_play_resume_btn)
  Button resumebtn;

  @BindView(R2.id.fragment_play_track_name)
  TextView trackNameTextView;

  @BindView(R2.id.fragment_play_track_cover)
  ImageView trackCover;


  private Unbinder mUnbinder;

  private DBHandler dbHandler;
  private HttpHandler httpHandler;
  private SharedPreferences sharedPreferences;

  public static PlayFragment newInstance() {
    return new PlayFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_play, container, false);
    mUnbinder = ButterKnife.bind(this,rootView);
    mBottomBar.selectTabWithId(R.id.tab_play);
    setUpBottomBar(mBottomBar, 2);

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

    new Thread(() -> {
      HttpHandler.CurrentTrack currentTrackObj = httpHandler.getCurrentTrackName(sharedPreferences.
          getString(Constants.USER_TOKEN, null));
      String lastTrack = currentTrackObj.trackName;
      String currentTrack;

      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          // Update the UI
          trackNameTextView.setText(currentTrackObj.trackName);
          Picasso.get()
              .load(currentTrackObj.coverImageUrl)
              .into(trackCover);
        }
      });

      while (true) {
        currentTrack = httpHandler.getCurrentTrackName(sharedPreferences.
            getString(Constants.USER_TOKEN, null)).trackName;
        if (!lastTrack.equals(currentTrack)) {
          System.out.println("NOT EQUAL!!!!!!");
          dbHandler.setTrackScore(0, lastTrack);
          httpHandler.pauseTrack(sharedPreferences.getString(Constants.USER_TOKEN, null));
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          List<Track> tracks = httpHandler.getTracksFromPlaylistNoUi(
              sharedPreferences.getString(Constants.PLAYLIST_ID, null),
              sharedPreferences.getString(Constants.USER_TOKEN, null)
          );
          String tmpName = httpHandler
              .getCurrentTrackName(sharedPreferences.getString(Constants.USER_TOKEN, null))
              .trackName;
          if (tracks.get(0).getTrackName().equals(currentTrack)) {
            System.out.println("***********************************************");
            System.out.println("First track name: " + tracks.get(0).getTrackName());
            System.out.println("Current track name: " + currentTrack);
            System.out.println("***********************************************");

            httpHandler.resumeTrack(sharedPreferences.getString(Constants.USER_TOKEN, null));
            lastTrack = currentTrack;
          } else {
            currentTrack = httpHandler
                .getCurrentTrackName(sharedPreferences.getString(Constants.USER_TOKEN, null))
                .trackName;
            httpHandler.playTrack(sharedPreferences.getString(Constants.USER_TOKEN, null),
                sharedPreferences.getString(Constants.PLAYLIST_ID, null));
          }
          HttpHandler.CurrentTrack newTrackObj = httpHandler.getCurrentTrackName(currentTrack);
          if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                trackNameTextView.setText(newTrackObj.trackName);
                Picasso.get()
                    .load(newTrackObj.coverImageUrl)
                    .into(trackCover);
              }
            });
          }
        }
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("Query the current track name");
      }
    }).start();

    return rootView;
  }

  @OnClick(R2.id.fragment_play_play_btn)
  public void setPlayBtn() {
    Toast.makeText(getContext(), "Start to play ...", Toast.LENGTH_LONG).show();
    httpHandler.playTrack(sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null));
  }

  @OnClick(R2.id.fragment_play_next_btn)
  public void setNextBtn() {
    Toast.makeText(getContext(), "Next track ...", Toast.LENGTH_LONG).show();
    httpHandler.playTrack(sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null));
  }

  @OnClick(R2.id.fragment_play_pause_btn)
  public void setPauseBtn() {
    Toast.makeText(getContext(), "Track paused ...", Toast.LENGTH_LONG).show();
    httpHandler.pauseTrack(sharedPreferences.getString(Constants.USER_TOKEN, null));
  }

  @OnClick(R2.id.fragment_play_resume_btn)
  public void setResumebtn() {
    Toast.makeText(getContext(), "Resume ...", Toast.LENGTH_LONG).show();
    httpHandler.resumeTrack(sharedPreferences.getString(Constants.USER_TOKEN, null));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mUnbinder.unbind();
  }
}
