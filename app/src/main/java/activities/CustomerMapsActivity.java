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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    public static final int PERMISSION_FINE_LOCATION = 99;
    private GeoQuery geoQuery;


    private String amount,charge,transaction;

    private TextView settings,logoutTv,vendorName,vendorPhoneNumber;
    private RelativeLayout relativeLayout;
    private TextView transactionDetails;
    private CircleImageView vendorImage;
    private ImageView imageViewCall;
    private Button placeARequest;
    private Boolean vendorFound = false,requestType=false;
    private String vendorFoundId;
    Marker vendorMarker,myLocationMarker;
    private ValueEventListener vendorLocationRefListener;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String customerId;
    private DatabaseReference customerDatabaseRef;
    private DatabaseReference vendorAvailableRef,vendorRef,vendorWorkingRef;
    private LatLng customerDeliveredLocation;
    private int radius = 1;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_customer_maps);
        settings = findViewById(R.id.customer_maps_setting);
        logoutTv = findViewById(R.id.customer_maps_logout);
        vendorImage = findViewById(R.id.vendor_image_customer_map);
        vendorName = findViewById(R.id.vendor_name_customer_map);
        imageViewCall = findViewById(R.id.image_call_a_vendor);
        transactionDetails = findViewById(R.id.transaction_details_customer_map_tv);
        vendorPhoneNumber = findViewById(R.id.vendor_phone_number_customer_map);
        placeARequest = findViewById(R.id.customer_place_a_request);
        relativeLayout = findViewById(R.id.layout1_customer_map);
        amount = getIntent().getStringExtra("amount");
        charge = getIntent().getStringExtra("charge");
        transaction = getIntent().getStringExtra("transaction");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        customerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Customers Request");
        vendorAvailableRef = FirebaseDatabase.getInstance().getReference().child("Vendors Available");
        vendorWorkingRef = FirebaseDatabase.getInstance().getReference().child("Vendors Working");
        databaseReference = FirebaseDatabase.getInstance().getReference();

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
                mAuth.signOut();
                logoutCustomer();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerMapsActivity.this,SettingsActivity.class);
                intent.putExtra("type","Customers");
                startActivity(intent);
            }
        });

        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTheVendor();
            }
        });

        placeARequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //first of all save the transaction information to the database so that the vendor can view it
                saveTransactionInformationForTheVendorToSee();

               if (requestType){
                   try {

                       requestType = false;
                   geoQuery.removeAllListeners();

                   if (vendorWorkingRef !=null){
                       vendorWorkingRef.removeEventListener(vendorLocationRefListener);
                   }
                   if (vendorFound !=null){
                       vendorRef = FirebaseDatabase.getInstance().getReference().child("Users")
                               .child("Vendor").child(vendorFoundId).child("customerFoundId");
                       vendorRef.removeValue();
                       vendorFoundId = null;
                   }}catch (Exception ignored){}
                   vendorFound = false;
                   radius = 1;
                   GeoFire geoFire = new GeoFire(customerDatabaseRef);
                   geoFire.removeLocation(customerId, new GeoFire.CompletionListener() {
                       @Override
                       public void onComplete(String key, DatabaseError error) {

                       }
                   });
                   if (myLocationMarker !=null){
                       myLocationMarker.remove();
                       Toast.makeText(CustomerMapsActivity.this,"Transaction Request Cancelled",Toast.LENGTH_SHORT).show();
                   }
                   if (vendorMarker !=null){
                       vendorMarker.remove();
                   }
                   placeARequest.setText("Place A Request");
                   relativeLayout.setVisibility(View.GONE);

               }else {
                   //store the location of the customer into Firebase Database
                   if (lastLocation !=null){



                   requestType = true;
                   GeoFire geoFire = new GeoFire(customerDatabaseRef);
                   geoFire.setLocation(customerId, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
                       @Override
                       public void onComplete(String key, DatabaseError error) {

                       }
                   });
                   //get the current the location of that customer that you want to deliver the transaction to
                   customerDeliveredLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                   myLocationMarker = mMap.addMarker(new MarkerOptions().position(customerDeliveredLocation).title("My Location")
                           .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("profile",80,80))));

                   placeARequest.setText("Searching For A Nearby Vendor...");
                   getTheClosestVendor();
               }
               }
            }
        });
    }

    private void getTheClosestVendor() {
        GeoFire geoFire = new GeoFire(vendorAvailableRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(customerDeliveredLocation.latitude,customerDeliveredLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                //Here the key is the unique key assigned to the vendor by firebase
                //The key will help to find the location of the vendor
                //and if a vendor is not found in that customer's nearby location, the radius will be increase
                //and the method which search for the closest vendor will be called again
                //the method is working as a recursive function

                if (!vendorFound && requestType){
                    vendorFound = true;
                    vendorFoundId = key;
                    vendorRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Vendor").child(vendorFoundId);
                    HashMap<String,Object> vedorMap = new HashMap<>();
                    vedorMap.put("customerFoundId",customerId);
                    vendorRef.updateChildren(vedorMap);

                    gettingTheVendorLocation();
                    placeARequest.setText("Searching For The Vendor Location...");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!vendorFound){
                    radius +=1;
                    getTheClosestVendor();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void gettingTheVendorLocation() {
        vendorLocationRefListener = vendorWorkingRef.child(vendorFoundId).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && requestType) {

                            List<Object> vendorLocationMap = (List<Object>) snapshot.getValue();
                            double vendorLatitude = 0;
                            double vendorLongitude = 0;
                            placeARequest.setText("Vendor Found");
                            relativeLayout.setVisibility(View.VISIBLE);
                            getAssginedVendorInformation();

                            if (vendorLocationMap.get(0) != null) {
                                vendorLatitude = Double.parseDouble(vendorLocationMap.get(0).toString());
                            }
                            if (vendorLocationMap.get(1) != null) {
                                vendorLongitude = Double.parseDouble(vendorLocationMap.get(1).toString());
                            }
                            LatLng vendorLatLong = new LatLng(vendorLatitude, vendorLongitude);

                            if (vendorMarker != null) {
                                vendorMarker.remove();
                            }

                            Location customerLocation = new Location("");
                            customerLocation.setLatitude(customerDeliveredLocation.latitude);
                            customerLocation.setLongitude(customerDeliveredLocation.longitude);

                            Location vendorLocation = new Location("");
                            vendorLocation.setLatitude(vendorLatLong.latitude);
                            vendorLocation.setLongitude(vendorLatLong.longitude);

                            float distance = customerLocation.distanceTo(vendorLocation);
                            if (distance < 90) {
                                placeARequest.setText("Your Vendor Has Arrived");
                            } else {
                                placeARequest.setText("Vendor's Distance " + distance);
                            }

                            vendorMarker = mMap.addMarker(new MarkerOptions().position(vendorLatLong).title("Your Vendor is Here")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("pos", 40, 40))));
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

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
                Log.d("TAG","user id "+customerId);
                Log.d("TAG","latitude is "+location.getLatitude());
                Log.d("TAG","longitude is "+location.getLongitude());
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
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
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(CustomerMapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
        return true;
    }
    private void logoutCustomer(){
        Intent intent = new Intent(CustomerMapsActivity.this,CustomerLoginRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void getAssginedVendorInformation(){

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Vendor").child(vendorFoundId);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        try {


                            String name = snapshot.child("name").getValue().toString();
                            String phone = snapshot.child("phoneNumber").getValue().toString();
                            String image = snapshot.child("profileImage").getValue().toString();
                            vendorName.setText(name);
                            vendorPhoneNumber.setText(phone);
                            Picasso.get().load(image).into(vendorImage);
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
    private void saveTransactionInformationForTheVendorToSee(){

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("transactionType",transaction);
        userMap.put("amount",amount);
        userMap.put("charge",charge);
        databaseReference.child("Users").child("Customers").child(mAuth.getCurrentUser().getUid())
                .updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CustomerMapsActivity.this,"Transaction Request saved",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void callTheVendor(){

        try {


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Vendor").child(vendorFoundId);
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
                                    Toast.makeText(CustomerMapsActivity.this,"Phone Number Not Available",Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(CustomerMapsActivity.this,"Phone Number Not Available",Toast.LENGTH_SHORT).show();
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