package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.elijah.ukeme.onlinecampospos.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);

                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {

                    Intent intent = new Intent(MainActivity.this,CustomerLoginRegisterActivity.class);
                    startActivity(intent);
                }
            }
        };
        thread.start();
    }
}