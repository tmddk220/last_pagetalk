package com.bokchi.mysolelife.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.board.BoardWriteActivity
import com.bokchi.mysolelife.board.MyBoardActivity
import com.bokchi.mysolelife.contentsList.ContentModel
import com.bokchi.mysolelife.databinding.FragmentBookmarkBinding
import com.bokchi.mysolelife.profile.MyBookmarkActivity
import com.bokchi.mysolelife.setting.SettingActivity
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookmarkFragment : Fragment() {

    private lateinit var binding : FragmentBookmarkBinding

    private val TAG = BookmarkFragment::class.java.simpleName

    val bookmarkIdList = mutableListOf<String>()
    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)

        // 사용자가 북마크한 정보를 가져옴
        getBookmarkData()

        // 탭 클릭 이벤트 처리
        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_tipFragment)
        }
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)
        }

        /*binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }*/

        // myboardbtn 클릭 시 MyBoardActivity로 이동하는 코드 추가
        binding.myboardbtn.setOnClickListener {
            val intent = Intent(requireContext(), MyBoardActivity::class.java)
            startActivity(intent)
        }

        // myprofilebtn 클릭 시 SettingActivity로 이동하는 코드 추가
        binding.myprofilebtn.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        // mybookmarkbtn 클릭 시 MyBookmarkActivity로 이동하는 코드 추가
        binding.mybookmarkbtn.setOnClickListener {
            val intent = Intent(requireContext(), MyBookmarkActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getCategoryData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(ContentModel::class.java)

                    // 전체 컨텐츠 중에서, 사용자가 북마크한 정보만 보여줌
                    if (bookmarkIdList.contains(dataModel.key.toString())){
                        items.add(item!!)
                        itemKeyList.add(dataModel.key.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.category1.addValueEventListener(postListener)
        FBRef.category2.addValueEventListener(postListener)
    }

    private fun getBookmarkData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children) {

                    Log.e(TAG, dataModel.toString())
                    bookmarkIdList.add(dataModel.key.toString())
                }

                // 전체 카테고리에 있는 컨텐츠 데이터들을 다 가져옴
                getCategoryData()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }
}
