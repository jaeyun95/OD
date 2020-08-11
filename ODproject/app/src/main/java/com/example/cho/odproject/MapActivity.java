package com.example.cho.odproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    double dlat = 35.90775699999999, dlng = 127.76692200000001;   // 대한민국 (지도 한번에 볼 수 있도록) default lat,lng

    private String uid;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("지도");
        setContentView(R.layout.activity_map);

        uid = mAuth.getCurrentUser().getUid();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.markedmap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap gmap) {
        databaseReference.child(uid).child("location_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                for (DataSnapshot location: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    LocationDTO dto = location.getValue(LocationDTO.class);
                    double lat = dto.getLatitude();
                    double lng = dto.getLongitude();

                    if(!dto.isVisit()) {
                        gmap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(dto.getTitle()));
                    }
                    else {
                        gmap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(dto.getTitle())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        LatLng ll = new LatLng(dlat, dlng);


        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 7));
    }
}
