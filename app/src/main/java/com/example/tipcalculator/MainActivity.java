package com.example.tipcalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
        implements TextWatcher,SeekBar.OnSeekBarChangeListener{

    private static String TAG = "MainActivity";
    //declare your variables for the widgets
    private EditText editTextBillAmount;
    private SeekBar sBar;
    private TextView textViewBillAmount, textViewPercent, tipTextView, textViewTotal;

    //declare the variables for the calculations
    private double billAmount = 0.0;
    private double percent = .15;

    //set the number formats to be used for the $ amounts , and % amounts
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //add Listeners to Widgets
        editTextBillAmount = findViewById(R.id.editText_BillAmount);
        sBar = findViewById(R.id.seekBar);
         /*  Note: each View that will be retrieved using findViewById needs to map to a View with the matching id
        Example: editTextBillAmount
        Needs to map to a View with the following: android:id="@+id/editText_BillAmount
        */
        editTextBillAmount.addTextChangedListener(this);
        sBar.setOnSeekBarChangeListener(this);
        tipTextView = findViewById(R.id.textView_tip);
        textViewPercent = findViewById(R.id.textView_percent);
        textViewBillAmount = findViewById(R.id.textView_BillAmount);
        textViewTotal = findViewById(R.id.textView_total);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    /*
    Note:   int i, int i1, and int i2
            represent start, before, count respectively
            The charSequence is converted to a String and parsed to a double for you
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("MainActivity", "inside onTextChanged method: charSequence= "+charSequence);
        //surround risky calculations with try catch (what if billAmount is 0 ?
        //charSequence is converted to a String and parsed to a double for you
        billAmount = Double.parseDouble(charSequence.toString()) / 100;
        Log.d("MainActivity", "Bill Amount = "+billAmount);
        //setText on the textView
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        //perform tip and total calculation and update UI by calling calculate
         calculate();//uncomment this line
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100.00; //calculate percent based on seeker value
        Log.d(TAG, "onStartTrackingTouch: " + progress);
        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    // calculate and display tip and total amounts
    private void calculate() {
        Log.d("MainActivity", "inside calculate method");
//        uncomment below

       // format percent and display in percentTextView
        textViewPercent.setText(percentFormat.format(percent));

       // calculate the tip and total
       double tip = billAmount * percent;
      //use the tip example to do the same for the Total

       // display tip and total formatted as currency
       //user currencyFormat instead of percentFormat to set the textViewTip
        tipTextView.setText(currencyFormat.format(tip));
       //use the tip example to do the same for the TotaL
        double total = billAmount + tip;
        textViewTotal.setText(currencyFormat.format(total));
    }
}

