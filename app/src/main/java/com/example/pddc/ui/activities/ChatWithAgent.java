package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.ChatAdapter;
import com.example.pddc.ui.classes.Message;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    String OPENAI_API_URL;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_agent);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR );

        api_key = getString(R.string.openai_api_key);
        OPENAI_API_URL = "https://api.openai.com/v1/completions";
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

        // Setup RecyclerView
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(chatAdapter);

        // Initialize with Assistant's Opening Message
        messages.add(new Message("Hello! I'm Talia, your virtual poultry assistant. I specialize in poultry health, diseases, cures, and smart farming techniques. How can I assist you today?", false));
        chatAdapter.notifyDataSetChanged();

        // Send Button Click Listener
        buttonSend.setOnClickListener(v -> {
            String userInput = editTextMessage.getText().toString().trim();
            if (!userInput.isEmpty()) {
                messages.add(new Message(userInput, true)); // Add user message
                chatAdapter.notifyDataSetChanged();
                editTextMessage.setText("");
                recyclerViewMessages.scrollToPosition(messages.size() - 1);

                // Send user input to AI
                sendMessageToAI(userInput);
            } else {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sends a message to OpenAI and retrieves a response.
     */
    private void sendMessageToAI(String userInput) {
        OkHttpClient client = new OkHttpClient();

        String jsonRequest = "{"
                + "\"model\":\"text-davinci-003\","
                + "\"prompt\":\"You are Talia, a virtual assistant focused on poultry topics such as health, diseases, cures, mitigations, and smart farming. Respond straight to the point and introduce yourself as Talia if necessary. Question: " + userInput + "\","
                + "\"max_tokens\":150,"
                + "\"temperature\":0.7"
                + "}";

        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + api_key)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    messages.add(new Message("I'm sorry, something went wrong. Please try again later.", false));
                    chatAdapter.notifyDataSetChanged();
                    recyclerViewMessages.scrollToPosition(messages.size() - 1);
                });
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    String aiResponse = extractResponseFromJSON(responseBody); // Parse JSON for AI response

                    runOnUiThread(() -> {
                        messages.add(new Message(aiResponse, false)); // Add AI message
                        chatAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(messages.size() - 1);
                    });
                } else {
                    runOnUiThread(() -> {
                        messages.add(new Message("I'm sorry, I couldn't process your request. Please try again.", false));
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
            // Use Gson or JSONObject to parse the response
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getJSONArray("choices").getJSONObject(0).getString("text").trim();
        } catch (Exception e) {
            return "I'm sorry, I couldn't understand the response.";
        }
    }
}
