package com.example.drinklistgame;


import static java.security.AccessController.getContext;

import android.content.ContentValues;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int BASE_ID = 1;
    private ArrayList<ImageInfo> leftImages = new ArrayList<>();
    private ArrayList<ImageInfo> rightImages = new ArrayList<>();
    private List<Long> trialTimes = new ArrayList<>();

    private List<String[]> trialList;

    private int selectedLeftImageId = -1;
    private int selectedRightImageId = -1;
    private TextView tv_TrueResult, tv_FalseResult, trialCompletion, tv_ErrorRate, tv1to1,tv1toM,tvMto1,tvMtoM;
    private int trueCount = 0;
    private int falseCount = 0;
    private long startTime = 0;
    private boolean timerRunning = false;

    private int[] rightImageIds;

    int randomNumber;
    int rightImageNum;
    int leftImageNum;
    int[] one2MSelected = {0,0,0,0};
    int[] cardinalityCount = {0,0,0,0};
    int cardType;
    boolean isOneToMany;
    int trialNumber =1;

     String outputUri;

    Intent result;
    String trial_type;
    Intent intent;
    String trialLevel;
    //change accordingly
    //trials per cardinality (each cardinality will have this many trials)
    int trialsPerCardinality = 4;

    //number of trials (make sure it is divisible by trialsPerCardinality exactly)
    // i.e. numberOfTrials/trialsPerCardinality == 4 always
    int numberOfTrials = 16;


    private void randomAndDisplayImg(){
        Random random = new Random();
        randomNumber = random.nextInt(12);
        int[] leftImageIds = {R.drawable.number1, R.drawable.number2, R.drawable.number3, R.drawable.number4};
        getDrinkList(randomNumber);
        initializeImages(leftImages, leftImageIds);
        initializeImages(rightImages, rightImageIds);

        //shuffleImages(leftImages);
        //shuffleImages(rightImages);
        //changes
        rightImageNum = leftImageIds.length;


        double swCon = Math.random();
        randomizeTrial(swCon);

    }
    private void randomizeTrial(double swCon){
        int m2m = (int)(Math.random()*4+1);
        while   (true) {
            if (swCon <= 0.25 && cardinalityCount[0] < trialsPerCardinality) {
                trialLevel = "m2m";
                displaySelectedImages(m2m, m2m);
                cardinalityCount[0] += 1;
                cardType = 0;
                break;
            } else if (swCon <= 0.5 && cardinalityCount[1] < trialsPerCardinality) {
                trialLevel = "12m";
                display12M(randomIntFromInterval(0, 3), randomIntFromInterval(2, 4));
                cardinalityCount[1] += 1;
                cardType = 1;
                break;
            } else if (swCon <= 0.75 && cardinalityCount[2] < trialsPerCardinality) {
                trialLevel = "m21";
                displayM21(randomIntFromInterval(0, 3), randomIntFromInterval(2, 4));
                cardinalityCount[2] += 1;
                cardType = 2;
                break;
            } else if (swCon <= 1 && cardinalityCount[3] < trialsPerCardinality) {
                trialLevel = "121";
                displaySelectedImages(1, 1);
                cardinalityCount[3] += 1;
                cardType = 3;
                break;
            }
            swCon = Math.random();
        }
    }
    private void initiateResultsActivity() {
        //initiate results activity
        result.putExtra("interactionType", trial_type);
        this.startActivity(result);
        //parentActivity.finishActivity(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        trialList =new ArrayList<>();
        cardType = 0;
        if (extras != null) {
            outputUri = extras.getString("uri");
            //The key argument here must match that used in the other activity
        }

        //wip comment out
        //this.outputUri = savedInstanceState.getString("csv_uri");
        ImageView refresh = findViewById(R.id.refresh);
        intent = getIntent();
        tv_TrueResult = findViewById(R.id.tv_TrueResult);
        tv_FalseResult = findViewById(R.id.tv_FalseResult);
        trialCompletion = findViewById(R.id.tv_TrialCompletion);
        tv_ErrorRate = findViewById(R.id.tv_ErrorRate);
        trial_type =  intent.getStringExtra("interactionType");
        isOneToMany = false;
        result = new Intent(MainActivity.this, ResultActivity.class);
        randomAndDisplayImg();
        //old
        //displayImages();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }
    private void refresh(){
        Random random = new Random();
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
        //shuffleImages(leftImages);
        //shuffleImages(rightImages);

        randomAndDisplayImg();
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
    private void displaySelectedImages(int leftIndex, int rightIndex){
        LinearLayout leftContainer = findViewById(R.id.leftContainer);
        ArrayList<ImageInfo> arrlistofOptions1 = new ArrayList<ImageInfo>(leftImages.subList(0,leftIndex));
        shuffleImages(arrlistofOptions1);
        addImagesToContainer(leftContainer, arrlistofOptions1, true);

        LinearLayout rightContainer = findViewById(R.id.rightContainer);
        ArrayList<ImageInfo> arrlistofOptions2 = new ArrayList<ImageInfo>(rightImages.subList(0,rightIndex));
        shuffleImages(arrlistofOptions2);
        addImagesToContainer(rightContainer, arrlistofOptions2, false);
        rightImageNum = arrlistofOptions2.size();
        leftImageNum = arrlistofOptions1.size();
    }
    private void display12M(int leftElementIndex, int occurrences){
        LinearLayout leftContainer = findViewById(R.id.leftContainer);
        LinearLayout rightContainer = findViewById(R.id.rightContainer);
        //initialize 1 side (right)
        ArrayList<ImageInfo> arrlistofOptions1 = new ArrayList<ImageInfo>();
        ArrayList<ImageInfo> arrlistofOptions2 = new ArrayList<ImageInfo>();
        arrlistofOptions1.add(leftImages.get(leftElementIndex));
        //add occurrences number of images to left
        for (int k = occurrences; k>0; k--){
            getDrinkList(randomIntFromInterval(0,11));
            //initializeImages(rightImages, rightImageIds);
            arrlistofOptions2.add(new ImageInfo(BASE_ID + k, rightImageIds[leftElementIndex]));
        }
        shuffleImages(arrlistofOptions2);
        addImagesToContainer(leftContainer, arrlistofOptions1, true);
        addImagesToContainer(rightContainer, arrlistofOptions2, false);
        leftImageNum = arrlistofOptions1.size();
        rightImageNum = arrlistofOptions2.size();
        isOneToMany = true;
    }
    private void displayM21(int leftElementIndex, int occurrences){
        LinearLayout leftContainer = findViewById(R.id.leftContainer);
        LinearLayout rightContainer = findViewById(R.id.rightContainer);
        //initialize 1 side (right)
        ArrayList<ImageInfo> arrlistofOptions1 = new ArrayList<ImageInfo>();
        ArrayList<ImageInfo> arrlistofOptions2 = new ArrayList<ImageInfo>();
        arrlistofOptions2.add(rightImages.get(leftElementIndex));
        //add occurrences number of images to left
        for (int k = occurrences; k>0; k--){
            arrlistofOptions1.add(leftImages.get(leftElementIndex));
        }
        shuffleImages(arrlistofOptions2);
        addImagesToContainer(leftContainer, arrlistofOptions1, true);
        addImagesToContainer(rightContainer, arrlistofOptions2, false);

        rightImageNum = arrlistofOptions1.size();
        leftImageNum = arrlistofOptions2.size();
    }
    private int randomIntFromInterval(int min, int max) { // min and max included
        return (int)(Math.floor(Math.random() * (max - min + 1) + min));
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
                        if(timerRunning) {
                            selectedLeftImageId = v.getId();
                            long endTime = System.currentTimeMillis();
                            trialTimes.add(endTime - startTime);
                            timerRunning = false;
                        } else{
                            selectedLeftImageId = v.getId();
                            startTime = System.currentTimeMillis();
                            timerRunning = true;
                        }

                    } else {
                            if (timerRunning){
                                selectedRightImageId = v.getId();
                                long endTime = System.currentTimeMillis();
                                trialTimes.add(endTime - startTime);
                                timerRunning = false;
                            }
                            else{
                                selectedRightImageId = v.getId();
                                timerRunning = true;
                            }

                    }
                    //trialCount, solves all the errors but it should be trueCount
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
                            Log.i("event", "onDrag pressed");

                            if (timerRunning) {
                                long endTime = System.currentTimeMillis();
                                trialTimes.add(endTime - startTime);
                                timerRunning = false;
                            }
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

    private TrialSummary generateSummary(TrialResult result,String type) {
        double completionTimeSec = result.getCompletionTimeMs() / 1000.0;
        double errorRate = (double) result.getErrorCount() / trialTimes.size() * 100;

        String[] compTime = new String[4];
        compTime[0] = String.valueOf(completionTimeSec);
        compTime[1] = String.valueOf(errorRate);
        compTime[2] = String.valueOf(cardType);
        compTime[3] = String.valueOf(trialNumber);
        trialList.add(compTime);
        return new TrialSummary(completionTimeSec, errorRate,type);
    }
    private void sendToCsv(int con){
        if(con == 1) {
            OutputStream outputStream;
            try {
                InputStream input = getContentResolver().openInputStream(Uri.parse(outputUri));
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                outputStream = getContentResolver().openOutputStream(Uri.parse(outputUri));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                for(String[] k:trialList) {
                    while(br.ready()) {
                        bw.append(br.readLine()).append("\n");
                    }
                    bw.append(k[0]).append(",");
                    bw.append(k[1]).append(",");
                    bw.append(k[2]).append(",");
                    bw.append(k[3]).append(",\n");
                }
                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean checkMany(int count){
        int sum = 0;
        for(int i = 0; i<one2MSelected.length; i++){
            if(one2MSelected[i] == 1)
                sum++;
        }
        if(sum == count){
            one2MSelected = new int[]{0, 0, 0, 0};
            isOneToMany = false;
            sendResults();
            return true;
        }
        return false;
    }
    private void checkSelectedImages() {
        String resultPoplation;
        if(isOneToMany){
            if(selectedRightImageId != -1) {
                one2MSelected[selectedRightImageId-2] = 1;
                trueCount++;
                tv_TrueResult.setText(String.valueOf(trueCount));
                if(checkMany(rightImageNum)){
                    if(trialNumber == numberOfTrials){
                        sendToCsv(1);
                        initiateResultsActivity();
                        return;
                    }
                    trialNumber++;
                    refresh();
                }
            }
            selectedLeftImageId = -1;
            selectedRightImageId = -1;
            return;
        }

        if (selectedLeftImageId != -1 && selectedRightImageId != -1) {
            if (selectedLeftImageId == selectedRightImageId) {
                trueCount++;
                tv_TrueResult.setText(String.valueOf(trueCount));
            } else {
                falseCount++;
                tv_FalseResult.setText(String.valueOf(falseCount));
            }
            if(rightImageNum <= leftImageNum){
                if(trueCount == leftImageNum){
                    sendResults();
                }
            }
            else {
                if(trueCount == rightImageNum) {
                    sendResults();
                }
            }
            Log.i("checkimages","trueCount: " + trueCount + ", rightnum = " + rightImageNum + ", trialLevel = " + trialLevel);
            if(trueCount >= rightImageNum && trueCount >= leftImageNum){
                //old way to reload, performance testing method
//                Intent intent = getIntent();
//                finish();
//                startActivity(intent);
                if(trialNumber == numberOfTrials){
                    sendToCsv(1);
                    initiateResultsActivity();
                    return;
                }
                trialNumber++;
                refresh();
            }

            selectedLeftImageId = -1;
            selectedRightImageId = -1;
        }
    }

    private void sendResults(){
        String resultPoplation;
        TrialResult resultPop = calculateTrialResult();
        TrialSummary summary = generateSummary(resultPop,trial_type);
        double completionTimeaverage = summary.getCompletionTimeSec() / trialTimes.size();
        resultPoplation = "Average - " + summary.getType() + ": " + completionTimeaverage + " seconds," + " Error Rate:" + summary.getErrorRate();
        result.putExtra(trialLevel.toString(), resultPoplation);
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
