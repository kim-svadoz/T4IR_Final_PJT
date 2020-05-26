package com.example.android.car;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.androdocs.httprequest.HttpRequest;
import com.example.android.R;
import com.example.android.driving.Driving_Info;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Car extends AppCompatActivity {
    ImageButton car_handle_img;
    ToggleButton car_lock;
    ToggleButton car_air;
    ImageButton car_navi;
    ToggleButton car_seat;
    TextView edit_oil;
    ProgressBar progressBar;

    /* ==========================날씨========================== */
    String CITY = "seoul,KR";
    String API = "1d05f37dc31eab19ba9ee3c97411cf25";
    String LAT = "";
    String LON = "";
    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        car_handle_img = findViewById(R.id.car_handle_img);
        car_lock = findViewById(R.id.car_lock);
        car_air = findViewById(R.id.car_air);
        car_navi = findViewById(R.id.car_navi);
        car_seat = findViewById(R.id.car_seat);
        progressBar = findViewById(R.id.oil_progressbar);
        edit_oil = findViewById(R.id.car_oil);

        car_handle_img.setImageResource(R.drawable.car_handle_img);
        car_navi.setImageResource(R.drawable.car_navi);
        edit_oil.setText("66");
        progressBar.setProgress(Integer.parseInt(edit_oil.getText().toString()));

        car_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car_lock.isChecked()){
                    car_lock.setBackgroundDrawable(getResources().getDrawable(R.drawable.car_lock_open));
                    Toast.makeText(Car.this,"UNLOCK",Toast.LENGTH_SHORT).show();
                }else{
                    car_lock.setBackgroundDrawable(getResources().getDrawable(R.drawable.car_lock_close));
                    Toast.makeText(Car.this,"LOCK",Toast.LENGTH_SHORT).show();
                }
            }
        });

        car_air.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car_air.isChecked()){
                    car_air.setBackgroundDrawable(getResources().getDrawable(R.drawable.car_air_open));
                    Toast.makeText(Car.this,"FAN ON",Toast.LENGTH_SHORT).show();
                }else{
                    car_air.setBackgroundDrawable(getResources().getDrawable(R.drawable.car_air_close));
                    Toast.makeText(Car.this,"FAN OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });

        car_seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(car_seat.isChecked()){
                    car_seat.setBackgroundDrawable(getResources().getDrawable(R.drawable.car_seat_open));
                    Toast.makeText(Car.this,"SEAT ON",Toast.LENGTH_SHORT).show();
                }else{
                    car_seat.setBackgroundDrawable(getResources().getDrawable(R.drawable.car_seat_close));
                    Toast.makeText(Car.this,"SEAT OFF",Toast.LENGTH_SHORT).show();
                }
            }
        });

        car_handle_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Car.this, Driving_Info.class);
                startActivity(intent);
            }
        });


        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);

        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            //findViewById(R.id.loader).setVisibility(View.VISIBLE);
            //findViewById(R.id.mainContainer).setVisibility(View.GONE);
            //findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
            //String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");


                /* Populating extracted data into our views */
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);

                /* Views populated, Hiding the loader, Showing the main design */
                //findViewById(R.id.loader).setVisibility(View.GONE);
                //findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                //findViewById(R.id.loader).setVisibility(View.GONE);
                //findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }
}

