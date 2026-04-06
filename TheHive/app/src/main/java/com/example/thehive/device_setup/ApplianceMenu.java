package com.example.thehive.device_setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thehive.R;
import com.example.thehive.timer.Timer;

import java.util.ArrayList;


public class ApplianceMenu extends AppCompatActivity {

    ToggleButton OnOFF;
    String urls1=null,qreply,murls;
     Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_menu);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        device = (Device) args.getSerializable("Device Data");
        ImageView dev_image=findViewById(R.id.dev_im);
        dev_image.setImageResource(device.getImageid());
        TextView dev_name= findViewById(R.id.devnm);
        dev_name.setText(device.get_Name());

        OnOFF = findViewById(R.id.toggleButton);
        setupToggleListener();
        communicate(sendURL_status());
    }

    void onTimer(View view)
    {
        Intent i = new Intent(this, Timer.class);
        startActivity(i);//Passing appliance data left
    }

    String sendURL_status()
    {
        urls1 = device.getIp() + "/status?pin=" + Integer.toString(device.getGpio());
        return urls1;
    }
    String sendURL_toggle()
    {
        urls1 = device.getIp() + "/toggle?pin=" + Integer.toString(device.getGpio()) + "&key=123456";
        return urls1;
    }

    public void communicate(String s) {
        Log.i("Info","Changing state of bulb");
        murls="http://"+urls1;
        talkNODEmcu(murls);
    }

    public void setstate(String response) {
        if (response.equals("404")) {
            Toast.makeText(ApplianceMenu.this, "Network Error...", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            org.json.JSONObject json = new org.json.JSONObject(response);
            if (json.has("state")) {
                String state = json.getString("state");
                Toast.makeText(ApplianceMenu.this, state, Toast.LENGTH_SHORT).show();
                if (state.equalsIgnoreCase("ON")) {
                    OnOFF.setOnCheckedChangeListener(null); 
                    OnOFF.setChecked(true);
                    setupToggleListener(); 
                } else {
                    OnOFF.setOnCheckedChangeListener(null);
                    OnOFF.setChecked(false);
                    setupToggleListener();
                }
            } else if (json.has("error")) {
                Toast.makeText(ApplianceMenu.this, "Error: " + json.getString("error"), Toast.LENGTH_SHORT).show();
            }
        } catch (org.json.JSONException e) {
            Toast.makeText(ApplianceMenu.this, "Invalid Response", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupToggleListener() {
        OnOFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                communicate(sendURL_toggle());
            }
        });
    }

    public void talkNODEmcu(String urls) { //make tcp communication with nodemcu

        if (urls != null) {

            Log.i("Info","url:" + urls);
            final RequestQueue requestQueue = Volley.newRequestQueue(ApplianceMenu.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urls,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            qreply = response;
                            Log.i("Server message", qreply);
                            setstate(qreply);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    qreply = "404";
                    error.printStackTrace();
                }
            });
            requestQueue.add(stringRequest);
        }
    }//talk nodemcu ends

}
