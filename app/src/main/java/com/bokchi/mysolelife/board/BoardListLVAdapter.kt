package com.bokchi.mysolelife.board

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.contentsList.BookmarkModel
import com.bokchi.mysolelife.contentsList.ContentRVAdapter
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardListLVAdapter(
    val context: Context,
    val boardList: MutableList<BoardModel>,
    val boardKeyList: MutableList<String>
) : BaseAdapter() {

    val bookmarkIdList = mutableListOf<String>()

    override fun getCount(): Int {
        return boardList.size
    }

    override fun getItem(position: Int): Any {
        return boardList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    lateinit var lvAdapter: BoardListLVAdapter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.board_list_item, parent, false)
        }

        Log.d("BoardListLVAdapter", boardKeyList.toString())
        Log.d("BoardListLVAdapter", bookmarkIdList.toString())

        val itemLinearLayoutView = view?.findViewById<LinearLayout>(R.id.itemView)
        val title = view?.findViewById<TextView>(R.id.titleArea)
        val reviewtitle = view?.findViewById<TextView>(R.id.reviewtitleArea)
        val author = view?.findViewById<TextView>(R.id.authorArea)
        //val content = view?.findViewById<TextView>(R.id.contentArea)
        val time = view?.findViewById<TextView>(R.id.timeArea)
        val image = view?.findViewById<ImageView>(R.id.getImageArea)
        val bookmarkArea = view?.findViewById<ImageView>(R.id.bookmarkArea)
        lvAdapter = BoardListLVAdapter(context, boardList, boardKeyList)

        //내가 쓴 글 background color 설정
        if (boardList[position].uid == FBAuth.getUid()) {
            itemLinearLayoutView?.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        title!!.text = boardList[position].title
        //content!!.text = boardList[position].content
        time!!.text = boardList[position].time
        author!!.text = boardList[position].author
        reviewtitle!!.text = boardList[position].reviewtitle

        // 이미지 다운로드 및 표시
        getImageData(boardKeyList[position], image)

        if (bookmarkIdList.contains(boardKeyList[position])) {
            bookmarkArea?.setImageResource(R.drawable.bookmark_color)
        }
        else {
            bookmarkArea?.setImageResource(R.drawable.bookmark_white)
        }

        /*bookmarkArea?.setOnClickListener {
            Log.d("BoardListLVAdapter", FBAuth.getUid())
            Toast.makeText(context, boardKeyList[position], Toast.LENGTH_SHORT).show()

            FBRef.bookmarkRef.child(FBAuth.getUid()).child(boardKeyList[position])
                .setValue(BookmarkModel(true))
        }*/

        bookmarkArea?.setOnClickListener {
            val isBookmarked = bookmarkIdList.contains(boardKeyList[position])

            if (isBookmarked) {
                FBRef.bookmarkRef.child(FBAuth.getUid()).child(boardKeyList[position]).removeValue()
            } else {
                FBRef.bookmarkRef.child(FBAuth.getUid()).child(boardKeyList[position]).setValue(BookmarkModel(true))
            }

            if (isBookmarked) {
                bookmarkIdList.remove(boardKeyList[position])
                bookmarkArea?.setImageResource(R.drawable.bookmark_white)
            } else {
                bookmarkIdList.add(boardKeyList[position])
                bookmarkArea?.setImageResource(R.drawable.bookmark_color)
            }
        }


        return view!!
    }

    private fun getBookmarkData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {
                    bookmarkIdList.add(dataModel.key.toString())
                }
                Log.d("BoardListLVAdapter", bookmarkIdList.toString())
                lvAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)

    }

    // 이미지 다운로드 및 표시
    private fun getImageData(key: String, imageView: ImageView?) {
        if (key.isNotEmpty()) { // key가 비어있지 않은지 확인
            val storageReference = Firebase.storage.reference.child("$key.png")
            storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result)
                        .placeholder(R.drawable.iconsplaceholder)
                        .error(R.drawable.iconserror)
                        .into(imageView!!)
                } else {
                    imageView?.setImageResource(R.drawable.iconsbook1) // 이미지 로드 실패 시 에러 이미지 표시
                    imageView?.isVisible = true
                }
                /* 아래 코드로 바꾸면 로딩중 사진 표시 잘됨
                else {
                    imageView?.isVisible = false
                }
                 */
            })
        }
    }
}
