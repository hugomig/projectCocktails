package e.g.hugom.projectcocktail.ui.Friend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import e.g.hugom.projectcocktail.R;

public class FriendFragment extends Fragment {

    private static final int PERMISSION_SEND_SMS = 123;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend, container, false);
        root.findViewById(R.id.btn_send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSmsPermission();
            }
        });
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
        }
    }

    public void sendSms(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+33679201212",null,"Salut !",null,null);
    }
}