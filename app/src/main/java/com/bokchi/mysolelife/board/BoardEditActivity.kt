package com.bokchi.mysolelife.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.databinding.ActivityBoardEditBinding
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardEditActivity : AppCompatActivity() {

    private lateinit var key: String
    private lateinit var binding: ActivityBoardEditBinding
    private val TAG = BoardEditActivity::class.java.simpleName
    private lateinit var writerUid: String
    private var selectedRadioButtonId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_edit)
        key = intent.getStringExtra("key").toString()
        getBoardData(key)
        getImageData(key)

        binding.editBtn.setOnClickListener {
            editBoardData(key)
        }

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

        radioButtons.forEach { setupRadioButton(it, radioButtons) }
    }

    private fun editBoardData(key: String) {
        val genre = findViewById<RadioButton>(selectedRadioButtonId)?.text.toString()

        FBRef.boardRef
            .child(key)
            .setValue(
                BoardModel(
                    binding.titleArea.text.toString(),
                    binding.contentArea.text.toString(),
                    writerUid,
                    FBAuth.getTime(),
                    binding.reviewtitleArea.text.toString(),
                    binding.authorArea.text.toString(),
                    binding.ratingBar.rating,
                    genre // 장르 추가
                )
            )

        Toast.makeText(this, "수정완료", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun getImageData(key: String) {
        val storageReference = Firebase.storage.reference.child("$key.png")

        val imageViewFromFB = binding.imageArea

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            }
        })
    }

    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                if (dataModel != null) {
                    Log.d(TAG, dataModel.toString())
                    Log.d(TAG, dataModel.title)
                    Log.d(TAG, dataModel.time)

                    binding.titleArea.setText(dataModel.title)
                    binding.contentArea.setText(dataModel.content)
                    binding.authorArea.setText(dataModel.author)
                    binding.reviewtitleArea.setText(dataModel.reviewtitle)
                    binding.ratingBar.rating = dataModel.rating
                    writerUid = dataModel.uid

                    // 장르 설정
                    setRadioButtonChecked(dataModel.genre)
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
            if (it.text.toString() == genre) {
                it.isChecked = true
                selectedRadioButtonId = it.id
                return
            }
        }
    }
}
