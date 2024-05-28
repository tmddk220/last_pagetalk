package com.bokchi.mysolelife.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.auth.IntroActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nicknameTextView: TextView
    private lateinit var birthdateTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        // FirebaseAuth에 로케일 설정
        auth.setLanguageCode(Locale.getDefault().language)

        nicknameTextView = findViewById(R.id.nicknameText)
        birthdateTextView = findViewById(R.id.birthdateText)
        phoneTextView = findViewById(R.id.phoneText)

        val logoutBtn: Button = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            auth.signOut()

            Toast.makeText(this, "로그아웃", Toast.LENGTH_LONG).show()

            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val deleteAccountBtn: Button = findViewById(R.id.deleteAccountBtn)
        deleteAccountBtn.setOnClickListener {
            showDeleteAccountDialog()
        }

        // 사용자 정보 불러오기
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nickname = snapshot.child("nickname").getValue(String::class.java)
                    val birthdate = snapshot.child("birthdate").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)

                    // 정보가 null이 아니면 TextView에 표시
                    if (nickname != null) nicknameTextView.text = "닉네임: $nickname"
                    if (birthdate != null) birthdateTextView.text = "생년월일: $birthdate"
                    if (phone != null) phoneTextView.text = "휴대폰 번호: $phone"
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터 로딩 실패 시 처리
                    Toast.makeText(this@SettingActivity, "사용자 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("회원탈퇴")
        builder.setMessage("정말로 회원탈퇴 하시겠습니까?")
        builder.setPositiveButton("예") { dialog, _ ->
            deleteAccount()
            dialog.dismiss()
        }
        builder.setNegativeButton("아니요") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        val userId = user?.uid

        if (user != null && userId != null) {
            // 데이터베이스에서 사용자 데이터 삭제
            database.child("users").child(userId).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 사용자 계정 삭제
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Toast.makeText(this, "회원탈퇴 성공", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, IntroActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "회원탈퇴 실패: ${deleteTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "회원탈퇴 실패: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
