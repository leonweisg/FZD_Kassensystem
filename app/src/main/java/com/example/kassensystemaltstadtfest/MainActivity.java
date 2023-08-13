package com.example.kassensystemaltstadtfest;

import static com.example.kassensystemaltstadtfest.Constants.ANZ_PRODUKTE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int[] clickCountArray = new int[ANZ_PRODUKTE];
    private float[] productPrices = new float[ANZ_PRODUKTE];
    private float[] pfandPrices = new float[ANZ_PRODUKTE];
    private float pfandRueckgabeValue1 = 0;
    private float pfandRueckgabeValue2 = 0;
    private float gesamt = 0;
    private int pfand1Counter = 0;
    private int pfand2Counter = 0;
    private LinearLayout linearLayoutForProducts;
    private TextView[] productTextViews = new TextView[ANZ_PRODUKTE];
    private TextView txtGesamt, txtPfand;
    private Button pfand1, pfand2;
    private ImageButton reloadButton;

    private String[] defaultArray = new String[ANZ_PRODUKTE];
    private float[] defaultProductPrices = new float[ANZ_PRODUKTE];
    private float[] defaultPfandPrices = new float[ANZ_PRODUKTE];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtGesamt = findViewById(R.id.txt_gesamt);
        firstLoadDefault();
        setStatusBarColor();
        initSettingsButton();
        loadFromSharedPreferences();
        linearLayoutForProducts = findViewById(R.id.linearLayoutForProducts);

        // Lade die Preise für die Produkte aus den SharedPreferences
        loadProductPrices();

        // Füge einen Click-Listener zu jedem Button hinzu
        for (int i = 1; i <= ANZ_PRODUKTE; i++) {
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
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Bestellung abbrechen")
                    .setMessage("Sind Sie sicher, dass Sie die Bestellung abbrechen möchten?")
                    .setPositiveButton("Ja", (dialog, which) -> reloadEverything())
                    .setNegativeButton("Nein", null)
                    .show();
        });
        Button bezahlen = findViewById(R.id.btn_bezahlen);
        bezahlen.setOnClickListener(v -> {
            if (gesamt == 0) {
                //Toast.makeText(this, "Noch kein Produkt hinzugefügt.", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = getLayoutInflater();
                View toastLayout = inflater.inflate(R.layout.custom_toast_layout, null);

                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(toastLayout);
                toast.show();


            } else {
                Intent intent = new Intent(this, BezahlenActivity.class);
                intent.putExtra("gesamt", gesamt);
                startActivity(intent);
            }
        });
    }

    private void firstLoadDefault() {
        SharedPreferences loadingPreferences = getSharedPreferences("initApp", MODE_PRIVATE);
        boolean initialized = loadingPreferences.getBoolean("initialized", false);
        SharedPreferences sharedPreferences = getSharedPreferences("product_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!initialized) {
            defaultArray[0] = "Bier";
            defaultProductPrices[0] = 3.5f;
            defaultPfandPrices[0] = 1f;
            defaultArray[1] = "Weizen";
            defaultProductPrices[1] = 5f;
            defaultPfandPrices[1] = 2f;
            defaultArray[2] = "Hugo";
            defaultProductPrices[2] = 3.5f;
            defaultPfandPrices[2] = 2f;
            defaultArray[3] = "Wein";
            defaultProductPrices[3] = 4f;
            defaultPfandPrices[3] = 2f;
            defaultArray[4] = "Alk. Mixgetr.";
            defaultProductPrices[4] = 5f;
            defaultPfandPrices[4] = 1f;
            defaultArray[5] = "Williams Birne";
            defaultProductPrices[5] = 2f;
            defaultPfandPrices[5] = 0f;
            defaultArray[6] = "Guido/Hubertus/Heydt";
            defaultProductPrices[6] = 2f;
            defaultPfandPrices[6] = 0f;
            defaultArray[7] = "Kurze";
            defaultProductPrices[7] = 1.5f;
            defaultPfandPrices[7] = 0f;
            defaultArray[8] = "Cola, Wasser";
            defaultProductPrices[8] = 2.5f;
            defaultPfandPrices[8] = 1f;
            defaultArray[9] = "Jägermeister Kurze";
            defaultProductPrices[9] = 2f;
            defaultPfandPrices[9] = 0f;

            for (int i = 0; i < ANZ_PRODUKTE; i++) {
                String productNameKey = "product_name_" + (i + 1);
                String productPriceKey = "product_price_" + (i + 1);
                String productPfandKey = "product_pfand_" + (i + 1);
                editor.putString(productNameKey, defaultArray[i]);
                editor.putFloat(productPriceKey, defaultProductPrices[i]);
                editor.putFloat(productPfandKey, defaultPfandPrices[i]);
            }

            editor.apply();

            SharedPreferences.Editor editorLoading = loadingPreferences.edit();
            editorLoading.putBoolean("initialized", true);
            editorLoading.apply();
        }

    }

    private void reloadEverything() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void pfandRueckgabe1() {
        pfandRueckgabeValue1--;
        pfand1Counter++;
        String eintrag = pfand1Counter + "x 1,00 €";
        pfand1.setText(eintrag);
        gesamt -= 1;
        reloadGesamt();
    }

    private void pfandRueckgabe2() {
        pfandRueckgabeValue2 -= 2;
        pfand2Counter++;
        String eintrag = pfand2Counter + "x 2,00 €";
        pfand2.setText(eintrag);
        gesamt -= 2;
        reloadGesamt();
    }

    private void loadProductPrices() {
        SharedPreferences sharedPreferences = getSharedPreferences("product_data", MODE_PRIVATE);
        for (int i = 0; i < ANZ_PRODUKTE; i++) {
            String productPriceKey = "product_price_" + (i + 1);
            productPrices[i] = sharedPreferences.getFloat(productPriceKey, 0.0f);
            String productPfandKey = "product_pfand_" + (i + 1);
            pfandPrices[i] = sharedPreferences.getFloat(productPfandKey, 0.0f);
        }
    }

    private void onButtonClicked(String productName, int buttonIndex) {
        clickCountArray[buttonIndex]++; // Erhöhe den Klickzähler für den entsprechenden Button
        int clickCount = clickCountArray[buttonIndex];


        if (productTextViews[buttonIndex] != null) {
            // Wenn bereits ein TextView für den Button vorhanden ist, aktualisiere nur den Text
            Float totalPrice = clickCount * productPrices[buttonIndex];
            String productEntry = clickCount + "x " + productName + " " + totalPrice + "0 €";
            productTextViews[buttonIndex].setText(productEntry);
        } else {
            // Wenn noch kein TextView für den Button vorhanden ist, füge ein neues hinzu
            Float totalPrice = clickCount * productPrices[buttonIndex];
            String productEntry = clickCount + "x " + productName + " " + totalPrice + "0 €";
            TextView entryTextView = new TextView(this);
            entryTextView.setTextColor(getColor(R.color.black));
            entryTextView.setTextSize(18);
            entryTextView.setText(productEntry);
            linearLayoutForProducts.addView(entryTextView);
            productTextViews[buttonIndex] = entryTextView;
        }
        addPrice(productPrices[buttonIndex], pfandPrices[buttonIndex]);
    }

    private void addPrice(float totalPrice, float pfand) {
        gesamt += totalPrice;
        gesamt += pfand;
        reloadGesamt();
    }

    private void reloadGesamt() {
        String eintrag = gesamt + "0 €";
        txtGesamt.setText(eintrag);
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

        for (int i = 1; i <= ANZ_PRODUKTE; i++) {
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