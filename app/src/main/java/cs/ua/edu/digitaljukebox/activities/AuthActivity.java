package cs.ua.edu.digitaljukebox.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.File;

import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.fragments.AuthFragment;
import cs.ua.edu.digitaljukebox.fragments.ProfileFragment;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class AuthActivity extends BaseFragmentActivity implements AuthFragment.OnDataPass {

  SharedPreferences sharedPreferences;

  private String roomCode = null;
  private String actualCode = null;
  private int role = 0;


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
          sharedPreferences.edit().putString(Constants.USER_TOKEN,
              response.getAccessToken()).apply();
          System.out.println("RRRRRRole: " + role);
          if (role == Constants.HOST) {
            startActivity(new Intent(this, ProfileActivity.class));
          } else if (role == Constants.CLIENT && roomCode.equals(actualCode)) {
            startActivity(new Intent(this, SearchSongActivity.class));
          }

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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    trimCache(this);
  }

  public static void trimCache(Context context) {
    try {
      File dir = context.getCacheDir();
      if (dir != null && dir.isDirectory()) {
        deleteDir(dir);
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  public static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }

  @Override
  public void onDataPass(String code, String actualCode, int role) {
    roomCode = code;
    this.actualCode = actualCode;
    this.role = role;
  }
}
