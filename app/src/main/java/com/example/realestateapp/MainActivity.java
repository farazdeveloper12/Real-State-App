package com.example.realestateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView chatRecyclerView, propertyRecyclerView;
    private LinearLayout optionsContainer;
    private Button option1Button, option2Button;
    private ConstraintLayout successMessageContainer;
    private TextView successMessageText;
    private FrameLayout fragmentContainer;
    private BottomNavigationView bottomNavigationView;
    private ChatAdapter chatAdapter;
    private PropertyAdapter propertyAdapter;
    private List<ChatMessage> chatMessages;
    private List<Property> propertyList;
    private int chatStep = 0; // To track the AI conversation steps
    private String userAction, userBudget, userCity;
    private static final String TAG = "MainActivity";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting MainActivity");
        try {
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to set content view", e);
            Toast.makeText(this, "Error initializing UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize Handler
        handler = new Handler(Looper.getMainLooper());

        // Initialize UI components with null checks
        try {
            toolbar = findViewById(R.id.toolbar);
            chatRecyclerView = findViewById(R.id.chatRecyclerView);
            propertyRecyclerView = findViewById(R.id.propertyRecyclerView);
            optionsContainer = findViewById(R.id.optionsContainer);
            option1Button = findViewById(R.id.option1Button);
            option2Button = findViewById(R.id.option2Button);
            successMessageContainer = findViewById(R.id.successMessageContainer);
            successMessageText = findViewById(R.id.successMessageText);
            fragmentContainer = findViewById(R.id.fragment_container);
            bottomNavigationView = findViewById(R.id.bottomNavigation);

            // Verify all views are initialized
            if (toolbar == null || chatRecyclerView == null || propertyRecyclerView == null ||
                    optionsContainer == null || option1Button == null || option2Button == null ||
                    successMessageContainer == null || successMessageText == null ||
                    fragmentContainer == null || bottomNavigationView == null) {
                Log.e(TAG, "onCreate: One or more UI components are null");
                Toast.makeText(this, "UI initialization error", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to initialize UI components", e);
            Toast.makeText(this, "Error initializing UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up Toolbar
        try {
            setSupportActionBar(toolbar);
            toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
            Log.d(TAG, "onCreate: Toolbar set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to set up toolbar", e);
            Toast.makeText(this, "Error setting up toolbar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Initialize Chat RecyclerView
        try {
            chatMessages = new ArrayList<>();
            chatAdapter = new ChatAdapter(chatMessages);
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            chatRecyclerView.setAdapter(chatAdapter);
            Log.d(TAG, "onCreate: Chat RecyclerView initialized");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to initialize Chat RecyclerView", e);
            Toast.makeText(this, "Error initializing chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Initialize Property RecyclerView (initially hidden)
        try {
            propertyList = new ArrayList<>();
            propertyAdapter = new PropertyAdapter(propertyList);
            propertyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            propertyRecyclerView.setAdapter(propertyAdapter);
            Log.d(TAG, "onCreate: Property RecyclerView initialized");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to initialize Property RecyclerView", e);
            Toast.makeText(this, "Error initializing property list: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Initialize BottomNavigationView
        try {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Show Home section (AI chat and properties)
                    fragmentContainer.setVisibility(View.GONE);
                    chatRecyclerView.setVisibility(View.VISIBLE);
                    optionsContainer.setVisibility(View.VISIBLE);
                    if (chatStep >= 4) {
                        propertyRecyclerView.setVisibility(View.VISIBLE);
                        chatRecyclerView.setVisibility(View.GONE);
                        optionsContainer.setVisibility(View.GONE);
                    }
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    // Hide Home section and show SearchFragment
                    chatRecyclerView.setVisibility(View.GONE);
                    propertyRecyclerView.setVisibility(View.GONE);
                    optionsContainer.setVisibility(View.GONE);
                    successMessageContainer.setVisibility(View.GONE);
                    fragmentContainer.setVisibility(View.VISIBLE);
                    loadFragment(new SearchFragment(), true);
                    return true;
                } else if (itemId == R.id.navigation_saved) {
                    // Hide Home section and show SavedFragment
                    chatRecyclerView.setVisibility(View.GONE);
                    propertyRecyclerView.setVisibility(View.GONE);
                    optionsContainer.setVisibility(View.GONE);
                    successMessageContainer.setVisibility(View.GONE);
                    fragmentContainer.setVisibility(View.VISIBLE);
                    loadFragment(new SavedFragment(), true);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    // Hide Home section and show ProfileFragment
                    chatRecyclerView.setVisibility(View.GONE);
                    propertyRecyclerView.setVisibility(View.GONE);
                    optionsContainer.setVisibility(View.GONE);
                    successMessageContainer.setVisibility(View.GONE);
                    fragmentContainer.setVisibility(View.VISIBLE);
                    loadFragment(new ProfileFragment(), true);
                    return true;
                }
                return false;
            });
            Log.d(TAG, "onCreate: BottomNavigationView set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to set up BottomNavigationView", e);
            Toast.makeText(this, "Error setting up navigation: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Start AI conversation
        try {
            startAIConversation();
            Log.d(TAG, "onCreate: Started AI conversation");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to start AI conversation", e);
            Toast.makeText(this, "Error starting AI conversation: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Log.d(TAG, "onCreate: Option button listeners set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Failed to set up option button listeners", e);
            Toast.makeText(this, "Error setting up options: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startAIConversation() {
        // Step 1: Welcome message and ask if user wants to buy or rent
        addAIMessage(getString(R.string.welcome_message));
        option1Button.setText(R.string.buy);
        option2Button.setText(R.string.rent);
        showOptions();
        chatStep = 1;
    }

    private void addAIMessage(String message) {
        chatMessages.add(new ChatMessage(ChatMessage.TYPE_AI, message));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void showOptions() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        option1Button.setVisibility(View.VISIBLE);
        option2Button.setVisibility(View.VISIBLE);
        option1Button.startAnimation(fadeIn);
        option2Button.startAnimation(fadeIn);
    }

    private void hideOptions() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
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
                    addAIMessage(getString(R.string.budget_question));
                    option1Button.setText(R.string.budget_low);
                    option2Button.setText(R.string.budget_high);
                    showOptions();
                    chatStep = 2;
                }
                break;
            case 2:
                // Step 2: User specifies budget
                userBudget = userSelection;
                addAIMessage(getString(R.string.city_question));
                option1Button.setText(R.string.city_islamabad);
                option2Button.setText(R.string.city_lahore);
                showOptions();
                chatStep = 3;
                break;
            case 3:
                // Step 3: User specifies city
                userCity = userSelection;
                addAIMessage(getString(R.string.processing_message));
                chatStep = 4;

                // Simulate AI fetching properties
                handler.postDelayed(() -> {
                    try {
                        if (!isFinishing()) {
                            fetchProperties();
                            chatRecyclerView.setVisibility(View.GONE);
                            optionsContainer.setVisibility(View.GONE);
                            showSuccessMessage(getString(R.string.success_message));
                            handler.postDelayed(() -> {
                                propertyRecyclerView.setVisibility(View.VISIBLE);
                                Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                                propertyRecyclerView.startAnimation(slideIn);
                            }, 2000); // Delay to show the message for 2 seconds
                            Log.d(TAG, "Successfully fetched properties and updated UI");
                        } else {
                            Log.w(TAG, "Activity is finishing, cannot fetch properties");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error during fetchProperties", e);
                        Toast.makeText(this, "Error fetching properties: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }, 1000); // Simulate a 1-second delay for AI "processing"
                break;
        }
    }

    private void fetchProperties() {
        // Simulate fetching properties based on user requirements
        // For now, we’ll use sample data
        Log.d(TAG, "Fetching properties for action: " + userAction + ", budget: " + userBudget + ", city: " + userCity);
        propertyList.clear();
        if (userCity != null && userCity.toLowerCase().contains("islamabad")) {
            propertyList.add(new Property("Modern House in F-7", "AED 1,200,000", "Islamabad, F-7", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
            propertyList.add(new Property("Luxury Apartment in E-11", "AED 800,000", "Islamabad, E-11", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
            propertyList.add(new Property("Villa in G-13", "AED 1,500,000", "Islamabad, G-13", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
        } else {
            propertyList.add(new Property("Cozy Townhouse", "AED 600,000", userCity != null ? userCity : "Unknown City", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
            propertyList.add(new Property("Spacious Penthouse", "AED 2,000,000", userCity != null ? userCity : "Unknown City", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c"));
        }
        // Using notifyDataSetChanged() since the entire list is cleared and repopulated
        propertyAdapter.notifyDataSetChanged();
    }

    private void showSuccessMessage(String message) {
        Log.d(TAG, "showSuccessMessage: Displaying message: " + message);
        try {
            if (successMessageText != null && successMessageContainer != null) {
                successMessageText.setText(message);
                successMessageContainer.setVisibility(View.VISIBLE);

                // Load and apply enter animation
                Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.success_message_enter);
                successMessageContainer.startAnimation(enterAnimation);
                Log.d(TAG, "showSuccessMessage: Enter animation started");

                // Hide the message after a delay with exit animation
                handler.postDelayed(() -> {
                    Log.d(TAG, "showSuccessMessage: Starting exit animation");
                    try {
                        if (!isFinishing()) {
                            Animation exitAnimation = AnimationUtils.loadAnimation(this, R.anim.success_message_exit);
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
                            Log.w(TAG, "showSuccessMessage: Activity is finishing, skipping exit animation");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error during success message exit animation", e);
                    }
                }, 1500); // Show the message for 1.5 seconds
            } else {
                Log.e(TAG, "Success message views are null during showSuccessMessage");
                Toast.makeText(this, "UI error: Cannot display success message", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying success message", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFragment(Fragment fragment, boolean withAnimation) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Always use animations for Fragment transitions as per requirement
        if (withAnimation) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,  // Enter animation
                    R.anim.slide_out_left,  // Exit animation
                    R.anim.slide_in_left,   // Pop enter animation
                    R.anim.slide_out_right  // Pop exit animation
            );
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_search) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_search);
            return true;
        } else if (itemId == R.id.action_filter) {
            // Handle filter action (to be implemented later)
            return true;
        } else if (itemId == R.id.action_profile) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Cleaning up MainActivity");
        // Remove any pending callbacks to prevent memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            Log.d(TAG, "onDestroy: Removed all handler callbacks");
        }
    }
}