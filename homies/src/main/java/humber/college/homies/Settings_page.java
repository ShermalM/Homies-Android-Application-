package humber.college.homies;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.AccessToken;

public class Settings_page extends AppCompatActivity {

    private Switch darkSwitch;
    boolean isLoggedIn;
    SharedPreferences USR;
    public static final String MYPREFERENCES = "nightModePrefs";
    public static final String KEY_ISNIGHTMODE = "isNightMode";
    SharedPreferences preferences;
    boolean vali_normal_login;
    boolean vali_face_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Checking if user is still logged
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        USR = getSharedPreferences("spDATABASE",0);


        // If user pressed log out
        //if (!isLoggedIn){
        //    Intent intent = new Intent(getApplicationContext(), Login_page.class);
        //    startActivity(intent);
        //}


        preferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        darkSwitch = (Switch)findViewById(R.id.darkmodeSwitch);
        checkNightModeActivated();

        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);
                    recreate();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveNightModeState(false);
                    recreate();
                }
            }
        });
    }

    private void saveNightModeState(boolean nightMode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_ISNIGHTMODE, nightMode);
        editor.apply();
    }

    public void checkNightModeActivated(){
        if(preferences.getBoolean(KEY_ISNIGHTMODE, false)){
            darkSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            darkSwitch.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onBackPressed() {
        vali_normal_login = false;
        vali_face_login = false;

        // checking if normal loging is good
        if(!USR.getBoolean("logbool",false)){
            vali_normal_login=true;
            //Toast.makeText(getApplicationContext(),"Normal False",Toast.LENGTH_LONG).show();
        }
        if(!isLoggedIn){
            vali_face_login = true;
        }
        if (vali_face_login&&vali_normal_login){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Login again")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), Login_page.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setIcon(R.drawable.ic_baseline_login_24);
            alert.show();
        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        //Toast.makeText(getApplicationContext(),"Back Press",Toast.LENGTH_LONG).show();
    }
}