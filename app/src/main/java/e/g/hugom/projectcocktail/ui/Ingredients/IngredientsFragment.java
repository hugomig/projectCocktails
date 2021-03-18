package e.g.hugom.projectcocktail.ui.Ingredients;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;
import java.util.Collections;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;

public class IngredientsFragment extends Fragment {

    private static final String urlIngredients = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ListView ingredientList = root.findViewById(R.id.ingredient_list);
        RequestQueue queue = MySingleton.getInstance(ingredientList.getContext()).getRequestQueue();
        AdapterIngredients adapter = new AdapterIngredients(getLayoutInflater(), queue);
        AsyncLoadIngredients asyncLoadIngredients = new AsyncLoadIngredients(adapter);
        asyncLoadIngredients.execute(urlIngredients);
        ingredientList.setAdapter(adapter);


        return root;
    }
}

class AdapterIngredients extends BaseAdapter {

    private ArrayList<String> ingredientsNames;
    private LayoutInflater inflater;
    private RequestQueue queue;

    public AdapterIngredients(LayoutInflater inflater, RequestQueue queue){
        this.inflater = inflater;
        ingredientsNames = new ArrayList<>();
        this.queue = queue;
    }


    @Override
    public int getCount() {
        return ingredientsNames.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredientsNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ingredientsNames.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.ingredient_row_layout, parent, false);
        TextView ingredientName = convertView.findViewById(R.id.ingredient_name);
        ingredientName.setText(ingredientsNames.get(position));
        ImageView ingredientPicture = convertView.findViewById(R.id.ingredient_picture);
        ImageRequest imageRequest = new ImageRequest("https://www.thecocktaildb.com/images/ingredients/" + ingredientsNames.get(position) + ".png", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ingredientPicture.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("antoine", error.toString());
            }
        });
        queue.add(imageRequest);
        
        return convertView;
    }

    public void add(String ingredientName){
        ingredientsNames.add(ingredientName);
    }

    public void sort(){
        Collections.sort(ingredientsNames);
    }
}
