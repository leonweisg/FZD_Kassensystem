package com.example.kassensystemaltstadtfest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BezahlenActivity extends AppCompatActivity {

    private float gesamt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezahlen);

        setStatusBarColor();
        gesamt = getIntent().getFloatExtra("gesamt",0L);
        Button beenden = findViewById(R.id.btn_beenden);
        beenden.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        Button abbrechen = findViewById(R.id.btn_abbrechen);
        abbrechen.setOnClickListener(v -> {
            finish();
        });
        showGesamt();
        initExchange();
    }

    private void initExchange() {
        EditText editTextGegeben = findViewById(R.id.editTextGegeben);

        editTextGegeben.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateAndDisplayRueckgeld(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }
    private void calculateAndDisplayRueckgeld(String eingabe) {
        TextView txtRueckgeld = findViewById(R.id.txt_rueckgeld);
        if (eingabe.isEmpty()) {
            txtRueckgeld.setText("Rückgeld: 0.00 €");
            return;
        }

        try {
            float gegeben = Float.parseFloat(eingabe);
            // Hier könntest du weitere Berechnungen durchführen, z.B. den Gesamtbetrag subtrahieren
            float rueckgeld = gegeben - gesamt; // Annahme: gesamt ist der Gesamtbetrag

            // Zeige das Rückgeld im TextView an
            txtRueckgeld.setText("Rückgeld: " + String.format("%.2f", rueckgeld) + " €");
        } catch (NumberFormatException e) {
            // Falls die Eingabe keine gültige Zahl ist
            txtRueckgeld.setText("Ungültige Eingabe");
        }
    }

    private void showGesamt() {
        TextView gesamtText = findViewById(R.id.txt_gesamt_sum);
        gesamtText.setText(gesamt + "0 €");
    }

    private void setStatusBarColor() {
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }
}