package e.g.hugom.projectcocktail.ui.Cocktails;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AsyncLoadCocktails extends AsyncTask<String,Void, JSONObject> {

    private AdapterCocktails adapter;

    public AsyncLoadCocktails(AdapterCocktails adapter){
        this.adapter = adapter;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        JSONObject jsonObject = new JSONObject();

        String startUrl = strings[0];
        for(char letter = 'a'; letter <= 'z'; letter++) {
            try {
                URL url = new URL(startUrl + letter);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String response = readStream(in);
                    JSONObject result = new JSONObject(response);
                    jsonObject.put(""+letter,result);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            for(char letter='a';letter<='z';letter++) {
                    if(!jsonObject.getJSONObject(""+letter).isNull("drinks")){
                        JSONArray array = jsonObject.getJSONObject(""+letter).getJSONArray("drinks");
                        for(int i=0; i<array.length();i++){
                            adapter.add(array.getJSONObject(i));
                            adapter.notifyDataSetChanged();
                        }
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

}
