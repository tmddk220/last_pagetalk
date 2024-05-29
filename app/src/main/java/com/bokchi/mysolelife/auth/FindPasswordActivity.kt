package com.bokchi.mysolelife.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.mysolelife.databinding.ActivityFindPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class FindPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityFindPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.findPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()

            // Firebase에서 비밀번호 재설정 이메일 보내기
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "비밀번호 재설정 이메일을 보내는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
