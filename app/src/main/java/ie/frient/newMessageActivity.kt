package ie.frient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class newMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        //val adapter = GroupAdapter<GroupieViewHolder>()

        //adapter.add(UserItem())
       // adapter.add(UserItem())
       // adapter.add(UserItem())

        //recyclerview_newmessage.adapter = adapter

            //group holder doesnt require you to override functions that create a overrideView

        fetchUser()

    }

    private fun fetchUser() {
       val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()


                p0.children.forEach {
                  Log.d("NewMessage", it.toString())

                    val user = it.getValue(User::class.java)
                    if (user!= null) {
                        adapter.add(UserItem(user))
                    }

                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_text_view_new_message.text = user.username

        //picasso library allows you to load images easily using one line
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_view_new_message_row)

        //only need a user item and a row you want for the layout
        //bind used for modifying a certain data set

    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}


