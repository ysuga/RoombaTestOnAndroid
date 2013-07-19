/*
 * Copyright (C) 2012 SUGAR SWEET ROBOTICS CO. LTD.
 * 
 * This file is provided for public domain.
 */
package com.ssr.experimental;

import jp.co.sec.rtm.Logger4RTC;
import jp.co.sec.rtm.NameServerConnectTask;
import jp.co.sec.rtm.NameServerConnectTask.NameServerConnectListener;
import jp.co.sec.rtm.RTCService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ssr.rtc.RoombaTesterImpl;
import com.ssr.rtc.RoombaTesterProfile;

/**
 * RTMonAndroid
 */
public class RoombaTester extends Activity {
	private static final String TAG = "MyRTC";

	private static final String Pref_NameServerAddress = "NameServerAddress";	
	private static final int MSG_TEXT	= 0x000;
	private static final int MSG_TOAST	= 0x100;

	private Context			context;
	private NameServerConnectTask	nameServerConnectTask = null;
	private ServiceConnection	serviceConnection = null;
	private RTCService		rtcService = null;
	private ToastHandler	mToastHandler;

	private EditText		myEditText;			
	private Button			myStartButton;		
	private Button			myStopButton;		
	private TextView		myInDataView;		
	private String			nameServer;			

	private RoombaTesterImpl rtcImpl;

	private boolean			drawText = false;

	/**
	 * 繧｢繝励Μ逕滓�
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		Logger4RTC.setDebuggable(context);
		Logger4RTC.debug(TAG, "onCreate Start");

		rtcService = null;
		serviceConnection = null;
		setContentView(R.layout.main);
		initToast();

		myEditText = (EditText)findViewById(R.id.editText);
		myEditText.setText(getNameServerAddress(RoombaTesterProfile.DefaultNameServer));

		myStartButton = (Button)findViewById(R.id.startButton);
		myStopButton  = (Button)findViewById(R.id.stopButton);

		myInDataView = (TextView)findViewById(R.id.inDataView);


		myStartButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Logger4RTC.debug(TAG, "startRTC Button Pushed");
				if (nameServerConnectTask == null) {
					connectNameServer();		
				}
				else {
					showToast("RTC Service already started !!", 1);
				}
			}
		});


		myStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Logger4RTC.debug(TAG, "stopRTC Button Pushed");
				if (rtcService != null) {
					rtcService.stopRTC();
					rtcService = null;
					showToast("RTCService destroyRTC", 1);
				}
				releaseService();
			}
		});
	}

	/**
	 * 繧｢繝励Μ襍ｷ蜍�	 */
	@Override
	public void onStart() {
		super.onStart();
		Logger4RTC.debug(TAG, "onStart");
	}

	/**
	 * 繧｢繝励Μ髢句ｧ�	 */
	@Override
	public void onResume() {
		super.onResume();
		drawText = true;
		Logger4RTC.debug(TAG, "onResume");
	}

	/**
	 * 繧｢繝励Μ蛛懈ｭ｢
	 */
	@Override
	public void onPause() {
		drawText = false;
		super.onPause();
		Logger4RTC.debug(TAG, "onPause");
	}

	/**
	 * 繧｢繝励Μ遐ｴ譽�	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger4RTC.debug(TAG, "onDestroy");
		if (rtcService != null) {
			rtcService.stopRTC();
			rtcService = null;
			showToast("RTCService destroyRTC", 1);
		}
		releaseService();
	}

	/**
	 * NameServer縺ｸ縺ｮ謗･邯�	 */
	private void connectNameServer() {
		Logger4RTC.debug(TAG, "connectNameServer");
		SpannableStringBuilder sb = (SpannableStringBuilder)myEditText.getText();
		nameServer = sb.toString();
		saveNameServerAddress(nameServer);	

		nameServerConnectTask = new NameServerConnectTask(this);
		nameServerConnectTask.setListener(new NameServerConnectListenerImpl());	
		nameServerConnectTask.setIpAddress(nameServer);
		nameServerConnectTask.setRetryCount(5);
		nameServerConnectTask.execute();	
	}

