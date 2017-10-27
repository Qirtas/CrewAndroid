package asplundh.sps.com.asplundhproductivity.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.json.JSONException;
import org.json.JSONObject;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Service.GeocodeAddressIntentService;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

public class MapDownloadActivity extends AppCompatActivity implements OnProgressBarListener
{
    private MapView mapView;
    BoundsResultReceiver mResultReceiver;
    OfflineManager offlineManager;
    private NumberProgressBar bnp;
    private MapboxMap map;
    
    String city_lat = "" ,  city_lng = "";
    String east_lat = "" ,  east_lng = "";
    String west_lat = "" ,  west_lng = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicWlydGFzIiwiYSI6ImNqOGZoajV2ODA0NDEycXMxNDJqbHIydnkifQ.HB3H8VxTc9hW_XR1DDSZJg");
    
        setContentView(R.layout.activity_map_download);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        bnp = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        bnp.setOnProgressBarListener(MapDownloadActivity.this);
        
        final String cityName = getIntent().getStringExtra(AppConstants.CITY_NAME);
        mResultReceiver = new BoundsResultReceiver(null);
        
        Log.i(AppConstants.TAG  ,"cityName in MapDownloadActivity: " + cityName);
        
        getCityBounds(cityName);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        
    }
    
    @Override
    public void onProgressChange(int current, int max) {
        if(current == max) {
            //  Toast.makeText(getApplicationContext(), getString(R.string.finish), Toast.LENGTH_SHORT).show();
            bnp.setProgress(0);
        }
    }
    
    @Override
    public void onBackPressed()
    {
        // super.onBackPressed(); // Comment this super call to avoid calling finish() or fragmentmanager's backstack pop operation.
    }
    
    public void getCityBounds(String cityName)
    {
        Intent intent = new Intent(MapDownloadActivity.this , GeocodeAddressIntentService.class);
        intent.putExtra(AppConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppConstants.CITY_NAME , cityName);
        startService(intent);
    }
    
    @SuppressLint("ParcelCreator")
    class BoundsResultReceiver extends ResultReceiver
    {
        public BoundsResultReceiver(Handler handler)
        {
            super(handler);
        }
        
        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData)
        {
            if (resultCode == AppConstants.SUCCESS_RESULT)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        
                        String northEastBounds = resultData.getString(AppConstants.NORTH_EAST);
                        String southWestBounds = resultData.getString(AppConstants.SOUTH_WEST);
                        String cityLocation = resultData.getString(AppConstants.CITY_LOCATION);
                        
                        Log.i(AppConstants.TAG , "Bounds in BoundsResultReceiver: " + northEastBounds +  "  " + southWestBounds);
                        Log.i(AppConstants.TAG , "City loc in BoundsResultReceiver: " + cityLocation);
                        
                        updateMapAndDownload( northEastBounds , southWestBounds,  cityLocation);
                    }
                });
            }
            else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.v(AppConstants.TAG, " " + resultData.getString(AppConstants.RESULT_DATA_KEY));
                    }
                });
            }
        }
    }
    
    public void updateMapAndDownload(String northEastBounds , String southWestBounds, String cityLocation)
    {
        try
        {
            JSONObject city_loc = new JSONObject(cityLocation);
            city_lat = city_loc.get("lat").toString();
            city_lng = city_loc.get("lng").toString();
        
            JSONObject east_obj = new JSONObject(northEastBounds);
            east_lat = east_obj.get("lat").toString();
            east_lng = east_obj.get("lng").toString();
        
            JSONObject west_obj = new JSONObject(southWestBounds);
            west_lat = west_obj.get("lat").toString();
            west_lng = west_obj.get("lng").toString();
        
        }
        catch (JSONException e)
        {
            Log.e(AppConstants.TAG , "JSONException while parsing city Loca:" + e.toString());
        }
    
        mapView.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(MapboxMap mapboxMap)
            {
                map = mapboxMap;
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(city_lat), Double.parseDouble(city_lng)));
                mapboxMap.addMarker(markerOptions);
    
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(city_lat), Double.parseDouble(city_lng)));
                CameraUpdate zoom= CameraUpdateFactory.zoomTo(12);
                mapboxMap.moveCamera(center);
                mapboxMap.animateCamera(zoom);
    
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        // .include(new LatLng(Double.parseDouble(east_lat) , Double.parseDouble(east_lng))) // Northeast
                        // .include(new LatLng(Double.parseDouble(west_lat) , Double.parseDouble(west_lng))) // Southwest
                        .include(new LatLng(33.737492, 73.079367)) // Northeast
                        .include(new LatLng(33.720641, 73.071711)) // Northeast
                        .build();
                
                offlineManager = OfflineManager.getInstance(MapDownloadActivity.this);
    
                // Implementation that uses JSON to store Yosemite National Park as the offline region name.
                byte[] metadata;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("REGION_NAME", "Yosemite National Park");
                    String json = jsonObject.toString();
                    metadata = json.getBytes("UTF-8");
                } catch (Exception exception) {
                    Log.e(AppConstants.TAG, "Failed to encode metadata: " + exception.getMessage());
                    metadata = null;
                }
    
                deleteAllRegions();
                
            }
        });
        
    }
    
    public void downloadOfflineRegion()
    {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(33.587444, 73.092093)) // Northeast
                .include(new LatLng(33.577789, 73.091201)) // Southwest
                .build();
    
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                map.getStyleUrl(),
                latLngBounds,
                10,
                20,
                MapDownloadActivity.this.getResources().getDisplayMetrics().density);
    
        byte[] metadata = null;
        
        offlineManager.createOfflineRegion(definition, metadata,
                                           new OfflineManager.CreateOfflineRegionCallback() {
                                               @Override
                                               public void onCreate(OfflineRegion offlineRegion) {
                                                   offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
            
                                                   // Monitor the download progress using setObserver
                                                   offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                                       @Override
                                                       public void onStatusChanged(OfflineRegionStatus status) {
                    
                                                           // Calculate the download percentage
                                                           double percentage = status.getRequiredResourceCount() >= 0
                                                                   ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                                   0.0;
    
                                                           int score = (int) percentage;
                                                           bnp.setProgress(score);
                    
                                                           if (status.isComplete())
                                                           {
                                                               // Download complete
                                                               Log.d(AppConstants.TAG, "Region downloaded successfully.");
    
                                                               Toast.makeText(MapDownloadActivity.this , "Map Downloaded successfully",
                                                                              Toast.LENGTH_LONG).show();
                                                               finish();
                                                               
                                                           } else if (status.isRequiredResourceCountPrecise()) {
                                                               Log.d(AppConstants.TAG, percentage + "");
                                                           }
                                                       }
                
                                                       @Override
                                                       public void onError(OfflineRegionError error) {
                                                           // If an error occurs, print to logcat
                                                           Log.e(AppConstants.TAG, "onError reason: " + error.getReason());
                                                           Log.e(AppConstants.TAG, "onError message: " + error.getMessage());
    
                                                           Toast.makeText(MapDownloadActivity.this , "Error occurred",
                                                                          Toast.LENGTH_LONG).show();
                                                       }
                
                                                       @Override
                                                       public void mapboxTileCountLimitExceeded(long limit) {
                                                           // Notify if offline region exceeds maximum tile count
                                                           Log.e(AppConstants.TAG, "Mapbox tile count limit exceeded: " + limit);
                                                       }
                                                   });
                                               }
        
                                               @Override
                                               public void onError(String error) {
                                                   Log.e(AppConstants.TAG, "Error: " + error);
                                               }
                                           });
    }
    
    
    private void deleteAllRegions() {
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                
                if(offlineRegions.length > 0)
                {
                    for (OfflineRegion region : offlineRegions) {
                        region.delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {
                
                                Log.e(AppConstants.TAG , "Deleetd");
                                finish();
                                
                               // downloadOfflineRegion();
                            }
            
                            @Override
                            public void onError(String error) {
                            }
                        });
                    }
                }
                else
                {
                    Log.e(AppConstants.TAG , "No old record found");
                    finish();
                    
                   // downloadOfflineRegion();
                }
            }
            
            @Override
            public void onError(String error) {
            }
        });
    }
}
