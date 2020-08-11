package com.example.cho.odproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private double betdistance;
    private double distance=100.0;
    NotificationManager Notifi_M;
    Notification Notifi ;
    private int MyNoti;
    private List<String> locationIndex  = new ArrayList<String>();
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    private String uid;
    List<String> category  = new ArrayList<String>();
    double latitude = 0, longitude = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        uid = mAuth.getCurrentUser().getUid();
        getSetting();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getLocationList(location.getLatitude(),location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,1,listener);
    }
    public void getLocationList(final Double myLatitude, final Double myLongitude){
        databaseReference.child(uid).child("location_list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                for (DataSnapshot location: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    LocationDTO dto = location.getValue(LocationDTO.class);
                    locationIndex.add(dto.getTitle());
                    latitude = dto.getLatitude();
                    longitude = dto.getLongitude();
                    betdistance = computeDistanceBetween(new LatLng(latitude,longitude),new LatLng(myLatitude,myLongitude));
                    if(betdistance<=distance) {
                        MyNoti = locationIndex.indexOf(dto.getTitle());
                        notifiFunction(dto.getTitle() + " " + Double.toString(distance) + "m 이내에 있습니다.");
                    }   //Toast.makeText(MyService.this,dto.getTitle()+" "+Double.toString(distance)+"m 이내에 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }
    public void getSetting(){
        databaseReference.child(uid).child("setting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SettingDTO setting = dataSnapshot.getValue(SettingDTO.class);
                distance = setting.getDistance();
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    public void notifiFunction(String note){
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(MyService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notifi = new Notification.Builder(getApplicationContext())
                .setContentTitle("오디?")
                .setContentText(note)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker(note)
                .setContentIntent(pendingIntent)
                .getNotification();
        //소리추가
        Notifi.defaults = Notification.DEFAULT_SOUND;
        //알림 소리를 한번만 내도록
        Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        //확인하면 자동으로 알림이 제거 되도록
        Notifi.flags = Notification.FLAG_AUTO_CANCEL;
        Notifi_M.notify( MyNoti , Notifi);
    }
}
