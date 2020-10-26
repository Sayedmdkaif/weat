package com.example.recordedweather.activity.activity.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {


    @GET
    Call<Object> fetch7DaysWeatherReport(@Url String url);


}
