//MyBoardListLAVdapter.kt
package com.bokchi.mysolelife.board

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.utils.FBAuth
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyBoardListLAVdapter(
    private val context: Context,
    private val myboardList: MutableList<BoardModel>,
    private val boardKeyList: MutableList<String>
) : BaseAdapter() {

    override fun getCount(): Int = myboardList.size

    override fun getItem(position: Int): Any = myboardList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.myboard_list_item, parent, false)

        val itemLinearLayoutView = view?.findViewById<LinearLayout>(R.id.itemView)
        val title = view?.findViewById<TextView>(R.id.titleArea)
        val reviewtitle = view?.findViewById<TextView>(R.id.reviewtitleArea)
        //val author = view.findViewById<TextView>(R.id.authorArea)
        //val time = view.findViewById<TextView>(R.id.timeArea)
        val image = view?.findViewById<ImageView>(R.id.getImageArea)

        if (myboardList[position].uid == FBAuth.getUid()) {
            itemLinearLayoutView?.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        title!!.text = myboardList[position].title
        //time?.text = boardList[position].time
        //author?.text = boardList[position].author
        reviewtitle!!.text = myboardList[position].reviewtitle

        getImageData(boardKeyList[position], image)

        return view
    }

    private fun getImageData(key: String, imageView: ImageView?) {
        if (key.isNotEmpty()) {
            val storageReference = Firebase.storage.reference.child("$key.png")
            storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result)
                        .placeholder(R.drawable.iconsplaceholder)
                        .error(R.drawable.iconserror)
                        .into(imageView!!)
                } else {
                    imageView?.setImageResource(R.drawable.iconsbook1)
                    imageView?.isVisible = true
                }
            })
        }
    }
}
