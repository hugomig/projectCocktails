package e.g.hugom.projectcocktail.ui.Ingredients;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;



public class AsyncLoadIngredients extends AsyncTask<String,Void,JSONObject> {


    private AdapterIngredients adapter;

    public AsyncLoadIngredients(AdapterIngredients adapter) {
        this.adapter = adapter;
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String response = readStream(in);
                JSONObject result = new JSONObject(response);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.i("antoine", jsonObject.toString());
        try {
            JSONArray ingredients = jsonObject.getJSONArray("drinks");
            for (int i = 0; i < ingredients.length(); i++) {
                JSONObject ingredient = ingredients.getJSONObject(i);
                String ingredientName = ingredient.getString("strIngredient1");
                adapter.add(ingredientName);
                adapter.notifyDataSetChanged();
            }

        } catch(JSONException e){
            e.printStackTrace();
        }


    }

    private String readStream (InputStream is){
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}

