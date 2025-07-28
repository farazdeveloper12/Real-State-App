package com.example.realestateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private MaterialToolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView profileImage;
    private TextView nameText, emailText, memberSinceText, userRoleText;
    private TextView savedPropertiesCount, searchesCount, viewsCount;
    private CardView documentsCard, helpCard;
    private LinearLayout savedStatsContainer, searchesStatsContainer, viewsStatsContainer;
    private MaterialButton editProfileButton;
    private ImageView settingsButton;
    private View rootView;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupToolbar();
        loadUserData();
        setupClickListeners();

        return rootView;
    }

    private void initializeViews() {
        toolbar = rootView.findViewById(R.id.toolbar);
        collapsingToolbar = rootView.findViewById(R.id.collapsingToolbar);
        profileImage = rootView.findViewById(R.id.profileImage);
        nameText = rootView.findViewById(R.id.userName);
        emailText = rootView.findViewById(R.id.userEmail);
        memberSinceText = rootView.findViewById(R.id.memberSince);
        userRoleText = rootView.findViewById(R.id.userRole);

        savedPropertiesCount = rootView.findViewById(R.id.savedCount);
        searchesCount = rootView.findViewById(R.id.searchesCount);
        viewsCount = rootView.findViewById(R.id.viewsCount);

        savedStatsContainer = rootView.findViewById(R.id.savedContainer);
        searchesStatsContainer = rootView.findViewById(R.id.searchesContainer);
        viewsStatsContainer = rootView.findViewById(R.id.viewsContainer);

        documentsCard = rootView.findViewById(R.id.documentsCard);
        helpCard = rootView.findViewById(R.id.helpCard);

        editProfileButton = rootView.findViewById(R.id.editProfileButton);
        settingsButton = rootView.findViewById(R.id.settingsButton);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");

            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle("My Profile");
                collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent, null));
                collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white, null));
            }

            toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (nameText != null) {
                String displayName = user.getDisplayName();
                nameText.setText(displayName != null ? displayName : "User");
            }

            if (emailText != null) {
                emailText.setText(user.getEmail());
            }

            if (memberSinceText != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String memberSince = "Member since: " + (user.getMetadata() != null && user.getMetadata().getCreationTimestamp() > 0 ?
                        sdf.format(new Date(user.getMetadata().getCreationTimestamp())) : "January 2024");
                memberSinceText.setText(memberSince);
            }

            if (profileImage != null) {
                if (user.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_person)
                            .circleCrop()
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.ic_person);
                }
            }

            fetchUserStatistics();
        }
    }

    private void fetchUserStatistics() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (savedPropertiesCount != null) savedPropertiesCount.setText("12");
            if (searchesCount != null) searchesCount.setText("25");
            if (viewsCount != null) viewsCount.setText("48");
        }
    }

    private void setupClickListeners() {
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> navigateToActivity(EditProfileActivity.class));
        }

        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> navigateToActivity(SettingsActivity.class));
        }

        setupStatsClick(savedStatsContainer, PropertyListActivity.class);
        setupStatsClick(searchesStatsContainer, SearchActivity.class);
        setupStatsClick(viewsStatsContainer, PropertyListActivity.class);

        setupCardClick(documentsCard, DocumentsActivity.class);

        if (helpCard != null) {
            helpCard.setOnClickListener(v -> showHelpDialog());
        }
    }

    private void showHelpDialog() {
        if (getContext() != null) {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Help & Support")
                    .setMessage("This is the Real Estate App help center. " +
                            "For assistance, please contact support@realestateapp.com or " +
                            "call our helpline at +92-123-4567890.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void setupStatsClick(View container, Class<?> activityClass) {
        if (container != null) {
            container.setOnClickListener(v -> navigateToActivity(activityClass));
        }
    }

    private void setupCardClick(CardView card, Class<?> activityClass) {
        if (card != null) {
            card.setOnClickListener(v -> navigateToActivity(activityClass));
        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), activityClass);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }
}
