package e.g.hugom.projectcocktail.ui.Ingredients;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;
import e.g.hugom.projectcocktail.ui.Cocktails.AdapterCocktails;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncBitmapDownloader;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncLoadCocktailsSearch;
import e.g.hugom.projectcocktail.ui.Cocktails.CocktailsFragment;

public class ShowIngredientDetails extends Fragment {

    private boolean isFav;
    private Button btnFav;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_show_ingredient_details, container, false );

        String ingredient = getArguments().getString("ingredient");
        Log.i("antoine", ingredient);

        TextView tvIngredientName = root.findViewById(R.id.tv_ingredient_name);
        tvIngredientName.setText(ingredient);

        AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader(root,root.findViewById(R.id.iv_ingredient));
        asyncBitmapDownloader.execute("https://www.thecocktaildb.com/images/ingredients/"+ingredient+".png");

        RequestQueue queue = MySingleton.getInstance(getContext()).getRequestQueue();
        ArrayList<String> cocktailsLiked = CocktailsFragment.readCocktailsLikes(getActivity());
        AdapterCocktails adapter = new AdapterCocktails(getLayoutInflater(),queue,root,0,false, cocktailsLiked);
        AsyncLoadCocktailsSearch asyncLoadCocktailsSearch = new AsyncLoadCocktailsSearch(adapter);
        asyncLoadCocktailsSearch.execute("https://www.thecocktaildb.com/api/json/v1/1/filter.php?i="+ingredient);

        ListView lvCocktails = root.findViewById(R.id.lv_cocktails_from_ingredient);
        lvCocktails.setAdapter(adapter);

        isFav = IngredientsFragment.readIngredientsLikes(getActivity()).contains(ingredient);

        btnFav = root.findViewById(R.id.btn_fav_ingredient);
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFav) {
                    removeLike(ingredient, getActivity());
                }
                else{
                    addLike(ingredient,getActivity());
                }
            }
        });

        if(isFav){
            btnFav.setText("Remove from favorite");
        }

        return root;
    }

    public void addLike(String ingredientName, Activity activity){
        ArrayList<String> liked = IngredientsFragment.readIngredientsLikes(activity);
        try {
            FileOutputStream fos = activity.openFileOutput(IngredientsFragment.urlFichierLikeIngredients, Context.MODE_PRIVATE);
            for(String ingr : liked){
                fos.write((ingr+"\n").getBytes());
            }
            fos.write((ingredientName+"\n").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isFav = true;
        btnFav.setText("Remove from favorite");
    }

    public void removeLike(String ingredientName, Activity activity){
        ArrayList<String> liked = IngredientsFragment.readIngredientsLikes(activity);
        try {
            FileOutputStream fos = activity.openFileOutput(IngredientsFragment.urlFichierLikeIngredients, Context.MODE_PRIVATE);
            for(String ingr : liked){
                if(!ingr.equals(ingredientName)) {
                    fos.write((ingr+"\n").getBytes());
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