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

public class ChargeActivity extends AppCompatActivity {

    private Button rejectButton, acceptButton;
    private TextView chargeDiplay;
    String amount,charge;
    String transaction ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_charge);
        acceptButton = findViewById(R.id.accept_button);
        rejectButton = findViewById(R.id.reject_button);
        chargeDiplay = findViewById(R.id.charge_display_textview);
        amount = getIntent().getStringExtra("amount");
        charge = getIntent().getStringExtra("charge");
        transaction = getIntent().getStringExtra("transaction");

        chargeDiplay.setText("You will be charge "+charge+" for this transaction");

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChargeActivity.this,CustomerLoginRegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChargeActivity.this,DetailsActivity.class);
                intent.putExtra("amount",amount);
                intent.putExtra("charge",charge);
                intent.putExtra("transaction",transaction);
                startActivity(intent);
            }
        });
    }
}