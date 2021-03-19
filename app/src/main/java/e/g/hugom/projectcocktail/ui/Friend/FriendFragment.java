package e.g.hugom.projectcocktail.ui.Friend;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import e.g.hugom.projectcocktail.MySingleton;
import e.g.hugom.projectcocktail.R;
import e.g.hugom.projectcocktail.ui.Cocktails.AsyncBitmapDownloader;

public class FriendFragment extends Fragment {

    private static final int PERMISSION_SEND_SMS = 123;
    private static final int PERMISSION_READ_CONTACT = 124;
    private static final int PICK_CONTACT = 1;

    private AdapterChooseIngredients adapter;
    private String cocktailNameStr;

    private String phoneNumber = "";
    private String contactName;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend, container, false);
        Button btnSendMsg = root.findViewById(R.id.btn_send_msg);
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSmsPermission();
            }
        });
        btnSendMsg.setVisibility(View.INVISIBLE);

        root.findViewById(R.id.btn_choose_cocktail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_navigation_friend_to_navigation_choose_cocktail);
            }
        });

        TextView tvChooseIngredient = root.findViewById(R.id.tv_select_ingredients);
        tvChooseIngredient.setVisibility(View.INVISIBLE);

        Button btnChooseContact = root.findViewById(R.id.btn_choose_contact);
        btnChooseContact.setVisibility(View.INVISIBLE);

        if(getArguments() != null){
            if(getArguments().containsKey("cocktail")){
                try {
                    JSONObject cocktail = new JSONObject(getArguments().getString("cocktail"));
                    ImageView cocktailPicture = root.findViewById(R.id.cocktail_picture);
                    TextView cocktailName = root.findViewById(R.id.cocktail_name);
                    AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader(root,cocktailPicture);
                    asyncBitmapDownloader.execute(cocktail.getString("strDrinkThumb"));
                    cocktailNameStr = cocktail.getString("strDrink");
                    cocktailName.setText(cocktailNameStr);

                    ListView lvIngredients = root.findViewById(R.id.lv_choose_ingredients);
                    RequestQueue queue = MySingleton.getInstance(lvIngredients.getContext()).getRequestQueue();
                    adapter = new AdapterChooseIngredients(getLayoutInflater(),cocktail,queue);
                    lvIngredients.setAdapter(adapter);

                    tvChooseIngredient.setVisibility(View.VISIBLE);
                    btnChooseContact.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        btnChooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReadContactPermission();
            }
        });
        this.root = root;
        return root;
    }

    private void requestSmsPermission() {
        // check permission is given
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSms();
        }
    }

    private void requestReadContactPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACT);
        }
        else{
            getContact();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSms();
                } else {
                    // permission denied
                    Toast.makeText(getContext(),"Message not sended you should allow the app to send it",Toast.LENGTH_LONG).show();
                }
                return;
            }

            case PERMISSION_READ_CONTACT: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContact();
                }
                else {
                    Toast.makeText(getContext(),"You need to allow to read contacts to send a message to your firend",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void sendSms(){
        ArrayList<String> ingredientsOwned = new ArrayList<String>();
        ArrayList<String> ingredientsDont = new ArrayList<String>();

        for(int i=0;i<adapter.getCount();i++){
            if(adapter.isChecked(i)){
                ingredientsOwned.add((String)adapter.getItem(i));
            }
            else{
                ingredientsDont.add((String)adapter.getItem(i));
            }
        }

        SmsManager smsManager = SmsManager.getDefault();
        String message = "Hello !\nLets drink a cocktail together !\nI propose you to drink a "+cocktailNameStr;
        String message2 = "";
        if(ingredientsOwned.size()>0){
            message2 += "\nI have :";
            for(String ingredient : ingredientsOwned){
                message2 += "\n - "+ingredient;
            }
        }
        if(ingredientsDont.size()>0){
            message2 += "\nCan you bring :";
            for(String ingredient : ingredientsDont){
               message2 += "\n - "+ingredient;
            }
        }
        if(message.length() + message2.length() >159){
            smsManager.sendTextMessage(phoneNumber,null,message,null,null);
            smsManager.sendTextMessage(phoneNumber,null,message2,null,null);
        }
        else{
            message += message2;
            smsManager.sendTextMessage(phoneNumber,null,message,null,null);
        }

        Toast.makeText(getContext(),"Message sended to "+contactName,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContext().getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            hasPhone = "true";
                        } else {
                            hasPhone = "false";
                        }

                        if (Boolean.parseBoolean(hasPhone)) {
                            Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            if(phones.moveToFirst()){
                                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                TextView tvPhoneNumber = root.findViewById(R.id.tv_phone_number);
                                if(!phoneNumber.equals("")){
                                    tvPhoneNumber.setText(contactName+" - "+phoneNumber);
                                    root.findViewById(R.id.btn_send_msg).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    public void getContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,PICK_CONTACT);
    }
}

class AdapterChooseIngredients extends BaseAdapter {

    ArrayList<String> ingredients;
    HashMap<Integer,Boolean> areChecked;
    LayoutInflater inflater;
    RequestQueue queue;

    public AdapterChooseIngredients(LayoutInflater inflater, JSONObject cocktail, RequestQueue queue){
        this.inflater = inflater;
        this.ingredients = new ArrayList<String>();
        this.queue = queue;
        this.areChecked = new HashMap<Integer, Boolean>();
        int i = 1;
        while(!cocktail.isNull("strIngredient"+i)){
            try {
                ingredients.add(cocktail.getString("strIngredient"+i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ingredients.get(position).hashCode();
    }

    public boolean isChecked(int position){
        if(!areChecked.containsKey(position)){
            return false;
        }
        return areChecked.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.choose_ingredients_row_layout, parent, false);

        TextView ingredientName = convertView.findViewById(R.id.ingredient_name);
        ImageView ingredientPicture = convertView.findViewById(R.id.ingredient_picture);
        CheckBox ingredientCb = convertView.findViewById(R.id.cb_ingredient);

        ingredientCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                areChecked.put(position,isChecked);
            }
        });

        if(areChecked.containsKey(position)){
            ingredientCb.setChecked(areChecked.get(position));
        }

        ingredientName.setText(ingredients.get(position));
        String url = "https://www.thecocktaildb.com/images/ingredients/" + ingredients.get(position) + ".png";

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                ingredientPicture.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(imageRequest);
        return convertView;
    }
}