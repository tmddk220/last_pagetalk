package com.bokchi.mysolelife.profile

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.board.BoardListLVAdapter
import com.bokchi.mysolelife.board.BoardModel
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyBookmarkActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var boardListAdapter: BoardListLVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookmark)

        gridView = findViewById(R.id.boardGridView)

        // 북마크된 데이터를 불러와서 표시
        loadBookmarkData()
    }

    private fun loadBookmarkData() {
        // 현재 사용자의 북마크 데이터를 불러오기
        val bookmarkRef = FBRef.bookmarkRef.child(FBAuth.getUid())
        bookmarkRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookmarkedKeys = mutableListOf<String>()
                for (data in snapshot.children) {
                    val key = data.key
                    if (key != null) {
                        bookmarkedKeys.add(key)
                    }
                }
                // 북마크된 게시물 키를 사용하여 해당 게시물 데이터 불러오기
                loadBoardData(bookmarkedKeys)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadBoardData(bookmarkedKeys: List<String>) {
        val boardList = mutableListOf<BoardModel>()
        val boardKeyList = mutableListOf<String>()

        // 북마크된 게시물 키를 사용하여 해당 게시물 데이터 불러오기
        for (key in bookmarkedKeys) {
            val boardRef = FBRef.boardRef.child(key)
            boardRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val boardModel = snapshot.getValue(BoardModel::class.java)
                    if (boardModel != null) {
                        boardList.add(boardModel)
                        boardKeyList.add(key)

                        // 모든 데이터를 불러왔을 때 어댑터 설정
                        if (boardList.size == bookmarkedKeys.size) {
                            setAdapter(boardList, boardKeyList)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun setAdapter(boardList: List<BoardModel>, boardKeyList: List<String>) {
        boardListAdapter = BoardListLVAdapter(this, boardList.toMutableList(), boardKeyList.toMutableList())
        gridView.adapter = boardListAdapter
    }
}
