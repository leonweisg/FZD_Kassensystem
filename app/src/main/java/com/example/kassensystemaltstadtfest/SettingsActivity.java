package com.example.kassensystemaltstadtfest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadFromSharedPreferences();
        initBackButton();
    }

    private void initBackButton() {
        Button button = findViewById(R.id.btn_back);
        button.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("product_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            LinearLayout scrollViewLayout = findViewById(R.id.scrollViewSettings);
            int childCount = scrollViewLayout.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childView = scrollViewLayout.getChildAt(i);
                if (childView instanceof LinearLayout) {
                    LinearLayout productLayout = (LinearLayout) childView;
                    EditText productNameEditText = productLayout.findViewById(R.id.et_product_name);
                    Spinner productPriceSpinner = productLayout.findViewById(R.id.spinner_product_price);
                    Spinner productPfandSpinner = productLayout.findViewById(R.id.spinner_product_pfand);

                    String productName = productNameEditText.getText().toString();
                    float productPrice = Float.parseFloat(
                            productPriceSpinner.getSelectedItem().toString().replace(" €", "").replace(",", "."));

                    float productPfand = Float.parseFloat(
                            productPfandSpinner.getSelectedItem().toString().replace(" €", "").replace(",", "."));

                    String productNameKey = "product_name_" + (i + 1);
                    String productPriceKey = "product_price_" + (i + 1);
                    String productPfandKey = "product_pfand_" + (i + 1);

                    editor.putString(productNameKey, productName);
                    editor.putFloat(productPriceKey, productPrice);
                    editor.putFloat(productPfandKey, productPfand);
                }
            }

            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }

    private void loadFromSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences("product_data", MODE_PRIVATE);

        LinearLayout scrollViewLayout = findViewById(R.id.scrollViewSettings);
        scrollViewLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 1; i <= 14; i++) {
            String productNameKey = "product_name_" + i;
            String productPriceKey = "product_price_" + i;
            String productPfandKey = "product_pfand_" + i;

            String productName = sharedPreferences.getString(productNameKey, "Produkt "+i);
            float productPrice = sharedPreferences.getFloat(productPriceKey, 0.0f);
            float productPfand = sharedPreferences.getFloat(productPfandKey, 0.0f);

            if (!productName.isEmpty()) {
                // Inflate the product entry layout
                LinearLayout productLayout = (LinearLayout) inflater.inflate(R.layout.product_entry_layout, scrollViewLayout, false);

                TextView productNumber = productLayout.findViewById(R.id.et_product_number);
                productNumber.setText(i+": ");

                // Get references to the views in the layout
                EditText productNameEditText = productLayout.findViewById(R.id.et_product_name);
                Spinner productPriceSpinner = productLayout.findViewById(R.id.spinner_product_price);
                Spinner productPfandSpinner = productLayout.findViewById(R.id.spinner_product_pfand);

                // Set values for the views
                productNameEditText.setText(productName);
                int selectedPosition = (int) (productPrice * 2.0f - 1);
                int selectedPositionPfand = (int) (productPfand * 2.0f - 1);
                productPriceSpinner.setSelection(selectedPosition);
                productPfandSpinner.setSelection(selectedPositionPfand);


                // Add the product layout to the main ScrollView layout
                scrollViewLayout.addView(productLayout);
            }
        }
    }


}