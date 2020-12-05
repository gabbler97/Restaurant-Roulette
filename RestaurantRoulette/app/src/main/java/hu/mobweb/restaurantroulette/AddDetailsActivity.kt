package hu.mobweb.restaurantroulette

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_details.*
import java.util.*


class AddDetailsActivity : AppCompatActivity() {

    private var lat: Double = 0.0
    private var long: Double = 0.0
    private var latstr: String? = ""
    private var longstr: String? = ""
    private var selectedRadio: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        var database = FirebaseDatabase.getInstance().reference
        val intent = intent

        latstr = intent.getStringExtra("clicklat")
        longstr = intent.getStringExtra("clicklong")

        if (latstr != null && longstr != null) {
            lat = latstr!!.toDouble()

            long = longstr!!.toDouble()
        }


        rg_budget_add.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { rg_budget, i ->
            if (i == R.id.rb_one_add)
                selectedRadio = 1
            if (i == R.id.rb_two_add)
                selectedRadio = 2
            if (i == R.id.rb_three_add)
                selectedRadio = 3

        })

        btn_register_restaurant.setOnClickListener {

            if(selectedRadio!=null && etClose.text.toString()!=null && etOpen.text.toString()!= null && etRestaurant_name.text.toString() != null) {
                val cost = selectedRadio
                val open: String = etOpen.text.toString()
                val stringArrayOpen: List<String> = open.split(":")
                val close: String = etClose.text.toString()
                val stringArrayClose: List<String> = close.split(":")


                val restaurantName : String = etRestaurant_name.text.toString()


                val closeHour: Int = stringArrayClose.get(0).toInt()
                val closeMinute: Int = stringArrayClose.get(1).toInt()

                val openHour: Int = stringArrayOpen.get(0).toInt()
                val openMinute: Int = stringArrayOpen.get(1).toInt()

                val sdf = SimpleDateFormat("yyyyMdd hh:mm:ss")
                val currentDate = sdf.format(Date())

                database.child(restaurantName + "@" + currentDate.toString()) .setValue(
                    RestaurantRecord(restaurantName,cost,openHour,openMinute,closeHour,
                        closeMinute,long,lat))

                Toast.makeText(baseContext, "We added your restaurant! ",
                    Toast.LENGTH_SHORT).show()


                startActivity(Intent(this, MainMenu::class.java))
            } else {
                Toast.makeText(baseContext, "Oops, you forgot something!",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }
}