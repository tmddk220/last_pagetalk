package com.bokchi.mysolelife.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.databinding.ActivityFindIdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FindIdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindIdBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_id)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        // 아이디 찾기 버튼 클릭 시 이벤트 처리
        binding.findIdButton.setOnClickListener {
            // 생년월일과 휴대폰 번호를 가져옴
            val birthdate = binding.birthdateEditText.text.toString()

            // 생년월일과 휴대폰 번호를 기준으로 해당하는 사용자를 찾음
            val query = database.orderByChild("birthdate").equalTo(birthdate)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // 생년월일이 일치하는 사용자가 있는 경우
                        for (userSnapshot in snapshot.children) {
                            val phone = userSnapshot.child("phone").getValue(String::class.java)
                            // 휴대폰 번호도 일치하는 경우에만 이메일을 가져와 화면에 표시
                            if (phone == binding.phoneEditText.text.toString()) {
                                val email = userSnapshot.child("email").getValue(String::class.java)
                                Toast.makeText(this@FindIdActivity, "이메일: $email", Toast.LENGTH_SHORT).show()
                                return
                            }
                        }
                        // 휴대폰 번호가 일치하는 사용자가 없는 경우
                        Toast.makeText(this@FindIdActivity, "일치하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        // 생년월일이 일치하는 사용자가 없는 경우
                        Toast.makeText(this@FindIdActivity, "일치하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터베이스 에러 발생 시 에러 메시지 표시
                    Toast.makeText(this@FindIdActivity, "데이터베이스 에러: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
