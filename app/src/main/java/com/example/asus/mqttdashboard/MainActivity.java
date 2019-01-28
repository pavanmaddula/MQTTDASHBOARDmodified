package com.example.asus.mqttdashboard;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    RingProgressBar progressBar, progressBarA, progressBarB, progressBarC,progressBarD;
    ImageView image;
    TextView textview;
    static String topicstr = "hehe";
    static String topicstr1 = "wT-Bh";//subscribe
    static String topicstr2 = "wT-BhC";//publish
    String message;
    String status;

    int red = Color.parseColor("#FF4500");
    int green = Color.parseColor("#008000");
    MqttAndroidClient client;
    MqttConnectOptions options;
    //static String MQTTHOST="tcp://test.mosquitto.org";
    static String MQTTHOST = "tcp://18.232.46.180";
    //static String MQTTHOST="tcp://";
    String value;
    Handler myHandler;
    int progress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView) findViewById(R.id.text2);
        image = (ImageView) findViewById(R.id.image);
        Button button=(Button)findViewById(R.id.w1);
        progressBar = (RingProgressBar) findViewById(R.id.ringprogress);
        progressBarA = (RingProgressBar) findViewById(R.id.ringprogress1);
        progressBarB = (RingProgressBar) findViewById(R.id.ringprogress2);
        progressBarC = (RingProgressBar) findViewById(R.id.ringprogress3);
        progressBarD = (RingProgressBar) findViewById(R.id.ringprogress4);
         mqttdashboard();
         }
    public void mqttdashboard(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        options = new MqttConnectOptions();
            try {
                IMqttToken token = client.connect(options);
                options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
                options.setCleanSession(true);
                options.setKeepAliveInterval(15000);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        status = "connected";
                        if (status != "connected") {
                            image.setColorFilter(red);
                        } else {
                            image.setColorFilter(green);
                        }
                        Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                        sub();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        status = "failure";
                        if (status == "failure") {
                            image.setColorFilter(red);
                        } else {
                            image.setColorFilter(green);
                        }
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(MainActivity.this, "failure", Toast.LENGTH_SHORT).show();


                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String message2=new String(message.getPayload());
                    String[] parts = message2.split(":");
                    String message3=parts[1];
                    String message4=parts[2];
                    String[] nextpart=message3.split(",");
                    String waste=parts[2].substring(0, parts[2].length() - 2);
                    System.out.println(waste);
                    waste = waste.substring(1);
                    System.out.println(waste);
                    int data=Integer.parseInt(waste);
                    System.out.println(data);
                    data=100-((data*100)/120);
                    nextpart[0]= nextpart[0].replace("\"", "");
                    System.out.println(nextpart[0]);
                    String tankid=nextpart[0];
                    tankid=tankid.substring(2);
                    System.out.println(tankid);
                    int id=Integer.parseInt(tankid);
                    //progressBar.setProgress(data);


                    if(id == 1){
                        progressBar.setProgress(data);

                    }
                    if(id == 2){
                        progressBarA.setProgress(data);
                    }
                    if(id == 3){
                        progressBarB.setProgress(data);
                    }
                    if(id == 4){
                        progressBarC.setProgress(data);
                    }
                    if(id == 5){
                        progressBarD.setProgress(data);
                    }
                    textview.setText(new String(message.getPayload()));
                    progressBar.setProgress(Integer.parseInt(new String(message.getPayload())));
                    progressBarA.setProgress(Integer.parseInt(new String(message.getPayload())));
                    progressBarB.setProgress(Integer.parseInt(new String(message.getPayload())));
                    progressBarC.setProgress(Integer.parseInt(new String(message.getPayload())));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });


        }
        public void ringProgress() {
            progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    Toast.makeText(MainActivity.this, "completed", Toast.LENGTH_SHORT).show();
                }
            });
            myHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    progressBar.setProgress(progress);
                    progressBar.setTextSize(50);
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        myHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //}
                }
            }).start();
        }

        private void sub() {
            try {
                client.subscribe(topicstr1, 0);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        public void publish() {
            try {
                client.publish(topicstr2, message.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        public void w1(View v){
            message = "11";
            publish();
        }
        public void w10(View v){
            message = "10";
            publish();
        }
        public void w21(View v){
            message = "21";
            publish();
        }
        public void w20(View v){
            message = "20";
            publish();
        }
        public void w31(View v){
            message = "31";
            publish();
        }
        public void w30(View v){
            message = "30";
            publish();
        }
        public void w41(View v){
            message = "41";
            publish();
        }
        public void w40(View v){
            message = "40";
            publish();
        }
        public void w51(View v){
            message = "51";
            publish();
        }
        public void w50(View v){
            message = "50";
            publish();
        }
    }



