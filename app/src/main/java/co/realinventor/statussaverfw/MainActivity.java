package co.realinventor.statussaverfw;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import co.realinventor.statussaverfw.Helpers.Constants;
import co.realinventor.statussaverfw.Helpers.MediaFiles;

public class MainActivity extends AppCompatActivity{
    private ImageView sadImageView;
    private TextView msgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sadImageView = findViewById(R.id.sadImageView);
        msgTextView = findViewById(R.id.msgTextView);

        if(checkWriteExternalPermission()){
            MediaFiles.INSTANCE.initAppDirectrories();

//            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isFirstTime = sharedPref.getBoolean("isFirstTime", true);

            if(isFirstTime){
                startActivity(new Intent(MainActivity.this, ConfigureActivity.class));
                finish();
            }
            else {
                Constants.PATH_WHATSAPP = sharedPref.getString("WHATSAPP_PATH", Constants.PATH_WHATSAPP);
                Constants.GRID_COUNT_IMAGE = sharedPref.getInt("IMAGE_GRID",3);
                Constants.GRID_COUNT_VIDEO = sharedPref.getInt("IMAGE_VIDEO",3);
                Constants.GRID_COUNT_SAVED = sharedPref.getInt("IMAGE_SAVED",3);
                startActivity(new Intent(MainActivity.this, HomesActivity.class));
                finish();
            }
        }

    }

    private boolean checkWriteExternalPermission()
    {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res =  checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void checkPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        MediaFiles.INSTANCE.initAppDirectrories();
                        startActivity(new Intent(MainActivity.this, ConfigureActivity.class));
                        finish();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        sadImageView.setVisibility(View.VISIBLE);
                        msgTextView.setVisibility(View.VISIBLE);
                        Log.e("Permission", "Denied");
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        sadImageView.setVisibility(View.VISIBLE);
                        msgTextView.setVisibility(View.VISIBLE);
                        Log.e("Permission", "Rationaleshouldbeshowm");
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void accessButtonClicked(View view){
        checkPermission();
    }
}


//Deal with ViewPager bug