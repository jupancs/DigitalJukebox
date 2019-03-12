package cs.ua.edu.digitaljukebox.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roughike.bottombar.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;

public class PlaylistFragment extends BaseFragment {

  @BindView(R2.id.bottomBar)
  BottomBar mBottomBar;

  private Unbinder mUnbinder;

  public static PlaylistFragment newInstance() {
    return new PlaylistFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
    mUnbinder = ButterKnife.bind(this,rootView);
    mBottomBar.selectTabWithId(R.id.tab_playlist);
    setUpBottomBar(mBottomBar, 3);

    return rootView;

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mUnbinder.unbind();
  }
}
