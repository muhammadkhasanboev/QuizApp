package android.bignerdranch.rebuildedquizapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private Button buttonAC, buttonDel, buttonPercent, buttonDivide;
    private Button button7, button8, button9, buttonMultiply;
    private Button button4, button5, button6, buttonSubtract;
    private Button button1, button2, button3, buttonAdd;
    private Button button0, buttonDot, buttonEqual;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link all buttons
        buttonAC = findViewById(R.id.button_ac);
        buttonDel = findViewById(R.id.button_del);
        buttonPercent = findViewById(R.id.button_percent);
        buttonDivide = findViewById(R.id.button_divide);
        buttonMultiply = findViewById(R.id.button_multiply);
        buttonSubtract = findViewById(R.id.button_subtract);
        buttonAdd = findViewById(R.id.button_add);
        buttonDot = findViewById(R.id.button_dot);
        buttonEqual = findViewById(R.id.button_equal);
        button0 = findViewById(R.id.button_0);
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);
        button3 = findViewById(R.id.button_3);
        button4 = findViewById(R.id.button_4);
        button5 = findViewById(R.id.button_5);
        button6 = findViewById(R.id.button_6);
        button7 = findViewById(R.id.button_7);
        button8 = findViewById(R.id.button_8);
        button9 = findViewById(R.id.button_9);
        textView = findViewById(R.id.textView);

        // Shared number click listener
        View.OnClickListener numberClickListener = v -> {
            Button clicked = (Button) v;
            textView.append(clicked.getText().toString());
        };

        // Assign listeners to number buttons
        Button[] numberButtons = {button0, button1, button2, button3, button4,
                button5, button6, button7, button8, button9};
        for (Button btn : numberButtons) {
            btn.setOnClickListener(numberClickListener);
        }

        // Operators: add spacing for readability
        View.OnClickListener operatorClickListener = v -> {
            Button clicked = (Button) v;
            String op = clicked.getText().toString();
            textView.append(" " + op + " ");
        };

        buttonAdd.setOnClickListener(operatorClickListener);
        buttonSubtract.setOnClickListener(operatorClickListener);
        buttonMultiply.setOnClickListener(operatorClickListener);
        buttonDivide.setOnClickListener(operatorClickListener);

        // Dot button
        buttonDot.setOnClickListener(numberClickListener);

        // Percent: insert as "/100"
        buttonPercent.setOnClickListener(v -> textView.append(" / 100 "));

        // Delete: remove last char (not smart-delete spaces/ops)
        buttonDel.setOnClickListener(v -> {
            String current = textView.getText().toString();
            if (!current.isEmpty()) {
                textView.setText(current.substring(0, current.length() - 1));
            }
        });

        // AC: clear all
        buttonAC.setOnClickListener(v -> textView.setText(""));

        // Equal button: evaluate expression
        buttonEqual.setOnClickListener(v -> {
            String expr = textView.getText().toString();
            try {
                double result = eval(expr);
                textView.setText(String.valueOf(result));
            } catch (Exception e) {
                textView.setText("Error");
            }
        });
    }

    // Expression evaluator
    private double eval(String expression) {
        try {
            final String expr = expression.replaceAll("×", "*").replaceAll("÷", "/");
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    while (true) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    while (true) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expr.substring(startPos, this.pos));
                    } else if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }
                    return x;
                }
            }.parse();
        } catch (Exception e) {
            return 0;
        }
    }

}
