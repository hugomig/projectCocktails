package e.g.hugom.projectcocktail.ui.Cocktails;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;

public class CocktailsFragment extends Fragment {

    public static final String urlCocktails = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f=";

    public static final String urlFichierLikeCocktail = "cocktailsLikes.txt";

    private EditText inptSearchCocktail;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cocktails, container, false);

        ListView lvCocktails = root.findViewById(R.id.lv_cocktails);

        RequestQueue queue = MySingleton.getInstance(lvCocktails.getContext()).getRequestQueue();
        ArrayList<String> cocktailsLiked = readCocktailsLikes(getActivity());
        AdapterCocktails adapter = new AdapterCocktails(getLayoutInflater(),queue,root,R.id.action_navigation_cocktails_to_navigation_show_cocktail_details,cocktailsLiked);

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

    public static ArrayList<String> readCocktailsLikes(Activity activity){
        ArrayList<String> cocktailsLiked = new ArrayList<String>();
        try {
            FileInputStream fis = activity.openFileInput(urlFichierLikeCocktail);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while((line = br.readLine()) != null){
                Log.i("HUGO",line);
                cocktailsLiked.add(line);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cocktailsLiked;
    }
}

