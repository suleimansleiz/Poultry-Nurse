package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.ChatAdapter;
import com.example.pddc.ui.classes.Message;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatWithAgent extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ChatAdapter chatAdapter;
    private List<Message> messages;

    private FirebaseFirestore db;
    private String farmerId;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_agent);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

         db = FirebaseFirestore.getInstance();
        SharedPreferences userCredPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        farmerId = userCredPreferences.getString("farmerId", "");
        if (!farmerId.isEmpty()) {
            loadMessagesFromFirestore(farmerId);
        }


        //Back Button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChatWithAgent.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize Views
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        ImageButton buttonSend = findViewById(R.id.buttonSend);
        editTextMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        // Setup RecyclerView
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(chatAdapter);


        // Send Button Click Listener
        buttonSend.setOnClickListener(v -> {
            String userInput = editTextMessage.getText().toString().trim();

            if (!userInput.isEmpty()) {
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        // Add user message
                        messages.add(new Message(userInput, true, time));
                        chatAdapter.notifyDataSetChanged();
                        editTextMessage.setText("");
                        recyclerViewMessages.scrollToPosition(messages.size() - 1);

                        saveMessageToFirestore(userInput, true, time);

                        // Send user input to AI
                        sendMessageToAI(userInput);
            } else {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * Saving Messages from Firestore db
     */
    private void saveMessageToFirestore(String content, boolean isUser, String time) {

        // Create message object
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("content", content);
        messageData.put("isUser", isUser);
        messageData.put("time", time);
        messageData.put("timestamp", FieldValue.serverTimestamp()); // For sorting messages

        // Save message under the user's chat collection
        db.collection("Users")
                .document(farmerId)
                .collection("chats")
                .add(messageData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Message saved!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving message", e));
    }


    /**
     * Loading Messages from Firestore db
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadMessagesFromFirestore(String farmerId) {
        db.collection("Users")
                .document(farmerId)
                .collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    messages.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String content = document.getString("content");
                        boolean isUser = Boolean.TRUE.equals(document.getBoolean("isUser"));
                        String time = document.getString("time");
                        messages.add(new Message(content, isUser, time));
                    }
                    chatAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading messages", e));
    }


    /**
     * Sends a message to AI and retrieves a response.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void sendMessageToAI(String userInput) {
        OkHttpClient client = new OkHttpClient();

        String jsonRequest = "{"
                + "\"contents\": [{"
                + "  \"parts\": [{\"text\": \"You are Talia, a virtual Poultry Nurse assistant. Your job is to help users with poultry farming questions. Stay professional and helpful.\\n\\nUser: " + userInput + "\"}]"
                + "}]}";

        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json"));
            String apiKey = getString(R.string.gemini_api_key); // Replace with your key
            Request request = new Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                    .post(RequestBody.create(jsonRequest, MediaType.get("application/json")))
                    .build();


            client.newCall(request).enqueue(new Callback() {

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(ChatWithAgent.this, "Network Error", Toast.LENGTH_SHORT).show());
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = Objects.requireNonNull(response.body()).string();
                        String aiResponse = extractResponseFromJSON(responseBody);

                        runOnUiThread(() -> {
                            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            messages.add(new Message(aiResponse, false, time));
                            chatAdapter.notifyDataSetChanged();
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);

                            saveMessageToFirestore(aiResponse, false, time);
                        });
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown Error";
                        Log.e("DeepSeek Error", "Response Code: " + response.code() + ", Body: " + errorBody);

                        runOnUiThread(() -> {
                            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            messages.add(new Message("I'm sorry, I couldn't process your request. Please try again.", false, time));
                            chatAdapter.notifyDataSetChanged();
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);
                        });
                    }
                }
            });

    }

    /**
     * Extracts AI response text from the JSON response.
     */
    private String extractResponseFromJSON(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text").trim();
                }
            }
            return "I'm sorry, I couldn't understand the response.";
        } catch (Exception e) {
            return "I'm sorry, I couldn't understand the response. Error: " + e.getMessage();
        }
    }


    /**
     * Network Connectivity.
     */
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView tvOnline = findViewById(R.id.tvAgentStatus);
            if (isOnline()) {
                tvOnline.setText(R.string.online);
            } else {
                tvOnline.setText(R.string.offline);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }



    //ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.agent_chat_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
