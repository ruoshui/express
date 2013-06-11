package cn.wang.yin.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.wang.yin.personal.R;
import cn.wang.yin.utils.CollectDebugLogUtil;
import cn.wang.yin.utils.PersonStringUtils;

public class FragmentSetting extends Fragment {
	View about_setting;
	View update_check;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			return null;
		}

		LayoutInflater myInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = myInflater.inflate(R.layout.setting_layout, container,
				false);
		about_setting = layout.findViewById(R.id.about_setting);
		update_check = layout.findViewById(R.id.update_check);
		return layout;
	}

	@Override
	public void onStart() {
		super.onStart();
		about_setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(
						(new Intent().setClass(getActivity(), about.class)),
						102);
			}
		});
		update_check.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				NetworkInfo netWork = PersonStringUtils
						.getActiveNetwork(getActivity());

				if (netWork != null) {
					try {
						SoftwareUpdate version = new SoftwareUpdate(
								getActivity());
						version.update();
					} catch (Exception e) {
						e.printStackTrace();
						CollectDebugLogUtil.saveDebug(e.getMessage(), e
								.getClass().toString(), this.getClass()
								.toString() + "\t" + "SoftwareUpdate--version");
					}
				} else {
					AlertDialog dialog = new AlertDialog.Builder(getActivity())
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

	}

}
