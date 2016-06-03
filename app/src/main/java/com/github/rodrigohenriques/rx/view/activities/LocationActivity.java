package com.github.rodrigohenriques.rx.view.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rodrigohenriques.rx.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LocationActivity extends BaseActivity {

    private static final int REQUEST_CHECK_SETTINGS = 0;
    private final String TAG = getClass().getSimpleName();
    private ReactiveLocationProvider locationProvider;

    private TextView lastKnownLocationView;
    private TextView updatableLocationView;
    private TextView addressLocationView;
    private TextView currentActivityView;

    private Observable<Location> lastKnownLocationObservable;
    private Observable<Location> locationUpdatesObservable;
    private Observable<ActivityRecognitionResult> activityObservable;

    private Subscription lastKnownLocationSubscription;
    private Subscription updatableLocationSubscription;
    private Subscription addressSubscription;
    private Subscription activitySubscription;
    private Observable<String> addressObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        lastKnownLocationView = (TextView) findViewById(R.id.last_known_location_view);
        updatableLocationView = (TextView) findViewById(R.id.updated_location_view);
        addressLocationView = (TextView) findViewById(R.id.address_for_location_view);
        currentActivityView = (TextView) findViewById(R.id.activity_recent_view);

        locationProvider = new ReactiveLocationProvider(getApplicationContext());
        lastKnownLocationObservable = locationProvider.getLastKnownLocation();

        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setInterval(100);

        locationUpdatesObservable = locationProvider
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .setAlwaysShow(true)
                                .build()
                )
                .doOnNext(locationSettingsResult -> {
                    Status status = locationSettingsResult.getStatus();
                    if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            status.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException th) {
                            Log.e("MainActivity", "Error opening settings activity.", th);
                        }
                    }
                })
                .flatMap(locationSettingsResult -> locationProvider.getUpdatedLocation(locationRequest));

        addressObservable = locationProvider.getUpdatedLocation(locationRequest)
                .flatMap(location -> locationProvider.getReverseGeocodeObservable(location.getLatitude(), location.getLongitude(), 1))
                .map(addresses -> addresses != null && !addresses.isEmpty() ? addresses.get(0) : null)
                .map(address -> {
                    if (address == null) return "";

                    String addressLines = "";
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressLines += address.getAddressLine(i) + '\n';
                    }
                    return addressLines;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        activityObservable = locationProvider.getDetectedActivity(50);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lastKnownLocationSubscription = lastKnownLocationObservable
                .map(this::locationToString)
                .subscribe(lastKnownLocationView::setText, this::handleError);

        updatableLocationSubscription = locationUpdatesObservable
                .map(this::locationToString)
                .map(new Func1<String, String>() {
                    int count = 0;

                    @Override
                    public String call(String locationString) {
                        return locationString + " " + count++;
                    }
                })
                .subscribe(updatableLocationView::setText, this::handleError);


        addressSubscription = addressObservable.compose(bindToLifecycle())
                .subscribe(addressLocationView::setText, this::handleError);

        activitySubscription = activityObservable
                .compose(bindToLifecycle())
                .map(ActivityRecognitionResult::getMostProbableActivity)
                .map(detectedActivity -> getNameFromType(detectedActivity.getType()) + " with confidence " + detectedActivity.getConfidence())
                .subscribe(currentActivityView::setText, this::handleError);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updatableLocationSubscription.unsubscribe();
        addressSubscription.unsubscribe();
        lastKnownLocationSubscription.unsubscribe();
        activitySubscription.unsubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                //Refrence: https://developers.google.com/android/reference/com/google/android/gms/location/SettingsApi
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        Log.d(TAG,"User enabled location");
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG,"User Cancelled enabling location");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void handleError(Throwable throwable) {
        Toast.makeText(LocationActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Error occurred", throwable);
    }

    private String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }

    private String locationToString(Location location) {
        if (location != null)
            return location.getLatitude() + " " + location.getLongitude() + " (" + location.getAccuracy() + ")";
        return "no location available";
    }
}
