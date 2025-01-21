package com.example.pddc.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.adapters.ChatAdapter;
import com.example.pddc.ui.classes.Message;

import java.util.ArrayList;
import java.util.List;



public class ChatWithAgent extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ChatAdapter chatAdapter;
    private List<Message> messages;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_agent);

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
        messages.add(new Message("Thank you for reaching out! Am PDDC Assistant AI, How can I assist you today? If you wish to be connected to our real Agent, type yes. To continue with me type no.", false));
        chatAdapter.notifyDataSetChanged();

        // Send Button Click Listener
        buttonSend.setOnClickListener(v -> {
            String userInput = editTextMessage.getText().toString().trim();
            if (!userInput.isEmpty()) {
                messages.add(new Message(userInput, true)); // Add user message
                handleUserInput(userInput); // Handle the user's input
                chatAdapter.notifyDataSetChanged();
                editTextMessage.setText("");
                recyclerViewMessages.scrollToPosition(messages.size() - 1);
            } else {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles user input and responds based on the current conversation flow.
     */
    private void handleUserInput(String userInput) {
        switch (userInput) {
            case "yes": // Connect to a real agent
                messages.add(new Message("Please wait, You will be connected shortly...", false));
                break;

            case "no": // Continue with the assistant
                messages.add(new Message("Thank you, please select the topic number about your issues:\n" +
                        "1: About Diseases,\n" +
                        "2: About your Poultry house/farm,\n" +
                        "3: About the PDDC.\n\nIf there is no choice of yours, press 0.", false));
                break;

            case "1": // User selected "About Diseases"
                messages.add(new Message("You selected 'About Diseases'. Please state your specific question or concern about diseases.", false));
                break;

            case "2": // User selected "About your Poultry house/farm"
                messages.add(new Message("You selected 'About your Poultry house/farm'. Please describe your issue or question.", false));
                break;

            case "3": // User selected "About the PDDC"
                messages.add(new Message("You selected 'About the PDDC'. Please specify your question or concern.", false));
                break;

            case "0": // No matching topics
                messages.add(new Message("Please state your issue.", false));
                break;

            default: // Invalid input
                messages.add(new Message("I'm sorry, I didn't understand that. Please enter a valid option.", false));
                break;
        }
    }
}

