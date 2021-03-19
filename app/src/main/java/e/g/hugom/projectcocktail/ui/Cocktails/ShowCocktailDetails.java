package e.g.hugom.projectcocktail.ui.Cocktails;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;
import e.g.hugom.projectcocktail.ui.Ingredients.IngredientsFragment;

public class ShowCocktailDetails extends Fragment {

    ArrayList<String> ingredients;
    ArrayList<String> measure;

    private Button btnFav;
    private boolean isFav;

    private String cocktailName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_show_cocktail_details, container, false);

        try {
            JSONObject cocktail = new JSONObject(getArguments().getString("cocktail"));
            Log.i("HUGO",cocktail.toString());

            TextView tvCocktailName = root.findViewById(R.id.tv_cocktail_name);
            cocktailName = cocktail.getString("strDrink");
            tvCocktailName.setText(cocktailName);

            AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader(root,root.findViewById(R.id.iv_cocktail));
            asyncBitmapDownloader.execute(cocktail.getString("strDrinkThumb"));

            ingredients = new ArrayList<String>();
            measure = new ArrayList<String>();

            int i = 1;
            while(!cocktail.isNull("strIngredient"+i)){
                ingredients.add(cocktail.getString("strIngredient"+i));
                if(cocktail.isNull("strMeasure"+i)){
                    measure.add("");
                }
                else {
                    measure.add(cocktail.getString("strMeasure" + i));
                }
                i++;
            }
            ListView lvIngredients = root.findViewById(R.id.lv_ingredients_cocktail);
            RequestQueue queue = MySingleton.getInstance(lvIngredients.getContext()).getRequestQueue();
            lvIngredients.setAdapter(new AdapterIngredientsCocktail(ingredients,measure,getLayoutInflater(),queue));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        isFav = CocktailsFragment.readCocktailsLikes(getActivity()).contains(cocktailName);

        btnFav = root.findViewById(R.id.btn_like_cocktail);
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFav) {
                    removeLike(cocktailName, getActivity());
                }
                else{
                    addLike(cocktailName,getActivity());
                }
            }
        });

        if(isFav){
            btnFav.setText("Remove from favorite");
        }

        return root;
    }

    public void addLike(String cocktailName, Activity activity){
        ArrayList<String> liked = CocktailsFragment.readCocktailsLikes(activity);
        try {
            FileOutputStream fos = activity.openFileOutput(CocktailsFragment.urlFichierLikeCocktail, Context.MODE_PRIVATE);
            for(String cocktail : liked){
                fos.write((cocktail+"\n").getBytes());
            }
            fos.write((cocktailName+"\n").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isFav = true;
        btnFav.setText("Remove from favorite");
    }

    public void removeLike(String cocktailName, Activity activity){
        ArrayList<String> liked = CocktailsFragment.readCocktailsLikes(activity);
        try {
            FileOutputStream fos = activity.openFileOutput(CocktailsFragment.urlFichierLikeCocktail, Context.MODE_PRIVATE);
            for(String cocktail : liked){
                if(!cocktail.equals(cocktailName)) {
                    fos.write((cocktail+"\n").getBytes());
                }
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isFav = false;
        btnFav.setText("Add to favorite");
    }

}

class AdapterIngredientsCocktail extends BaseAdapter {

    private ArrayList<String> ingredients;
    private ArrayList<String> measure;

    private LayoutInflater inflater;
    private RequestQueue queue;

    public AdapterIngredientsCocktail(ArrayList<String> ingredients, ArrayList<String> measure, LayoutInflater inflater, RequestQueue queue){
        this.ingredients = ingredients;
        this.measure = measure;
        this.inflater = inflater;
        this.queue = queue;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ingredients.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.cocktail_ingredient_layout,parent,false);

        TextView tvIngredientName = convertView.findViewById(R.id.cocktail_ingredient_name);
        TextView tvIngredientMeasure = convertView.findViewById(R.id.cocktail_ingredient_measure);
        ImageView ivIngredientPicture = convertView.findViewById(R.id.cocktail_ingredient_picture);

        String urlImg = "https://www.thecocktaildb.com/images/ingredients/" + ingredients.get(position) + ".png";

        ImageRequest imageRequest = new ImageRequest(urlImg, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ivIngredientPicture.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("HUGO","Error volley "+error.toString());
            }
        });

        queue.add(imageRequest);

        tvIngredientName.setText(ingredients.get(position));
        tvIngredientMeasure.setText(measure.get(position));

        return convertView;
    }
}