package ie.frient

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        register_button.setOnClickListener {

           performRegister()

        }

        already_have_an_account_text_view.setOnClickListener {
            Log.d("RegisterActivity", "Try to show log in activity")

            //launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //running the activity
        profile_image_button_register.setOnClickListener {
            Log.d("MainActivity", "Show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }

    }

    var selectedPhotoUri: Uri? = null

    //catching the activities result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            //check what the selected image was
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data

            //getting access to the bitmap that was selected
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_image_view_register.setImageBitmap(bitmap)

            profile_image_button_register.alpha = 0f

            //val bitmapDrawable = BitmapDrawable(bitmap)
           // profile_image_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister(){

        val email = email_edit_text_registration.text.toString()
        val password = password_edit_text_registration.text.toString()

        if(email.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Email is +email")
        Log.d("RegisterActivity", "Password: $password")

        //Firebase auth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()
            }

            .addOnFailureListener {

                Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user email already in use or password is too short", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully upload image: ${it.metadata?.path}")

                //access to file location
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToDatabase(it.toString())
                }
            }

            .addOnFailureListener {

            }
    }

    private fun saveUserToDatabase(profileImageUrl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""
       val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edit_text_registration.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Save user to Firebase database")

                val intent = Intent (this, MessagesActivity::class.java)
                //brings you back to the home screen of the phone and not back to the register activity
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }
}


//storing the user in firebase with the uid, username and profileimageurl
class User(val uid: String, val username: String, val profileImageUrl: String){
    constructor() : this("","","")
}
