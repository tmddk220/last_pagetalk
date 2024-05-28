package com.bokchi.mysolelife.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.bokchi.mysolelife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentLVAdapter(val commentList: MutableList<CommentModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return commentList.size
    }

    override fun getItem(position: Int): Any {
        return commentList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_item, parent, false)
        }

        val title = view?.findViewById<TextView>(R.id.titleArea)
        val time = view?.findViewById<TextView>(R.id.timeArea)
        val nickname = view?.findViewById<TextView>(R.id.nicknameArea)

        title!!.text = commentList[position].commentTitle
        time!!.text = commentList[position].commentCreatedTime

        // 닉네임 가져오기
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(uid).child("nickname")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val nicknameValue = dataSnapshot.getValue(String::class.java)
                    nickname?.text = nicknameValue
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(parent?.context, "사용자 정보를 가져오는 데 실패했습니다: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view!!
    }
}
