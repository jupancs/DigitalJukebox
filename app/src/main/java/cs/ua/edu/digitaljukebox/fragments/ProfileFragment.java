package cs.ua.edu.digitaljukebox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.services.HttpHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class ProfileFragment extends BaseFragment {

  @BindView(R2.id.fragment_profile_userPicture)
  ImageView mUserPicture;

  @BindView(R2.id.fragment_profile_userName)
  TextView mUserName;

  @BindView(R2.id.fragment_profile_fetch_list)
  Button createListBtn;

  @BindView(R2.id.bottomBar)
  BottomBar mBottomBar;

  private Unbinder mUnbinder;

  private HttpHandler httpHandler;

  private String accessToken;

  private SharedPreferences sharedPreferences;

  private DBHandler dbHandler;

  public static ProfileFragment newInstance() {
    return new ProfileFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sharedPreferences = getContext().getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
    httpHandler = new HttpHandler();
    dbHandler = new DBHandler(
        sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null)
    );
    dbHandler.databaseListener();
    dbHandler.addNewPlaylist("list1");
    dbHandler.addNewPlaylist("list2");
    dbHandler.addNewPlaylist("list3");
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    mBottomBar.selectTabWithId(R.id.tab_profile);
    setUpBottomBar(mBottomBar, 1);

    accessToken = sharedPreferences.getString(Constants.USER_TOKEN, null);
    System.out.println("ProfileFragment -> Token: " + accessToken);

    // Update profile image and username
    JSONObject profile = httpHandler.fetchProfile(accessToken);
    JSONArray jsonArray;
    try {
      jsonArray = profile.getJSONArray("images");
      JSONObject image = jsonArray.getJSONObject(0);
      String imageUrl = image.getString("url");
      String userName = profile.getString("display_name");
      Picasso.get()
          .load(imageUrl)
          .into(mUserPicture);
      mUserName.setText(userName);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return rootView;
  }

  @OnClick(R2.id.fragment_profile_fetch_list)
  public void setCreateListBtn() {
    JSONObject playlist = httpHandler.fetchPlaylist(accessToken);
    JSONArray jsonArray;
    try {
      jsonArray = playlist.getJSONArray("items");
      String playlistName = jsonArray.getJSONObject(0).getString("name");
      String playlistId = jsonArray.getJSONObject(0).getString("id");
      sharedPreferences.edit().putString(Constants.PLAYLIST_ID, playlistId).apply();
      System.out.println("Playlist name is: " + playlistName);
      System.out.println("Playlist id is: " + playlistId);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }
}
