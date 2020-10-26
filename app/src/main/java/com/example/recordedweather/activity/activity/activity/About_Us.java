package com.example.recordedweather.activity.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.recordedweather.R;
import com.shashank.sony.fancyaboutpagelib.FancyAboutPage;

public class About_Us extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about__us);



        FancyAboutPage fancyAboutPage=findViewById(R.id.fancyaboutpage);
        //fancyAboutPage.setCoverTintColor(Color.BLUE);  //Optional
        fancyAboutPage.setCover(R.drawable.night); //Pass your cover image
        fancyAboutPage.setName(getString(R.string.app_name));
        fancyAboutPage.setDescription("Recorded weather figures weather day by day and hourly. Weather figure application is a climate channel which has exact Weather data.");
        fancyAboutPage.setAppIcon(R.drawable.app_icon); //Pass your app icon image
        fancyAboutPage.setAppName(getString(R.string.app_name));
        fancyAboutPage.setVersionNameAsAppSubTitle("1.1");
        fancyAboutPage.setAppDescription(getString(R.string.app_desc));



        fancyAboutPage.addEmailLink("digitalforgex@gmail.com");
        /*fancyAboutPage.addEmailLink("shashanksinghal02@gmail.com");     //Add your email id
        fancyAboutPage.addFacebookLink("https://www.facebook.com/shashanksinghal02");  //Add your facebook address url
        fancyAboutPage.addTwitterLink("https://twitter.com/shashank020597");
        fancyAboutPage.addLinkedinLink("https://www.linkedin.com/in/shashank-singhal-a87729b5/");
        fancyAboutPage.addGitHubLink("https://github.com/Shashank02051997");
   */ }

    public void gotoBack(View view) {
        onBackPressed();
    }
}
