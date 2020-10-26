package com.example.recordedweather.activity.activity.location;

import android.location.Location;

/**
 * Created by Yash on 22/3/17.
 */
public interface NetworkNotifier
{

    void locationUpdates(Location location);
    void locationFailed(String message);
}
