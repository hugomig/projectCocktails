package e.g.hugom.projectcocktail.ui.Ingredients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import e.g.hugom.projectcocktail.R;

public class IngredientsFragment extends Fragment {

    private static final String urlIngredients = "https://www.thecocktaildb.com/api/json/v1/1/list.php?i=list";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ingredients, container, false);
        AdapterIngredients adapter = new AdapterIngredients(getLayoutInflater());
        AsyncLoadIngredients asyncLoadIngredients = new AsyncLoadIngredients(adapter);
        asyncLoadIngredients.execute(urlIngredients);
        ListView ingredientList = root.findViewById(R.id.ingredient_list);
        ingredientList.setAdapter(adapter);


        return root;
    }
}

class AdapterIngredients extends BaseAdapter {

    private ArrayList<String> ingredientsNames;
    private LayoutInflater inflater;

    public AdapterIngredients(LayoutInflater inflater){
        this.inflater = inflater;
        ingredientsNames = new ArrayList<>();
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


        return convertView;
    }

    public void add(String ingredientName){
        ingredientsNames.add(ingredientName);
    }
}
