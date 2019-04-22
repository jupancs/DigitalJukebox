package cs.ua.edu.digitaljukebox.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.utils.Constants;
import cs.ua.edu.digitaljukebox.views.PlaylistViews.PlaylistAdapter;

public class DBHandler {

  private DatabaseReference mDatabase;
  private HttpHandler httpHandler;
  private String accessToken;
  private String playlistId;

  private SharedPreferences sharedPreferences;

  private Set<String> set = new HashSet<>();

  public DBHandler(String mAccessToken, String mPlaylistId, Context context) {
    httpHandler = new HttpHandler(context);
    accessToken = mAccessToken;
    playlistId = mPlaylistId;
    sharedPreferences = context.getSharedPreferences(
        Constants.USER_INFO,
        Context.MODE_PRIVATE
    );
  }

  /**
   * Update the current playlist id in the database.
   * Delete the last playlist id and add the latest playlist id.
   * @param playlistId
   */
  public void addNewPlaylist(String playlistId) {
    mDatabase = FirebaseDatabase.getInstance().getReference("/playlist");
    mDatabase.child(playlistId).removeValue();
    mDatabase.child("listId").setValue(playlistId);
  }

  public void getPlaylistId() {
    mDatabase = FirebaseDatabase.getInstance().getReference("/playlist");
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        sharedPreferences.edit().putString(Constants.PLAYLIST_ID,
            dataSnapshot.child("listId").getValue(String.class)).apply();
        System.out.println("PPPPPPP: " + sharedPreferences.getString(Constants.PLAYLIST_ID, null));
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  /**
   * Listen to the change of databse
   * @param adapter
   * @param mRecyclerView
   * @param mTextView
   */
  public void newTrackListener(PlaylistAdapter adapter , RecyclerView mRecyclerView,
                               TextView mTextView, Set<String> setNotUsed) {
    mDatabase = FirebaseDatabase.getInstance().getReference("/queue");
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          if (snapshot.child("state").getValue().equals(true) && !set.contains(snapshot.getKey())) {
            System.out.println("{}{}{}{}{}{}{}{}{}{}{}{");
//            set.add(snapshot.getKey());
            mDatabase = FirebaseDatabase.getInstance().getReference("/queue");
            mDatabase.child(snapshot.getKey()).child("state").setValue(false);
            System.out.println("key: " + snapshot.getKey() + ", state: " +
                snapshot.child("state").getValue());
            addSong(nameDecoder(snapshot.getKey()));

            httpHandler.getTracksFromPlaylist(playlistId, accessToken, adapter ,
                mRecyclerView, mTextView);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        System.out.println("Failed to read the data. " + databaseError.toException());
      }
    });
  }

  /**
   * Add a track to the playlist
   * @param name
   */
  public void addSong(String name) {
      System.out.println("Add song: " + name);
    System.out.println("ROLEEEEEE: " + sharedPreferences.getInt("ROLE", Constants.HOST));
    if (sharedPreferences.getInt("ROLE", Constants.HOST) == Constants.CLIENT) {

      httpHandler.addTrackToPlaylist(name, playlistId, accessToken);
      set.add(nameDecoder(name));
    }
  }

  /**
   * Set room code.
   * @param code
   */
  public void setRoomCode(String code) {
    mDatabase = FirebaseDatabase.getInstance().getReference("/room");
    mDatabase.child("code").setValue(code);
  }

  /**
   * Get room code.
   * @return
   */
  public void getRoomCode(String[] actualCode) {
    mDatabase = FirebaseDatabase.getInstance().getReference("/room");
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        actualCode[0] = dataSnapshot.child("code").getValue(String.class);
        System.out.println("Code is: " + actualCode[0]);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  /**
   * Add client's song to the database
   * @param songName
   */
  public void addSongToQueue(String songName) {
    HttpHandler.Song song = httpHandler.searchTrack(songName, accessToken);
    mDatabase = FirebaseDatabase.getInstance().getReference("/queue");
    String encodedName = nameEnoder(song.name);
    mDatabase.child(encodedName).child("state").setValue(true);
    mDatabase.child(encodedName).child("trackID").setValue(song.uri);
    mDatabase.child(encodedName).child("trackScore").setValue(0);
    System.out.println("Added the song '" + songName);
  }

  /**
   * Update the track score on the upvote action
   * @param track
   * @param context
   */
  public void updateTrackScore(Track track, Context context) {
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("/queue");
    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        // Data stored in Firebase has no whitespace, should replace it with +
        String name = track.getTrackName().replace("\\s" , "+");
        String encodedName = nameEnoder(name);
        System.out.println("Name is: " + encodedName);
        if (dataSnapshot.hasChild(encodedName)) {
          int score = dataSnapshot.child(encodedName).child("trackScore").getValue(Integer.class);
          mDatabaseReference.child(encodedName).child("trackScore").setValue(score + 1);
          Toast.makeText(context, "Track score is " + (score + 1) + " now ...", Toast.LENGTH_LONG)
              .show();
        } else {
          Toast.makeText(context, "Track <" + track.getTrackName() + "> doesn't exist ...",
              Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  /**
   * Listen to the socre change and update the recycleview list
   * @param adapter
   * @param mRecyclerView
   * @param mTextView
   */
  public void scoreChangeListener(PlaylistAdapter adapter , RecyclerView mRecyclerView,
                                  TextView mTextView) {
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("/queue");
    mDatabaseReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        HashMap<String, Integer> map = new HashMap<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
          if (ds.child("trackScore").getValue(Integer.class) == null) {
            map.put(ds.getKey(), 0);
          } else {
            map.put(ds.getKey(), ds.child("trackScore").getValue(Integer.class));
          }
        }
        List<Track> tracks = httpHandler.getTracksFromPlaylist(
            sharedPreferences.getString(Constants.PLAYLIST_ID, null),
            sharedPreferences.getString(Constants.USER_TOKEN, null),
            adapter , mRecyclerView, mTextView);
        for (int i = 0; i < tracks.size(); i++) {
          if (map.containsKey(nameEnoder(tracks.get(i).getTrackName()))) {
            tracks.get(i).setTrackScore(map.get(nameEnoder(tracks.get(i).getTrackName())));
          }
        }
        List<Track> oldOrderTracks = new ArrayList(tracks);
        Collections.sort(tracks, new Comparator<Track>() {
            @Override
            public int compare(Track t1, Track t2) {
              if (t2.getTrackScore() - t1.getTrackScore() != 0) {
                return t2.getTrackScore() - t1.getTrackScore();
              }
              return t2.getTrackName().compareTo(t1.getTrackName());
            }
          }
        );

        adapter.setmTracks(tracks);

        List<Track> copy = new ArrayList(oldOrderTracks);

        // TODO: 4/1/19 Reorder Spotify playlist
        for (int i = 0; i < copy.size(); i++) {
          System.out.println("RRRRRRRRRRRRRRRRRRRR");
          Track track = copy.get(i);
          int oldIndex = oldOrderTracks.indexOf(track);
          int newIndex = findIndexofTrack(tracks, track);
          System.out.println("oldindex: " + oldIndex + " newindex: " + newIndex);
          if (newIndex != oldIndex) {
            if (newIndex <= oldIndex) {
              httpHandler.reorderPlaylist(accessToken, playlistId, oldIndex, newIndex);
              oldOrderTracks.add(newIndex, track);
              oldOrderTracks.remove(oldIndex + 1);
            } else {
              httpHandler.reorderPlaylist(accessToken, playlistId, oldIndex, newIndex + 1);
              oldOrderTracks.add(newIndex + 1, track);
              oldOrderTracks.remove(i);
            }
          }
        }


//        // TODO: 4/1/19 Reorder Spotify playlist
//        for (int i = 0; i < oldOrderTracks.size(); i++) {
//          int newIndex = findIndexofTrack(tracks, oldOrderTracks.get(i));
//          System.out.println("i: " + i + " index: " + newIndex);
//          if (newIndex != i && sharedPreferences.getInt("ROLE", Constants.HOST) != Constants.CLIENT) {
//            System.out.println("RRRRRRRRRRRRRRRRRRRR");
//            httpHandler.reorderPlaylist(accessToken, playlistId, i, newIndex);
//            Track track = oldOrderTracks.get(i);
//            int oldIndex = oldOrderTracks.indexOf(track);
//            httpHandler.reorderPlaylist(accessToken, playlistId, oldIndex, newIndex);
//            if (newIndex <= i) {
//              oldOrderTracks.add(newIndex, track);
//              oldOrderTracks.remove(oldIndex + 1);
//            } else {
//              oldOrderTracks.add(newIndex + 1, track);
//              oldOrderTracks.remove(i);
//            }
//          }
//
//        }
      }



      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  /**
   * Set a track's score
   * @param targetScore
   * @param trackName
   */
  public void setTrackScore(int targetScore, String trackName) {
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("/queue");
    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        // Data stored in Firebase has no whitespace, should replace it with +
        String name = trackName.replace("\\s" , "+");
        String encodedName = nameEnoder(name);
        System.out.println("Name is: " + encodedName);
        if (dataSnapshot.hasChild(encodedName)) {
          mDatabaseReference.child(encodedName).child("trackScore").setValue(targetScore);
        } else {
          System.out.print("No such song exists ...");
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  /* ------------------------------- Private ----------------------------------  */

  /**
   * Find the index of a track in the List of tracks
   * @param tracks
   * @param track
   * @return The index of the track
   */
  private int findIndexofTrack(List<Track> tracks, Track track) {
    int res = -1;
    for (int i = 0; i < tracks.size(); i++) {
      if (tracks.get(i).getTrackName().equals(track.getTrackName())) {
        res = i;
        break;
      }
    }
    return res;
  }

  /**
   * Firebase data key doesn't support '.', '#', '$', '[', or ']'
   * @param nameWithSpecials
   * @return encoded name
   */
  private String nameEnoder(String nameWithSpecials) {
    return nameWithSpecials
        .replace(".", "*")
        .replace("#", "^")
        .replace("$", "@")
        .replace("[", "{")
        .replace("]", "}");
  }

  /**
   * Firebase data key doesn't support '.', '#', '$', '[', or ']'
   * @param nameWithoutSpecials
   * @return decoded name
   */
  private String nameDecoder(String nameWithoutSpecials) {
    return nameWithoutSpecials
        .replace("*", ".")
        .replace("^", "#")
        .replace("@", "$")
        .replace("{", "[")
        .replace("}", "]");
  }
}
