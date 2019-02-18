package cs.ua.edu.digitaljukebox.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cs.ua.edu.digitaljukebox.R;

public abstract class BaseFragmentActivity extends AppCompatActivity {
  abstract Fragment createFragment();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acitvity_fragment_base);

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = fragmentManager.findFragmentById(R.id.activity_fragment_base_fragmentContainer);

    if (fragment == null) {
      fragment = createFragment();
      fragmentManager.beginTransaction()
          .add(R.id.activity_fragment_base_fragmentContainer, fragment)
          .commit();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

}