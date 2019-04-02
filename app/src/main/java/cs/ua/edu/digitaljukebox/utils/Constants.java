package cs.ua.edu.digitaljukebox.utils;

public class Constants {
  public static final String CLIENT_ID = "06eb348293764a95b439b7a99c56ce67";
  public static final int REQUEST_CODE = 1337;
  public static final String REDIRECT_URI = "cs.ua.edu.digitaljukebox://callback";

  public static final String PROFILE_URL = "https://api.spotify.com/v1/me";

  public static final String CREATE_LIST_URL = "https://api.spotify.com/v1/users/06eb348293764a95b439b7a99c56ce67/playlists";

  public static final String FETCH_LIST_URL = "https://api.spotify.com/v1/me/playlists?limit=1&offset=0";

  public static final String SEARCH_TRACK_URL_1 = "https://api.spotify.com/v1/search?q=";
  public static final String SEARCH_TRACK_URL_2 = "&type=track&market=US&limit=1&offset=0";

  public static final String ADD_TRACK_URL_1 = "https://api.spotify.com/v1/playlists/";
  public static final String ADD_TRACK_URL_2 = "/tracks?uris=";

  public static final String GET_TRACKS_OF_PLAYLIST_1 = "https://api.spotify.com/v1/playlists/";
  public static final String GET_TRACKS_OF_PLAYLIST_2 = "/tracks?market=US&fields=items(track(name%2C%20href))&offset=0";

  public static final String GET_CURRENT_TRACK = "https://api.spotify.com/v1/me/player/currently-playing?market=US";

  public static final String REORDER_PLAYLIST_1 = "https://api.spotify.com/v1/playlists/";
  public static final String REORDER_PLAYLIST_2 = "/tracks";

  public static final String PLAY_TRACK = "https://api.spotify.com/v1/me/player/play";
  public static final String NEXT_TRACK = "https://api.spotify.com/v1/me/player/next";
  public static final String PAUSE_TRACK = "https://api.spotify.com/v1/me/player/pause";

  public static final String GET_TRACK_NAME = "https://api.spotify.com/v1/me/player/currently-playing?market=US";

  public static final String USER_INFO = "user_info";
  public static final String USER_TOKEN = "user_token";
  public static final String PLAYLIST_ID = "playlist_id";

  public static final int HOST = 0;
  public static final int CLIENT = 1;

}
