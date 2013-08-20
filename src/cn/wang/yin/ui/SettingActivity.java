package cn.wang.yin.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import cn.wang.yin.personal.R;
import cn.wang.yin.utils.CollectDebugLogUtil;
import cn.wang.yin.utils.PersonStringUtils;

public class SettingActivity extends Activity {
	View about_setting;
	View update_check;
	View objection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		about_setting = findViewById(R.id.about_setting);
		update_check = findViewById(R.id.update_check);
		objection = findViewById(R.id.objection);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		about_setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult((new Intent().setClass(
						SettingActivity.this, about.class)), 102);
			}
		});
		update_check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				NetworkInfo netWork = PersonStringUtils
						.getActiveNetwork(getApplicationContext());

				if (netWork != null) {
					try {
						SoftwareUpdate version = new SoftwareUpdate(
								SettingActivity.this);
						version.update();
					} catch (Exception e) {
						e.printStackTrace();
						CollectDebugLogUtil.saveDebug(e.getMessage(), e
								.getClass().toString(), this.getClass()
								.toString() + "\t" + "SoftwareUpdate--version");
					}
				} else {
					AlertDialog dialog = new AlertDialog.Builder(
							getApplicationContext())
							.setTitle("提示")
							.setMessage("您的手机当前网络不可用，请设置您的网络")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					dialog.show();
				}
			}
		});

		objection.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult((new Intent().setClass(
						SettingActivity.this, about.class)), 102);
			}
		});

	}
}
