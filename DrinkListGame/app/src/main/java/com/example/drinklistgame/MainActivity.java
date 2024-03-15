package com.example.drinklistgame;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends AppCompatActivity {

    private int[] imageResources1 = {
            R.drawable.number1,
            R.drawable.number2,
            R.drawable.number3,
            R.drawable.number4,
            R.drawable.number5
    };

    private int[] imageResources2 = {
            R.drawable.drink1,
            R.drawable.drink2,
            R.drawable.drink3,
            R.drawable.drink4,
            R.drawable.drink5
    };

    private DrawView drawView;
    private TextView scoreTextView;

    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.textView);

        loadImagesIntoContainer(R.id.leftContainer, imageResources1);
        loadImagesIntoContainer(R.id.rightContainer, imageResources2);

        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        drawView = new DrawView(this);
        constraintLayout.addView(drawView);
    }

    private void loadImagesIntoContainer(int containerId, int[] imageResources) {
        LinearLayout container = findViewById(containerId);
        for (int i = 0; i < 5; i++) {
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.card_margin_top),
                    0, getResources().getDimensionPixelSize(R.dimen.card_margin_bottom));
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(getResources().getDimension(R.dimen.corner_radius));

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResources[i]);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.image_width),
                    getResources().getDimensionPixelSize(R.dimen.image_height)
            );
            imageView.setLayoutParams(layoutParams);

            final int index = i;

            cardView.addView(imageView);
            container.addView(cardView);
        }
    }
}
