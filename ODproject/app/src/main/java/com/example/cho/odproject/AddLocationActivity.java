package com.example.cho.odproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private android.support.v7.widget.Toolbar toolbar;
    private String uid, category_name, date;
    private EditText title,address,memo;
    private TextView category;
    private Button bt_add, bt_cancel, bt_getSpot;
    private SupportMapFragment mapFragment;
    private  static final int PLACE_PICKER_REQUEST = 1;
    double latitude = 37.555744, longitude = 126.970431;    // 이거는 지도 초기 위치 설정한건데.. 걍 어떤사람이 예제에서 서울역 찍어놔서 좌표 걍 가져옴. 목표는 현재위치 받아오는거지만 힘들어...

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_location);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = (EditText) findViewById(R.id.Title);
        //category = (TextView)findViewById(R.id.Category);
        address = (EditText) findViewById(R.id.Address);
        address.setEnabled(false);
        memo = (EditText) findViewById(R.id.Memo);
        bt_add = (Button) findViewById(R.id.bt_add);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_getSpot = (Button) findViewById(R.id.bt_getSpot);
        uid = mAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        category_name = intent.getStringExtra("category");
        //category.setText(category_name);

        long now = System.currentTimeMillis();
        Date d = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy년 MM월 dd일");
        date = sdfNow.format(d);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddLocationActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                if (title.getText().toString().equals("")){
                    alert.setMessage("제목을 입력하시오.");
                    alert.show();
                    return;
                }
                //double betdistance = computeDistanceBetween(new LatLng(latitude,longitude),new LatLng(37.555744,126.970431));
                LocationDTO dto = new LocationDTO(title.getText().toString(), category_name, date, address.getText().toString(), memo.getText().toString(), latitude, longitude, false);
                databaseReference.child(uid).child("category").child(category_name).child(dto.getTitle()).setValue(dto); // 데이터 푸쉬
                databaseReference.child(uid).child("location_list").child(dto.getTitle()).setValue(dto); // 데이터 푸쉬
                Toast.makeText(AddLocationActivity.this, "확인", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.putExtra("location_title",dto.getTitle());
                intent.putExtra("category",dto.getCategory());
                startActivity(intent);
            }
        });
        bt_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddLocationActivity.this, "취소", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                intent.putExtra("category", category_name);
                startActivity(intent);
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bt_getSpot.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {    // 장소 검색을 위한 버튼 클릭했을 경우
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = intentBuilder.build(AddLocationActivity.this);
                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();  // 이름은 사용자가 별도로 지정하니까 이름까지 불러올 필요 없겠지...? 그렇게 생각한다면 지우시오.
            final CharSequence addr = place.getAddress();
            final LatLng latlng = place.getLatLng();  //lat/lng:(위도, 경도) 로 저장이 됨
            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }

            address.setText(addr);
            address.append(Html.fromHtml(attributions));

            String latalng = latlng.toString();
            int cut1 = latalng.indexOf("(");
            int cut2 = latalng.indexOf(")");
            latalng = latalng.substring(cut1+1, cut2);
            String[] latlong =  latalng.split(",");
            latitude = Double.parseDouble(latlong[0]);
            longitude = Double.parseDouble(latlong[1]);

            mapFragment.getMapAsync(this);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ll = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(ll));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));
    }
}
