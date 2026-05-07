package com.example.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {
    private TextView tvDisplay;
    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // ক্যালকুলেটর বাটন সেটআপ
        String[] buttons = {
            "AC", "DEL", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "00", "0", ".", "="
        };

        for (String btnText : buttons) {
            Button button = new Button(this);
            button.setText(btnText);
            button.setTextSize(26);
            button.setTextColor(Color.WHITE);
            
            // প্রিমিয়াম ডার্ক থিম স্টাইলিং
            if (btnText.matches("[0-9]|00|\\.")) {
                button.setBackgroundColor(Color.parseColor("#2C2C2C"));
            } else if (btnText.equals("=") || btnText.equals("/") || btnText.equals("*") || btnText.equals("-") || btnText.equals("+")) {
                button.setBackgroundColor(Color.parseColor("#FF9500"));
            } else {
                button.setBackgroundColor(Color.parseColor("#A5A5A5"));
                button.setTextColor(Color.BLACK);
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);
            button.setLayoutParams(params);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleInput(btnText);
                }
            });
            gridLayout.addView(button);
        }
    }

    private void handleInput(String input) {
        if (input.equals("AC")) {
            currentInput = "";
        } else if (input.equals("DEL")) {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }
        } else if (input.equals("=")) {
            try {
                currentInput = String.valueOf(eval(currentInput));
                // দশমিকের পর .0 থাকলে বাদ দেওয়ার জন্য
                if (currentInput.endsWith(".0")) {
                    currentInput = currentInput.replace(".0", "");
                }
            } catch (Exception e) {
                currentInput = "Error";
            }
        } else {
            currentInput += input;
        }
        tvDisplay.setText(currentInput.isEmpty() ? "0" : currentInput.replace("*", "×").replace("/", "÷"));
    }

    // অ্যাডভান্সড এক্সপ্রেশন ক্যালকুলেশন ইঞ্জিন (Pure Java)
    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); 
                    else if (eat('-')) x -= parseTerm(); 
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); 
                    else if (eat('/')) x /= parseFactor(); 
                    else if (eat('%')) x %= parseFactor(); 
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor(); 
                if (eat('-')) return -parseFactor(); 
                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Error");
                }
                return x;
            }
        }.parse();
    }
}
