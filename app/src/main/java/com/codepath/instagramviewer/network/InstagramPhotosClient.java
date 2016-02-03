package com.codepath.instagramviewer.network;

import android.util.Log;

import com.codepath.instagramviewer.adapters.InstagramPhotosAdapter;
import com.codepath.instagramviewer.models.InstagramPhoto;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Shyam Rokde on 1/30/16.
 */
public class InstagramPhotosClient {
  private static final String CLIENT_ID = "e05c462ebd86446ea48a5af73769b602";


  public void fetchPopularPhotos(final ArrayList<InstagramPhoto> photos, final InstagramPhotosAdapter adapter) {
    String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        // handle response
        Log.i("DEBUG", response.toString());
        try {
          // CLEAR OUT old items before appending in the new ones
          adapter.clear();

          JSONArray photosJSON = response.getJSONArray("data");
          for (int i = 0; i < photosJSON.length(); i++) {
            JSONObject photoJSON = photosJSON.getJSONObject(i);
            // Check type=image
            InstagramPhoto photo = new InstagramPhoto();
            if (photoJSON.optJSONObject("caption") != null) {
              photo.setCaption(photoJSON.getJSONObject("caption").getString("text"));
            }
            if (photoJSON.optJSONObject("user") != null) {
              photo.setUsername(photoJSON.getJSONObject("user").getString("username"));
            }
            if (photoJSON.optJSONObject("likes") != null) {
              photo.setLikeCount(photoJSON.getJSONObject("likes").getInt("count"));
            }
            if (photoJSON.optJSONObject("images") != null) {
              photo.setImageHeight(photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height"));
              photo.setImageUrl(photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
            }

            // Set Photos
            photos.add(photo);
          }
        } catch (Exception ex) {
          Log.i("ERROR", ex.getMessage());
        }

        // callback
        adapter.notifyDataSetChanged();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        // Log Something
        Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
      }
    });
  }

}
