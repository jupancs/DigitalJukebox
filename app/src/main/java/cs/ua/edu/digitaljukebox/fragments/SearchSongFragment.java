package cs.ua.edu.digitaljukebox.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cs.ua.edu.digitaljukebox.R;
import cs.ua.edu.digitaljukebox.R2;
import cs.ua.edu.digitaljukebox.services.DBHandler;
import cs.ua.edu.digitaljukebox.services.HttpHandler;
import cs.ua.edu.digitaljukebox.utils.Constants;

public class SearchSongFragment extends BaseFragment {

  @BindView(R2.id.search_fragment_edittext)
  EditText searchSongEdt;

  @BindView(R2.id.search_fragment_add_btn)
  Button searchSongAddBtn;

  private Unbinder mUnbinder;
  private String songName;

  private DBHandler dbHandler;
  private SharedPreferences sharedPreferences;

  public static SearchSongFragment newInstance() {
    return new SearchSongFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_search_song, container, false);
    mUnbinder = ButterKnife.bind(this, rootView);
    sharedPreferences = getContext().getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
    dbHandler = new DBHandler(
        sharedPreferences.getString(Constants.USER_TOKEN, null),
        sharedPreferences.getString(Constants.PLAYLIST_ID, null)
    );
    return rootView;
  }

  @OnClick(R2.id.search_fragment_add_btn)
  public void setSearchSongAddBtn() {
    songName = searchSongEdt.getText().toString();
    dbHandler.addSongToQueue(songName);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnbinder.unbind();
  }
}
