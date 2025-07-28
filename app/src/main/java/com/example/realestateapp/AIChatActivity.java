package com.example.realestateapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private FloatingActionButton sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private Handler handler = new Handler();
    private Random random = new Random();

    // Store some pre-defined responses for different types of queries
    private final String[] propertySearchResponses = {
            "I found several properties matching your criteria. Would you like to see properties in a specific neighborhood?",
            "There are a few great options available! Are you looking for any specific amenities like a pool or garden?",
            "I've found some properties that might interest you. What's your preferred budget range so I can narrow down the results?",
            "Based on your preferences, I can show you 12 properties. Would you like to sort them by price or location?"
    };

    private final String[] priceResponses = {
            "Properties in that area typically range from PKR 7,500,000 to PKR 25,000,000 depending on size and amenities. What's your budget?",
            "For a 3-bedroom property in that location, prices start around PKR 12,000,000. Is that within your budget?",
            "Luxury properties in that neighborhood are priced between PKR 30,000,000 and PKR 50,000,000. Should I show you some options?",
            "There are some great affordable options starting at PKR 5,000,000. Would you like me to share more details?"
    };

    private final String[] locationResponses = {
            "That's a great area! It's close to shopping centers, parks, and has excellent schools nearby. What type of property are you looking for there?",
            "This neighborhood is trending right now with many new developments. Would you prefer an apartment or a house?",
            "That area has excellent transportation links and is very family-friendly. Are you looking for something ready to move in or open to renovation?",
            "I have several listings in that location. The area has seen 15% property value growth over the last 2 years. Would you like to know more about investment potential?"
    };

    private final String[] generalResponses = {
            "I'm here to help with your property search! Are you looking to buy, rent, or sell?",
            "I can assist with finding properties, providing market insights, or answering questions about the buying process. What can I help with today?",
            "Would you like me to recommend properties based on your preferences? I just need to know a bit more about what you're looking for.",
            "I can help you find your dream home! What features are most important to you in a property?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("AI Property Assistant");

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Add welcome message
        messages.add(new ChatMessage(ChatMessage.TYPE_AI,
                "Hello! I'm your AI property assistant. How can I help you find your perfect property today?"));
        chatAdapter.notifyItemInserted(messages.size() - 1);

        sendButton.setOnClickListener(v -> sendMessage());

        // Show typing indicator after 5 seconds to engage the user
        handler.postDelayed(() -> {
            if (messages.size() == 1) { // Only if user hasn't sent a message yet
                showTypingIndicator();
                handler.postDelayed(() -> {
                    removeTypingIndicator();
                    String suggestion = "I can help you search for properties, provide market insights, or answer questions about neighborhoods. What are you interested in today?";
                    messages.add(new ChatMessage(ChatMessage.TYPE_AI, suggestion));
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    chatRecyclerView.scrollToPosition(messages.size() - 1);
                }, 2000);
            }
        }, 5000);
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            // Add user message
            messages.add(new ChatMessage(ChatMessage.TYPE_USER, message));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.scrollToPosition(messages.size() - 1);

            messageInput.setText("");

            // Show typing indicator
            showTypingIndicator();

            // Generate response based on message content with a realistic delay
            int typingTime = 1000 + random.nextInt(2000); // 1-3 seconds

            handler.postDelayed(() -> {
                // Remove typing indicator
                removeTypingIndicator();

                // Generate AI response based on message content
                String aiResponse = generateAIResponse(message);
                messages.add(new ChatMessage(ChatMessage.TYPE_AI, aiResponse));
                chatAdapter.notifyItemInserted(messages.size() - 1);
                chatRecyclerView.scrollToPosition(messages.size() - 1);

                // Sometimes add a follow-up question after a delay
                if (random.nextInt(100) < 70) { // 70% chance
                    showTypingIndicator();

                    handler.postDelayed(() -> {
                        removeTypingIndicator();
                        String followUp = generateFollowUpQuestion(message);
                        messages.add(new ChatMessage(ChatMessage.TYPE_AI, followUp));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        chatRecyclerView.scrollToPosition(messages.size() - 1);
                    }, 1500 + random.nextInt(1000));
                }
            }, typingTime);
        }
    }

    private void showTypingIndicator() {
        messages.add(new ChatMessage(ChatMessage.TYPE_TYPING, null));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.scrollToPosition(messages.size() - 1);
    }

    private void removeTypingIndicator() {
        // Find and remove typing indicator
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getType() == ChatMessage.TYPE_TYPING) {
                messages.remove(i);
                chatAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    private String generateAIResponse(String userMessage) {
        // Process user message for more intelligent responses
        String lowercaseMessage = userMessage.toLowerCase();

        // Check message content to provide contextual responses
        if (lowercaseMessage.contains("price") || lowercaseMessage.contains("cost") ||
                lowercaseMessage.contains("budget") || lowercaseMessage.contains("expensive") ||
                lowercaseMessage.contains("cheap")) {
            return getRandomResponse(priceResponses);
        } else if (lowercaseMessage.contains("location") || lowercaseMessage.contains("area") ||
                lowercaseMessage.contains("neighborhood") || lowercaseMessage.contains("city") ||
                lowercaseMessage.contains("islamabad") || lowercaseMessage.contains("lahore") ||
                lowercaseMessage.contains("karachi")) {
            return getRandomResponse(locationResponses);
        } else if (lowercaseMessage.contains("property") || lowercaseMessage.contains("house") ||
                lowercaseMessage.contains("apartment") || lowercaseMessage.contains("flat") ||
                lowercaseMessage.contains("home") || lowercaseMessage.contains("buy") ||
                lowercaseMessage.contains("rent") || lowercaseMessage.contains("sell")) {
            return getRandomResponse(propertySearchResponses);
        } else {
            return getRandomResponse(generalResponses);
        }
    }

    private String generateFollowUpQuestion(String userMessage) {
        String[] followUpQuestions = {
                "Can I help you with anything else about your property search?",
                "Would you like me to show you some featured properties that match your criteria?",
                "Are there specific amenities you're looking for in your ideal property?",
                "What areas are you most interested in?",
                "Are you looking for a long-term investment or a home to live in?",
                "Would you like to schedule a viewing for any properties you're interested in?",
                "Have you considered financing options? I can provide some information on current mortgage rates."
        };

        return getRandomResponse(followUpQuestions);
    }

    private String getRandomResponse(String[] responses) {
        int index = random.nextInt(responses.length);
        return responses[index];
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
        return true;
    }

    // Add this class to avoid android.os.Handler import issues
    private static class Handler {
        private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

        public void postDelayed(Runnable r, long delayMillis) {
            handler.postDelayed(r, delayMillis);
        }

        public void removeCallbacksAndMessages() {
            handler.removeCallbacksAndMessages(null);
        }
    }
}