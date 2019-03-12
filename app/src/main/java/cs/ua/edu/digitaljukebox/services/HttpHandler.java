package cs.ua.edu.digitaljukebox.services;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cs.ua.edu.digitaljukebox.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHandler {

  private OkHttpClient client;

  public HttpHandler() {
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
    String trackUri = searchTrack(trackName, accessToken);
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
  public String searchTrack(String track, String accessToken) {
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
    try {
      System.out.println("Search result: " + jsonObject.toString());
      trackUri = jsonObject.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("uri");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    System.out.println("Track URI: " + trackUri);
    return trackUri;
  }
}
