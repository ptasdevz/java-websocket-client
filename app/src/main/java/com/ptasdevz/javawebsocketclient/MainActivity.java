package com.ptasdevz.javawebsocketclient;

import android.nfc.Tag;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class MainActivity extends AppCompatActivity {

    WebSocketClient webSocketClient;
    private StompClient mStompClient;

    private static final String TAG = "WebSocket";
    private String sendAnimalSoundUri = "/app/send-animal-sound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animal_sound);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createWebSocketClient();
        setupStompConnection();

        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeAdapter(AbstractElement.class, new AbstractElementAdapter());
        Gson gson = gsonBilder.create();
        String msgStr = gson.toJson(new MyMessages());
        ImageButton dogBtn = findViewById(R.id.dog_btn);
        dogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webSocketClient.send("1");
                mStompClient.send(sendAnimalSoundUri, msgStr).subscribe();
            }
        });
        ImageButton catBtn = findViewById(R.id.cat_btn);
        catBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                webSocketClient.send("2");
                mStompClient.send(sendAnimalSoundUri, "2").subscribe();
            }
        });
        ImageButton foxBtn = findViewById(R.id.fox_btn);
        foxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                webSocketClient.send("3");
                mStompClient.send(sendAnimalSoundUri, "3").subscribe();
            }
        });
        ImageButton pigBtn = findViewById(R.id.pig_btn);
        pigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                webSocketClient.send("4");
                mStompClient.send(sendAnimalSoundUri, "4").subscribe();

            }
        });


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void setupStompConnection() {
        try {
            mStompClient = Stomp.over(
                    Stomp.ConnectionProvider.OKHTTP, "ws://192.168.137.1:8080/endpoint/websocket");
            mStompClient.connect();

            mStompClient.topic("/topic/animal-sounds").subscribe(topicMessage -> {
                String payload = topicMessage.getPayload();
                Log.d(TAG, payload);
                final String msg = payload;
                runOnUiThread(() -> {
                    try {
                        TextView textView = findViewById(R.id.animal_sound);
                        textView.setText(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });

            mStompClient.lifecycle().subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {
                    case OPENED:
                        Log.d(TAG, "Stomp connection opened");
                        break;
                    case CLOSED:
                        Log.d(TAG, "Stomp connection closed");
                        break;
                    case ERROR:
                        Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                        break;
                }
            });

            mStompClient.topic("/topic/greetings").subscribe(topicMessage -> {
                Log.d(TAG, topicMessage.getPayload());
            });

            mStompClient.send("/topic/hello-msg-mapping", "My first STOMP message!").subscribe();

//        mStompClient.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void createWebSocketClient() {

        URI uri;
        try {
            uri = new URI("ws://192.168.137.1:8080/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i(TAG, "Session is starting");
                webSocketClient.send("Hello World");
            }

            @Override
            public void onTextReceived(String message) {
                Log.i(TAG, "Message received.");
                final String msg = message;
                runOnUiThread(() -> {
                    try {
                        TextView textView = findViewById(R.id.animal_sound);
                        textView.setText(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void onCloseReceived() {
                Log.i(TAG, "Closed ");
                System.out.println("onCloseReceived");
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
