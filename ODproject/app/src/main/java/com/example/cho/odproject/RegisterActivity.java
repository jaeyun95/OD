package com.example.cho.odproject;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private boolean state_switch;
    private EditText UserName,EmailText,PasswordText;
    private Switch Manwoman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("회원가입");
        mAuth = FirebaseAuth.getInstance();

        UserName = (EditText) findViewById(R.id.UserName);
        EmailText = (EditText) findViewById(R.id.EmailText);
        PasswordText = (EditText) findViewById(R.id.PasswordText);
        Manwoman = (Switch) findViewById(R.id.user_sex);

        state_switch = false;

        Manwoman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                state_switch = isChecked;
                if(isChecked){
                }
                else{
                }
            }
        });

        Button RegisterButton = (Button) findViewById(R.id.RegisterButton);
        Button CancelButton = (Button) findViewById(R.id.CancelButton);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(EmailText.getText().toString(),PasswordText.getText().toString());

                //DB에 성별이랑 이름 등록하기
                //Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                //startActivity(loginIntent);
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(backIntent);
            }
        });
    }

    private void createUser(final String email,final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    SettingDTO setting = new SettingDTO(500.0,true);
                    UserDTO userDto = new UserDTO(UserName.getText().toString(),EmailText.getText().toString(),PasswordText.getText().toString(), state_switch);
                    databaseReference.child(mAuth.getCurrentUser().getUid()).child("setting").setValue(setting);
                    databaseReference.child(mAuth.getCurrentUser().getUid()).child("user_info").setValue(userDto);
                    databaseReference.child(mAuth.getCurrentUser().getUid()).child("location_list").setValue("location_list");
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    //보통 이메일이 이미 존재하거나, 이메일 형식이 아니거나, 비밀번호가 6자리 이상이 아닐 경우
                    //loginUser(email,password);
                    Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loginUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                        } else {
                            Toast.makeText(RegisterActivity.this, "로그인 완료",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}