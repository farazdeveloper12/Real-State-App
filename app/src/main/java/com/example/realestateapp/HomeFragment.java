package com.example.realestateapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView chatRecyclerView, propertyRecyclerView;
    private LinearLayout optionsContainer;
    private Button option1Button, option2Button;
    private ConstraintLayout successMessageContainer;
    private TextView successMessageText;
    private ChatAdapter chatAdapter;
    private PropertyAdapter propertyAdapter;
    private List<ChatMessage> chatMessages;
    private List<Property> propertyList;
    private int chatStep = 0;
    private String userAction, userBudget, userCity;
    private static final String TAG = "HomeFragment";
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Handler
        handler = new Handler(Looper.getMainLooper());

        // Initialize UI components with null checks
        try {
            chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
            propertyRecyclerView = view.findViewById(R.id.propertyRecyclerView);
            optionsContainer = view.findViewById(R.id.optionsContainer);
            option1Button = view.findViewById(R.id.option1Button);
            option2Button = view.findViewById(R.id.option2Button);
            successMessageContainer = view.findViewById(R.id.successMessageContainer);
            successMessageText = view.findViewById(R.id.successMessageText);

            // Verify all views are initialized
            if (chatRecyclerView == null || propertyRecyclerView == null ||
                    optionsContainer == null || option1Button == null || option2Button == null ||
                    successMessageContainer == null || successMessageText == null) {
                Log.e(TAG, "onCreateView: One or more UI components are null");
                Toast.makeText(getContext(), "UI initialization error", Toast.LENGTH_LONG).show();
                return view;
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Failed to initialize UI components", e);
            Toast.makeText(getContext(), "Error initializing UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return view;
        }

        // Initialize Chat RecyclerView
        try {
            chatMessages = new ArrayList<>();
            chatAdapter = new ChatAdapter(chatMessages);
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chatRecyclerView.setAdapter(chatAdapter);
            Log.d(TAG, "onCreateView: Chat RecyclerView initialized");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Failed to initialize Chat RecyclerView", e);
            Toast.makeText(getContext(), "Error initializing chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Initialize Property RecyclerView (initially hidden)
        try {
            propertyList = new ArrayList<>();
            propertyAdapter = new PropertyAdapter(propertyList);
            propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            propertyRecyclerView.setAdapter(propertyAdapter);
            propertyRecyclerView.setVisibility(View.GONE);
            Log.d(TAG, "onCreateView: Property RecyclerView initialized");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Failed to initialize Property RecyclerView", e);
            Toast.makeText(getContext(), "Error initializing property list: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Start AI conversation
        try {
            startAIConversation();
            Log.d(TAG, "onCreateView: Started AI conversation");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Failed to start AI conversation", e);
            Toast.makeText(getContext(), "Error starting AI conversation: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Set up option button listeners
        try {
            option1Button.setOnClickListener(v -> {
                Log.d(TAG, "Option 1 button clicked: " + option1Button.getText());
                String userSelection = option1Button.getText().toString();
                processUserSelection(userSelection);
                hideOptions();
            });

            option2Button.setOnClickListener(v -> {
                Log.d(TAG, "Option 2 button clicked: " + option2Button.getText());
                String userSelection = option2Button.getText().toString();
                processUserSelection(userSelection);
                hideOptions();
            });
            Log.d(TAG, "onCreateView: Option button listeners set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Failed to set up option button listeners", e);
            Toast.makeText(getContext(), "Error setting up options: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void startAIConversation() {
        // Step 1: Welcome message and ask if user wants to buy or rent
        addAIMessage("Hello! Welcome to RealEstateApp. I’m here to help you find the perfect property. Are you looking to buy or rent?");
        option1Button.setText("Buy");
        option2Button.setText("Rent");
        showOptions();
        chatStep = 1;
    }

    private void addAIMessage(String message) {
        chatMessages.add(new ChatMessage(ChatMessage.TYPE_AI, message));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void showOptions() {
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        option1Button.setVisibility(View.VISIBLE);
        option2Button.setVisibility(View.VISIBLE);
        option1Button.startAnimation(fadeIn);
        option2Button.startAnimation(fadeIn);
    }

    private void hideOptions() {
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                option1Button.setVisibility(View.GONE);
                option2Button.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        option1Button.startAnimation(fadeOut);
        option2Button.startAnimation(fadeOut);
    }

    private void processUserSelection(String userSelection) {
        switch (chatStep) {
            case 1:
                // Step 1: User specifies buy or rent
                userAction = userSelection.toLowerCase();
                if (userAction.equals("buy") || userAction.equals("rent")) {
                    addAIMessage("Great! What’s your budget range?");
                    option1Button.setText("AED 0 - 500,000");
                    option2Button.setText("AED 500,000+");
                    showOptions();
                    chatStep = 2;
                }
                break;
            case 2:
                // Step 2: User specifies budget
                userBudget = userSelection;
                addAIMessage("Got it! Which city are you interested in?");
                option1Button.setText("Islamabad");
                option2Button.setText("Lahore");
                showOptions();
                chatStep = 3;
                break;
            case 3:
                // Step 3: User specifies city
                userCity = userSelection;
                addAIMessage("Thanks for your input! I’ll find properties matching your requirements...");
                chatStep = 4;

                // Simulate AI fetching properties
                handler.postDelayed(() -> {
                    try {
                        if (isAdded()) {
                            fetchProperties();
                            chatRecyclerView.setVisibility(View.GONE);
                            optionsContainer.setVisibility(View.GONE);
                            showSuccessMessage("Congratulations! Jo ghar aap dhoondna chahte the, aapko woh mil jayega. Is app ke through yahan aapke har problem aur need ko pura kiya jayega.");
                            handler.postDelayed(() -> {
                                propertyRecyclerView.setVisibility(View.VISIBLE);
                                Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
                                propertyRecyclerView.startAnimation(slideIn);
                            }, 2000); // Delay to show the message for 2 seconds
                            Log.d(TAG, "Successfully fetched properties and updated UI");
                        } else {
                            Log.w(TAG, "Fragment is detached, cannot fetch properties");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error during fetchProperties", e);
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error fetching properties: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, 1000); // Simulate a 1-second delay for AI "processing"
                break;
        }
    }

    private void fetchProperties() {
        // Simulate fetching properties based on user requirements
        // For now, we’ll use sample data
        propertyList.clear();
        if (userCity != null && userCity.toLowerCase().contains("islamabad")) {
            propertyList.add(new Property("Modern House in F-7", "AED 1,200,000", "Islamabad, F-7", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
            propertyList.add(new Property("Luxury Apartment in E-11", "AED 800,000", "Islamabad, E-11", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
            propertyList.add(new Property("Villa in G-13", "AED 1,500,000", "Islamabad, G-13", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
        } else {
            propertyList.add(new Property("Cozy Townhouse", "AED 600,000", userCity != null ? userCity : "Unknown City", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
            propertyList.add(new Property("Spacious Penthouse", "AED 2,000,000", userCity != null ? userCity : "Unknown City", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
        }
        propertyAdapter.notifyDataSetChanged();
    }

    private void showSuccessMessage(String message) {
        Log.d(TAG, "showSuccessMessage: Displaying message: " + message);
        try {
            if (successMessageText != null && successMessageContainer != null) {
                successMessageText.setText(message);
                successMessageContainer.setVisibility(View.VISIBLE);

                // Load and apply enter animation
                Animation enterAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.success_message_enter);
                successMessageContainer.startAnimation(enterAnimation);
                Log.d(TAG, "showSuccessMessage: Enter animation started");

                // Hide the message after a delay with exit animation
                handler.postDelayed(() -> {
                    Log.d(TAG, "showSuccessMessage: Starting exit animation");
                    try {
                        if (isAdded()) {
                            Animation exitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.success_message_exit);
                            exitAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    Log.d(TAG, "showSuccessMessage: Exit animation started");
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    successMessageContainer.setVisibility(View.GONE);
                                    Log.d(TAG, "showSuccessMessage: Exit animation ended, view hidden");
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {}
                            });
                            successMessageContainer.startAnimation(exitAnimation);
                        } else {
                            Log.w(TAG, "showSuccessMessage: Fragment is detached, skipping exit animation");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error during success message exit animation", e);
                    }
                }, 1500); // Show the message for 1.5 seconds
            } else {
                Log.e(TAG, "Success message views are null during showSuccessMessage");
                if (isAdded()) {
                    Toast.makeText(getContext(), "UI error: Cannot display success message", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying success message", e);
            if (isAdded()) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove any pending callbacks to prevent memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}