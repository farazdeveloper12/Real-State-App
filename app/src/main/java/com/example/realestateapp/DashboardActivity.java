package com.example.realestateapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View searchCard;
    private FloatingActionButton fabAIChat;
    private SharedPreferences preferences;
    private boolean isDarkMode = false;
    private Fragment currentFragment = null;
    private FirebaseAuth mAuth;
    private PropertyAdapter propertyAdapter;
    private final List<Property> recommendedProperties = new ArrayList<>();

    // Custom Navigation Views
    private LinearLayout navHome, navSearch, navSaved, navProfile;
    private View homeBackground, searchBackground, savedBackground, profileBackground;
    private TextView homeText, searchText, savedText, profileText;
    private ImageView homeIcon, searchIcon, savedIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply dark mode if enabled
        preferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        isDarkMode = preferences.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Log.d(TAG, "Setting content view to activity_dashboard");
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase Auth", e);
            finish();
            return;
        }

        Log.d(TAG, "Initializing views");
        initializeViews();
        Log.d(TAG, "Setting up navigation header");
        setupNavHeader();
        Log.d(TAG, "Setting up drawer");
        setupDrawer();
        Log.d(TAG, "Setting up custom bottom navigation");
        setupBottomNavigation();
        Log.d(TAG, "Setting up service options");
        setupServiceOptions();
        Log.d(TAG, "Displaying welcome animation");
        displayWelcomeAnimation();
        Log.d(TAG, "Showing recommended properties");
        showRecommendedProperties();
        Log.d(TAG, "Setting up AI chat");
        setupAIChat();

        // Replace onBackPressed with OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(DashboardActivity.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentFragment != null) {
                    clearFragments();
                } else if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    DashboardActivity.this.finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        if (mAuth != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String displayName = user.getDisplayName();
            String email = user.getEmail();

            // Update main dashboard welcome text
            TextView userNameText = findViewById(R.id.userNameText);
            if (userNameText != null) {
                userNameText.setText(displayName != null && !displayName.isEmpty() ?
                        displayName : getString(R.string.user_placeholder));
            }

            // Update navigation drawer header
            if (navigationView != null) {
                View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
                    TextView navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);

                    if (navHeaderName != null && navHeaderEmail != null) {
                        navHeaderName.setText(displayName != null && !displayName.isEmpty() ?
                                displayName : getString(R.string.user_placeholder));
                        navHeaderEmail.setText(email != null ? email : "");
                    }
                }
            }
        }
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        searchCard = findViewById(R.id.searchCard);
        fabAIChat = findViewById(R.id.fabAIChat);

        // Set up toolbar as action bar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Set up properties recycler view with horizontal scrolling
        RecyclerView propertyRecyclerView = findViewById(R.id.propertyRecyclerView);
        if (propertyRecyclerView != null) {
            propertyRecyclerView.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            propertyAdapter = new PropertyAdapter(recommendedProperties);
            propertyRecyclerView.setAdapter(propertyAdapter);
        }

        // Set click listener for search
        if (searchCard != null) {
            searchCard.setOnClickListener(v -> showFragment(new SearchFragment(), "search"));
        }

        // Custom Bottom Navigation Views
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navSaved = findViewById(R.id.navSaved);
        navProfile = findViewById(R.id.navProfile);

        homeBackground = findViewById(R.id.homeBackground);
        searchBackground = findViewById(R.id.searchBackground);
        savedBackground = findViewById(R.id.savedBackground);
        profileBackground = findViewById(R.id.profileBackground);

        homeText = findViewById(R.id.homeText);
        searchText = findViewById(R.id.searchText);
        savedText = findViewById(R.id.savedText);
        profileText = findViewById(R.id.profileText);

        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        savedIcon = findViewById(R.id.savedIcon);
        profileIcon = findViewById(R.id.profileIcon);
    }

    private void setupNavHeader() {
        if (navigationView == null) return;

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            de.hdodenhof.circleimageview.CircleImageView profileImage =
                    headerView.findViewById(R.id.navHeaderImage);

            FirebaseUser user = mAuth != null ? mAuth.getCurrentUser() : null;
            if (user != null && user.getPhotoUrl() != null && profileImage != null) {
                try {
                    com.bumptech.glide.Glide.with(this)
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_person)
                            .into(profileImage);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load profile image with Glide", e);
                }
            }
        }
    }

    private void setupDrawer() {
        // Enable hamburger menu to open drawer
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        if (navigationView == null) return;

        // Set up drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                clearFragments();
                setSelectedNavItem(navHome);
            } else if (itemId == R.id.nav_search) {
                showFragment(new SearchFragment(), "search");
                setSelectedNavItem(navSearch);
            } else if (itemId == R.id.nav_saved) {
                showFragment(new SavedFragment(), "saved");
                setSelectedNavItem(navSaved);
            } else if (itemId == R.id.nav_profile) {
                showFragment(new ProfileFragment(), "profile");
                setSelectedNavItem(navProfile);
            } else if (itemId == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (itemId == R.id.nav_help) {
                Intent intent = new Intent(this, HelpCenterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }

    private void setupBottomNavigation() {
        if (navHome == null || navSearch == null || navSaved == null || navProfile == null) {
            Log.e(TAG, "Custom navigation views not found in layout");
            return;
        }

        // Set initial state
        setSelectedNavItem(navHome);

        // Set click listeners
        navHome.setOnClickListener(v -> {
            setSelectedNavItem(navHome);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.dashboard_title);
            }
            clearFragments();
        });

        navSearch.setOnClickListener(v -> {
            setSelectedNavItem(navSearch);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.search_title);
            }
            showFragment(new SearchFragment(), "search");
        });

        navSaved.setOnClickListener(v -> {
            setSelectedNavItem(navSaved);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.saved_properties);
            }
            showFragment(new SavedFragment(), "saved");
        });

        navProfile.setOnClickListener(v -> {
            setSelectedNavItem(navProfile);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.profile_title);
            }
            showFragment(new ProfileFragment(), "profile");
        });
    }

    private void setSelectedNavItem(LinearLayout selectedItem) {
        // Reset all navigation items
        homeBackground.setVisibility(View.GONE);
        searchBackground.setVisibility(View.GONE);
        savedBackground.setVisibility(View.GONE);
        profileBackground.setVisibility(View.GONE);

        homeText.setTextColor(ContextCompat.getColor(this, R.color.colorNavInactive));
        searchText.setTextColor(ContextCompat.getColor(this, R.color.colorNavInactive));
        savedText.setTextColor(ContextCompat.getColor(this, R.color.colorNavInactive));
        profileText.setTextColor(ContextCompat.getColor(this, R.color.colorNavInactive));

        homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavInactive));
        searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavInactive));
        savedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavInactive));
        profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavInactive));

        // Set the selected item
        if (selectedItem == navHome) {
            homeBackground.setVisibility(View.VISIBLE);
            homeText.setTextColor(ContextCompat.getColor(this, R.color.colorNavActive));
            homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavActive));
        } else if (selectedItem == navSearch) {
            searchBackground.setVisibility(View.VISIBLE);
            searchText.setTextColor(ContextCompat.getColor(this, R.color.colorNavActive));
            searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavActive));
        } else if (selectedItem == navSaved) {
            savedBackground.setVisibility(View.VISIBLE);
            savedText.setTextColor(ContextCompat.getColor(this, R.color.colorNavActive));
            savedIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavActive));
        } else if (selectedItem == navProfile) {
            profileBackground.setVisibility(View.VISIBLE);
            profileText.setTextColor(ContextCompat.getColor(this, R.color.colorNavActive));
            profileIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorNavActive));
        }
    }

    private void setupServiceOptions() {
        // Service options
        LinearLayout buyOption = findViewById(R.id.buyOption);
        LinearLayout rentOption = findViewById(R.id.rentOption);
        LinearLayout sellOption = findViewById(R.id.sellOption);
        LinearLayout aiAdvisorOption = findViewById(R.id.aiAdvisorOption);

        // Set up click listeners for service options
        if (buyOption != null) {
            buyOption.setOnClickListener(v -> {
                Intent intent = new Intent(this, PropertyListActivity.class);
                intent.putExtra("MODE", "BUY");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (rentOption != null) {
            rentOption.setOnClickListener(v -> {
                Intent intent = new Intent(this, PropertyListActivity.class);
                intent.putExtra("MODE", "RENT");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (sellOption != null) {
            sellOption.setOnClickListener(v -> {
                Intent intent = new Intent(this, SellPropertyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (aiAdvisorOption != null) {
            aiAdvisorOption.setOnClickListener(v -> {
                Intent intent = new Intent(this, AIChatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
            });
        }
    }

    private void showFragment(Fragment fragment, String tag) {
        View dashboardContent = findViewById(R.id.dashboardContent);
        View fragmentContainer = findViewById(R.id.fragment_container);

        if (dashboardContent != null) {
            dashboardContent.setVisibility(View.GONE);
        }

        if (fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();

        currentFragment = fragment;
    }

    private void clearFragments() {
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commit();
            currentFragment = null;

            View dashboardContent = findViewById(R.id.dashboardContent);
            View fragmentContainer = findViewById(R.id.fragment_container);

            if (dashboardContent != null) {
                dashboardContent.setVisibility(View.VISIBLE);
            }
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.GONE);
            }
        }
    }

    private void displayWelcomeAnimation() {
        TextView welcomeText = findViewById(R.id.welcomeText);
        TextView userNameText = findViewById(R.id.userNameText);

        if (welcomeText != null) {
            welcomeText.setAlpha(0f);
            welcomeText.setTranslationY(50f);
            welcomeText.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(1000)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }

        if (userNameText != null) {
            userNameText.setAlpha(0f);
            userNameText.setTranslationY(50f);
            userNameText.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(300)
                    .setDuration(1000)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showRecommendedProperties() {
        LinearLayout mainContainer = findViewById(R.id.mainContainer);
        if (mainContainer == null) {
            Log.e(TAG, "Main container is null");
            return;
        }

        View loadingView = null;
        try {
            loadingView = getLayoutInflater().inflate(R.layout.loading_animation, mainContainer, false);
            mainContainer.addView(loadingView);
        } catch (Exception e) {
            Log.e(TAG, "Failed to inflate loading animation", e);
            return;
        }

        final View finalLoadingView = loadingView;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            recommendedProperties.clear();

            recommendedProperties.add(new Property(
                    "Modern Downtown Apartment",
                    "PKR 9,500,000",
                    "Islamabad, F-7 Markaz",
                    "https://images.unsplash.com/photo-1540518614846-7eded433c457?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            recommendedProperties.add(new Property(
                    "Luxury Villa with Pool",
                    "PKR 25,000,000",
                    "DHA Phase 5, Lahore",
                    "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            recommendedProperties.add(new Property(
                    "Cozy Family Home",
                    "PKR 7,000,000",
                    "Gulberg, Lahore",
                    "https://images.unsplash.com/photo-1576941089067-2de3c901e126?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            recommendedProperties.add(new Property(
                    "Bahria Town Penthouse",
                    "PKR 15,500,000",
                    "Bahria Town, Karachi",
                    "https://images.unsplash.com/photo-1493809842364-78817add7ffb?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
            ));

            if (propertyAdapter != null) {
                propertyAdapter.notifyDataSetChanged();
            }

            if (mainContainer != null && finalLoadingView != null && finalLoadingView.getParent() != null) {
                mainContainer.removeView(finalLoadingView);
            }

            RecyclerView propertyRecyclerView = findViewById(R.id.propertyRecyclerView);
            if (propertyRecyclerView != null) {
                propertyRecyclerView.setAlpha(0f);
                propertyRecyclerView.setTranslationY(100f);
                propertyRecyclerView.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(800)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();
            }
        }, 1500);
    }

    private void setupAIChat() {
        if (fabAIChat != null) {
            fabAIChat.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_click));
                Intent intent = new Intent(DashboardActivity.this, AIChatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return true;
    }
}