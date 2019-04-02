package cs.ua.edu.digitaljukebox.services;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cs.ua.edu.digitaljukebox.Entities.Track;
import cs.ua.edu.digitaljukebox.utils.Constants;
import cs.ua.edu.digitaljukebox.views.PlaylistViews.PlaylistAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHandler {

  /**
   * name + uri
   */
  public class Song {
    public String name;
    public String uri;
    public Song(String name, String uri) {
      this.name = name;
      this.uri = uri;
    }
  }

  /**
   * name + iamge-url
   */
  public class CurrentTrack {
    public String trackName;
    public String coverImageUrl;
    public CurrentTrack(String trackName, String coverImageUrl) {
      this.trackName = trackName;
      this.coverImageUrl = coverImageUrl;
    }
  }

  private OkHttpClient client;

  private Context context;

  public HttpHandler(Context context) {
    this.context = context;
    // To allow http request in main thread
    StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
    StrictMode.setThreadPolicy(policy);
    client = new OkHttpClient();
  }

  /**
   * Fetch profile JSONObject using Spotify web api.
   * @return Profile JSONObject
   */
  public JSONObject fetchProfile(String accessToken) {
    Request request = new Request.Builder()
        .url(Constants.PROFILE_URL)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();
    Response response = null;

    System.out.println("Profile Request: " + request.headers().toString());
    try {
      response = client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String jsonData;
    JSONObject jsonObject = null;
    try {
      jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject;
  }

  /**
   * Fetch a playlist
   * @param accessToken
   */
  public JSONObject fetchPlaylist(String accessToken) {
    Request request = new Request.Builder()
        .url(Constants.FETCH_LIST_URL)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();
    Response response = null;

    System.out.println("Fetch List Request: " + request.headers().toString());
    try {
      response = client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String jsonData;
    JSONObject jsonObject = null;
    try {
      jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    System.out.println("List response: " + jsonObject.toString());
    return jsonObject;
  }

  /**
   * Add a track to the playlist
   * @param trackName
   * @param playlistId
   * @param accessToken
   */
  public void addTrackToPlaylist(String trackName, String playlistId, String accessToken) {
    String trackUri = searchTrack(trackName, accessToken).uri;
    trackUri = trackUri.replace(":", "%3A");
    StringBuilder sb = new StringBuilder();
    sb.append(Constants.ADD_TRACK_URL_1).append(playlistId)
        .append(Constants.ADD_TRACK_URL_2).append(trackUri);
    RequestBody requestBody = RequestBody.create(null, new byte[]{});
    Request request = new Request.Builder()
        .url(sb.toString())
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .post(requestBody)
        .build();
    Response response = null;
    try {
      response = client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("Add track response: " + response.toString());
  }

  /**
   * Search a track's uri
   * @param track
   * @param accessToken
   * @return The track's a uri.
   */
  public Song searchTrack(String track, String accessToken) {
    track = track.replace(" ", "%20");
    StringBuilder sb = new StringBuilder();
    sb.append(Constants.SEARCH_TRACK_URL_1).append(track).append(Constants.SEARCH_TRACK_URL_2);
    Request request = new Request.Builder()
        .url(sb.toString())
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    Response response = null;
    try {
      response = client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Search Track URL: " + sb.toString());
    System.out.println("Fetch List Request: " + request.headers().toString());

    String jsonData;
    JSONObject jsonObject = null;
    try {
      jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    String trackUri = null;
    String trackName = null;
    try {
      System.out.println("Search result: " + jsonObject.toString());
      trackUri = jsonObject.getJSONObject("tracks").getJSONArray("items").getJSONObject(0)
          .getString("uri");
      trackName = jsonObject.getJSONObject("tracks").getJSONArray("items").getJSONObject(0)
          .getString("name");
      System.out.println(">>>>>>>" + trackName);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    System.out.println("Track URI: " + trackUri);
    return new Song(trackName, trackUri);
  }

  /**
   * Get all tracks from the playlist and put them into a list.
   * @param playlistId
   * @param accessToken
   * @return a list of tracks from a playlist
   */
  public List<Track> getTracksFromPlaylist(String playlistId, String accessToken,
                                           PlaylistAdapter adapter , RecyclerView mRecyclerView,
                                           TextView mTextView) {
    StringBuilder sb = new StringBuilder();
    sb.append(Constants.GET_TRACKS_OF_PLAYLIST_1).append(playlistId)
        .append(Constants.GET_TRACKS_OF_PLAYLIST_2);
    Request request = new Request.Builder()
        .url(sb.toString())
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    Response response;
    JSONObject jsonObject;
    List<Track> tracks = new ArrayList<>();
    try {
      response = client.newCall(request).execute();
      String jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);

      System.out.println("Check: " + jsonObject.toString());

      JSONArray trackJsonArray = jsonObject.getJSONArray("items");
      System.out.println(trackJsonArray);
      for (int i = 0; i < trackJsonArray.length(); i++) {
        String trackName = trackJsonArray.getJSONObject(i).getJSONObject("track").getString("name");
        String trackId = parseTrackId(trackJsonArray.getJSONObject(i).getJSONObject("track")
            .getString("href"));
        Track track = new Track(trackName, trackId);
        tracks.add(track);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    if (tracks.isEmpty()) {
      mRecyclerView.setVisibility(View.GONE);
      mTextView.setVisibility(View.VISIBLE);
    } else {
      mRecyclerView.setVisibility(View.VISIBLE);
      mTextView.setVisibility(View.GONE);

      adapter.setmTracks(tracks);
    }

    return tracks;
  }

  public List<Track> getTracksFromPlaylistNoUi(String playlistId, String accessToken) {
    StringBuilder sb = new StringBuilder();
    sb.append(Constants.GET_TRACKS_OF_PLAYLIST_1).append(playlistId)
        .append(Constants.GET_TRACKS_OF_PLAYLIST_2);
    Request request = new Request.Builder()
        .url(sb.toString())
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    Response response;
    JSONObject jsonObject;
    List<Track> tracks = new ArrayList<>();
    try {
      response = client.newCall(request).execute();
      String jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);

      System.out.println("Check: " + jsonObject.toString());

      JSONArray trackJsonArray = jsonObject.getJSONArray("items");
      System.out.println(trackJsonArray);
      for (int i = 0; i < trackJsonArray.length(); i++) {
        String trackName = trackJsonArray.getJSONObject(i).getJSONObject("track").getString("name");
        String trackId = parseTrackId(trackJsonArray.getJSONObject(i).getJSONObject("track")
            .getString("href"));
        Track track = new Track(trackName, trackId);
        tracks.add(track);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return tracks;
  }

  /**
   * Reorder the playlist according to the client's upvotes
   * @param accessToken
   * @param playlistId
   * @param start
   * @param before
   */
  public void reorderPlaylist(String accessToken, String playlistId, int start, int before) {
    String jsonBody = "{\"range_start\":" + start +
        ",\"range_length\":1,\"insert_before\":" + before + "}";
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(JSON, jsonBody);

    StringBuilder sb = new StringBuilder();
    sb.append(Constants.REORDER_PLAYLIST_1).append(playlistId)
        .append(Constants.REORDER_PLAYLIST_2);

    Request request = new Request.Builder()
        .url(sb.toString())
        .put(body)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    try {
      client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Play the first song (the song with the most votes) in the playlist
   * @param accessToken
   * @param playlist
   */
  public void playTrack(String accessToken, String playlist) {
    String jsonBody = "{\"context_uri\":\"spotify:playlist:" + playlist
        + "\",\"offset\":{\"position\": 0}}";
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(JSON, jsonBody);

    StringBuilder sb = new StringBuilder();
    sb.append(Constants.PLAY_TRACK);

    Request request = new Request.Builder()
        .url(sb.toString())
        .put(body)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    try {
      client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Next track in the playlist
   * @param accessToken
   */
  public void nextTrack(String accessToken) {
    RequestBody requestBody = RequestBody.create(null, new byte[]{});
    Request request = new Request.Builder()
        .url(Constants.NEXT_TRACK)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .post(requestBody)
        .build();
    try {
      client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Pause the track that is currently playing
   * @param accessToken
   */
  public void pauseTrack(String accessToken) {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(null, new byte[]{});

    Request request = new Request.Builder()
        .url(Constants.PAUSE_TRACK)
        .put(body)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    try {
      client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Resume a track.
   * @param accessToken
   */
  public void resumeTrack(String accessToken) {
    RequestBody body = RequestBody.create(null, new byte[]{});

    Request request = new Request.Builder()
        .url(Constants.PLAY_TRACK)
        .put(body)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    try {
      client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public CurrentTrack getCurrentTrackName(String accessToken) {
    Request request = new Request.Builder()
        .url(Constants.GET_CURRENT_TRACK)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer " + accessToken)
        .build();

    Response response;
    JSONObject jsonObject;
    String trackName = null;
    String trackImageUrl = null;
    try {
      response = client.newCall(request).execute();
      String jsonData = response.body().string();
      jsonObject = new JSONObject(jsonData);
      System.out.println("Check: " + jsonObject.toString());
      trackName = jsonObject.getJSONObject("item").getString("name");
      trackImageUrl = jsonObject.getJSONObject("item").getJSONObject("album")
          .getJSONArray("images").getJSONObject(1).getString("url");

      System.out.println("Current track name is: " + trackName);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return new CurrentTrack(trackName, trackImageUrl);
  }

  /* ------------------------------- Private ----------------------------------  */

  /**
   * Parse out trackId from href link.
   * @param href
   * @return trackId
   */
  private String parseTrackId(String href) {
    String[] parts = href.split("/");
    return parts[parts.length - 1];
  }
}
