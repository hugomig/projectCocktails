package e.g.hugom.projectcocktail.ui.Cocktails;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;

public class CocktailsFragment extends Fragment {

    private static final String urlCocktails = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f=";

    private EditText inptSearchCocktail;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cocktails, container, false);

        ListView lvCocktails = root.findViewById(R.id.lv_cocktails);

        RequestQueue queue = MySingleton.getInstance(lvCocktails.getContext()).getRequestQueue();
        AdapterCocktails adapter = new AdapterCocktails(getLayoutInflater(),queue,root);

        AsyncLoadCocktails asyncLoadCocktails = new AsyncLoadCocktails(adapter);
        asyncLoadCocktails.execute(urlCocktails);

        lvCocktails.setAdapter(adapter);

        inptSearchCocktail = root.findViewById(R.id.inpt_search_cocktail);
        Button btnSearch = root.findViewById(R.id.btn_search_cocktail);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                AsyncLoadCocktailsSearch asyncLoadCocktailsSearch = new AsyncLoadCocktailsSearch(adapter);
                asyncLoadCocktailsSearch.execute("https://www.thecocktaildb.com/api/json/v1/1/search.php?s="+inptSearchCocktail.getText().toString());
            }
        });

        return root;
    }
}

class AdapterCocktails extends BaseAdapter {

    ArrayList<JSONObject> cocktails;
    LayoutInflater inflater;
    RequestQueue queue;
    View root;

    public AdapterCocktails(LayoutInflater inflater, RequestQueue queue, View root){
        cocktails = new ArrayList<JSONObject>();
        this.inflater = inflater;
        this.queue = queue;
        this.root = root;
    }

    public void add(JSONObject cocktail){
        cocktails.add(cocktail);
    }

    public void clear(){
        cocktails = new ArrayList<JSONObject>();
    }

    @Override
    public int getCount() {
        return cocktails.size();
    }

    @Override
    public Object getItem(int position) {
        return cocktails.get(position);
    }

    @Override
    public long getItemId(int position) {
        try {
            return cocktails.get(position).getInt("idDrink");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.cocktail_row_layout,parent,false);
        ImageView cocktailPicture = convertView.findViewById(R.id.cocktail_picture);
        TextView cocktailName = convertView.findViewById(R.id.cocktail_name);
        try {
            cocktailName.setText(cocktails.get(position).getString("strDrink"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            ImageRequest imageRequest = new ImageRequest(cocktails.get(position).getString("strDrinkThumb"), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    cocktailPicture.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("HUGO","Error volley "+error.toString());
                }
            });

            queue.add(imageRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("cocktail",cocktails.get(position).toString());
                Navigation.findNavController(root).navigate(R.id.action_navigation_cocktails_to_navigation_show_cocktail_details,bundle);
            }
        });

        return convertView;
    }
}