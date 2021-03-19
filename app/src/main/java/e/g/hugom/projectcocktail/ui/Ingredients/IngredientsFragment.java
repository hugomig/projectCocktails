package e.g.hugom.projectcocktail.ui.Ingredients;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;

public class IngredientsFragment extends Fragment {

    private static final String urlIngredients = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";

    public static final String urlFichierLikeIngredients = "ingredientsLikes.txt";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);

        ListView ingredientList = root.findViewById(R.id.ingredient_list);
        RequestQueue queue = MySingleton.getInstance(ingredientList.getContext()).getRequestQueue();
        ArrayList<String> ingredientsLiked = readIngredientsLikes(getActivity());
        AdapterIngredients adapter = new AdapterIngredients(getLayoutInflater(), queue, root, ingredientsLiked);
        AsyncLoadIngredients asyncLoadIngredients = new AsyncLoadIngredients(adapter);
        asyncLoadIngredients.execute(urlIngredients);
        ingredientList.setAdapter(adapter);

        return root;
    }

    public static ArrayList<String> readIngredientsLikes(Activity activity){
        ArrayList<String> ingredientsLiked = new ArrayList<String>();
        try {
            FileInputStream fis = activity.openFileInput(urlFichierLikeIngredients);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while((line = br.readLine()) != null){
                Log.i("HUGO",line);
                ingredientsLiked.add(line);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ingredientsLiked;
    }

}

class AdapterIngredients extends BaseAdapter {

    private ArrayList<String> ingredientsNames;
    private ArrayList<String> ingredientsLiked;
    private LayoutInflater inflater;
    private RequestQueue queue;
    private View root;
    private AssetManager asset;

    public AdapterIngredients(LayoutInflater inflater, RequestQueue queue, View root, ArrayList<String> ingredientsLiked){
        this.inflater = inflater;
        ingredientsNames = new ArrayList<>();
        this.queue = queue;
        this.root = root;
        this.ingredientsLiked = ingredientsLiked;
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("ingredient",ingredientsNames.get(position));
                Navigation.findNavController(root).navigate(R.id.action_navigation_ingredients_to_navigation_show_ingredient_details,bundle);
            }
        });

        if(ingredientsLiked.contains(ingredientsNames.get(position))) {
            ImageView ivLike = convertView.findViewById(R.id.iv_like_ingredient);
            ivLike.setImageBitmap(BitmapFactory.decodeResource(root.getResources(), R.drawable.heart));
        }

        return convertView;
    }

    public void add(String ingredientName){
        ingredientsNames.add(ingredientName);
    }

    public void sort(){
        Collections.sort(ingredientsNames);
    }


}
