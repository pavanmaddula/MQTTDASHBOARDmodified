package com.example.asus.mqttdashboard;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.io.UnsupportedEncodingException;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    RingProgressBar progressBar;
    ProgressBar progressBar1;
    TextView textview;
    static  String topicstr = "hehe";
    static  String topicstr1 = "hello";
    MqttAndroidClient client;
    MqttConnectOptions options;
    static String MQTTHOST="tcp://test.mosquitto.org";

    String value;
    Handler myHandler;
    int progress=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview=(TextView)findViewById(R.id.text2);
        String clientId = MqttClient.generateClientId();
         client = new MqttAndroidClient(this.getApplicationContext(),MQTTHOST , clientId);
         options = new MqttConnectOptions();


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"connected",Toast.LENGTH_SHORT).show();
                      sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this,"failure",Toast.LENGTH_SHORT).show();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

              textview.setText(new String(message.getPayload()));

              progressBar.setProgress(Integer.parseInt(new String(message.getPayload())));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        progressBar= (RingProgressBar) findViewById(R.id.ringprogress);

        Button button=(Button)findViewById(R.id.buttonpanel);
        final EditText editText= (EditText)findViewById(R.id.text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value=editText.getText().toString();
                progress=Integer.parseInt(value);
                ringProgress();
            }
        });
        // ringProgress();
    }
    public void ringProgress(){
        progressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                Toast.makeText(MainActivity.this,"completed",Toast.LENGTH_SHORT).show();
            }
        });
        myHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
           /*if (msg.what== 0){
                if(progress<100){
                    progress++;
                    progressBar.setProgress(progress);
                }
            }*/
                progressBar.setProgress(progress);

                progressBar.setTextSize(50);


            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //for (int i = 0; i < 100; i++) {

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


public void pub(View v){
    String topic = topicstr;
    String message = "message from mqtt";

    try {

        client.publish(topic, message.getBytes(),0,false);
    } catch ( MqttException e) {
        e.printStackTrace();
    }

}
private void sub(){
        try{
            client.subscribe(topicstr1,0);
        }catch (MqttException e){
            e.printStackTrace();

        }

}
}

