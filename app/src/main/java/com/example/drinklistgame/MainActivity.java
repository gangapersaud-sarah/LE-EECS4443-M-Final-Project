package com.example.drinklistgame;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int BASE_ID = 1;
    private ArrayList<ImageInfo> leftImages = new ArrayList<>();
    private ArrayList<ImageInfo> rightImages = new ArrayList<>();
    private List<Long> trialTimes = new ArrayList<>();

    private int selectedLeftImageId = -1;
    private int selectedRightImageId = -1;
    private TextView tv_TrueResult, tv_FalseResult, trialCompletion, tv_ErrorRate;
    private int trueCount = 0;
    private int falseCount = 0;
    private long startTime = 0;
    private boolean timerRunning = false;

    private int[] rightImageIds;

    int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView refresh = findViewById(R.id.refresh);
        tv_TrueResult = findViewById(R.id.tv_TrueResult);
        tv_FalseResult = findViewById(R.id.tv_FalseResult);
        trialCompletion = findViewById(R.id.tv_TrialCompletion);
        tv_ErrorRate = findViewById(R.id.tv_ErrorRate);

        Random random = new Random();
        randomNumber = random.nextInt(12);

        int[] leftImageIds = {R.drawable.number1, R.drawable.number2, R.drawable.number3, R.drawable.number4};
        getDrinkList(randomNumber);
        initializeImages(leftImages, leftImageIds);
        initializeImages(rightImages, rightImageIds);

        shuffleImages(leftImages);
        shuffleImages(rightImages);

        displayImages();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomNumber = random.nextInt(12);
                getDrinkList(randomNumber);

                rightImages.clear();
                for (int i = 0; i < rightImageIds.length; i++) {
                    rightImages.add(new ImageInfo(BASE_ID + i, rightImageIds[i]));
                }

                trueCount = 0;
                falseCount = 0;
                trialTimes.clear();
                trialCompletion.setText("");
                tv_ErrorRate.setText("");
                tv_TrueResult.setText(String.valueOf(0));
                tv_FalseResult.setText(String.valueOf(0));
                shuffleImages(leftImages);
                shuffleImages(rightImages);
                displayImages();
            }
        });
    }

    private void initializeImages(ArrayList<ImageInfo> imagesList, int[] imageIds) {
        imagesList.clear();
        for (int i = 0; i < imageIds.length; i++) {
            imagesList.add(new ImageInfo(BASE_ID + i, imageIds[i]));
        }
    }

    private void shuffleImages(ArrayList<ImageInfo> imagesList) {
        Collections.shuffle(imagesList);
    }

    private void displayImages() {
        LinearLayout leftContainer = findViewById(R.id.leftContainer);
        addImagesToContainer(leftContainer, leftImages, true);

        LinearLayout rightContainer = findViewById(R.id.rightContainer);
        addImagesToContainer(rightContainer, rightImages, false);
    }

    private void addImagesToContainer(LinearLayout container, ArrayList<ImageInfo> imagesList, boolean isLeft) {
        container.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPx(100),
                dpToPx(100)
        );
        layoutParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

        for (ImageInfo imageInfo : imagesList) {
            CardView cardView = new CardView(this);
            cardView.setLayoutParams(layoutParams);
            cardView.setRadius(dpToPx(4));
            cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

            ImageView imageView = new ImageView(this);
            imageView.setId(imageInfo.getImageId());
            imageView.setImageResource(imageInfo.getImageResource());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isLeft) {
                        selectedLeftImageId = v.getId();
                    } else {
                        selectedRightImageId = v.getId();
                    }
                    checkSelectedImages();
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!timerRunning) {
                        startTime = System.currentTimeMillis();
                        timerRunning = true;
                    }
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(null, shadowBuilder, v, 0);
                    return true;
                }
            });

            imageView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DROP:
                            View draggedView = (View) event.getLocalState();
                            int droppedImageId = draggedView.getId();
                            selectedLeftImageId = droppedImageId;
                            selectedRightImageId = imageInfo.getImageId();
                            checkSelectedImages();

                            if (timerRunning) {
                                long endTime = System.currentTimeMillis();
                                trialTimes.add(endTime - startTime);
                                timerRunning = false;
                            }

                            TrialResult result = calculateTrialResult();
                            TrialSummary summary = generateSummary(result);

                            trialCompletion.setText("Trial completed!\nCompletion Time: " + summary.getCompletionTimeSec() + " seconds");
                            tv_ErrorRate.setText("Error Rate: " + summary.getErrorRate() + "%");
                            break;
                    }
                    return true;
                }
            });

            cardView.addView(imageView);
            container.addView(cardView);
        }
    }

    private TrialResult calculateTrialResult() {
        long completionTimeMs = 0;
        for (Long time : trialTimes) {
            completionTimeMs += time;
        }
        return new TrialResult(completionTimeMs, falseCount);
    }

    private TrialSummary generateSummary(TrialResult result) {
        double completionTimeSec = result.getCompletionTimeMs() / 1000.0;
        double errorRate = (double) result.getErrorCount() / trialTimes.size() * 100;
        return new TrialSummary(completionTimeSec, errorRate);
    }
    private void checkSelectedImages() {
        if (selectedLeftImageId != -1 && selectedRightImageId != -1) {
            if (selectedLeftImageId == selectedRightImageId) {
                trueCount++;
                tv_TrueResult.setText(String.valueOf(trueCount));
            } else {
                falseCount++;
                tv_FalseResult.setText(String.valueOf(falseCount));
            }

            selectedLeftImageId = -1;
            selectedRightImageId = -1;
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void getDrinkList(int num) {
        switch (num) {
            case 0:
                rightImageIds = new int[]{R.drawable.cm1, R.drawable.cm2, R.drawable.cm3, R.drawable.cm4};
                break;
            case 1:
                rightImageIds = new int[]{R.drawable.ws1, R.drawable.ws2, R.drawable.ws3, R.drawable.ws4};
                break;
            case 2:
                rightImageIds = new int[]{R.drawable.rg1, R.drawable.rg2, R.drawable.rg3, R.drawable.rg4};
                break;
            case 3:
                rightImageIds = new int[]{R.drawable.mm1, R.drawable.mm2, R.drawable.mm3, R.drawable.mm4};
                break;
            case 4:
                rightImageIds = new int[]{R.drawable.ds1, R.drawable.ds2, R.drawable.ds3, R.drawable.ds4};
                break;
            case 5:
                rightImageIds = new int[]{R.drawable.cm1, R.drawable.cm2, R.drawable.cm3, R.drawable.cm4};
                break;
            case 6:
                rightImageIds = new int[]{R.drawable.wg1, R.drawable.wg2, R.drawable.wg3, R.drawable.wg4};
                break;
            case 7:
                rightImageIds = new int[]{R.drawable.rb1, R.drawable.rb2, R.drawable.rb3, R.drawable.rb4};
                break;
            case 8:
                rightImageIds = new int[]{R.drawable.ts1, R.drawable.ts2, R.drawable.ts3, R.drawable.ts4};
                break;
            case 9:
                rightImageIds = new int[]{R.drawable.rs1, R.drawable.rs2, R.drawable.rs3, R.drawable.rs4};
                break;
            case 10:
                rightImageIds = new int[]{R.drawable.tc1, R.drawable.tc2, R.drawable.tc3, R.drawable.tc4};
                break;
            case 11:
                rightImageIds = new int[]{R.drawable.wc1, R.drawable.wc2, R.drawable.wc3, R.drawable.wc4};
                break;
            default:
                rightImageIds = new int[]{R.drawable.drink1, R.drawable.drink2, R.drawable.drink3, R.drawable.drink4};
                break;
        }
    }
}
