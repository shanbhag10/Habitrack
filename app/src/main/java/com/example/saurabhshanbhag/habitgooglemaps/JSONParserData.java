package com.example.saurabhshanbhag.habitgooglemaps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class JSONParserData extends AsyncTask<Void,Void,Void> {

    private static final String TAG = "JSONDataParser";
    private static String placesUrl;
    private static final String APIkey = "AIzaSyCcEjcJng3DGKBc4JAtKwb6ikCuZFoQNaU";
    public static Set<String> categories;

    public JSONParserData (double latitude,double longitude) {

        placesUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+Double.toString(latitude)+","+Double.toString(longitude)+"&key="+APIkey;

    }

    @Override
    protected Void doInBackground(Void... voids) {

            Log.d(TAG," getPlaceName : "+placesUrl);
            URL url = null;
            String response = "";
            try {
                url = new URL(placesUrl);
            } catch (MalformedURLException e) {
                Log.e(TAG,"invalid url");
            }

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                Log.d(TAG," getPlaceName : HTTPConnection done");

                int status = conn.getResponseCode();
                if (status != 200) {
                    Log.e(TAG,"Post failed with error code " + status);
                } else {
                    Log.d(TAG," getPlaceName : Reading Data");
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response+=inputLine;
                    }
                    in.close();
                }
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }

            }

            Log.d(TAG,"JSON Data Response : "+response);

            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            JSONObject data = null;
            try {
                data = (JSONObject)parser.parse(response);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONArray results = (JSONArray) data.get("results");

            categories = new HashSet<>();

            Log.d(TAG," getPlaceName : Converting to JSON");
            for (int i=0;i<results.size();i++)
            {
                JSONObject object = (JSONObject)results.get(i);
                JSONArray types = (JSONArray) object.get("types");

                for (int j=0;j<types.size();j++) {
                    categories.add((String)types.get(j));
                }
            }
            Log.d(TAG,categories.toString());
            return null;
        }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //MainActivity.place.setText(categories.toString());

    }
}
