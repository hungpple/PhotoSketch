package com.example.photosketch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.blurry.Blurry;

import static java.lang.Math.round;

public class SettingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private Switch enebleDarkmode;
    private Spinner langOption;
    private Button saveButton;
    private RatingBar numOfImage;
    private static String lang = "none";
    private static boolean isDarkmodeEnebled = false;
    private int numberOfImageEachRow;
    private ActionMenuView menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        numOfImage = findViewById(R.id.numOfImage);
        setDefaultMinRatingBarValue(numOfImage);

        //Initialize SettingActivity value
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
        String mLang = settings.getString("lang", "0");
        boolean mIsDark = settings.getBoolean("bool", false);
        int mNumOfImage = settings.getInt("int", 3);
        lang = mLang;
        isDarkmodeEnebled = mIsDark;
        numberOfImageEachRow = mNumOfImage;

        setupToolBar();
        init();
    }

    //---------------------- Tool bar-------------------------
    private void setupToolBar() {
        Toolbar t = findViewById(R.id.toolbar);
        menu = t.findViewById(R.id.menu);
        menu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // use amvMenu here
        inflater.inflate(R.menu.menu, this.menu.getMenu());
        MenuItem item = this.menu.getMenu().findItem(R.id.share);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        switch (item.getItemId()) {
            case R.id.menu:
                Toast.makeText(this, "Review", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), PopupMenuActivity.class);
                startActivity(intent);
                Blurry.with(SettingActivity.this).radius(5).sampling(2).onto(viewGroup);
                return true;
            case R.id.home:
                Toast.makeText(this, "Go To LibraryActivity", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //------------------- End of setting up toolbar------------


    // Turn off blurry
    @Override
    protected void onPostResume() {
        Blurry.delete((ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0));
        super.onPostResume();
    }

    public void setDefaultMinRatingBarValue(RatingBar bar){
        if(round(bar.getRating()) <= 0){
            bar.setRating(1);
        }
    }

    public void init(){
        enebleDarkmode = findViewById(R.id.switch1);
        langOption = findViewById(R.id.languageOptions);
        saveButton = findViewById(R.id.saveButton);
        numOfImage.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override public void onRatingChanged(RatingBar ratingBar, float rating,
                                                  boolean fromUser) {
                if(rating<1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        enebleDarkmode.setChecked(isDarkmodeEnebled);
        numOfImage.setRating(numberOfImageEachRow);
        // Set up languages options
        initSpinner(langOption);
    }

    // Set up spinner (Language Options)
    public void initSpinner(Spinner spinner) {
        ArrayList<String> itemss;
        // Show the selected item first
        if(lang.equals("vi")){
            itemss = new ArrayList<String>();
            itemss.add("Vietnamese");
            itemss.add("English");
        } else {
            itemss = new ArrayList<String>();
            itemss.add("English");
            itemss.add("Vietnamese");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemss);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    // Select LANGUAGE
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
        switch (text) {
            //English
            case "English":
                lang = "en";
                break;
            //Vietnamese
            case "Vietnamese":
                lang = "vi";
                break;
            default:
                break;
        }
        setSaveButton(saveButton, lang);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


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

    public void setSaveButton(Button button, final String lang) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(lang.equals("en")){
                    setAppLocale("en");
                } else{
                    setAppLocale("vi");
                }
                if(enebleDarkmode.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    isDarkmodeEnebled = true;
                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    isDarkmodeEnebled = false;
                }
                numberOfImageEachRow = Integer.valueOf(round(numOfImage.getRating()));
                //Persist app setting value, so when the app is restarted, the setting value is still saved
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("lang", lang);
                editor.putBoolean("bool", isDarkmodeEnebled);
                editor.putInt("int", numberOfImageEachRow);
                editor.commit();
                //-------------------------
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                intent.putExtra("numOfImage", numberOfImageEachRow);
                startActivity(intent);
            }
        });
    }
}
