package com.bokchi.mysolelife.setting

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.mysolelife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class EditNicknameActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var nicknameEditText: EditText
    private lateinit var saveNicknameBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_nickname)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference
        nicknameEditText = findViewById(R.id.nicknameEditText)
        saveNicknameBtn = findViewById(R.id.saveNicknameBtn)

        saveNicknameBtn.setOnClickListener {
            val newNickname = nicknameEditText.text.toString().trim()
            if (newNickname.isNotEmpty()) {
                updateNickname(newNickname)
            } else {
                Toast.makeText(this, "새 닉네임을 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNickname(newNickname: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("nickname").setValue(newNickname)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "닉네임이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 수정 완료 후 액티비티 종료
                    } else {
                        Toast.makeText(this, "닉네임 변경에 실패했습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
