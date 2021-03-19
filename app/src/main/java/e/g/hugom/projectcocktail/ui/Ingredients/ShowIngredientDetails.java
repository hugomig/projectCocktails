package e.g.hugom.projectcocktail.ui.Ingredients;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import e.g.hugom.projectcocktail.R;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncBitmapDownloader;

public class ShowIngredientDetails extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_show_ingredient_details, container, false );

                try {
                    JSONObject ingredient = new JSONObject(getArguments().getString("ingredient"));
                    //Log.i("antoine", ingredient.toString());

                    TextView tvIngredientName = root.findViewById(R.id.tv_ingredient_name);
                    tvIngredientName.setText(ingredient.getString("strIngredient"));

                    AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader(root,root.findViewById(R.id.iv_ingredient));
                    asyncBitmapDownloader.execute(ingredient.getString("strDrinkThumb"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        return null;
    }
}
