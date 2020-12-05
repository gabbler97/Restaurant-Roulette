package hu.mobweb.restaurantroulette


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions





class AddRestaurantActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lat: Double = 0.0
    private var long: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)


        val intent = intent

        val latstr = intent.getStringExtra("lat")
        val longstr = intent.getStringExtra("long")
        if (latstr != null && longstr!=null) {
            lat = latstr.toDouble()

            long = longstr.toDouble()

        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val intent = intent
        val searchoradd = intent.getStringExtra("searchoradd")
        if (searchoradd.toString().toInt() == 0) {

            mMap.setOnMapClickListener { point ->
                val myIntent = Intent(this, AddDetailsActivity::class.java)
                val clickstrlat: String = point.latitude.toString()
                val clickstrlong: String = point.longitude.toString()
                myIntent.putExtra("clicklat", clickstrlat)
                myIntent.putExtra("clicklong", clickstrlong)
                startActivity(myIntent)
            }


            val myPlace = LatLng(lat, long)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0F));

        } else {

            val latstr = intent.getStringExtra("reslat")
            val longstr = intent.getStringExtra("reslong")
            if (latstr != null && longstr!=null) {
                lat = latstr.toDouble()

                long = longstr.toDouble()

            }

            val myPlace = LatLng(lat,long)
            val restaurantName = intent.getStringExtra("restaurantName")
            val openHour = intent.getStringExtra("openHour")
            val openMinute =  intent.getStringExtra("openMinute")
            val closeHour = intent.getStringExtra("closeHour")
            val closeMinute = intent.getStringExtra("closeMinute")

            var marker : Marker = mMap.addMarker(MarkerOptions().
            position(myPlace).snippet("Nyitvatartás:" + openHour + ":"
                    + openMinute + " - " + closeHour + ":" + closeMinute ).title(restaurantName))
            marker.showInfoWindow()
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 18.0F));
        }
    }
}