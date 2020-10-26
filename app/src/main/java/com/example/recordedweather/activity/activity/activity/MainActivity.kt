package com.example.recordedweather.activity.activity.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.recordedweather.R
import com.example.recordedweather.activity.activity.Network_Receiver.MyReceiver
import com.example.recordedweather.activity.activity.PowerMenuUtils
import com.example.recordedweather.activity.activity.location.AcessLocation
import com.example.recordedweather.activity.activity.location.NetworkNotifier
import com.example.recordedweather.activity.activity.location.NetworkUtil
import com.example.recordedweather.activity.activity.modal.CurrentWeatherModal
import com.example.recordedweather.activity.activity.modal.WeatherModal
import com.example.recordedweather.activity.activity.viewmodel.Fetch_Weather_VM
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), View.OnClickListener, NetworkNotifier {

    override fun locationUpdates(location: Location?) {
        println("locationkaif" + location!!.latitude + " " + location!!.longitude)

        lattitude= location!!.latitude.toString()
        longtitude= location!!.longitude.toString()

        if(firstTime) {

            getCompleteAddressString(location!!.latitude, location!!.longitude)

            fetch_Data()

            firstTime = false

        }
    }

    internal var firstTimeNet = true

    private var myReceiver: MyReceiver? = null
    var lattitude:String?=null
    var longtitude:String?=null

    private val onHamburgerMenuDismissedListener = {
        Log.d("Test", "onDismissed hamburger menu") }

    private var vibe: Vibrator? = null
    var activity_view: View? = null
    var menu: View? = null
    var datalayout: RelativeLayout? = null
    var searchbar: EditText? = null
    var loading_anim: LottieAnimationView? = null

    var address: String = ""


    var skyDescription:TextView?=null
    var weekday:TextView?=null
    var date:TextView?=null
    var temperature:TextView?=null
    var precip:TextView?=null
    var uvindex:TextView?=null
    var humidity:TextView?=null
    var search_icon:ImageView?=null

    var day2:TextView?=null
    var day2img:ImageView?=null
    var day2mintemp:TextView?=null
    var day2maxtemp:TextView?=null

    var day3:TextView?=null
    var day3img:ImageView?=null
    var day3mintemp:TextView?=null
    var day3maxtemp:TextView?=null

    var day4:TextView?=null
    var day4img:ImageView?=null
    var day4mintemp:TextView?=null
    var day4maxtemp:TextView?=null


    var day5:TextView?=null
    var day5img:ImageView?=null
    var day5mintemp:TextView?=null
    var day5maxtemp:TextView?=null


    var day6:TextView?=null
    var day6img:ImageView?=null
    var day6mintemp:TextView?=null
    var day6maxtemp:TextView?=null


    var day7:TextView?=null
    var day7img:ImageView?=null
    var day7mintemp:TextView?=null
    var day7maxtemp:TextView?=null

    var bgImage:ImageView?=null
    var no_internet_layout:LinearLayout?=null

    private val AUTOCOMPLETE_REQUEST_CODE = 22
    private val TAG = MainActivity::class.java!!.getSimpleName()

    private val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var networkUtilObj: NetworkUtil? = null


    private var userViewModel: Fetch_Weather_VM? = null
    var firstTime:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_main)





        initViews()


    }

    private fun fetch_Data() {



        var sevendaysurl ="https://weather.cit.api.here.com/weather/1.0/report.json?product=forecast_7days_simple&name=" + address + "&app_id=devportal-demo-20180625&app_code=9v2BkviRwi9Ot26kp2IysQ"

        val current_url="https://weather.cit.api.here.com/weather/1.0/report.json?product=observation&latitude="+lattitude+"&longitude="+longtitude+"&oneobservation=true&app_id=devportal-demo-20180625&app_code=9v2BkviRwi9Ot26kp2IysQ"


        println("sevendaysurl" + sevendaysurl)
        println("current_url" + current_url)





        //fetch current weather data
        userViewModel!!.fetchCurrentData(current_url).observe(this, Observer { userModel ->


            println("current_data" + userModel!!)

            try {
              prepareCurrentData(userModel!!)

            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
        })


        //fetch seven days weather data
        userViewModel!!.fetch7DaysWeather(sevendaysurl).observe(this, Observer { userModel ->


            println("sevendaysdata" + userModel!!)

            try {
                prepareWeatherData(userModel!!)

            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
        })




    }

    private fun prepareCurrentData(userModel: String) {

        var observationarr:JSONArray?=null


        val mainjson=JSONObject(userModel)
        var observations=mainjson.getJSONObject("observations")
        var locationarr=observations.getJSONArray("location")

        val mainjson2=locationarr.getJSONObject(0)
        val observation=mainjson2.getJSONArray("observation")
        val item=observation.getJSONObject(0)




        println("mainjson2_item"+item.getString("temperature"))
        println("locationarr"+locationarr)


        val modal:CurrentWeatherModal=CurrentWeatherModal(
            item.getString("iconName"),
            item.getString("utcTime"),
            item.getString("temperature"),
            item.getString("daylight")
        )


        setDataTOCUrrentUI(modal)



    }



    private fun prepareWeatherData(userModel: String) {


        if(weatherList!=null)
            weatherList.clear()



        val mainjson=JSONObject(userModel)
        var dailyforcast=mainjson.getJSONObject("dailyForecasts")
        var forecastLocation=dailyforcast.getJSONObject("forecastLocation")
        var forecastarr=forecastLocation.getJSONArray("forecast")


        println("kaifsize"+forecastarr.length())
        for(i in 0..forecastarr.length()-1)
        {
            val item=forecastarr.getJSONObject(i)

            weatherList.add(
                WeatherModal(
                item.getString("description"),
                    "",
                item.getString("temperatureDesc"),
                item.getString("highTemperature").substringBefore("."),
                item.getString("lowTemperature").substringBefore("."),
                item.getString("humidity"),
                item.getString("windDesc"),
                item.getString("uvIndex"),
                item.getString("precipitationProbability"),
                item.getString("weekday"),
                    getDate(item.getString("utcTime").substringBefore("T")),
                item.getString("iconLink"),
                item.getString("daylight")

            )
            )
        }



        setDataToUI()


    }

    private fun setDataTOCUrrentUI(modal: CurrentWeatherModal) {
        var skydata:String

        try {
            var sky =   modal.skydescrition.split("_")
            skydata=sky.get(sky.size-1)

        }
        catch (e:Exception)
        {
            skydata=modal.skydescrition
        }

        skyDescription!!.setText(skydata)

        temperature!!.setText(modal.temperature.substringBefore(".")+0x00B0.toChar().toString())


        setBgImage(modal)

    }

    private fun setDataToUI()
    {



        //skyDescription!!.setText(weatherList.get(0).skyDescription)


        //According to Weather


        weekday!!.setText(weatherList.get(0).weekday)
        date!!.setText(weatherList.get(0).utcTime)
       // temperature!!.setText(weatherList.get(0).highTemperature+0x00B0.toChar().toString())


        precip!!.setText("Precip:"+weatherList.get(0).precipitationProbability+"%")
        uvindex!!.setText("Uv index:"+weatherList.get(0).uvIndex)
        humidity!!.setText("Humidity:"+weatherList.get(0).humidity+"%")




        //bottom data
        day2!!.setText(weatherList.get(1).weekday.substring(0,3))
        day2mintemp!!.setText(weatherList.get(1).lowTemperature+0x00B0.toChar().toString()+"C")
        day2maxtemp!!.setText(weatherList.get(1).highTemperature+0x00B0.toChar().toString()+"C")
        setImageIcon(weatherList.get(1).iconLink,day2img!!)

         day3!!.setText(weatherList.get(2).weekday.substring(0,3))
        day3mintemp!!.setText(weatherList.get(2).lowTemperature+0x00B0.toChar().toString()+"C")
        day3maxtemp!!.setText(weatherList.get(2).highTemperature+0x00B0.toChar().toString()+"C")
        setImageIcon(weatherList.get(2).iconLink,day3img!!)

         day4!!.setText(weatherList.get(3).weekday.substring(0,3))
        day4mintemp!!.setText(weatherList.get(3).lowTemperature+0x00B0.toChar().toString()+"C")
        day4maxtemp!!.setText(weatherList.get(3).highTemperature+0x00B0.toChar().toString()+"C")
        setImageIcon(weatherList.get(3).iconLink,day4img!!)

         day5!!.setText(weatherList.get(4).weekday.substring(0,3))
        day5mintemp!!.setText(weatherList.get(4).lowTemperature+0x00B0.toChar().toString()+"C")
        day5maxtemp!!.setText(weatherList.get(4).highTemperature+0x00B0.toChar().toString()+"C")
        setImageIcon(weatherList.get(4).iconLink,day5img!!)

         day6!!.setText(weatherList.get(5).weekday.substring(0,3))
        day6mintemp!!.setText(weatherList.get(5).lowTemperature+0x00B0.toChar().toString()+"C")
        day6maxtemp!!.setText(weatherList.get(5).highTemperature+0x00B0.toChar().toString()+"C")
        setImageIcon(weatherList.get(5).iconLink,day6img!!)

         day7!!.setText(weatherList.get(6).weekday.substring(0,3))
        day7mintemp!!.setText(weatherList.get(6).lowTemperature+0x00B0.toChar().toString()+"C")
        day7maxtemp!!.setText(weatherList.get(6).highTemperature+0x00B0.toChar().toString()+"C")
        setImageIcon(weatherList.get(6).iconLink,day7img!!)


        visibleAnimation(false)
        datalayout!!.visibility=View.VISIBLE




    }

    private fun setBgImage(modal: CurrentWeatherModal) {

        println("sallu"+skyDescription!!.text.toString())
        println("sallu__22"+modal.daylight)

        try
        {

            if(modal.daylight.equals("D")) {

                if (skyDescription!!.text.toString().equals("sunny"))
                    bgImage!!.setImageResource(R.drawable.sunny_bg)
                else if (skyDescription!!.text.toString().equals("tstorms"))
                    bgImage!!.setImageResource(R.drawable.tstorms_bg)
                else if (skyDescription!!.text.toString().equals("cloudy"))
                    bgImage!!.setImageResource(R.drawable.cloud_bg)
                else if (skyDescription!!.text.toString().equals("showers") || skyDescription!!.text.toString().equals(
                        "sprinkles"
                    )
                )
                    bgImage!!.setImageResource(R.drawable.rainy_bg)

            }
            else
                bgImage!!.setImageResource(R.drawable.night)



        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
    private fun setImageIcon(url: String,into :ImageView) {

          Glide.with(getApplicationContext())
                .load(url).
                into(into);

    }

    fun isNetworkAvailable(context: Context)//check internet of device
            : Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
    }

    override fun onResume() {
        checkPermissions()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(myReceiver, filter)

        super.onResume()
    }





    fun broadcast_Receiver() {
        if (firstTimeNet == false) {
            println("broadcast_Receiver_method_called")

            visibleNoInternetLayout(false)
            visibleAnimation(true)
            fetch_Data()

            println("net_connected")
        }
        firstTimeNet = false
    }

    public override fun onStop() {
        super.onStop()
        unregisterReceiver(myReceiver)

    }


    private fun initViews() {

        userViewModel = ViewModelProviders.of(this).get(Fetch_Weather_VM::class.java)



        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }


        hamburgerMenu =
            PowerMenuUtils.getHamburgerPowerMenu(
                this, this, onHamburgerItemClickListener, onHamburgerMenuDismissedListener
            )

        activity_view = getWindow().getDecorView().getRootView()
        vibe = this@MainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


        myReceiver = MyReceiver(this)


        activity_view = findViewById(R.id.menu)
        menu = findViewById(R.id.menu)
        searchbar = findViewById(R.id.searchbar)
        searchbar!!.keyListener=null
        datalayout = findViewById(R.id.datalayout)
        loading_anim = findViewById(R.id.loading_anim)
        bgImage = findViewById(R.id.bgImage)
        no_internet_layout = findViewById(R.id.no_internet_layout)




        skyDescription = findViewById(R.id.skyDescription)
        weekday = findViewById(R.id.weekday)
        date = findViewById(R.id.date)
        temperature = findViewById(R.id.temperature)
        precip = findViewById(R.id.precip)
        uvindex = findViewById(R.id.uvindex)
        humidity = findViewById(R.id.humidity)
        search_icon = findViewById(R.id.search_icon)

        day2 = findViewById(R.id.day2)
        day2img = findViewById(R.id.day2img)
        day2mintemp = findViewById(R.id.day2temp)
        day2maxtemp = findViewById(R.id.day2maxtemp)


        day3 = findViewById(R.id.day3)
        day3img = findViewById(R.id.day3img)
        day3mintemp = findViewById(R.id.day3temp)
        day3maxtemp = findViewById(R.id.day3maxtemp)


        day4 = findViewById(R.id.day4)
        day4img = findViewById(R.id.day4img)
        day4mintemp = findViewById(R.id.day4temp)
        day4maxtemp = findViewById(R.id.day4maxtemp)


        day5 = findViewById(R.id.day5)
        day5img = findViewById(R.id.day5img)
        day5mintemp = findViewById(R.id.day5temp)
        day5maxtemp = findViewById(R.id.day5maxtemp)


        day6 = findViewById(R.id.day6)
        day6img = findViewById(R.id.day6img)
        day6mintemp = findViewById(R.id.day6temp)
        day6maxtemp = findViewById(R.id.day6maxtemp)


        day7 = findViewById(R.id.day7)
        day7img = findViewById(R.id.day7img)
        day7mintemp = findViewById(R.id.day7temp)
        day7maxtemp = findViewById(R.id.day7maxtemp)






        hideKeyboardFrom(applicationContext, activity_view!!)
        celcius.setOnClickListener(this)
        farhen.setOnClickListener(this)
        menu!!.setOnClickListener(this)
        search_icon!!.setOnClickListener(this)



        if (!isNetworkAvailable(applicationContext!!)) run {

            visibleNoInternetLayout(true)
        }

        else
            visibleAnimation(true)


    }

    private fun visibleNoInternetLayout(value: Boolean) {

        if(value)
            no_internet_layout!!.visibility=View.VISIBLE;
        else
            no_internet_layout!!.visibility=View.GONE;


    }


    private fun checkPermissions() {


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            networkUtilObj =
                NetworkUtil(this, this);

            networkUtilObj!!.connectGoogleApiClient();
            println("if_kaif")
            // gotoNextActivity()
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                networkUtilObj =
                    NetworkUtil(this, this);

                println("permission_granted")
                println("elseif_kaif")
                networkUtilObj!!.connectGoogleApiClient();


                // gotoNextActivity2(3000)

            } else {
                println("elseif_sayed")
                val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 2
                AcessLocation.getLocation(
                    this@MainActivity, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
            }
        }


    }


    override fun onPause() {
        super.onPause()

        if (networkUtilObj != null) {
            networkUtilObj!!.disconnectGoogleApiClient()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        println("request_code" + requestCode)


        when (requestCode) {
            2 -> {
                val perms = HashMap<String, Int>()

                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED

                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]

                if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                    networkUtilObj =
                        NetworkUtil(
                            this,
                            this
                        )

                    networkUtilObj!!.connectGoogleApiClient()
                }



                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    val should = ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    println("shoutresult" + should)

                    if (should) {
                        val builder =
                            AlertDialog.Builder(this@MainActivity, R.style.MyAlertDialogStyle)
                        builder.setTitle(R.string.permission_denied)
                        builder.setMessage(R.string.permission_access_fine_location)
                        builder.setPositiveButton(R.string.i_am_sure,
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        builder.setNegativeButton(R.string.re_try,
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                                )
                            })

                        println("show_called")
                        builder.show()

                    }
                }
            }

            1 -> {
                val perms = HashMap<String, Int>()

                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED

                for (i in permissions.indices)
                    perms[permissions[i]] = grantResults[i]

                if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                    networkUtilObj =
                        NetworkUtil(
                            this,
                            this
                        )

                    networkUtilObj!!.connectGoogleApiClient()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }


    private fun visibleAnimation(value: Boolean) {

        if(value)
        loading_anim!!.visibility=View.VISIBLE
        else
        loading_anim!!.visibility=View.GONE
    }

    fun hideKeyboardFrom(context: Context, view: View) {

        try {

            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {

        }
    }


    override fun onClick(v: View?) {

        if (v!!.id == R.id.search_icon) {
            hideKeyboardFrom(applicationContext, activity_view!!)
            onSearchCalled()
        }
        else if (v!!.id == R.id.celcius) {
            convertCTOF(false)
            celcius.setTextColor(getResources().getColor(R.color.white));
            farhen.setTextColor(getResources().getColor(R.color.black));

            vibe!!.vibrate(80);
            celcius.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            farhen.setBackgroundTintList(getResources().getColorStateList(R.color.gre3));

        }
        else  if (v!!.id == R.id.farhen) {
            celcius.setTextColor(getResources().getColor(R.color.black));
            farhen.setTextColor(getResources().getColor(R.color.white));
            convertCTOF(true)
            vibe!!.vibrate(80);
            celcius.setBackgroundTintList(getResources().getColorStateList(R.color.gre3));
            farhen.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));

        }
        else if (v!!.id == R.id.menu) {

            val rotation = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate)
            menu!!.startAnimation(rotation)
            vibe!!.vibrate(80);
            if (hamburgerMenu!!.isShowing) {
                hamburgerMenu!!.dismiss()
                return
            }
            hamburgerMenu!!.showAsDropDown(activity_view)
        }


    }

    private fun convertCTOF(vale: Boolean) {



        //CTOF
        if(vale) {
            //(30°C x 1.8) + 32 = 86°F
            val a = (weatherList.get(0).highTemperature.toInt() * 1.8) + 32
            temperature!!.setText(a.toString().substringBefore(".")+0x00B0.toChar().toString())
        }
        else
            temperature!!.setText(weatherList.get(0).highTemperature+0x00B0.toChar().toString())

    }


    fun onSearchCalled() {

        // Set the fields to specify which types of place data to return.
        val fields = Arrays.asList<Place.Field>(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        // Start the autocomplete intent.
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i(TAG, "Place: " + place.name + ", " + place.id + ", " + place.address)

                searchbar!!.setText(place.address)

                //lat/lng: (
                // 29.5347633,-98.57461389999999)
                val latlong=place.latLng.toString().substringAfter("(")
                val lat=latlong.substringBefore(",")
                val long=latlong.substringAfterLast(",",")").substringBefore(")")


                println("lalttlong1"+latlong)

                println("kaif_lat"+lat)
                println("kaif_long"+long)


                lattitude=lat
                longtitude=long

                address = place.address!!

                try {
                    //get City from Address
                    getCityFromAddress(place.address!!)
                }catch (e:Exception)
                {
                    e.printStackTrace()
                }







            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(
                    this@MainActivity,
                    "Error: " + status.statusMessage!!,
                    Toast.LENGTH_LONG
                ).show()
                Log.i(TAG, status.statusMessage!!)
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private fun getCityFromAddress(addressss: String) {


        println("getcityFrom"+addressss)


        val address1 = addressss.split(",").toTypedArray()


        println("sizeofaddress"+address1.size)

        println("city"+address1.get(address1.size-3))
        println("state"+address1.get(address1.size-2))
        println("country"+address1.get(address1.size-1))



        address=address1.get(address1.size-3)+address1.get(address1.size-2)+address1.get(address1.size-1)



        visibleAnimation(true)
        fetch_Data()
    }

    override fun onBackPressed() {
        if (hamburgerMenu!!.isShowing)
            hamburgerMenu!!.dismiss()
        else
            super.onBackPressed()
    }






    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")

                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                address = strReturnedAddress.toString()
                searchbar!!.setText(strReturnedAddress.toString())

                // Log.w("My Current loction address", strReturnedAddress.toString())
            } else {
                Log.e("Sallu", "Canont get Address!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Sallu", "Canont get Address!")
        }

        return strAdd
    }


    override fun locationFailed(message: String?) {
    }

    private var hamburgerMenu: PowerMenu? = null

    var weatherList = ArrayList<WeatherModal>()


    private val onHamburgerItemClickListener =
        OnMenuItemClickListener<PowerMenuItem> { position, item ->
            // hamburgerMenu!!.selectedPosition = position

            if(!firstTime) {

                if (item.title.equals(getString(R.string.rate_me)))
                    rateMe()
                else if (item.title.equals(getString(R.string.share)))
                    share()
                else if (item.title.equals(getString(R.string.about)))
                    gotoABoutActivity()
            }


        }

    private fun gotoABoutActivity() {

        startActivity(Intent(applicationContext,About_Us::class.java));


    }

    private fun rateMe() {

        println("rate_me_called")

        val builder:MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this, R.style.RoundShapeTheme)

        val dialog = builder.create()
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.rate_me_layout, null)
        val rate_me:TextView = dialogLayout.findViewById(R.id.rate_me_text)
        rate_me.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            goto_Playstore_for_rateMe()
        })
        dialog.setView(dialogLayout)
        dialog.show()

    }

    fun goto_Playstore_for_rateMe() {

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }

    }

    fun share() {

        val post_link = "http://play.google.com/store/apps/details?id=$packageName"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val shareBodyText = "Share the app to your friends"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Currency Converter")
        intent.putExtra(Intent.EXTRA_TEXT, post_link)
        startActivity(Intent.createChooser(intent, "Choose sharing method"))
    }

    private fun getDate(date: String):String {

        //convert yy-mm-dd to mm-dd-yy format
        var df_in =  SimpleDateFormat("yyyy-MM-dd");
        var df_output =  SimpleDateFormat("dd-MM-yyyy");
        var date2:Date?=null;
        try {
            date2 = df_in.parse(date);
        } catch (e:ParseException) {
            e.printStackTrace();
        }

        //return df_output.format(date2)




        val sdf =  SimpleDateFormat("dd-MM-yyyy", java.util.Locale.ENGLISH);
        val myDate:Date = sdf.parse(df_output.format(date2));
        sdf.applyPattern("d MMM yyyy");
        return sdf.format(myDate);

    }



}


