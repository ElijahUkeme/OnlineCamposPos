package activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;

public class SelectActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner spinner;
    private Button next;
    String transaction ="";
    int amount,charge = 0;
    boolean cancel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_select);
        amountEditText = findViewById(R.id.input_amount_detail);
        spinner = findViewById(R.id.transaction_type_spinner);
        next = findViewById(R.id.next_button);
        if (!amountEditText.getText().toString().isEmpty()){
            amount = Integer.parseInt(amountEditText.getText().toString());
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });


        String[] transactionArray = {"Select Transaction Here","Deposit","Withdrawal","Transfer","Buy Airtime","Pay Bills"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,transactionArray);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView)adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView)adapterView.getChildAt(0)).setTextSize(16);

                switch (i){
                    case 0:
                        transaction = "Nothing Selected";
                        break;
                    case 1:
                        transaction = "Deposit";
                        break;
                    case 2:
                        transaction = "Withdrawal";
                        break;
                    case 3:
                        transaction = "Transfer";
                        break;
                    case 4:
                        transaction = "Buy Airtime";
                        break;
                    case 5:
                        transaction = "Pay Bills";
                        break;
                    default:
                        transaction = "";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void proceed(){
        if (amountEditText.getText().toString().isEmpty()){
            amountEditText.setError("Please Select An Amount");
            cancel = true;
            amountEditText.requestFocus();
        }
        else if (transaction.equalsIgnoreCase("Nothing Selected")){
            Toast.makeText(SelectActivity.this,"Please Select Transaction Type",Toast.LENGTH_SHORT).show();
        }else {

            amount = Integer.parseInt(amountEditText.getText().toString());

            if (amount <=500){
                charge = 50;
            }else if (amount >500 && amount <=1000){
                charge = 100;
            }else if (amount >1000 && amount <=5000){
                charge = 150;
            }else if (amount >5000 && amount <=10000){
                charge = 200;
            }else if (amount >10000 && amount <=20000){
                charge = 300;
            }else if (amount >20000 && amount <= 50000){
                charge = 500;
            }else if (amount >50000 && amount <=100000){
                charge = 1000;
            }else if (amount >100000){
                charge = 1500;
            }

            String transactionCharge = String.valueOf(charge);
            String theAmount = String.valueOf(amount);

            Intent intent = new Intent(SelectActivity.this, ChargeActivity.class);
            intent.putExtra("amount",theAmount);
            intent.putExtra("transaction",transaction);
            intent.putExtra("charge",transactionCharge);
            startActivity(intent);
        }
    }
}