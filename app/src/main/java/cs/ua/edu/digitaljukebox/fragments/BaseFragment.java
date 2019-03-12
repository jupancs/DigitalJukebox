package cs.ua.edu.digitaljukebox.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.activities.PlayActivity;
import cs.ua.edu.digitaljukebox.activities.PlaylistActivity;
import cs.ua.edu.digitaljukebox.activities.ProfileActivity;

public class BaseFragment extends Fragment {
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public void setUpBottomBar(BottomBar bottomBar, final int index) {
    bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
      @Override
      public void onTabSelected(@IdRes int tabId) {
        switch (index) {
          case 1:
            if (tabId == R.id.tab_playlist) {
              Intent intent = new Intent(getActivity(), PlaylistActivity.class);
              startActivity(intent);
              getActivity().finish();
              getActivity().overridePendingTransition(android.R.anim.fade_in,
                  android.R.anim.fade_out);
            } else if (tabId == R.id.tab_play) {
              Intent intent = new Intent(getActivity(), PlayActivity.class);
              startActivity(intent);
              getActivity().finish();
              getActivity().overridePendingTransition(android.R.anim.fade_in,
                  android.R.anim.fade_out);
            }
            break;
          case 2:
            if (tabId == R.id.tab_profile) {
              Intent intent = new Intent(getActivity(), ProfileActivity.class);
              startActivity(intent);
              getActivity().finish();
              getActivity().overridePendingTransition(android.R.anim.fade_in,
                  android.R.anim.fade_out);
            } else if (tabId == R.id.tab_playlist) {
              Intent intent = new Intent(getActivity(), PlaylistActivity.class);
              startActivity(intent);
              getActivity().finish();
              getActivity().overridePendingTransition(android.R.anim.fade_in,
                  android.R.anim.fade_out);
            }
            break;
          case 3:
            if (tabId == R.id.tab_profile) {
              Intent intent = new Intent(getActivity(), ProfileActivity.class);
              startActivity(intent);
              getActivity().finish();
              getActivity().overridePendingTransition(android.R.anim.fade_in,
                  android.R.anim.fade_out);
            } else if (tabId == R.id.tab_play) {
              Intent intent = new Intent(getActivity(), PlayActivity.class);
              startActivity(intent);
              getActivity().finish();
              getActivity().overridePendingTransition(android.R.anim.fade_in,
                  android.R.anim.fade_out);
            }
            break;
        }
      }
    });
  }
}
