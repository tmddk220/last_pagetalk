// SetNicknameActivity.kt

package com.bokchi.mysolelife.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.mysolelife.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SetNicknameActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var saveNicknameButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_nickname)

        nicknameEditText = findViewById(R.id.nicknameEditText)
        saveNicknameButton = findViewById(R.id.saveNicknameButton)

        saveNicknameButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString().trim()
            if (nickname.isNotEmpty()) {
                saveNickname(nickname)
            } else {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNickname(nickname: String) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            val userRef = Firebase.database.reference.child("users").child(userId)
            userRef.child("nickname").setValue(nickname).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, SettingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "닉네임 저장 실패: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
