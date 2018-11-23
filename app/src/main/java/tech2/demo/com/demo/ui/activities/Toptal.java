package tech2.demo.com.demo.ui.activities;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Joel on 02-Mar-16.
 */
public class Toptal extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
