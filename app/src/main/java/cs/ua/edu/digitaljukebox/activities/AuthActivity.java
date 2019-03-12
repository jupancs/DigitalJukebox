package cs.ua.edu.digitaljukebox.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.fragments.AuthFragment;
import cs.ua.edu.digitaljukebox.fragments.ProfileFragment;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class AuthActivity extends BaseFragmentActivity {

  SharedPreferences sharedPreferences;

  @Override
  Fragment createFragment() {
    return AuthFragment.newInstance();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sharedPreferences = getApplicationContext().getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    // Check if result comes from the correct activity
    if (requestCode == Constants.REQUEST_CODE) {
      AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

      switch (response.getType()) {
        // Response was successful and contains auth token
        case TOKEN:
          // Handle successful response
          System.out.println("Token: " + response.getAccessToken());
          sharedPreferences.edit().putString(Constants.USER_TOKEN, response.getAccessToken()).apply();
          startActivity(new Intent(this, ProfileActivity.class).putExtra("ACCESS_TOKEN", response.getAccessToken()));
          break;

        // Auth flow returned an error
        case ERROR:
          // Handle error response\
          System.out.println(">>>>>> Error <<<<<<");
          break;

        // Most likely auth flow was cancelled
        default:
          System.out.println(">>>>>> default <<<<<<");
          // Handle other cases
      }
    }
  }
}
