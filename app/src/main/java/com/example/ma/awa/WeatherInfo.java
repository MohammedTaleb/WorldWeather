package com.example.ma.awa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import java.io.IOException;


public class WeatherInfo extends AppCompatActivity implements View.OnClickListener{
	SharedPreferences sp;

	DataBase DB=new DataBase(this);

	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_info);

		connectionDialog();


		mWebView= (WebView) findViewById(R.id.webv);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl("https://map.worldweatheronline.com");




		sp=this.getSharedPreferences("com.example.ma.awa", Context.MODE_PRIVATE);
		if (sp.getBoolean("Key",true)){
			DB.insert("Amman");
			DB.insert("London");
			DB.insert("New York");
			DB.insert("Barcelona");
		}
		sp.edit().putBoolean("Key",false).apply(); //once the application started for the first time must change to false







	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.weatherinfo,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.weatherinfo:
				Intent intent= new Intent(this,Weather_Popup.class);
				startActivity(intent);
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {


	}


	public void connectionDialog() {

		Log.i("Dialog", "in Dialog");
		if (!isConnected()) {
			Log.i("Dialog", "is Online");
			AlertDialog.Builder diolag;
			diolag = new AlertDialog.Builder(this);
			diolag.setTitle("Conncation ");
			diolag.setMessage("Please check your Connication ");
			diolag.setIcon(android.R.drawable.ic_dialog_alert);
			diolag.setPositiveButton("Restart", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent=new Intent(WeatherInfo.this,WeatherInfo.class);
					startActivity(intent);
				}
			});
			diolag.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(1);
				}
			});

			diolag.show();

		}

	}//close fn Dialog

	public boolean isConnected() {
		String command = "ping -c 1 google.com";
		try {
			Log.i("Ping",Runtime.getRuntime().exec(command).waitFor()+"");
			return (Runtime.getRuntime().exec(command).waitFor() == 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;

		}
	}




	}









