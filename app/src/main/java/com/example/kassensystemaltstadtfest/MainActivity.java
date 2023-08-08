package com.example.kassensystemaltstadtfest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int[] clickCountArray = new int[14];
    private float[] productPrices = new float[14];
    private float pfandRueckgabeValue1 = 0;
    private float pfandRueckgabeValue2 = 0;
    private int pfand1Counter = 0;
    private int pfand2Counter = 0;
    private LinearLayout linearLayoutForProducts;
    private TextView[] productTextViews = new TextView[14];
    private TextView txtGesamt, txtPfand;
    private Button pfand1, pfand2;
    private ImageButton reloadButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtGesamt = findViewById(R.id.txt_gesamt);
        txtPfand = findViewById(R.id.txt_pfand);
        setStatusBarColor();
        initSettingsButton();
        loadFromSharedPreferences();
        linearLayoutForProducts = findViewById(R.id.linearLayoutForProducts);

        // Lade die Preise für die Produkte aus den SharedPreferences
        loadProductPrices();

        // Füge einen Click-Listener zu jedem Button hinzu
        for (int i = 1; i <= 14; i++) {
            String buttonId = "btn_product_" + i;
            int resourceId = getResources().getIdentifier(buttonId, "id", getPackageName());
            Button button = findViewById(resourceId);
            final int buttonIndex = i - 1; // Das Array beginnt bei Index 0
            button.setOnClickListener(v -> onButtonClicked(button.getText().toString(), buttonIndex));
        }
        pfand1 = findViewById(R.id.btn_pfand_rueckgabe_1);
        pfand1.setText("1,00 €");
        pfand1.setOnClickListener(v -> {
            pfandRueckgabe1();
        });
        pfand2 = findViewById(R.id.btn_pfand_rueckgabe_2);
        pfand2.setText("2,00 €");
        pfand2.setOnClickListener(v -> {
            pfandRueckgabe2();
        });
        reloadButton = findViewById(R.id.btn_reload);
        reloadButton.setOnClickListener(v -> {
            reloadEverything();
        });
    }

    private void reloadEverything() {
        pfand1.setText("1,00 €");
        pfand2.setText("2,00 €");
        pfand1Counter = 0;
        pfand2Counter = 0;
        pfandRueckgabeValue1 = 0;
        pfandRueckgabeValue2 = 0;
        linearLayoutForProducts.removeAllViews();
        for (int i = 1; i <= 14; i++) {
            clickCountArray[i] = 0;
        }
    }

    private void pfandRueckgabe1() {
        pfandRueckgabeValue1--;
        pfand1Counter++;
        String eintrag = pfand1Counter + "x 1,00 €";
        pfand1.setText(eintrag);
        reloadGesamtPfand();
    }

    private void reloadGesamtPfand() {
        String pfand = String.valueOf(pfandRueckgabeValue1 + pfandRueckgabeValue2);
        String eintrag = pfand + "0 €";
        txtPfand.setText(eintrag);
    }

    private void pfandRueckgabe2() {
        pfandRueckgabeValue2 -= 2;
        pfand2Counter++;
        String eintrag = pfand1Counter + "x 2,00 €";
        pfand2.setText(eintrag);
        reloadGesamtPfand();
    }

    private void loadProductPrices() {
        SharedPreferences sharedPreferences = getSharedPreferences("product_data", MODE_PRIVATE);
        for (int i = 0; i < 14; i++) {
            String productPriceKey = "product_price_" + (i + 1);
            productPrices[i] = sharedPreferences.getFloat(productPriceKey, 0.0f);
        }
    }

    private void onButtonClicked(String productName, int buttonIndex) {
        clickCountArray[buttonIndex]++; // Erhöhe den Klickzähler für den entsprechenden Button
        int clickCount = clickCountArray[buttonIndex];

        if (productTextViews[buttonIndex] != null) {
            // Wenn bereits ein TextView für den Button vorhanden ist, aktualisiere nur den Text
            float totalPrice = clickCount * productPrices[buttonIndex];
            String productEntry = clickCount + "x " + productName + " " + totalPrice + " €";
            productTextViews[buttonIndex].setText(productEntry);
        } else {
            // Wenn noch kein TextView für den Button vorhanden ist, füge ein neues hinzu
            float totalPrice = clickCount * productPrices[buttonIndex];
            String productEntry = clickCount + "x " + productName + " " + totalPrice + " €";
            TextView entryTextView = new TextView(this);
            entryTextView.setTextColor(getColor(R.color.black));
            entryTextView.setTextSize(18);
            entryTextView.setText(productEntry);
            linearLayoutForProducts.addView(entryTextView);
            productTextViews[buttonIndex] = entryTextView;
        }
    }


    private void initSettingsButton() {
        ImageButton imageButton = findViewById(R.id.btn_settings_help);
        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("product_data", MODE_PRIVATE);

        for (int i = 1; i <= 14; i++) {
            String productName = sharedPreferences.getString("product_name_" + i, "");
            int buttonId = getResources().getIdentifier("btn_product_" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setText(productName);
        }
    }


    private void setStatusBarColor() {
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }
}