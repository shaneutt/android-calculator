package com.shaneutt.calculator;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ShanesCalculator extends AppCompatActivity {
    private Boolean  usingDecimal;  // are we using a decimal?
    private Double   storedValue;   // store values to be used in mathematical operations
    private EditText mainDisplay;   // the main area for calculation input and output
    private String   lastOperation; // the previous mathematical operation used

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
        ////////////////////////////
        // Initialize mainDisplay //
        ////////////////////////////
        mainDisplay = (EditText) findViewById(R.id.mainDisplay);
        mainDisplay.setText("0");
        ///////////////////////////////
        // Initialize Number Buttons //
        ///////////////////////////////
        Resources resources = getResources();
        for (int i = 0; i < 10; i++) {
            int buttonID = resources.getIdentifier("button" + i, "id", this.getBaseContext().getPackageName());
            Button numberButton = (Button) findViewById(buttonID);
            final int finalI = i;
            numberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add to the existing number in the mainDisplay
                    String currentValue = mainDisplay.getText().toString();
                    Double newValue = Double.valueOf(currentValue + finalI);
                    if (usingDecimal) {
                        mainDisplay.setText(newValue.toString());
                    } else {
                        Integer newValueInteger = newValue.intValue();
                        mainDisplay.setText(newValueInteger.toString());
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
        Button clear = (Button) findViewById(R.id.buttonClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDisplay.setHint(null);
                mainDisplay.setText("0");
            }
        });
        Button clearEverything = (Button) findViewById(R.id.buttonClearEverything);
        clearEverything.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDisplay.setHint(null);
                mainDisplay.setText("0");
                lastOperation = null;
                storedValue   = null;
            }
        });
        Button decimal = (Button) findViewById(R.id.buttonDecimal);
        decimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement using decimals
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
        // protect again empty display issues
        if (currentValueString.length() < 1) {
            currentValueString = "0";
        }
        Double currentValue = Double.valueOf(currentValueString);
        if (lastOperation != null) {
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

    private void updateDisplay() {
        updateDisplay(true);
    }

    private void updateDisplay(Boolean clearDisplay) {
        if (usingDecimal) {
            if (clearDisplay) {
                mainDisplay.setHint(storedValue.toString());
                mainDisplay.setText(null);
            } else {
                mainDisplay.setText(storedValue.toString());
            }
        } else {
            Integer storedValueInteger = storedValue.intValue();
            if (clearDisplay) {
                mainDisplay.setHint(storedValueInteger.toString());
                mainDisplay.setText(null);
            } else {
                mainDisplay.setText(storedValueInteger.toString());
            }
        }
    }
}
