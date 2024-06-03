package com.bokchi.mysolelife.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.board.BoardListLVAdapter
import com.bokchi.mysolelife.board.BoardModel
import com.bokchi.mysolelife.board.BoardWriteActivity
import com.bokchi.mysolelife.databinding.FragmentTipBinding
import com.bokchi.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TipFragment : Fragment() {

    private lateinit var binding: FragmentTipBinding
    private lateinit var boardAdapter: BoardListLVAdapter
    private val boardList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tip, container, false)

        // RecyclerView 어댑터 설정
        boardAdapter = BoardListLVAdapter(requireContext(), boardList, boardKeyList)
        //binding.recyclerView.adapter = boardAdapter

        // 게시물 로드
        getFBBoardData()

        // 검색창 설정
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchBoardData(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchBoardData(it) }
                return false
            }
        })

        // 기존 버튼 클릭 이벤트 설정
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tipFragment_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tipFragment_to_bookmarkFragment)
        }

        return binding.root
    }

    private fun getFBBoardData() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardList.clear()
                boardKeyList.clear()

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(BoardModel::class.java)
                    item?.let {
                        boardList.add(it)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }
                boardAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle Error
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)
    }

    private fun searchBoardData(query: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardList.clear()
                boardKeyList.clear()

                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(BoardModel::class.java)
                    if (item != null && item.reviewtitle.contains(query, ignoreCase = true)) {
                        boardList.add(item)
                        boardKeyList.add(dataModel.key.toString())
                    }
                }
                boardAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle Error
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)
    }
}
