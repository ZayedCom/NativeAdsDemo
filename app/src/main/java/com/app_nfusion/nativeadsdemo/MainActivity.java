package com.app_nfusion.nativeadsdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    //StartApp Code
    //Initialization of the ad object
    private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
    private int adNumber = 0;

    ArrayList<String> titleArrayList = new ArrayList<>();
    ArrayList<String> descriptionArrayList = new ArrayList<>();

    ListView listView;

    BaseAdapter baseAdapter;

    public void generateListView(){
        for (int i = 0; i <= 25; i++) {
            titleArrayList.add("Item Title " + i);
            descriptionArrayList.add("Item Description " + i);
            if (i % 5 == 0) {
                titleArrayList.add("");
                descriptionArrayList.add("");
            }
        }
    }

    public AlertDialog gdpr(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("This app uses personalized advertisement")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StartAppSDK.setUserConsent (MainActivity.this,
                                "pas",
                                System.currentTimeMillis(),
                                true);
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StartAppSDK.setUserConsent (MainActivity.this,
                                "pas",
                                System.currentTimeMillis(),
                                false);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //StartApp Code
        StartAppAd.disableSplash();
        gdpr();

        listView = findViewById(R.id.listView);

        generateListView();

        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return titleArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @SuppressLint("ViewHolder")
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                convertView = getLayoutInflater().inflate(R.layout.base_adapter_item, null);

                final TextView title = convertView.findViewById(R.id.item_title);
                final TextView description = convertView.findViewById(R.id.item_description);

                title.setText(titleArrayList.get(position));
                description.setText(descriptionArrayList.get(position));

                if (titleArrayList.get(position).isEmpty()) {

                    convertView = getLayoutInflater().inflate(R.layout.base_adapter_ad, null);

                    final TextView titleAd = convertView.findViewById(R.id.item_title);
                    final TextView descriptionAd = convertView.findViewById(R.id.item_description);
                    final TextView ratingAd = convertView.findViewById(R.id.item_rating);
                    final TextView installsAd = convertView.findViewById(R.id.item_installs);
                    final ImageView imageAd = convertView.findViewById(R.id.adImage);

                    //StartApp Code
                    // Declare Native Ad Preferences
                    final NativeAdPreferences nativePrefs = new NativeAdPreferences()
                            .setAdsNumber(5)                // Load 5 Native Ads
                            .setAutoBitmapDownload(true)    // Retrieve Images object
                            .setPrimaryImageSize(1);        // 100x100 image

                    // Declare Ad Callbacks Listener
                    final View finalConvertView = convertView;
                    final AdEventListener adListener = new AdEventListener() {     // Callback Listener

                        @Override
                        public void onReceiveAd(Ad arg0) {
                            // Native Ad received
                            ArrayList<NativeAdDetails> nativeAdsList = new ArrayList<>();
                            nativeAdsList.add(startAppNativeAd.getNativeAds().get(adNumber));
                            NativeAdDetails adDetails = nativeAdsList.iterator().next();
                            titleAd.setText(adDetails.getTitle());
                            descriptionAd.setText(adDetails.getDescription());
                            ratingAd.setText(String.valueOf(adDetails.getRating()));
                            installsAd.setText(adDetails.getInstalls());
                            imageAd.setImageBitmap(adDetails.getImageBitmap());
                            adDetails.registerViewForInteraction(finalConvertView);
                            if (adNumber <= nativePrefs.getAdsNumber()) {
                                adNumber++;
                                Log.i("Ad Number", String.valueOf(adNumber));
                                if (adNumber >= nativePrefs.getAdsNumber()){
                                    adNumber = 0;
                                }
                            }
                            ArrayList ads = startAppNativeAd.getNativeAds();    // get NativeAds list
                            // Print all ads details to log
                            Iterator iterator = ads.iterator();
                            while(iterator.hasNext()){
                                Log.d("MyApplication", iterator.next().toString());
                            }
                        }

                        @Override
                        public void onFailedToReceiveAd(Ad arg0) {
                            // Native Ad failed to receive
                            Log.e("MyApplication", "Error while loading Ad");
                            titleAd.setText("Failed to Load ad Title");
                            description.setText("Failed to Load ad Description");
                        }
                    };
                    // Load Native Ads
                    startAppNativeAd.loadAd(nativePrefs, adListener);
                }
                return convertView;
            }
        };

        listView.setAdapter(baseAdapter);
    }
}
