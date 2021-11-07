package com.supermap.onlinedemo;



import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import pub.devrel.easypermissions.EasyPermissions;

public class DemoInterfaceActivity extends Activity implements OnClickListener {

	private Button btnNavigation;
	private Button btnPOIQuery;
	private Button btnGeocoding;
	private Button btnTrafficTransfer;

	/**
	 * 需要申请的权限数组
	 */
	protected String[] needPermissions = {
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.ACCESS_WIFI_STATE,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.CHANGE_WIFI_STATE,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_demo_interface);
		btnNavigation=(Button) findViewById(R.id.btnNavigation);
		btnPOIQuery=(Button) findViewById(R.id.btnPOIQuery);
		btnGeocoding=(Button) findViewById(R.id.btnGeocoding);
		btnTrafficTransfer=(Button) findViewById(R.id.btnTrafficTransfer);
		btnNavigation.setOnClickListener(this);
		btnPOIQuery.setOnClickListener(this);
		btnGeocoding.setOnClickListener(this);
		btnTrafficTransfer.setOnClickListener(this);

		requestPermissions();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNavigation:
			Intent navigation=new Intent(this,NavigationOnlineActivity.class);
			startActivity(navigation);
			break;
			
		case R.id.btnGeocoding:
			Intent geocoding=new Intent(this,GeocodingActivity.class);
			startActivity(geocoding);
			break;
			
		case R.id.btnPOIQuery:
			Intent poiQuery=new Intent(this,POIQueryActivity.class);
			startActivity(poiQuery);
			break;
		case R.id.btnTrafficTransfer:
			Intent coordconvert=new Intent(this,TrafficTransferActivity.class);
			startActivity(coordconvert);
			break;
		default:
			break;
		}
	}

	/**
	 * 检测权限
	 * return true:已经获取权限
	 * return false: 未获取权限，主动请求权限
	 */
	public boolean checkPermissions(String[] permissions) {
		return EasyPermissions.hasPermissions(this, permissions);
	}

	/**
	 * 申请动态权限
	 */
	private void requestPermissions() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return;
		}
		if (!checkPermissions(needPermissions)) {
			EasyPermissions.requestPermissions(
					this,
					"为了应用的正常使用，请允许以下权限。",
					0,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.ACCESS_WIFI_STATE,
					Manifest.permission.ACCESS_NETWORK_STATE,
					Manifest.permission.CHANGE_WIFI_STATE);
			//没有授权，编写申请权限代码
		} else {
			//已经授权，执行操作代码
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// Forward results to EasyPermissions
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

}
