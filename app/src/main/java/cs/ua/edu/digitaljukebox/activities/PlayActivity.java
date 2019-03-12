package cs.ua.edu.digitaljukebox.activities;

import android.support.v4.app.Fragment;

import cs.ua.edu.digitaljukebox.fragments.PlayFragment;
import cs.ua.edu.digitaljukebox.fragments.ProfileFragment;

public class PlayActivity extends BaseFragmentActivity {
  @Override
  Fragment createFragment() {
    return PlayFragment.newInstance();
  }

}
