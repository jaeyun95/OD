package com.example.cho.odproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {
    private TextView distance;
    private Switch onOff;
    private boolean state_switch;
    private double distance_value;
    private Button bt_change;
    private String uid;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("설정");
        setContentView(R.layout.activity_setting);

        distance = (TextView) findViewById(R.id.distance);
        bt_change = (Button) findViewById(R.id.bt_change);
        onOff = (Switch) findViewById(R.id.switch_on_off);
        uid = mAuth.getCurrentUser().getUid();

        showSetting();

        bt_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });

        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            final SettingDTO new_setting = new SettingDTO();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                state_switch = isChecked;
                new_setting.setOn_off(isChecked);
                new_setting.setDistance(distance_value);
                databaseReference.child(uid).child("setting").setValue(new_setting);
                if(isChecked){
                    Toast.makeText(SettingActivity.this, "ON", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MyService.class);
                    startService(i);
                }
                else{
                    Toast.makeText(SettingActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MyService.class);
                    stopService(i);
                }
            }
        });
    }
    void show() {
        final EditText edittext = new EditText(this);
        final SettingDTO new_setting = new SettingDTO();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("거리 변경");
        builder.setMessage("거리를 입력하시오.");
        builder.setView(edittext);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        distance_value = Double.parseDouble(edittext.getText().toString());
                        if (edittext.getText().toString().equals(""))
                            return;
                        new_setting.setDistance(distance_value);
                        new_setting.setOn_off(state_switch);
                        //new_setting.setOn_off(text_onoff.getText().toString());
                        databaseReference.child(uid).child("setting").setValue(new_setting);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        builder.show();
    }
    public void showSetting() {
        databaseReference.child(uid).child("setting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                SettingDTO setting = dataSnapshot.getValue(SettingDTO.class);
                distance.setText(Double.toString(setting.getDistance())+" m");
                distance_value = setting.getDistance();
                onOff.setChecked(setting.getOn_off());
                state_switch = setting.getOn_off();
                //text_onoff.setText(setting.getOn_off());
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

}