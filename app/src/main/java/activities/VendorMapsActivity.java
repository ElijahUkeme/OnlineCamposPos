package activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendorMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    public static final int PERMISSION_FINE_LOCATION = 99;
    private LatLng vendorLocationLatLng;
    Marker customerDeliveredLocationMarker;


    private TextView settings,cancelRequest,logoutTv,customerName,customerPhoneNumber;
    private RelativeLayout relativeLayout;
    private TextView transactionDetails,customerDistance;
    private CircleImageView customerImage;
    private ImageView imageViewCall;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Boolean currentVendorLogoutStatus = false,requestType = false;
    private DatabaseReference vendorWorkingRef,assginedCustomerRef,assignedCustomerDeliveredRef;
    private String vendorId,customerId="";
    private ValueEventListener assignedCustomerDeliveredRefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_vendor_maps);
        relativeLayout = findViewById(R.id.layout2);
        settings = findViewById(R.id.vendor_maps_setting);
        cancelRequest = findViewById(R.id.vendor_maps_cancel_request);
        logoutTv = findViewById(R.id.vendor_maps_logout);
        customerDistance = findViewById(R.id.vendor_text_distance);
        customerName = findViewById(R.id.customer_name);
        customerPhoneNumber = findViewById(R.id.customer_phone_number);
        transactionDetails = findViewById(R.id.transaction_details_tv);
        customerImage = findViewById(R.id.customer_image);
        imageViewCall = findViewById(R.id.image_call_a_customer);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        vendorId = mAuth.getCurrentUser().getUid();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationPermission();


        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria,true);
        lastLocation = locationManager.getLastKnownLocation(provider);


        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentVendorLogoutStatus = true;
                disconnectTheVendor();
                mAuth.signOut();
                logoutVendor();
            }
        });
        getAssginedCustomerRequest();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(VendorMapsActivity.this,SettingsActivity.class);
                intent.putExtra("type","Vendor");
                startActivity(intent);

            }
        });

        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTheCustomer();
            }
        });
    }

    private void getAssginedCustomerRequest() {
        try {


        assginedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Vendors").child(vendorId).child("customerFoundId");
        assginedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    customerId = snapshot.getValue().toString();

                    relativeLayout.setVisibility(View.VISIBLE);
                    getAssignedCustomerInformation();
                    getAssignedCustomerDeliveredLocation();
                }else {
                    customerId = "";
                    if (customerDeliveredLocationMarker !=null){
                        customerDeliveredLocationMarker.remove();
                    }
                    if (assignedCustomerDeliveredRefListener !=null){
                        assignedCustomerDeliveredRef.removeEventListener(assignedCustomerDeliveredRefListener);
                    }
                    relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });}catch (Exception ignored){}
    }

    private void getAssignedCustomerDeliveredLocation() {
        assignedCustomerDeliveredRef = FirebaseDatabase.getInstance().getReference().child("Customers Request")
                .child(customerId).child("l");
        assignedCustomerDeliveredRefListener = assignedCustomerDeliveredRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    List<Object> customerLocationMap = (List<Object>) snapshot.getValue();
                    double customerLatitude = 0;
                    double customerLongitude = 0;


                    if (customerLocationMap.get(0) !=null){
                        customerLatitude = Double.parseDouble(customerLocationMap.get(0).toString());
                    }
                    if (customerLocationMap.get(1) !=null){
                        customerLongitude = Double.parseDouble(customerLocationMap.get(1).toString());
                    }

                    vendorLocationLatLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                    LatLng customerLatLong = new LatLng(customerLatitude,customerLongitude);

                    Location customerLocation = new Location("");
                    customerLocation.setLatitude(customerLatLong.latitude);
                    customerLocation.setLongitude(customerLatLong.longitude);

                    Location vendorLocation = new Location("");
                    vendorLocation.setLatitude(vendorLocationLatLng.latitude);
                    vendorLocation.setLongitude(vendorLocationLatLng.longitude);

                    float distance = vendorLocation.distanceTo(customerLocation);

                    if (distance <90){
                        customerDistance.setText("You Have Arrived");
                    }else {
                        customerDistance.setText("Customer's Distance "+ distance);
                    }
                    customerDeliveredLocationMarker = mMap.addMarker(new MarkerOptions().position(customerLatLong).title("Customer Delivered Location")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("profile",80,80))));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //update the location after one one second
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (lastLocation !=null){
            lastLocation = location;

            Log.d("TAG","latitude is "+lastLocation.getLatitude());
            Log.d("TAG","longitude is "+lastLocation.getLongitude());

            if (getApplicationContext() !=null){
                try {
                    lastLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());


                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                //store the location of the current online vendor by using geofire to get the longitude and latitude of the vendor
                String onlineVendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference vendorAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Vendors Available");
                GeoFire geoFireVendorAvailable = new GeoFire(vendorAvailabilityRef);

                vendorWorkingRef = FirebaseDatabase.getInstance().getReference().child("Vendors Working");
                GeoFire geoFireVendorWorking = new GeoFire(vendorWorkingRef);

                switch (customerId){
                    case "":
                        geoFireVendorWorking.removeLocation(onlineVendorId, new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });
                        geoFireVendorAvailable.setLocation(vendorId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });
                        break;
                    default:
                        geoFireVendorAvailable.removeLocation(onlineVendorId, new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });
                        geoFireVendorWorking.setLocation(onlineVendorId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {

                            }
                        });
                }
            }catch (Exception ignored){}
            }
        }

    }
    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
if (!currentVendorLogoutStatus){
    disconnectTheVendor();
}

    }

    private void disconnectTheVendor() {

        try {

        //remove the vendor if the app is stopped
        String onlineVendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference vendorAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Vendors Available");
        GeoFire geoFireVendorAvailable = new GeoFire(vendorAvailabilityRef);
        geoFireVendorAvailable.removeLocation(onlineVendorId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
        }catch (Exception ignored){
        }
    }

    private void logoutVendor() {
        Intent intent = new Intent(VendorMapsActivity.this,VendorLoginRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(VendorMapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VendorMapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
        return true;
    }

    private void getAssignedCustomerInformation(){

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Customers").child(customerId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        try {


                            String name = snapshot.child("name").getValue().toString();
                            String phone = snapshot.child("phoneNumber").getValue().toString();
                            String image = snapshot.child("profileImage").getValue().toString();
                            String transaction = snapshot.child("transactionType").getValue().toString();
                            String amount = snapshot.child("amount").getValue().toString();
                            String charge = snapshot.child("charge").getValue().toString();
                            customerName.setText(name);
                            customerPhoneNumber.setText(phone);
                            transactionDetails.setText("Transaction: "+transaction+" Amount: "+amount+" Charge: "+charge);
                            Picasso.get().load(image).into(customerImage);
                        }catch (Exception ignored){}
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception ignored){

        }
    }
    private void callTheCustomer(){

        try {


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Customers").child(customerId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        try {

                            if (snapshot.hasChild("phoneNumber")){
                                String phone = snapshot.child("phoneNumber").getValue().toString();
                                if (phone !=null){
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" +phone));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(VendorMapsActivity.this,"Phone Number Not Available",Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(VendorMapsActivity.this,"Phone Number Not Available",Toast.LENGTH_SHORT).show();
                            }}catch (Exception ignored){}
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception ignored){

        }
    }
}