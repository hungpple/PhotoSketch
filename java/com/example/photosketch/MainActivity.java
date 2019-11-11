package com.example.photosketch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.opencv.android.OpenCVLoader;

import java.util.Locale;

//TODO
//  Convert Image to Sketch                     (TOP)
//  Allow sharing on Facebook and Instagram
//  Import image from gallery
//  Import image from camera
//  Save Images to file/database                (TOP)
//  Blur background activity
//  AsyncTask when load the image from file
//  Landscape layout
//


public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private static String languageOption = "none";
    private static boolean isDarkmodeEnebled = false;
    private int numberOfImageEachRow;
    private ViewFlipper imageSlide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initAppSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //Slide Show
        int images[] = {R.drawable.i1, R.drawable.i2,R.drawable.i3,R.drawable.i4,R.drawable.i5,R.drawable.i6,R.drawable.i7,R.drawable.i8};

        for(int image : images){
            slideImages(image);
        }
    }

    private void slideImages(int image){
        ImageView imgView = new ImageView(this);
        imgView.setBackgroundResource(image);

        imageSlide.addView(imgView);
        imageSlide.setFlipInterval(3000);
        imageSlide.setAutoStart(true);

        //animation
        imageSlide.setInAnimation(this, android.R.anim.slide_in_left);
        imageSlide.setOutAnimation(this, android.R.anim.slide_out_right);
    }


    public void init(){
        startButton = findViewById(R.id.startButton);
        imageSlide = findViewById(R.id.slide);
        setButton(startButton);
    }

    public void initAppSetting(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String mLang = settings.getString("lang", "0");
        boolean mIsDark = settings.getBoolean("bool", false);
        int mNumOfImage = settings.getInt("int", 3);
        languageOption = mLang;
        isDarkmodeEnebled = mIsDark;
        numberOfImageEachRow = mNumOfImage;
        //Initialize app lang
        if(languageOption.equals("vi")){
            setAppLocale("vi");
        } else{
            setAppLocale("en");
        }
        //Initialize app dark mode or light mode
        if(isDarkmodeEnebled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    public void setButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("numOfImage", numberOfImageEachRow);
                startActivity(intent);
            }
        });
    }

    // This part is used for initially setting up the app (language and darkmode)
    private void setAppLocale(String localeCode){
        Resources rs = getResources();
        DisplayMetrics dm = rs.getDisplayMetrics();
        Configuration conf = rs.getConfiguration();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            conf.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            conf.locale = new Locale(localeCode.toLowerCase());
        }
        rs.updateConfiguration(conf, dm);
    }
}
