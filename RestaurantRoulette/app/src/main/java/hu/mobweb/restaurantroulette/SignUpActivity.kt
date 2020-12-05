package hu.mobweb.restaurantroulette

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class  SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser(){

        if(tv_username.text.toString().isEmpty()) {

            tv_username.error = "Please enter your email address"
            tv_username.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()){

            tv_username.error = "Please enter a valid email"
            tv_username.requestFocus()
            return
        }

        if(tv_password.text.toString().isEmpty()){

            tv_password.error = "Please enter a password"
            tv_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString() )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this,LoginActivity::class.java))
                            }
                        }
                } else {
                    Toast.makeText(baseContext, "Sign Up failed. Try again",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
}