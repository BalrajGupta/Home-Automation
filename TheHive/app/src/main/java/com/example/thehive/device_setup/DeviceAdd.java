package com.example.thehive.device_setup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.thehive.R;
import com.example.thehive.RoomData;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.thehive.StorageUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DeviceAdd extends AppCompatActivity {

    //private ListView listView;
    //private DeviceAdapter dAdapter;
    EditText dev_ip;
    EditText dev_gpio;
    EditText dev_name;
    //ArrayList<Device> deviceList = new ArrayList<>();
    String ip;
    String name,roomfile,namea,ipa;
    int gpio,imageid,gpioa,imagea;
    Button show;
    RoomData room;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_device_add);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("Room BUNDLE");
        room = (RoomData) args.getSerializable("Room Data");

        Bundle sec=getIntent().getExtras();
        roomfile=sec.getString("roomname");
        Log.d("device-page",roomfile);


        show= findViewById(R.id.addBtn);
        dev_ip = findViewById(R.id.dev_ip);
        dev_gpio = findViewById(R.id.dev_gpio);
        dev_name = findViewById(R.id.dev_name);

        discoverDeviceIP();

        try {
            JSONArray deviceArray = StorageUtils.readJsonArray(this, roomfile);
            for(int i=0; i<deviceArray.length(); i++) {
                try {
                    JSONObject obj = deviceArray.getJSONObject(i);
                    room.deviceList.add(new Device(
                        obj.getString("name"),
                        obj.getString("ip"),
                        obj.getInt("gpio"),
                        obj.getInt("image")
                    ));
                } catch(Exception e) { e.printStackTrace(); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("system","button preseed");
                ip = dev_ip.getText().toString();
                name = dev_name.getText().toString();
                gpio = Integer.parseInt(dev_gpio.getText().toString());

                if(name.contains("fan")||name.contains("Fan"))
                    imageid=R.drawable.fan;
                if(name.contains("AC")||name.contains("A.C.")||name.contains("air conditioner")||name.contains("Air Conditioner")||name.contains("ac"))
                    imageid=R.drawable.air_conditioner;
                if(name.contains("bulb")||name.contains("Bulb"))
                    imageid=R.drawable.led;
                if(name.contains("Overhead Lamp")||name.contains("Ceiling Lamp"))
                    imageid=R.drawable.lamp;
                if(name.contains("free socket")||name.contains("Free Socket"))
                    imageid=R.drawable.plug;
                if(name.contains("Table Lamp")||name.contains("table lamp"))
                    imageid=R.drawable.table_lamp_clipart;

              room.deviceList.add(new Device(name,ip,gpio,imageid));
                //dAdapter.notifyDataSetChanged();

                Log.d("device-page image",Integer.toString(imageid));

                //saving in the JSON file.
                try {
                    JSONArray deviceArray = StorageUtils.readJsonArray(DeviceAdd.this, roomfile);
                    JSONObject newObj = new JSONObject();
                    newObj.put("name", name);
                    newObj.put("ip", ip);
                    newObj.put("gpio", gpio);
                    newObj.put("image", imageid);
                    deviceArray.put(newObj);
                    StorageUtils.writeJsonArray(DeviceAdd.this, roomfile, deviceArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dev_gpio.setText(null);
                dev_name.setText(null);
                Toast.makeText(getBaseContext(),"Device Added",Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void proceed(View view)
    {

        Intent intent = new Intent(DeviceAdd.this, DeviceShow.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", room.deviceList);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    private void discoverDeviceIP() {
        final NsdManager nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) { }
            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) { }
            @Override
            public void onDiscoveryStarted(String serviceType) { }
            @Override
            public void onDiscoveryStopped(String serviceType) { }
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                if (serviceInfo.getServiceName().contains("esp8266")) {
                    nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
                        @Override
                        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) { }
                        @Override
                        public void onServiceResolved(NsdServiceInfo serviceInfo) {
                            final String foundIp = serviceInfo.getHost().getHostAddress();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(dev_ip.getText().toString().isEmpty()) {
                                        dev_ip.setText(foundIp);
                                        Toast.makeText(DeviceAdd.this, "Found SmartHome node: " + foundIp, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) { }
        };
        try {
            nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
