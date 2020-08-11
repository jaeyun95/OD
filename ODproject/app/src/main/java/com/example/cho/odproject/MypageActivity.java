package com.example.cho.odproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MypageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText user_name;
    private EditText user_pwd;
    private TextView user_email;
    private Switch user_sex;
    private String uid;
    private UserDTO userInfo;
    private Button UpdateButton;
    private Intent intent;
    private Button WithdrawButton;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("마이페이지");
        setContentView(R.layout.activity_mypage);
        uid = mAuth.getCurrentUser().getUid();

        user_name =(EditText)findViewById(R.id.user_name);
        user_email =(TextView) findViewById(R.id.user_email);
        user_sex =(Switch) findViewById(R.id.user_sex);
        user_pwd = (EditText) findViewById(R.id.user_pwd);
        WithdrawButton = (Button) findViewById(R.id.WithdrawButton);
        UpdateButton = (Button) findViewById(R.id.UpdateButton);

        databaseReference.child(uid).child("user_info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    intent = new Intent(MypageActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    userInfo = dataSnapshot.getValue(UserDTO.class);
                    user_name.setText(userInfo.getUser_name());
                    user_email.setText(userInfo.getUser_email());
                    user_sex.setChecked(userInfo.getUser_sex());
                    UpdateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserDTO userDto = new UserDTO(user_name.getText().toString(),user_email.getText().toString(),user_pwd.getText().toString(), user_sex.isChecked());
                            databaseReference.child(mAuth.getCurrentUser().getUid()).child("user_info").setValue(userDto);
                            user.updatePassword(user_pwd.toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MypageActivity.this, "수정 완료",Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(MypageActivity.this,MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    });
                    WithdrawButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder = new AlertDialog.Builder(MypageActivity.this);
                            builder.setTitle("확인 알람");
                            builder.setMessage("정말로 탈퇴하시겠습니까?");
                            builder.setPositiveButton("예",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            AuthCredential credential = EmailAuthProvider
                                                    .getCredential(userInfo.getUser_email(), userInfo.getUser_pwd());
                                            user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            user.delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                databaseReference.child(uid).removeValue();
                                                                                Toast.makeText(getApplicationContext(), "탈퇴 완료", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                Toast.makeText(getApplicationContext(), "탈퇴 실패", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                            builder.setNegativeButton("아니오",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(),"탈퇴 취소",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            builder.show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }}