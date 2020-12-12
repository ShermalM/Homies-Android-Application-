package humber.college.homies;
//Team Name: Homies
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile_page extends Fragment {
    SharedPreferences USR;
    public String Name;
    CallbackManager callbackManager;
    public String first_name,last_name;
    public ImageView image;
    TextView textview,textview2,textview3,textview4,textview5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getContext());
        final View view = inflater.inflate(R.layout.profile_layout, container, false);

        USR = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String username = USR.getString("usernameStorage", getString(R.string.nothing_found));
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PROFILES");

        //Intials
        textview = view.findViewById(R.id.UserName);
        textview2 = view.findViewById(R.id.add_number);
        textview3 = view.findViewById(R.id.add_price);
        textview4 = view.findViewById(R.id.Roommates);
        textview5 = view.findViewById(R.id.Description);
        image = view.findViewById(R.id.profilePic);
        image.setVisibility(View.INVISIBLE);

        // Faceboook Stuff
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            image.setVisibility(View.VISIBLE);
            getUserProfile(AccessToken.getCurrentAccessToken());
        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(username).exists()&&isLoggedIn==false){
                    image.setVisibility(View.VISIBLE);
                    ProfileData data = snapshot.child(username).getValue(ProfileData.class);
                    if(data.getProfilepicture() == null){
                        image.setImageResource(R.drawable.profile_icon);
                    }
                    else{
                        image.setImageBitmap(string_toImage(data.getProfilepicture()));
                    }
                    textview.setText(getString(R.string.profileName) +data.getUsername());
                    textview2.setText(getString(R.string.profileAge) +data.getAge());
                    textview3.setText(getString(R.string.profilePhone) +data.getPhoneNumber());
                    textview4.setText(getString(R.string.profileRoommates) +data.getRoomMates());
                    textview5.setText(data.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    } //end of oncreate

    public Bitmap string_toImage(String img) {
        String default_img ="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";
        Bitmap error_img;
        if (!img.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(img, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            return bitmap;
        } else {
            byte[] b = Base64.decode(default_img, Base64.DEFAULT);
            error_img = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return error_img;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            first_name= object.getString("first_name");
                            last_name = object.getString("last_name");
                            textview.setText(getString(R.string.profileName) +first_name+" "+last_name);
                            textview2.setText(getString(R.string.profileAge) +getString(R.string.NA));
                            textview3.setText(getString(R.string.profilePhone) +getString(R.string.NA));
                            textview4.setText(getString(R.string.profileRoommates) +getString(R.string.NA));
                            textview5.setText(getString(R.string.editprofile_description)+getString(R.string.NA));
                            String id = "https://graph.facebook.com/"+object.getString("id")+"/picture?type=normal";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
}