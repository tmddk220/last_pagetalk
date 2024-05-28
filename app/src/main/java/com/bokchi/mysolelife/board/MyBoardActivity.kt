// MyBoardActivity.kt
package com.bokchi.mysolelife.board

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.databinding.ActivityMyBoardBinding
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyBoardBinding

    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()

    private val TAG = MyBoardActivity::class.java.simpleName

    private lateinit var boardRVAdapter: MyBoardListLAVdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_board)

        boardRVAdapter = MyBoardListLAVdapter(this, boardDataList, boardKeyList)
        binding.boardGridView.adapter = boardRVAdapter

        binding.boardGridView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@MyBoardActivity, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position])
            startActivity(intent)
        }

        getFBBoardData()
    }

    private fun getFBBoardData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataList.clear()
                boardKeyList.clear()

                val currentUserUid = FBAuth.getUid()

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(BoardModel::class.java)
                    if (item != null && item.uid == currentUserUid) {
                        boardDataList.add(item)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }
                boardKeyList.reverse()
                boardDataList.reverse()
                boardRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)
    }
}
