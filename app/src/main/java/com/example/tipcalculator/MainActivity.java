package com.example.tipcalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

public class MainActivity extends AppCompatActivity
        implements TextWatcher,SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener{

    private static String TAG = "MainActivity";
    //declare your variables for the widgets
    private String spinnerLabel;
    private String price;
    private String checkedItem = "No";
    private int numPeople;
    private double perPerson, tip, total;
    private ArrayAdapter<CharSequence> adapter;
    private EditText editTextBillAmount;
    private SeekBar sBar;
    private Spinner spinner;
    private TextView textViewBillAmount, textViewPercent, tipTextView, textViewTotal, perPersonView;
    private RadioGroup radioGroup;

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
        Log.d(TAG, "Start of onCreate Method");
        //add Listeners to Widgets
        setReferences();
        adapter = ArrayAdapter.createFromResource(this, R.array.amount_of_people, R.layout.support_simple_spinner_dropdown_item);
        if(editTextBillAmount != null && spinner != null) {
            setListeners();
            spinner.setAdapter(adapter);
        }
        price = perPersonView.getText().toString();
        perPersonView.setText(price + currencyFormat.format(0));
         /*  Note: each View that will be retrieved using findViewById needs to map to a View with the matching id
        Example: editTextBillAmount
        Needs to map to a View with the following: android:id="@+id/editText_BillAmount
        */
        Log.d(TAG, "End of onCreate Method");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "Start of onSaveInstanceState Method");
        super.onSaveInstanceState(outState);
        outState.putDouble("total", total);
        outState.putDouble("tip", tip);
        outState.putDouble("per_person", perPerson);
        Log.d(TAG, "End of onSaveInstanceState Method");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "Start of onRestoreInstanceState Method");
        super.onRestoreInstanceState(savedInstanceState);
        total = savedInstanceState.getDouble("total", 0.0);
        tip = savedInstanceState.getDouble("tip", 0.0);
        perPerson = savedInstanceState.getDouble("per_person", 0.0);

        textViewTotal.setText(currencyFormat.format(total));
        tipTextView.setText(currencyFormat.format(tip));
        perPersonView.setText(currencyFormat.format(perPerson));
        Log.d(TAG, "End of onRestoreInstanceState Method");
    }

    private void setListeners() {
        Log.d(TAG, "Start of setListeners Method");
        editTextBillAmount.addTextChangedListener(this);
        sBar.setOnSeekBarChangeListener(this);
        spinner.setOnItemSelectedListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        Log.d(TAG, "End of setListeners Method");
    }

    private void setReferences() {
        Log.d(TAG, "Start of setReferences Method");
        editTextBillAmount = findViewById(R.id.editText_BillAmount);
        tipTextView = findViewById(R.id.textView_tip);
        textViewPercent = findViewById(R.id.textView_percent);
        textViewBillAmount = findViewById(R.id.textView_BillAmount);
        textViewTotal = findViewById(R.id.textView_total);
        sBar = findViewById(R.id.seekBar);
        spinner = findViewById(R.id.number_of_people);
        perPersonView = findViewById(R.id.per_person_total);
        radioGroup = findViewById(R.id.round_options);
        Log.d(TAG, "End of setReferences Method");
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
        Log.d("MainActivity", "Start of onTextChanged method: charSequence= "+ charSequence);
        if(charSequence.length() > 0) {
            //surround risky calculations with try catch (what if billAmount is 0 ?
            //charSequence is converted to a String and parsed to a double for you
            billAmount = Double.parseDouble(charSequence.toString()) / 100;
            Log.d("MainActivity", "Bill Amount = " + billAmount);
            //setText on the textView
            textViewBillAmount.setText(currencyFormat.format(billAmount));
            //perform tip and total calculation and update UI by calling calculate
            //         calculate();//uncomment this line
        } else {
            textViewBillAmount.setText(currencyFormat.format(0.0));
        }
        Log.d(TAG, "End of onTextChanged Method");
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100.00; //calculate percent based on seeker value
        // format percent and display in percentTextView
        textViewPercent.setText(percentFormat.format(percent));
        Log.d(TAG, "onStartTrackingTouch: " + progress);
//        calculate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    // calculate and display tip and total amounts
    public void calculate(View view) {
        Log.d("MainActivity", "Start of calculate method");
        switch(checkedItem) {
            case "No":
                Log.d(TAG, "Radio button value: " + checkedItem);
                getTotal("No");
                break;
            case "Tip":
                Log.d(TAG, "Radio button value: " + checkedItem);
                getTotal("Tip");
                break;
            case "Total":
                Log.d(TAG, "Radio button value: " + checkedItem);
                getTotal("Total");
                break;
            default:
                getTotal("default");
        }
        Log.d("MainActivity", "End of calculate method");
    }

    private void getTotal(String round) {
        Log.d("MainActivity", "Start of getTotal method");
        // calculate the tip and total
        tip = billAmount * percent;
        //use the tip example to do the same for the Total
        if(round.equals("Tip")) {
            tip = Math.round(tip);
        }
        // display tip and total formatted as currency
        //user currencyFormat instead of percentFormat to set the textViewTip
        tipTextView.setText(currencyFormat.format(tip));
        //use the tip example to do the same for the TotaL
        total = billAmount + tip;
        if(round.equals("Total")) {
            total = Math.round(total);
        }
        perPerson = total / numPeople;
        textViewTotal.setText(currencyFormat.format(total));
        perPersonView.setText(price + currencyFormat.format(perPerson));
        Log.d("MainActivity", "End of getTotal method");
    }

    public void reset(View view) {
        Log.d("MainActivity", "Start of reset method");
        billAmount = 0.0;
        percent = .15;
        textViewBillAmount.setText(currencyFormat.format(billAmount));
        textViewPercent.setText(percentFormat.format(percent));
        textViewTotal.setText(currencyFormat.format(billAmount));
        tipTextView.setText(currencyFormat.format(billAmount));
        editTextBillAmount.setText("");
        perPersonView.setText(price + currencyFormat.format(billAmount));
        Log.d("MainActivity", "End of reset method");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.d("MainActivity", "Start of onCheckedChanged method");
        RadioButton radioButton = findViewById(checkedId);
        checkedItem = radioButton.getText().toString();
        Log.d("MainActivity", "End of onCheckedChanged method");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        spinnerLabel = adapterView.getItemAtPosition(position).toString();
        numPeople = Integer.parseInt(spinnerLabel);
        Log.d(TAG, "Spinner Label: " + spinnerLabel);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity", "Start of onCreateOptionsMenu method");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("MainActivity", "End of onCreateOptionsMenu method");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.share:
                sendText();
                break;
            case R.id.info:
                Toast.makeText(this,
                        "The spinner allows the user to pick how many people to split the bill with.",
                        Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendText() {
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder
                .from(this)
                .setType(mimeType)
                .setChooserTitle("Pick an app")
                .setText("Hey, The bill came out to " + billAmount + ". The tip will be " + tip + "(" + percent + ") bringing the total to " + total + ". " +
                        "If we split the bill amongst us it will come out to " + perPerson + " per person.")
                .startChooser();
    }
}

