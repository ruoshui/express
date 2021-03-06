package cn.wang.yin.ui;

import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cn.wang.yin.personal.R;
import cn.wang.yin.personal.service.HandlerService;
import cn.wang.yin.ui.fragment.FragmentExecute;
import cn.wang.yin.ui.widget.BottomBar;
import cn.wang.yin.ui.widget.BottomBar.OnItemChangedListener;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonDbUtils;

/**
 * This demo shows how to use FragmentActivity to build the frame of a common
 * application. To replace the deprecated class such as TabActivity,
 * ActivityGroup,and so on.
 * 
 * 这个demo展示了如何使用FragmentActivity来构建应用程序的框架
 * 可以使用这个来替代原来的TabActivity，ActivityGroup等等
 * 
 * @author MichaelYe
 * @since 2012-9-6
 * @see http://developer.android.com/reference/android/app/Fragment.html
 * @see http://developer.android.com/training/basics/fragments/index.html
 * @see http://developer.android.com/guide/components/fragments.html
 * */
public class FragmentMain extends FragmentActivity implements
		OnBackStackChangedListener {
	Fragment myexprss;
	Fragment SaveImage;
	Fragment Compass;
	Fragment historyExpress;
	Fragment setting;

	public static FragmentManager fm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (!PersonDbUtils.getValue(PersonConstant.USER_FIRST_OPEN, false)) {
			startActivityForResult((new Intent().setClass(FragmentMain.this,
					GuideViewActivity.class)),
					PersonConstant.ETONG_RESULTCODE_INDEX);

		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_main);
		
		Intent intent = new Intent(getApplicationContext(),
				HandlerService.class);
		startService(intent);
		
		
		final BottomBar bottomBar = (BottomBar) findViewById(R.id.ll_bottom_bar);
		bottomBar.setOnItemChangedListener(new OnItemChangedListener() {

			@Override
			public void onItemChanged(int index) {

				showDetails(index);
			}
		});
		bottomBar.setSelectedState(0);
		myexprss = new FragmentExecute();
		historyExpress = new FragmentHistoryExpress();

		setting = new FragmentSetting();
		// bottomBar.hideIndicate();//这个代码原来控制红色小图标的可见性
		// bottomBar.showIndicate(12);
		fm = getSupportFragmentManager();

	}

	private void showDetails(int index) {
		Fragment details = (Fragment) getSupportFragmentManager()
				.findFragmentById(R.id.details);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.beginTransaction();
		switch (index) {
		case 0:
			details = new FragmentExecute();
			break;
		case 1:
			details = historyExpress;
			break;
		case 2:
			details = setting;
			break;
		}

		ft.replace(R.id.details, details);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// ft.addToBackStack(index);// 这行代码可以返回之前的操作（横屏的情况下，即两边都显示的情况下）
		ft.commit();
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) { // super.onResume(); //
	 * super.onStop(); finish(); return true; } else if (keyCode ==
	 * KeyEvent.KEYCODE_MENU) {
	 * 
	 * }
	 * 
	 * return super.onKeyDown(keyCode, event); }
	 */

	public static void launch(Context c) {
		Intent intent = new Intent(c, FragmentMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	@Override
	public void onBackStackChanged() {

	}

}
