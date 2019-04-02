package cs.ua.edu.digitaljukebox.Entities;

public class Track {
  private String trackName;
  private String trackId;
  private int trackScore;

  public Track() {}

  public Track(String trackName, String trackId) {
    this.trackName = trackName;
    this.trackId = trackId;
    this.trackScore = 0;
  }

  public Track(String trackName, String trackId, int trackScore) {
    this.trackName = trackName;
    this.trackId = trackId;
    this.trackScore = trackScore;
  }

  public String getTrackName() {
    return trackName;
  }

  public String getTrackId() {
    return trackId;
  }

  public int getTrackScore() {
    return trackScore;
  }

  public void setTrackName(String trackName) {
    this.trackName = trackName;
  }

  public void setTrackId(String trackId) {
    this.trackId = trackId;
  }

  public void setTrackScore(int trackScore) {
    this.trackScore = trackScore;
  }
}
