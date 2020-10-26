package com.example.recordedweather.activity.activity.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recordedweather.activity.activity.retrofit.ApiClient
import com.example.recordedweather.activity.activity.retrofit.ApiInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Fetch_Weather_VM : ViewModel() {
    var sevendaysres_object: MutableLiveData<String>? = null
    var currentres_object: MutableLiveData<String>? = null

    lateinit var apiInterface: ApiInterface


    fun fetch7DaysWeather(url: String): MutableLiveData<String> {

        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        sevendaysres_object = MutableLiveData()
        fetchWeather_API(url, "seven")

        return sevendaysres_object as MutableLiveData<String>

    }


    fun fetchCurrentData(url: String): MutableLiveData<String> {

        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
        currentres_object = MutableLiveData()
        fetchWeather_API(url, "current")

        return currentres_object as MutableLiveData<String>

    }


    fun fetchWeather_API(url: String, from: String) {
        println("api_url$url")
        val call = apiInterface.fetch7DaysWeatherReport(url)
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

                val postResponse = response.body()

                println("api_response" + postResponse)

                val g = Gson()

                val str: String = g.toJson(postResponse)

                if (from.equals("seven"))
                    sevendaysres_object!!.value = str

                if (from.equals("current"))
                    currentres_object!!.value = str

                println("kaifff" + str)


            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                println("Failed" + t.message)
                call.cancel()

            }
        })


    }


}
