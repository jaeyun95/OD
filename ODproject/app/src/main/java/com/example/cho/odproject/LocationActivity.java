package com.example.cho.odproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView title, category, date, address, memo;
    private String key, uid, category_name;
    private LocationDTO locationDto;
    private Switch visit;
    private Button bt_back;
    private boolean state_visit;
    double latitude = 37.555744, longitude = 126.970431;
    private SupportMapFragment mapFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        title = (TextView)findViewById(R.id.Title);
        category = (TextView)findViewById(R.id.Category);
        date = (TextView)findViewById(R.id.Date);
        address = (TextView)findViewById(R.id.Address);
        memo = (TextView)findViewById(R.id.Memo);
        visit = (Switch) findViewById(R.id.switch_visit);
        uid = mAuth.getCurrentUser().getUid();
        bt_back = (Button)findViewById(R.id.bt_back);

        Intent intent = getIntent();
        key = intent.getStringExtra("location_title");
        getSupportActionBar().setTitle(key);
        category_name = intent.getStringExtra("category");
        showLocation();

        bt_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                intent.putExtra("category", category_name);
                startActivity(intent);
            }
        });

        visit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                state_visit = isChecked;
                locationDto.setVisit(state_visit);
                databaseReference.child(uid).child("category").child(category_name).child(key).setValue(locationDto);
                databaseReference.child(uid).child("location_list").child(key).setValue(locationDto);
                if(isChecked){
                    Toast.makeText(LocationActivity.this, "ON", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LocationActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //setTitle(key);

        //startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.edit_location) {  // 수정

            Intent intent = new Intent(this,EditLocationActivity.class);
            intent.putExtra("category", category_name);
            intent.putExtra("location_title", key);
            startActivity(intent);

        }

        if(id == R.id.remove_location) {
            // 삭제
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("장소 삭제");
            builder.setMessage("장소에 대한 모든 정보가 삭제됩니다.\n정말 삭제하시겠습니까?");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(uid).child("category").child(category_name).child(key).removeValue();
                            databaseReference.child(uid).child("location_list").child(key).removeValue();
                            Toast.makeText(getApplicationContext(),"삭제 완료.",Toast.LENGTH_LONG).show();
                        }
                    });
            builder.setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    public void showLocation() {
        databaseReference.child(uid).child("category").child(category_name).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.getValue()==null){
                    Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                    intent.putExtra("category", category_name);
                    startActivity(intent);
                }
                else {
                    LocationDTO dto = dataSnapshot.getValue(LocationDTO.class);
                    locationDto = dto;
                    title.setText(dto.getTitle());
                    category.setText(dto.getCategory());
                    date.setText(dto.getDate());
                    address.setText(dto.getAddress());
                    memo.setText(dto.getMemo());
                    latitude = dto.getLatitude();
                    longitude = dto.getLongitude();
                    visit.setChecked(dto.isVisit());
                    state_visit = dto.isVisit();
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(LocationActivity.this);

                    Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ll = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(ll).title(title.getText().toString()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
        intent.putExtra("category", category_name);
        startActivity(intent);
    }

}
