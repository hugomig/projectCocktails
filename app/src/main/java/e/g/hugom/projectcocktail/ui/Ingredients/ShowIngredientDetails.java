package e.g.hugom.projectcocktail.ui.Ingredients;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;
import e.g.hugom.projectcocktail.ui.Cocktails.AdapterCocktails;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncBitmapDownloader;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncLoadCocktailsSearch;

public class ShowIngredientDetails extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_show_ingredient_details, container, false );

        String ingredient = getArguments().getString("ingredient");
        Log.i("antoine", ingredient);

        TextView tvIngredientName = root.findViewById(R.id.tv_ingredient_name);
        tvIngredientName.setText(ingredient);

        AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader(root,root.findViewById(R.id.iv_ingredient));
        asyncBitmapDownloader.execute("https://www.thecocktaildb.com/images/ingredients/"+ingredient+".png");

        RequestQueue queue = MySingleton.getInstance(getContext()).getRequestQueue();
        AdapterCocktails adapter = new AdapterCocktails(getLayoutInflater(),queue,root,0,false);
        AsyncLoadCocktailsSearch asyncLoadCocktailsSearch = new AsyncLoadCocktailsSearch(adapter);
        asyncLoadCocktailsSearch.execute("https://www.thecocktaildb.com/api/json/v1/1/filter.php?i="+ingredient);

        ListView lvCocktails = root.findViewById(R.id.lv_cocktails_from_ingredient);
        lvCocktails.setAdapter(adapter);

        return root;
    }
}