package hu.mobweb.restaurantroulette

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main_menu.*
import java.util.*


class MainMenu : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var selectedRadio: Int = -1
    private var selectedRange: Double = -1.0
    private var gpslat: Double = 0.0
    private var gpslong: Double = 0.0
    private var netlat: Double = 0.0
    private var netlong: Double = 0.0
    private var allRestaurants = mutableListOf<RestaurantRecord>()
    private var accurateLat: Double = 0.0
    private var accurateLong: Double = 0.0
    private var randomres: RestaurantRecord? = null

    var database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        permissionCheck()
        getLocation()

        if(locationNetwork!=null || locationGps!=null) {
            if (locationGps != null && locationNetwork != null) {
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    accurateLat = gpslat
                    accurateLong = gpslong
                } else {
                    accurateLat = netlat
                    accurateLong = netlong
                }
            }
            if(locationGps!=null && locationNetwork==null){
                accurateLat = gpslat
                accurateLong = gpslong
            }
            if(locationGps== null && locationNetwork!= null){
                accurateLat = netlat
                accurateLong = netlong
            }
        } else {
            Toast.makeText(baseContext, "We can't get your location!",
                    Toast.LENGTH_SHORT).show()
        }

        btn_log_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }


        rg_budget.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rg_budget, i ->
            if (i == R.id.rb_one)
                selectedRadio = 1
            if (i == R.id.rb_two)
                selectedRadio = 2
            if (i == R.id.rb_three)
                selectedRadio = 3
        })

        btn_search.setOnClickListener {
                selectedRange = etNumberDecimal.getText().toString().toDouble()


                var getdata = object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            var restaurantName: String = i.child("restaurantName").getValue().toString()
                            var cost: Int = i.child("cost").getValue().toString().toInt()
                            var openHour: Int = i.child("openHour").getValue().toString().toInt()
                            var openMinute: Int = i.child("openMinute").getValue().toString().toInt()
                            var closeHour: Int = i.child("closeHour").getValue().toString().toInt()
                            var closeMinute: Int = i.child("closeMinute").getValue().toString().toInt()
                            var long: Double = i.child("long").getValue().toString().toDouble()
                            var lat: Double = i.child("lat").getValue().toString().toDouble()


                            val newres: RestaurantRecord = RestaurantRecord(restaurantName, cost, openHour, openMinute, closeHour, closeMinute, long, lat)
                            val endPoint: Location = Location("")

                            endPoint.longitude = long
                            endPoint.latitude = lat


                            Log.d("CALCLOCATION", "DISTANCE" + calcLocation(endPoint))

                            if (calcLocation(endPoint) <= selectedRange && inTime(openHour, openMinute, closeHour, closeMinute) && selectedRadio == cost) {
                                allRestaurants.add(newres)

                            }
                        }
                        if (allRestaurants.isNotEmpty()) {
                            allRestaurants.shuffle()
                            randomres = allRestaurants.first()
                            allRestaurants.clear()
                        } else {
                            Toast.makeText(baseContext, "Sorry we can't find a good Restaurant",
                                    Toast.LENGTH_SHORT).show()
                        }

                    }

                }
                if (randomres != null) {
                    var myIntent = Intent(this, AddRestaurantActivity::class.java)
                    myIntent.putExtra("searchoradd", "1")

                    var openMinutestr: String = ""
                    var closeMinutestr: String = ""
                    if (randomres!!.openMinute < 10) {
                        openMinutestr = "0" + randomres!!.openMinute.toString()
                    } else {
                        openMinutestr = "0" + randomres!!.openMinute.toString()
                    }
                    if (randomres!!.closeMinute < 10) {
                        closeMinutestr = "0" + randomres!!.openMinute.toString()
                    } else {
                        closeMinutestr = "0" + randomres!!.openMinute.toString()
                    }

                    myIntent.putExtra("restaurantName", randomres!!.restaurantName)
                    myIntent.putExtra("openHour", randomres!!.openHour.toString())
                    myIntent.putExtra("openMinute", openMinutestr)
                    myIntent.putExtra("closeHour", randomres!!.closeHour.toString())
                    myIntent.putExtra("closeMinute", closeMinutestr)
                    myIntent.putExtra("reslat", randomres!!.lat.toString())
                    myIntent.putExtra("reslong", randomres!!.long.toString())
                    startActivity(myIntent)
                    randomres?.let { randomres = null }
                }
                database.addValueEventListener(getdata)
        }

        btn_add_restaurant.setOnClickListener {



            if(locationNetwork!=null || locationGps!=null) {
                var myIntent = Intent(this, AddRestaurantActivity::class.java)

                if (locationGps != null && locationNetwork != null) {
                    if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                        var gpslatstr: String = gpslat.toString()
                        var gpslongstr: String = gpslong.toString()
                        myIntent.putExtra("lat", gpslatstr)
                        myIntent.putExtra("long", gpslongstr)
                    } else {
                        var netlatstr: String = netlat.toString()
                        var netlongstr: String = netlong.toString()
                        myIntent.putExtra("lat", netlatstr)
                        myIntent.putExtra("long", netlongstr)
                    }
                }
                if(locationGps!=null && locationNetwork==null){
                    var gpslatstr: String = gpslat.toString()
                    var gpslongstr: String = gpslong.toString()
                    myIntent.putExtra("lat", gpslatstr)
                    myIntent.putExtra("long", gpslongstr)
                }
                if(locationGps== null && locationNetwork!= null){
                    var netlatstr: String = netlat.toString()
                    var netlongstr: String = netlong.toString()
                    myIntent.putExtra("lat", netlatstr)
                    myIntent.putExtra("long", netlongstr)
                }
                myIntent.putExtra("searchoradd", "0")
                startActivity(myIntent)

            } else {
                Toast.makeText(baseContext, "We can't get your location!",
                        Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun getLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(hasGps || hasNetwork){
                if (hasGps) {

                    //Android javaslatara benne hagytam, nagyon sirt ha az en permissioncheck fuggvenyemet hasznaltam, amit az OnCreate-ben is
                    if (ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }
                    //Android javaslatara

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0F, object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            locationGps = location
                        }
                    })

                    val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (localGpsLocation != null) {
                        locationGps = localGpsLocation
                        gpslat = locationGps!!.latitude
                        gpslong = locationGps!!.longitude
                    }

                }

                if (hasNetwork) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0F, object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            locationNetwork = location

                        }
                    })

                    val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (localNetworkLocation != null) {
                        locationNetwork = localNetworkLocation
                        netlat = locationNetwork!!.latitude
                        netlong = locationNetwork!!.longitude
                    }
                }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }

    private fun permissionCheck() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ,
                1
            )
        }
    }

    private fun calcLocation(endPoint: Location): Double {

        var startPoint: Location = Location("")

        startPoint.longitude = accurateLong
        startPoint.latitude = accurateLat

        val distance: Double = startPoint.distanceTo(endPoint).toDouble()

        return distance / 1000
    }

    private fun inTime(
        openHour: Int,
        openMinute: Int,
        closeHour: Int,
        closeMinute: Int
    ): Boolean {
        val sdf = SimpleDateFormat("hh:mm")
        val currentDate = sdf.format(Date())


        val stringArrayClose: List<String> = currentDate.split(":")
        val hourNow: Int = stringArrayClose.get(0).toInt()
        val minuteNow: Int = stringArrayClose.get(1).toInt()

        if (hourNow > openHour && hourNow < closeHour) return true
        if(hourNow == openHour && minuteNow >= openMinute) return true
        if(hourNow == closeHour &&minuteNow <= closeMinute) return true
        return false
    }

}

