package e.g.hugom.projectcocktail.ui.Friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;
import e.g.hugom.projectcocktail.ui.Cocktails.AdapterCocktails;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncLoadCocktails;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncLoadCocktailsSearch;
import e.g.hugom.projectcocktail.ui.Cocktails.CocktailsFragment;

import static e.g.hugom.projectcocktail.ui.Cocktails.CocktailsFragment.urlCocktails;

public class ChooseCocktail extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_cocktail, container, false);

        ListView lvChoose = root.findViewById(R.id.lv_choose_cocktail);

        RequestQueue queue = MySingleton.getInstance(lvChoose.getContext()).getRequestQueue();
        ArrayList<String> cocktailsLiked = CocktailsFragment.readCocktailsLikes(getActivity());
        AdapterCocktails adapter = new AdapterCocktails(getLayoutInflater(), queue, root, R.id.action_navigation_choose_cocktail_to_navigation_friend,cocktailsLiked);

        AsyncLoadCocktails asyncLoadCocktails = new AsyncLoadCocktails(adapter);
        asyncLoadCocktails.execute(urlCocktails);

        lvChoose.setAdapter(adapter);

        EditText inptSearchCocktail = root.findViewById(R.id.inpt_search_choose_cocktail);
        Button btnSearch = root.findViewById(R.id.btn_search_choose_cocktail);
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