	/**
	 * NameServer謗･邯壼ｮ御ｺ�Μ繧ｹ繝翫�
	 */
	private class NameServerConnectListenerImpl implements NameServerConnectListener {
		/**
		 * RTC繧ｵ繝ｼ繝薙せ繧帝幕蟋�		 */
		public void onConnected() {
			Intent intent = new Intent(RoombaTester.this, RTCService.class);
			startService(intent);
			serviceConnection = new RtcServiceConnection();
			bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		}

		/**
		 * 謗･邯壼､ｱ謨�		 */
		public void onConnectFailed() {
		}

		/**
		 * 謗･邯壹く繝｣繝ｳ繧ｻ繝ｫ
		 */
		public void onConnectCanceled() {
		}
	}

	/**
	 * RTCService謗･邯壼�逅�	 */
	private class RtcServiceConnection implements ServiceConnection {
		/**
		 * RTCService bind螳御ｺ�		 */
		public void onServiceConnected(ComponentName className, IBinder service) {
			Logger4RTC.debug(TAG, "RTCService binded");
			rtcService = ((RTCService.RTCServiceBinder) service).getService();
			
			rtcService.setProfiles(
					RoombaTesterProfile.DefaultNameServer,	RoombaTesterProfile.Name,
					RoombaTesterProfile.ImplementationId,	RoombaTesterProfile.Type,
					RoombaTesterProfile.Description,		RoombaTesterProfile.Version,
					RoombaTesterProfile.Vendor,				RoombaTesterProfile.Category,
					String.valueOf(RoombaTesterProfile.execute_rate));

			//rtcService.setLongConfig(RTMonAndroidProfile.ConfigName1, 0);
			rtcImpl = new RoombaTesterImpl(RoombaTester.this, rtcService);

			SpannableStringBuilder sb = (SpannableStringBuilder)myEditText.getText();	
			rtcService.startRTC(sb.toString(), context.getPackageName());		// START RTC
			showToast("start RTC : " + nameServer, 1);
		}

		/**
		 * RTCService邨ゆｺ��逅�		 */
		public void onServiceDisconnected(ComponentName className) {
			Logger4RTC.debug(TAG, "onServiceDisconnected");
		}
	}

	private void releaseService() {
		if (nameServerConnectTask != null) {
			nameServerConnectTask.cancel(true);
			nameServerConnectTask = null;
		}
		if (serviceConnection != null) {
			unbindService(serviceConnection);
			serviceConnection = null;
			Intent intent = new Intent(RoombaTester.this, RTCService.class);
			stopService(intent);
		}

		if (rtcImpl != null){
			rtcImpl = null;
		}
	}

	public void textDraw(String str) {
		Logger4RTC.debug(TAG, "receiverDraw : " + str);
		mToastHandler.sendMessage(mToastHandler.obtainMessage(MSG_TEXT, str));
	}

	
	private void initToast(){
		mToastHandler = new ToastHandler();
	}

	public void showToast(String msg){
		mToastHandler.sendMessage(mToastHandler.obtainMessage(MSG_TOAST, msg));
	}


	public void showToast(String msg, int mode){
		int lng = Toast.LENGTH_LONG;
		if (0 == mode) lng = Toast.LENGTH_SHORT;
		mToastHandler.sendMessage(mToastHandler.obtainMessage(MSG_TOAST+lng, msg));
	}

	private class ToastHandler extends Handler{
		public void handleMessage(Message msg) {
			int para = msg.what;
			if (MSG_TOAST > para){
				if (drawText){
					myInDataView.setText(msg.obj.toString());	// TEXT謠冗判
				}
			}
			else{
				Toast toast = Toast.makeText(getBaseContext(), msg.obj.toString(), msg.what);
				toast.show();
			}
		}
	}


	private String getNameServerAddress(String default_name_server){
		String nameServer = default_name_server;
		try{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			nameServer = sp.getString(Pref_NameServerAddress, nameServer);
		}
		catch (Exception e) {
		}
		return nameServer;
	}

	private boolean saveNameServerAddress(String nameServerAddress){
		try{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(Pref_NameServerAddress, nameServerAddress);
			editor.commit();
		}
		catch (Exception e) {
			Logger4RTC.error(TAG, "error Preference Write");
			return false;
		}
		return true;
	}
}
