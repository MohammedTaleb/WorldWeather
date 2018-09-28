package com.example.ma.awa;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Mohammed_Aqrabawi on 9/22/2018.
 */

	public class City_search extends AppCompatActivity {
	String[] res=new String[2];

		EditText citySearch;
		String cityData;
		TextView responseView;
		ProgressBar progressBar;
		static final String API_KEY = "2060b34ad27d48e6bf8190341182109";
		static final String API_URL = "http://api.worldweatheronline.com/premium/v1/search.ashx?";

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.addcity);

			responseView = (TextView) findViewById(R.id.responseView);
			citySearch = (EditText) findViewById(R.id.city);



			progressBar = (ProgressBar) findViewById(R.id.progressBar);

			Button search = (Button) findViewById(R.id.Search);
			search.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					cityData=citySearch.getText().toString();
					if(cityData.length()>2){
						res =cityData.split("\\ ");
						if (res.length>1)
							cityData=res[0]+"+"+res[1];
						else
							cityData=res[0];

					new RetrieveFeedTask().execute();
					}
					else Toast.makeText(getApplicationContext(),"Enter a valid city Name",Toast.LENGTH_LONG).show();
				}
			});
		}


	class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			responseView.setText("");
		}

		protected String doInBackground(Void... urls) {

			try {
				URL url = new URL(API_URL + "query=" + cityData +"&num_of_results="+1+"&format=json"+ "&Key=" + API_KEY);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
			progressBar.setVisibility(View.GONE);
			Log.i("INFO2", response);

			try {
				JSONObject json =  new JSONObject(response);
				JSONObject data =  json.getJSONObject("data");
				JSONArray error = data.getJSONArray("error");
				JSONObject msg=error.getJSONObject(0);
				Log.i("error",msg.getString("msg"));
				Toast.makeText(getApplicationContext(),msg.getString("msg"),Toast.LENGTH_LONG).show();

			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {

				JSONObject json =  new JSONObject(response);
				JSONObject search_api =  json.getJSONObject("search_api");
				JSONArray result = search_api.getJSONArray("result");
				JSONObject areaName =result.getJSONObject(0);

				JSONArray msgArea=areaName.getJSONArray("areaName");
				JSONObject msgAreaValue= msgArea.getJSONObject(0);
				JSONArray msgCountry=areaName.getJSONArray("country");
				JSONObject msgCountryValue=msgCountry.optJSONObject(0);

				Log.i("names",msgAreaValue.getString("value")+",  "+msgCountryValue.getString("value"));
				sureDialog(msgAreaValue.getString("value"),msgCountryValue.getString("value"));

			}catch (JSONException e){
			e.printStackTrace();}

		}
	}
	DataBase DB=new DataBase(this);
	public void sureDialog(String x, String y) {

		Log.i("Dialog", "sure Dialog");

		AlertDialog.Builder diolag;
		diolag = new AlertDialog.Builder(this);
		diolag.setTitle("Add!! ");
		diolag.setMessage("are you sure to Add ' "+x+",   "+y+" ' ! ");
		diolag.setIcon(android.R.drawable.ic_dialog_alert);
		diolag.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				DB.insert(citySearch.getText().toString());
				Intent intent=new Intent(getApplicationContext(),WeatherInfo.class);
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

}