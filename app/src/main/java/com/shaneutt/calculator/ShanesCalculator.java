package com.shaneutt.calculator;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ShanesCalculator extends AppCompatActivity {
    private Boolean  usingDecimal;  // are we using a decimal?
    private Double   storedValue;   // store values to be used in mathematical operations
    private String   lastOperation; // the previous mathematical operation used
    private EditText mainDisplay;   // the main area for calculation input and output
    private Toast    toaster;       // don't forget the butter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ///////////////////////////
        // Initialize Attributes //
        ///////////////////////////
        lastOperation = null;
        storedValue   = (double) 0;
        usingDecimal  = false;
        toaster       = Toast.makeText(this, null, Toast.LENGTH_LONG);
        ////////////////////////////
        // Initialize mainDisplay //
        ////////////////////////////
        mainDisplay = (EditText) findViewById(R.id.mainDisplay);
        mainDisplay.setText(null);
        ///////////////////////////////
        // Initialize Number Buttons //
        ///////////////////////////////
        Resources resources = getResources();
        for (int i = 0; i < 10; i++) {
            int buttonID = resources.getIdentifier("button" + i, "id", this.getBaseContext().getPackageName());
            Button numberButton = (Button) findViewById(buttonID);
            final Integer currentNumber = i;
            numberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // append the existing number to the mainDisplay
                    if (usingDecimal) {
                        mainDisplay.append(currentNumber.toString());
                    } else {
                        Integer newValueInteger = currentNumber.intValue();
                        mainDisplay.append(newValueInteger.toString());
                    }
                }
            });
        }
        ///////////////////////////////////////////////
        // Initialize Mathematical Operation Buttons //
        ///////////////////////////////////////////////
        String operationList[] = {"Addition", "Subtraction", "Multiplication", "Division"};
        for (final String operation: operationList) {
            int buttonID = resources.getIdentifier("button" + operation, "id", this.getBaseContext().getPackageName());
            Button operationButton = (Button) findViewById(buttonID);
            operationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // reconcile the stack to calculate any remaining operations into our storedValue
                    reconcileStack();
                    // update the mainDisplay
                    updateDisplay();
                    // update the last operation
                    lastOperation = operation;
                }
            });
        }
        //////////////////////////////////////////////////
        // Initialize Equal, Clear, and Decimal Buttons //
        //////////////////////////////////////////////////
        Button equals = (Button) findViewById(R.id.buttonEquals);
        equals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reconcile the existing stack
                reconcileStack();
                // update the mainDisplay, but disable clearing the mainDisplay
                updateDisplay(false);
                // wipe out the last operation and previously stored value
                lastOperation = null;
                storedValue   = null;
            }
        });
        Button clearEverything = (Button) findViewById(R.id.buttonClearEverything);
        clearEverything.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // wipe everything out
                mainDisplay.setHint(null);
                mainDisplay.setText(null);
                lastOperation = null;
                storedValue   = null;
            }
        });
        Button decimal = (Button) findViewById(R.id.buttonDecimal);
        decimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // place a decimal unless one is already placed
                if (mainDisplay.getText().toString().contains(".")) {
                    toaster.setText("a decimal is already placed");
                    toaster.show();
                } else {
                    mainDisplay.append(".");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reconcileStack() {
        String currentValueString = mainDisplay.getText().toString();
        // protect against issues where the display is empty or invalid
        if (currentValueString.length() < 1 || currentValueString == ".") {
            return;
        }
        // determine the current value as a double
        Double currentValue = Double.valueOf(currentValueString);
        if (lastOperation != null && storedValue != null) {
            // if we had a previous operation on the stack, use it to get a new total
            Double Total;
            switch (lastOperation) {
                case "Addition":
                    Total = storedValue + currentValue;
                    break;
                case "Subtraction":
                    Total = storedValue - currentValue;
                    break;
                case "Multiplication":
                    Total = storedValue * currentValue;
                    break;
                case "Division":
                    Total = storedValue / currentValue;
                    if (Total.isInfinite()) {
                        // for some reason java Double thinks dividing by zero equals infinite :(
                        Toast.makeText(this, "ILLEGAL DIVISION BY ZERO", Toast.LENGTH_LONG).show();
                        mainDisplay.setHint(null);
                        mainDisplay.setText(null);
                        lastOperation = null;
                        storedValue   = 0.00;
                        return;
                    }
                    break;
                default:
                    return;
            }
            storedValue = Total;
        } else {
            // if we didn't have an operation on the stack, our current value is our new previous value
            storedValue = currentValue;
        }
    }

    private void updateDisplay() { updateDisplay(true); }

    private void updateDisplay(boolean clearText) {
        // bail out if theres nothing stored
        if (storedValue == null) return;
        // test if we should be displaying in decimal or not
        String newText = new String();
        if (usingDecimal()) {
            newText = storedValue.toString();
        } else {
            Integer newNum = storedValue.intValue();
            newText = newNum.toString();
        }
        String newHint = null;
        if (clearText) {
            // clearing the text means add a hint that there's a number on the stack but
            // allow the user to input a new set of numbers
            newHint = newText.toString();
            newText = null;
        }
        updateDisplay(newHint, newText);
    }

    private void updateDisplay(String newHint, String newText) {
        mainDisplay.setHint(newHint);
        mainDisplay.setText(newText);
    }

    private boolean usingDecimal() {
        if (storedValue % 1 == 0) {
            return false;
        } else {
            return true;
        }
    }
}
