package cs.ua.edu.digitaljukebox.fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.activities.ProfileActivity;
import cs.ua.edu.digitaljukebox.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileFragment extends BaseFragment {

  @BindView(R2.id.fragment_profile_userPicture)
  ImageView mUserPicture;

  @BindView(R2.id.fragment_profile_userName)
  TextView mUserName;

  @BindView(R2.id.bottomBar)
  BottomBar mBottomBar;

  private Unbinder mUnbinder;

  private String accessToken;

  private OkHttpClient client = new OkHttpClient();

  public static ProfileFragment newInstance() {
    return new ProfileFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);

    mBottomBar.selectTabWithId(R.id.tab_profile);
    setUpBottomBar(mBottomBar, 1);

    ProfileActivity profileActivity = (ProfileActivity) getActivity();
    accessToken = profileActivity.getAccessToken();

    // To allow http request in main thread
    StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);

    // Update profile image and username
    JSONObject profile = fetchProfile();
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

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

  /**
   * Fetch profile JSONObject using Spotify web api.
   * @return Profile JSONObject
   */
  private JSONObject fetchProfile() {
    Request request = new Request.Builder()
        .url(Constants.PROFILE_URL)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();
    Response response = null;

    try {
      response = client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String jsonData;
    JSONObject jsonObject = null;
    try {
      jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }
}
