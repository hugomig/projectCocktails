package e.g.hugom.projectcocktail.ui.Ingredients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import e.g.hugom.projectcocktail.R;

public class IngredientsFragment extends Fragment {

    private static final String urlIngredients = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);
        AsyncLoadIngredients asyncLoadIngredients = new AsyncLoadIngredients();
        asyncLoadIngredients.execute(urlIngredients);



        return root;
    }
}
