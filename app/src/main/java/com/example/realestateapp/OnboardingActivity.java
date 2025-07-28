package com.example.realestateapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardingActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView questionText;
    private LinearLayout optionsContainer;
    private Button nextButton;
    private Button backButton;
    private ProgressBar progressBar;
    private ImageView aiAssistantImage;
    private TextView progressText;
    private int currentQuestionIndex = 0;
    private Map<String, String> userPreferences = new HashMap<>();

    private List<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initializeViews();
        initializeQuestions();
        animateWelcomeScreen();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        questionText = findViewById(R.id.questionText);
        optionsContainer = findViewById(R.id.optionsContainer);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        aiAssistantImage = findViewById(R.id.aiAssistantImage);
        progressText = findViewById(R.id.progressText);

        nextButton.setOnClickListener(v -> moveToNextQuestion());
        backButton.setOnClickListener(v -> moveToPreviousQuestion());
    }

    private void initializeQuestions() {
        questions.add(new Question(
                "What type of property are you looking for?",
                Arrays.asList("Apartment", "House", "Villa", "Commercial")
        ));

        questions.add(new Question(
                "Which area do you prefer?",
                Arrays.asList("City Center", "Suburbs", "Countryside", "Coastal Area")
        ));

        questions.add(new Question(
                "What's your budget range?",
                Arrays.asList("Under $100k", "$100k - $300k", "$300k - $500k", "Above $500k")
        ));

        questions.add(new Question(
                "How urgent is your requirement?",
                Arrays.asList("Immediate", "Within 3 months", "Within 6 months", "Just exploring")
        ));

        questions.add(new Question(
                "Which amenities are important to you?",
                Arrays.asList("Parking", "Swimming Pool", "Gym", "Security")
        ));
    }

    private void animateWelcomeScreen() {
        // Animate welcome text
        welcomeText.setAlpha(0f);
        welcomeText.setTranslationY(50f);
        welcomeText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        new Handler().postDelayed(() -> startQuestions(), 2000);
                    }
                });
    }

    private void startQuestions() {
        welcomeText.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        welcomeText.setVisibility(View.GONE);
                        showQuestion(0);
                    }
                });
    }

    private void showQuestion(int index) {
        currentQuestionIndex = index;
        updateProgressBar();

        Question question = questions.get(index);
        questionText.setText(question.getText());

        // Animate question text
        questionText.setAlpha(0f);
        questionText.setTranslationY(30f);
        questionText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator());

        // Clear previous options
        optionsContainer.removeAllViews();

        // Add option cards with animation
        for (int i = 0; i < question.getOptions().size(); i++) {
            String option = question.getOptions().get(i);
            CardView optionCard = createOptionCard(option);
            optionCard.setAlpha(0f);
            optionCard.setTranslationX(-50f);
            optionsContainer.addView(optionCard);

            // Animate with delay for each card
            optionCard.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(500)
                    .setStartDelay(i * 100)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        // Update button states
        backButton.setVisibility(index > 0 ? View.VISIBLE : View.GONE);
        nextButton.setText(index == questions.size() - 1 ? "Finish" : "Next");
        nextButton.setEnabled(false);
    }

    private CardView createOptionCard(String option) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        cardView.setLayoutParams(params);
        cardView.setRadius(16);
        cardView.setCardElevation(8);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));

        TextView textView = new TextView(this);
        textView.setText(option);
        textView.setPadding(32, 24, 32, 24);
        textView.setTextSize(16);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        cardView.addView(textView);

        cardView.setOnClickListener(v -> selectOption(cardView, option));

        return cardView;
    }

    private void selectOption(CardView selectedCard, String option) {
        // Deselect all cards
        for (int i = 0; i < optionsContainer.getChildCount(); i++) {
            CardView card = (CardView) optionsContainer.getChildAt(i);
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }

        // Select this card
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        userPreferences.put(questions.get(currentQuestionIndex).getText(), option);
        nextButton.setEnabled(true);
    }

    private void moveToNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            showQuestion(currentQuestionIndex + 1);
        } else {
            finishOnboarding();
        }
    }

    private void moveToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            showQuestion(currentQuestionIndex - 1);
        }
    }

    private void updateProgressBar() {
        int progress = (int) (((float) (currentQuestionIndex + 1) / questions.size()) * 100);
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

        progressText.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
    }

    private void finishOnboarding() {
        // Show AI processing animation
        showProcessingAnimation();
    }

    private void showProcessingAnimation() {
        optionsContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        optionsContainer.setVisibility(View.GONE);
                        questionText.setText("AI is analyzing your preferences...");
                        nextButton.setVisibility(View.GONE);
                        backButton.setVisibility(View.GONE);

                        // Show circular progress
                        ProgressBar circularProgress = new ProgressBar(OnboardingActivity.this);
                        optionsContainer.removeAllViews();
                        optionsContainer.addView(circularProgress);
                        optionsContainer.setAlpha(1f);
                        optionsContainer.setVisibility(View.VISIBLE);

                        // Simulate processing time
                        new Handler().postDelayed(() -> showSuccessAnimation(), 3000);
                    }
                });
    }

    private void showSuccessAnimation() {
        setContentView(R.layout.success_message_layout);

        View successView = findViewById(R.id.successView);
        TextView successMessage = findViewById(R.id.successMessage);
        Button continueButton = findViewById(R.id.continueButton);

        successView.setScaleX(0f);
        successView.setScaleY(0f);
        successView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, DashboardActivity.class);
            intent.putExtra("preferences", (HashMap<String, String>) userPreferences);
            startActivity(intent);
            finish();
        });
    }

    private static class Question {
        private String text;
        private List<String> options;

        public Question(String text, List<String> options) {
            this.text = text;
            this.options = options;
        }

        public String getText() {
            return text;
        }

        public List<String> getOptions() {
            return options;
        }
    }
}
