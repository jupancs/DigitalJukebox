package cs.ua.edu.digitaljukebox.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.activities.BaseFragmentActivity;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class AuthFragment extends BaseFragment {

  @BindView(R2.id.auth_fragment_connect_spotify_btn)
  Button connectSpotifyButton;

  private Unbinder mUnbinder;
  private BaseFragmentActivity mActivity;

  public static AuthFragment newInstance() {
    return new AuthFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_login, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    return rootView;
  }

  @OnClick(R2.id.auth_fragment_connect_spotify_btn)
  public void setConnectSpotifyButton() {
    AuthenticationRequest.Builder builder =
        new AuthenticationRequest.Builder(Constants.CLIENT_ID,
            AuthenticationResponse.Type.TOKEN, Constants.REDIRECT_URI);

    builder.setScopes(new String[]{"streaming"});
    AuthenticationRequest request = builder.build();

    AuthenticationClient.openLoginActivity(mActivity, Constants.REQUEST_CODE, request);
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
}
