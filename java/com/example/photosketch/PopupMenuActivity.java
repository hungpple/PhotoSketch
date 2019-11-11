package com.example.photosketch;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class PopupMenuActivity extends Activity {

    private Button followButton;
    private Button rateButton;
    private Button libraryButton;
    private Button settingButton;

    private ViewFlipper imageSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_menu);

        popupMenu();
        init();

        //Slide Show
        int images[] = {R.drawable.i1, R.drawable.i2,R.drawable.i3,R.drawable.i4,R.drawable.i5,R.drawable.i6,R.drawable.i7,R.drawable.i8};

        for(int image : images){
            slideImages(image);
        }
    }


    public void init(){
        followButton = findViewById(R.id.followButton);
        rateButton = findViewById(R.id.rateButton);
        imageSlide = findViewById(R.id.slide);
        libraryButton = findViewById(R.id.libraryButton);
        settingButton = findViewById(R.id.settingButton);

        setFollowButton(followButton);
        setRateButton(rateButton);
        setLibraryButton(libraryButton);
        setSettingButton(settingButton);

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

    // Pop up menu window
    public void popupMenu(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.7), (int)(height*.58));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes((params));
        getWindow().setElevation(100 * getApplicationContext().getResources().getDisplayMetrics().density);


    }

    // Rate button will lead user to CH play app
    public void setRateButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }



    // Rate button will lead user to CH play app
    public void setLibraryButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
            }
        });
    }


    // Rate button will lead user to CH play app
    public void setSettingButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    // Follow button will lead to developer instagram
    public void setFollowButton(Button button) {
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Uri uri = Uri.parse("http://instagram.com/phuc.hungg");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/phuc.hungg")));
                }
            }
        });
    }



}
