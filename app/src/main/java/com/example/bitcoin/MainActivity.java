package com.example.bitcoin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String BPI_ENDPOINT = "https://api.coindesk.com/v1/bpi/currentprice.json";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog progressDialog;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.txt);


        txt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                load();
                return false;
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("BPI Loading");
        progressDialog.setMessage("Wait ...");


    }

    private void load() {
        Request request = new Request.Builder()
                .url(BPI_ENDPOINT)
                .build();

        progressDialog.show();


    okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Toast.makeText(MainActivity.this, "Error during BPI loading : "
                    + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                   final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

     //que manda el json:
     Toast.makeText(MainActivity.this, body, Toast.LENGTH_SHORT).show();

                        parseBpiResponse(body);
                    }
                });
        }
    });

    }
    private void parseBpiResponse(String body) {
        try {
            StringBuilder builder = new StringBuilder();

            JSONObject jsonObject = new JSONObject(body);
            JSONObject timeObject = jsonObject.getJSONObject("time");
            builder.append(timeObject.getString("updated")).append("\n\n");

            JSONObject bpiObject = jsonObject.getJSONObject("bpi");
            JSONObject usdObject = bpiObject.getJSONObject("USD");
            builder.append(usdObject.getString("rate")).append("$").append("\n");

            JSONObject gbpObject = bpiObject.getJSONObject("GBP");
    //  builder.append(gbpObject.getString("rate")).append("£").append("\n");

            JSONObject euroObject = bpiObject.getJSONObject("EUR");
    //  builder.append(euroObject.getString("rate")).append("€").append("\n");


            txt.setTextColor(Color.BLUE);
            txt.setText(builder.toString().trim());


        } catch (Exception e) {

        }
    }

}