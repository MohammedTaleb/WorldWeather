package com.example.ma.awa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mohammed_Aqrabawi on 9/28/2018.
 */

public class Weather_Popup extends AppCompatActivity implements View.OnClickListener{
	SharedPreferences sp;
	Spinner city;
	ImageButton delete,add;
	ImageButton refresh;
	DataBase DB=new DataBase(this);
	ListView listOfDays;
	ProgressBar pBar;


	TextView today,sec,third,forth,fifth,todaymin,secmin,thirdmin,forthmin,fifthmin,todaymax,secmax,thirdmax,forthmax,fifthmax,current;

	ArrayList <String> jsonDate;
	ArrayList <String> jsonMax;
	ArrayList <String> jsonmin;
	static final String API_KEY = "2060b34ad27d48e6bf8190341182109";
	static final String API_URL = "http://api.worldweatheronline.com/premium/v1/weather.ashx?";
	private String[] res = new String[2] ;
	String cityData;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_popup);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width=dm.widthPixels;
		int height=dm.heightPixels;
		getWindow().setLayout((int)(width*.75),(int)(height*.65));


		listOfDays= (ListView) findViewById(R.id.days);
		pBar = (ProgressBar) findViewById(R.id.progBar);

		current = (TextView) findViewById(R.id.temp);

		today= (TextView) findViewById(R.id.today);
		todaymin= (TextView) findViewById(R.id.todaymin);
		todaymax= (TextView) findViewById(R.id.todaymax);

		sec= (TextView) findViewById(R.id.second);
		secmin= (TextView) findViewById(R.id.secondmin);
		secmax= (TextView) findViewById(R.id.secondmax);

		third= (TextView) findViewById(R.id.third);
		thirdmin= (TextView) findViewById(R.id.thirdmin);
		thirdmax= (TextView) findViewById(R.id.thirdmax);

		forth= (TextView) findViewById(R.id.fourth);
		forthmin= (TextView) findViewById(R.id.forthmin);
		forthmax= (TextView) findViewById(R.id.forthmax);

		fifth= (TextView) findViewById(R.id.fifth);
		fifthmin= (TextView) findViewById(R.id.fifthmin);
		fifthmax= (TextView) findViewById(R.id.fifthmax);


		sp=this.getSharedPreferences("com.example.ma.awa", Context.MODE_PRIVATE);
		if (sp.getBoolean("Key",true)){
			DB.insert("Amman");
			DB.insert("London");
			DB.insert("New York");
			DB.insert("Barcelona");
		}
		sp.edit().putBoolean("Key",false).apply(); //once the application started for the first time must change to false

		delete= (ImageButton) findViewById(R.id.Del);
		delete.setOnClickListener(this);

		add= (ImageButton) findViewById(R.id.Add);
		add.setOnClickListener(this);

		refresh= (ImageButton) findViewById(R.id.re);
		refresh.setOnClickListener(this);

		city= (Spinner) findViewById(R.id.CT);

		final ArrayList<String> cty=DB.readCity();

		ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,cty);
		city.setAdapter(adapter);
		city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				res =city.getSelectedItem().toString().split("\\ ");
				if (res.length>1)
					cityData=res[0]+"+"+res[1];
				else
					cityData=res[0];
				sp.edit().putString("city",cityData).apply();
				new RetrieveFeedTask().execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				res =city.getSelectedItem().toString().split("\\ ");
				if (res.length>0)
					cityData=res[0]+"+"+res[1];
				sp.edit().putString("city",cityData).apply();
				new RetrieveFeedTask().execute();

			}

		});

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.Del:
				sureDialog();
				break;
			case R.id.Add:
				Intent intent = new Intent(this, City_search.class);
				startActivity(intent);
				finish();
				break;
			case R.id.re:
				new RetrieveFeedTask().execute();
				break;
		}
	}

	public void sureDialog() {

		Log.i("Dialog", "sure Dialog");

		AlertDialog.Builder diolag;
		diolag = new AlertDialog.Builder(this);
		diolag.setTitle("Delete!! ");
		diolag.setMessage("are you sure to delete the selected city! ");
		diolag.setIcon(android.R.drawable.ic_dialog_alert);
		diolag.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DB.delCity(sp.getString("city",null));
				finish();
				Intent intent=new Intent(Weather_Popup.this,Weather_Popup.class);
				startActivity(intent);

			}
		});
		diolag.setNegativeButton("Back", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		diolag.show();



	}//close fn Dialog

	class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
			pBar.setVisibility(View.VISIBLE);

		}

		protected String doInBackground(Void... urls) {
			String city = sp.getString("city",null);
			// Do some validation here

			try {
				URL url = new URL(API_URL+ "&Key=" + API_KEY + "&q=" + city +"&num_of_days"+5+"&format=json");				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					StringBuilder stringBuilder = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line).append("\n");
					}
					bufferedReader.close();
					return stringBuilder.toString();
				}
				finally{
					urlConnection.disconnect();
				}
			}
			catch(Exception e) {
				Log.e("ERROR", e.getMessage(), e);
				return null;
			}
		}

		protected void onPostExecute(String response) {
			if(response == null) {
				response = "THERE WAS AN ERROR";
			}
			pBar.setVisibility(View.GONE);
			Log.i("INFO", response);
			jsonDate=new ArrayList<>();
			jsonMax=new ArrayList<>();
			jsonmin=new ArrayList<>();
			try {
				JSONObject json =  new JSONObject(response);
				JSONObject data =  json.getJSONObject("data");
				JSONArray weather = data.getJSONArray("weather");

				for (int i=0;i<5;i++) {
					JSONObject data1= weather.getJSONObject(i);
					String date = data1.getString("date");
					int maxtempC = data1.getInt("maxtempC");
					int mintempC = data1.getInt("mintempC");


					String[] out = date.split("-");
					int yr = Integer.parseInt(out[0]);
					int month = Integer.parseInt(out[1]) - 1;
					int day = Integer.parseInt(out[2]);
					Calendar cal        = Calendar.getInstance();
					cal.set(yr, month, day);
					String dayname = null;
					switch (cal.get(Calendar.DAY_OF_WEEK)){

						case Calendar.SATURDAY:   dayname="Sat" ; break;
						case Calendar.SUNDAY:   dayname="Sun"; break;
						case Calendar.MONDAY: dayname="Mon"; break;
						case Calendar.TUESDAY: dayname="Tue" ; break;
						case Calendar.WEDNESDAY: dayname = "Tue"; break;
						case Calendar.THURSDAY: dayname="Thu"; break;
						case Calendar.FRIDAY: dayname="Fri" ;break;

					}


					jsonDate.add(dayname);
					jsonMax.add(maxtempC+" C");
					jsonmin.add(mintempC+" C");
					//jsonDate.add(date+"=>"+maxtempC+"/"+mintempC);
					Log.i("test",date+"/*"+maxtempC+"**"+mintempC);

				}
				JSONArray req=data.getJSONArray("request");
				JSONObject query= req.getJSONObject(0);
				current.setText(query.getString("query"));
				Log.i("test",query.getString("query"));

			} catch (JSONException e) {
				e.printStackTrace();
			}

		try {


			sec.setText(jsonDate.get(1));
			third.setText(jsonDate.get(2));
			forth.setText(jsonDate.get(3));
			fifth.setText(jsonDate.get(4));


			todaymin.setText(jsonmin.get(0));
			todaymax.setText(jsonMax.get(0));

			secmin.setText(jsonmin.get(1));
			secmax.setText(jsonMax.get(1));

			thirdmin.setText(jsonmin.get(2));
			thirdmax.setText(jsonMax.get(2));

			forthmin.setText(jsonmin.get(3));
			forthmax.setText(jsonMax.get(3));

			fifthmin.setText(jsonmin.get(4));
			fifthmax.setText(jsonMax.get(4));

			}catch (Exception e){}

		}
	}

}