package com.bokchi.mysolelife.board
//BoardInsideActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.comment.CommentLVAdapter
import com.bokchi.mysolelife.comment.CommentModel
import com.bokchi.mysolelife.databinding.ActivityBoardInsideBinding
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName
    private lateinit var binding: ActivityBoardInsideBinding
    private lateinit var key: String
    private val commentDataList = mutableListOf<CommentModel>()
    private lateinit var commentAdapter: CommentLVAdapter
    private var selectedRadioButtonId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)

        binding.boardSettingIcon.setOnClickListener {
            showDialog()
        }

        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)

        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }

        getCommentData(key)
        commentAdapter = CommentLVAdapter(commentDataList)
        binding.commentLV.adapter = commentAdapter
    }

    private fun getCommentData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentDataList.clear()
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }

    private fun insertComment(key: String) {
        FBRef.commentRef
            .child(key)
            .push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    FBAuth.getTime()
                )
            )
        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("")
    }

    private fun showDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        val alertDialog = mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
            Toast.makeText(this, "수정 버튼을 눌렀습니다", Toast.LENGTH_LONG).show()
            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this, "삭제완료", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun getImageData(key: String) {
        val storageReference = Firebase.storage.reference.child("$key.png")
        val imageViewFromFB = binding.getImageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else {
                binding.getImageArea.isVisible = false
            }
        })
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    binding.titleArea.text = dataModel.reviewtitle
                    binding.textArea.text = dataModel.content
                    binding.timeArea.text = dataModel.time
                    binding.authorArea.text = dataModel.author
                    binding.ratingBar.rating = dataModel.rating
                    binding.ratingBar.isClickable = false
                    binding.booktitleArea.text = dataModel.title

                    // 수정된 부분: 장르 정보를 새로운 정보로 설정하고, 클릭 방지
                    val genre = dataModel.genre
                    setRadioButtonChecked(genre)

                    val myUid = FBAuth.getUid()
                    val writerUid = dataModel.uid

                    if (myUid == writerUid) {
                        Log.d(TAG, "내가 쓴 글")
                        binding.boardSettingIcon.isVisible = true
                    } else {
                        Log.d(TAG, "내가 쓴 글 아님")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "삭제완료")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }

    private fun setupRadioButton(radioButton: RadioButton, radioButtons: List<RadioButton>) {
        radioButton.setOnClickListener {
            if (radioButton.isChecked) {
                if (selectedRadioButtonId == radioButton.id) {
                    radioButton.isChecked = false
                    selectedRadioButtonId = -1
                } else {
                    radioButtons.forEach { it.isChecked = false }
                    radioButton.isChecked = true
                    selectedRadioButtonId = radioButton.id
                }
            } else {
                radioButton.isChecked = true
                selectedRadioButtonId = radioButton.id
            }
        }
        radioButton.isClickable = false // 클릭 방지
    }

    private fun setRadioButtonChecked(genre: String) {
        val radioButtons = listOf(
            binding.genre1,
            binding.genre2,
            binding.genre3,
            binding.genre4,
            binding.genre5,
            binding.genre6,
            binding.genre7,
            binding.genre8,
            binding.genre9,
            binding.genre10,
            binding.genre11,
            binding.genre12,
            binding.genre13,
            binding.genre14
        )

        radioButtons.forEach {
            it.isClickable = false // 클릭 방지
            if (it.text.toString() == genre) {
                it.isChecked = true
                return
            }
        }
    }
}
