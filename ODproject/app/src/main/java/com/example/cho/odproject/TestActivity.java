package com.example.cho.odproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    private String uid;
    private TextView text;
    List<String> category  = new ArrayList<String>();
    double latitude = 37.555744, longitude = 126.970431;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        text = (TextView) findViewById(R.id.text);
        uid = mAuth.getCurrentUser().getUid();


        databaseReference.child(uid).child("category").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                category.add(dataSnapshot.getKey().toString()); //카테고리 이름들 다 가져옴

                for(final String data:category){ //카테고리 이름들 마다 하나하나 들어가는 과정
                    final List<String> location  = new ArrayList<String>();
                    databaseReference.child(uid).child("category").child(data).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                            location.add(dataSnapshot.getKey().toString());
                            //text.setText(location.toString());
                            for(final String location_data: location){ //하나의 카테고리 안에 하나의 장소에 대한 정보 가져오기
                                databaseReference.child(uid).child("category").child(data).child(location_data).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                                        LocationDTO dto = dataSnapshot.getValue(LocationDTO.class);
                                        text.setText(Double.toString(dto.getLatitude()));
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                    }
                                });
                            }
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
