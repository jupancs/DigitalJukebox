package cs.ua.edu.digitaljukebox.activities;

import android.support.v4.app.Fragment;

import cs.ua.edu.digitaljukebox.fragments.PlaylistFragment;


public class PlaylistActivity extends BaseFragmentActivity {
  @Override
  Fragment createFragment() {
    return PlaylistFragment.newInstance();
  }

}
