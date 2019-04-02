package cs.ua.edu.digitaljukebox.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.activities.AuthActivity;
import cs.ua.edu.digitaljukebox.activities.BaseFragmentActivity;
import cs.ua.edu.digitaljukebox.activities.ProfileActivity;
import cs.ua.edu.digitaljukebox.activities.SearchSongActivity;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class AuthFragment extends BaseFragment {

  public interface OnDataPass {
    public void onDataPass(String code, String actualCode, int role);
  }

  @BindView(R2.id.auth_fragment_host)
  Button connectSpotifyButton;

  @BindView(R2.id.auth_fragment_client)
  Button clientButton;

  @BindView(R2.id.auth_fragment_room_code)
  EditText roomCodeEdt;

  @BindView(R2.id.auth_fragment_confirm_btn)
  Button confirmButton;

  private Unbinder mUnbinder;
  private BaseFragmentActivity mActivity;
  private String code = "jukebox";

  private int role;

  private DBHandler dbHandler;
  private SharedPreferences sharedPreferences;
  private String[] actualCode = new String[1];

  public static AuthFragment newInstance() {
    return new AuthFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_login, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    sharedPreferences = getContext().getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
    dbHandler = new DBHandler(
        sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null),
        getContext()
    );

    dbHandler.getRoomCode(actualCode);
    return rootView;
  }

  @OnClick(R2.id.auth_fragment_host)
  public void setConnectSpotifyButton() {
    role = Constants.HOST;
    roomCodeEdt.setVisibility(View.VISIBLE);
    confirmButton.setVisibility(View.VISIBLE);
  }

  @OnClick(R2.id.auth_fragment_client)
  public void setClientButton() {
    role = Constants.CLIENT;
    roomCodeEdt.setVisibility(View.VISIBLE);
    confirmButton.setVisibility(View.VISIBLE);
  }

  @OnClick(R.id.auth_fragment_confirm_btn)
  public void setConfirmBtn() {
    code = roomCodeEdt.getText().toString();
    switch (role) {
      case Constants.CLIENT:
        ((OnDataPass) getActivity()).onDataPass(code, actualCode[0], role);
        System.out.println("Actual code is: " + actualCode[0]);
        AuthenticationRequest.Builder builderClient = new AuthenticationRequest.Builder(Constants.CLIENT_ID,
            AuthenticationResponse.Type.TOKEN, Constants.REDIRECT_URI);
        System.out.println("In auth code");
        builderClient.setScopes(new String[]{
            "playlist-read-private",
            "playlist-modify-public",
            "playlist-modify-private",
            "playlist-read-collaborative",
            "user-read-playback-state"
        });
        AuthenticationRequest requestClient = builderClient.build();
        AuthenticationClient.openLoginActivity(mActivity, Constants.REQUEST_CODE, requestClient);
        break;

      case Constants.HOST:
        ((OnDataPass) getActivity()).onDataPass(code, actualCode[0], role);
        dbHandler.setRoomCode(code);
        AuthenticationRequest.Builder builder =
            new AuthenticationRequest.Builder(Constants.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, Constants.REDIRECT_URI);
        builder.setScopes(new String[]{
            "playlist-read-private",
            "playlist-modify-public",
            "playlist-modify-private",
            "playlist-read-collaborative",
            "user-read-playback-state"
        });
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(mActivity, Constants.REQUEST_CODE, request);
        break;
    }

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mActivity = (BaseFragmentActivity) context;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mActivity = null;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }

  OnDataPass dataPasser;

  @Override
  public void onAttachFragment(Fragment childFragment) {
    super.onAttachFragment(childFragment);
    dataPasser = (OnDataPass) childFragment.getContext();
  }

  public void passData(String code, String actualCode, int role) {
    dataPasser.onDataPass(code, actualCode, role);
  }
}
