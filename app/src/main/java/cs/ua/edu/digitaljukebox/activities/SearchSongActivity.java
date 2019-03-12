package cs.ua.edu.digitaljukebox.activities;

import android.support.v4.app.Fragment;

import cs.ua.edu.digitaljukebox.fragments.SearchSongFragment;

public class SearchSongActivity extends BaseFragmentActivity {
  @Override
  Fragment createFragment() {
    return SearchSongFragment.newInstance();
  }
}
