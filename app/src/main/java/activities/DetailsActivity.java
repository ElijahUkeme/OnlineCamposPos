package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;

public class DetailsActivity extends AppCompatActivity {
    private Button cancelButton, proceedButton;
    private TextView amounTv,chargeTv,transactionTv;
    private String amount,charge;
    private String transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_details);
        cancelButton = findViewById(R.id.cancel_button_details);
        proceedButton = findViewById(R.id.proceed_button);
        amounTv = findViewById(R.id.transaction_amount_tv);
        chargeTv = findViewById(R.id.transaction_charge_tv);
        transactionTv = findViewById(R.id.transaction_type_tv);
        amount = getIntent().getStringExtra("amount");
        charge = getIntent().getStringExtra("charge");
        transaction = getIntent().getStringExtra("transaction");

        amounTv.setText(amount);
        chargeTv.setText(charge);
        transactionTv.setText(transaction);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this,CustomerLoginRegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this,CustomerMapsActivity.class);
                intent.putExtra("amount",amount);
                intent.putExtra("charge",charge);
                intent.putExtra("transaction",transaction);
                startActivity(intent);
            }
        });
    }
}