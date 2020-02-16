package ie.frient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            val email = email_edit_text_login.text.toString()
            val password = password_edit_text_login.text.toString()
            Log.d("Login", "Attempted Login with email and password $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    //else if successful
                    Log.d("Main", "Successfully signed in user with uid: ${it.result?.user?.uid}")
                    val intent = Intent (this, MessagesActivity::class.java)

                    //brings you back to the home screen of the phone and not back to the login activity
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

                .addOnFailureListener {

                    Log.d("Main", "Failed to sign in user: ${it.message}")
                    Toast.makeText(this, "Failed to sign in  user", Toast.LENGTH_SHORT).show()
                }


        }

        back_to_registration_text_view.setOnClickListener {
            finish()
        }

    }

}