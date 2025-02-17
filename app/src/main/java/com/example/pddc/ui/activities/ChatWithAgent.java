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
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.ChatAdapter;
import com.example.pddc.ui.classes.Message;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    String api_key;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_agent);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        //Emoji Button
        ImageButton btnEmoji = findViewById(R.id.btnEmoji);
        btnEmoji.setOnClickListener(v -> {
            editTextMessage.requestFocus();
            editTextMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(editTextMessage, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        //Attachment Button
        ImageButton btnAttachFile = findViewById(R.id.btnAttachFile);
        btnAttachFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a file"),100);
        });


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

        SharedPreferences displayCredentials = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String userId = displayCredentials.getString("membershipNo", null);

        String agentId = "VA25-Agent-00001";
        String chatRoomId = userId + "_" + agentId; // Unique chat room ID

        // Load existing messages

        // Send Button Click Listener
        buttonSend.setOnClickListener(v -> {
            String userInput = editTextMessage.getText().toString().trim();

            if (!userInput.isEmpty()) {
                        // Add user message
                        messages.add(new Message(userInput, true, getCurrentTime()));
                        chatAdapter.notifyDataSetChanged();
                        editTextMessage.setText("");
                        recyclerViewMessages.scrollToPosition(messages.size() - 1);

                        // Send user input to AI
                        sendMessageToAI(userInput);

            } else {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });

//        // Initializing Assistant's Message
//        messages.add(new Message("Hello! I'm Talia, your virtual Poultry Nurse. How can I assist you today?", false, getCurrentTime()));
//        chatAdapter.notifyDataSetChanged();
    }



    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            Objects.requireNonNull(selectedFileUri).getLastPathSegment();

        }
    }

    /**
     * Sends a message to OpenAI and retrieves a response.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void sendMessageToAI(String userInput) {
        OkHttpClient client = new OkHttpClient();

        if (Objects.equals(userInput, "Hello")) {
            messages.add(new Message("Hi, I'm Talia, your virtual Poultry Nurse. How can I assist you today?", false, getCurrentTime()));
            chatAdapter.notifyDataSetChanged();
        } else {
            String jsonRequest = "{"
                    + "\"model\": \"deepseek-chat\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userInput + "\"}],"
                    + "\"temperature\": 0.7,"
                    + "\"max_tokens\": 150"
                    + "}";

            RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json"));
            String deepSeekApiKey = "YOUR_DEEPSEEK_API_KEY";
            Request request = new Request.Builder()
                    .url("https://api.deepseek.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + deepSeekApiKey)
                    .post(body)
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
                            messages.add(new Message(aiResponse, false, getCurrentTime()));
                            chatAdapter.notifyDataSetChanged();
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);

                        });
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown Error";
                        Log.e("DeepSeek Error", errorBody);

                        runOnUiThread(() -> {
                            messages.add(new Message("I'm sorry, I couldn't process your request. Please try again.", false, getCurrentTime()));
                            chatAdapter.notifyDataSetChanged();
                            recyclerViewMessages.scrollToPosition(messages.size() - 1);
                        });
                    }
                }
            });
        }

    }

    /**
     * Extracts AI response text from the JSON response.
     */
    private String extractResponseFromJSON(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
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
