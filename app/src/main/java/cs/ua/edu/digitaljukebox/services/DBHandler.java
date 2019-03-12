package cs.ua.edu.digitaljukebox.services;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBHandler {

  private DatabaseReference mDatabase;
  private HttpHandler httpHandler;
  private String accessToken;
  private String playlistId;

  public DBHandler(String mAccessToken, String mPlaylistId) {
    httpHandler = new HttpHandler();
    accessToken = mAccessToken;
    playlistId = mPlaylistId;
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

  /**
   * Listen to the database update.
   */
  public void databaseListener() {
    mDatabase = FirebaseDatabase.getInstance().getReference("/playlist/queue");
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
           if (snapshot.getValue().equals(true)) {
             mDatabase.child("queue").child(snapshot.getKey()).setValue(false);
             addSong(snapshot.getKey());
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
    httpHandler.addTrackToPlaylist(name, playlistId, accessToken);
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
    mDatabase = FirebaseDatabase.getInstance().getReference("/playlist/queue");
    mDatabase.child(songName).setValue(true);
    System.out.println("Added the song '" + songName +  "' to the queue.");
  }
}
