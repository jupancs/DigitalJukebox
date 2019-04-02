package cs.ua.edu.digitaljukebox.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cs.ua.edu.digitaljukebox.fragments.ProfileFragment;

public class ProfileActivity extends BaseFragmentActivity {

  @Override
  Fragment createFragment() {
    return ProfileFragment.newInstance();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
}
