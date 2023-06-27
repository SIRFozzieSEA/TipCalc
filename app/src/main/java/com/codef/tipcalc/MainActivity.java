package com.codef.tipcalc;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText oBillAmountEditText = findViewById(R.id.amountText);
        oBillAmountEditText.setOnClickListener(v -> {
            TextView oTipAmountTextView = findViewById(R.id.totalTip);
            oTipAmountTextView.setText("");
            TextView oTotalBillTextView = findViewById(R.id.totalBill);
            oTotalBillTextView.setText("");
        });

        oBillAmountEditText.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {

                EditText oBillAmountEditText = findViewById(R.id.amountText);
                if (oBillAmountEditText.getText().toString().matches("^\\d+\\.\\d{2}$"))
                {
                    handleChangeEvent();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(oBillAmountEditText.getWindowToken(), 0);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
        });

        Button oCalcButton = findViewById(R.id.button1);
        oCalcButton.setOnClickListener(v -> handleChangeEvent());

        RadioGroup oPercentRadioGroup = findViewById(R.id.radioGroupPercent);
        oPercentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> handleChangeEvent());

        RadioGroup oRoundingRadioGroup = findViewById(R.id.radioGroupRounding);
        oRoundingRadioGroup.setOnCheckedChangeListener((group, checkedId) -> handleChangeEvent());
    }

    private void handleChangeEvent() {
        TextView oBillAmountTextView = findViewById(R.id.amountText);

        double dTipPercent = 0;
        RadioGroup oPercentRadioGroup = findViewById(R.id.radioGroupPercent);
        int nIndexSelected = oPercentRadioGroup.getCheckedRadioButtonId();
        RadioButton oPercentRadioButton = findViewById(nIndexSelected);
        if (oPercentRadioButton.getId() == R.id.radioTenPercent)
        {
            dTipPercent = 10;
        }
        else if (oPercentRadioButton.getId() == R.id.radioFifteenPercent)
        {
            dTipPercent = 15;
        }
        else if (oPercentRadioButton.getId() == R.id.radioTwentyPercent)
        {
            dTipPercent = 20;
        }
        else if (oPercentRadioButton.getId() == R.id.radioTwentyFivePercent)
        {
            dTipPercent = 25;
        }
        else if (oPercentRadioButton.getId() == R.id.radioThirtyPercent)
        {
            dTipPercent = 30;
        }

        String sRadioDirection = "UP";
        RadioGroup oRoundingRadioGroup = findViewById(R.id.radioGroupRounding);
        nIndexSelected = oRoundingRadioGroup.getCheckedRadioButtonId();
        RadioButton oRoundingRadioButton = findViewById(nIndexSelected);
        if (oRoundingRadioButton.getId() == R.id.radioRoundUp)
        {
            sRadioDirection = "UP";
        }
        else if (oRoundingRadioButton.getId() == R.id.radioRoundDown)
        {
            sRadioDirection = "DOWN";
        }
        else if (oRoundingRadioButton.getId() == R.id.radioNeverRound)
        {
            sRadioDirection = "NEVER";
        }

        if (oBillAmountTextView.getText().toString().equalsIgnoreCase(""))
        {
            oBillAmountTextView.setText(String.format(Locale.getDefault(), "%.2f", 0.0));
        }

        String[] sCalcResults = calculateTip(Double.parseDouble(oBillAmountTextView.getText().toString()), dTipPercent, sRadioDirection);

        TextView oTipAmountTextView = findViewById(R.id.totalTip);
        oTipAmountTextView.setText(sCalcResults[0]);
        TextView oTotalBillTextView = findViewById(R.id.totalBill);
        oTotalBillTextView.setText(sCalcResults[1]);
    }

    private static String[] calculateTip(double _dBillAmount, double _dTipPercent, String _sRoundScheme)
    {

        NumberFormat oNumberFormat = new DecimalFormat("#,###,##0.00");

        double dFinalTip;
        double dFinalTotal;

        double dTip = _dTipPercent * _dBillAmount / 100;
        double dBillPlusTip = _dBillAmount + dTip;

        if (_sRoundScheme.equalsIgnoreCase("UP"))
        {

            String[] sBillAmount = String.valueOf(dBillPlusTip).split("\\.");
            double dTipMod = 1 - Double.parseDouble("." + sBillAmount[1]);

            if (dTipMod == 1)
            {
                dTipMod = 0;
            }

            dFinalTip = dTip + dTipMod;
            dFinalTotal = dFinalTip + _dBillAmount;

            return new String[] { "$" + oNumberFormat.format(dFinalTip) + " (+$" + oNumberFormat.format(dTipMod) + " Round)",
                    "$" + oNumberFormat.format(dFinalTotal) };
        }
        else if (_sRoundScheme.equalsIgnoreCase("DOWN"))
        {

            String[] sBillAmount = String.valueOf(dBillPlusTip).split("\\.");
            double dTipMod = Double.parseDouble("." + sBillAmount[1]);

            if (dTipMod == 1)
            {
                dTipMod = 0;
            }

            dFinalTip = dTip - dTipMod;
            dFinalTotal = dFinalTip + _dBillAmount;

            return new String[] { "$" + oNumberFormat.format(dFinalTip) + " (-$" + oNumberFormat.format(dTipMod) + " Round)",
                    "$" + oNumberFormat.format(dFinalTotal) };
        }
        else
        {
            dFinalTip = dTip;
            dFinalTotal = dFinalTip + _dBillAmount;
            return new String[] { "$" + oNumberFormat.format(dFinalTip), "$" + oNumberFormat.format(dFinalTotal) };
        }

    }

}