package cn.wang.yin.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import cn.wang.yin.personal.R;

public class TabMain extends TabActivity {
	RadioGroup radioGroup;
	ImageView img;
	TabHost tabHost;
	int startLeft;

	public static final String SEARCH_BTN = "SEARCH";
	public static final String HISTORY_BTN = "HISTORY";
	public static final String SETTING_BTN = "SETTING";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintabs);

		tabHost = getTabHost();

		radioGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);

		// -------------------------------------------------//
		// InputSearchActivity
		// HistoryActivity
		// SettingActivity
		tabHost.addTab(tabHost.newTabSpec(SEARCH_BTN).setIndicator(SEARCH_BTN)
				.setContent(new Intent(this, InputSearchActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(HISTORY_BTN)
				.setIndicator(HISTORY_BTN)
				.setContent(new Intent(this, HistoryActivity.class)));
		tabHost.addTab(tabHost.newTabSpec(SETTING_BTN)
				.setIndicator(SETTING_BTN)
				.setContent(new Intent(this, SettingActivity.class)));

		tabHost.setCurrentTab(0);
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// SEARCH_BTN = "SEARCH";
			// HISTORY_BTN = "HISTORY";
			// SETTING_BTN = "SETTING";
			switch (checkedId) {

			case R.id.radio_button0:
				tabHost.setCurrentTabByTag(SEARCH_BTN);
				// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				// R.layout.mycartitle);
				// mycar_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.travellv), null,
				// null);
				// travel_btn
				// .setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.xing),
				// null, null);
				// center_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.center), null,
				// null);
				// set_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.set), null, null);
				// pageRefresh = (ImageView)
				// findViewById(R.id.my_car_imageview_title);
				break;
			case R.id.radio_button1:
				tabHost.setCurrentTabByTag(HISTORY_BTN);
				// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				// R.layout.trivelititle);
				// mycar_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.travel), null,
				// null);
				// travel_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.xinglv), null,
				// null);
				// center_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.center), null,
				// null);
				// set_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.set), null, null);
				break;
			case R.id.radio_button2:
				tabHost.setCurrentTabByTag(SETTING_BTN);
				// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				// R.layout.individualtitle);
				// mycar_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.travel), null,
				// null);
				// travel_btn
				// .setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.xing),
				// null, null);
				// center_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.centerlv), null,
				// null);
				// set_btn.setCompoundDrawablesWithIntrinsicBounds(null,
				// getResources().getDrawable(R.drawable.set), null, null);
				break;
			default:
				break;
			}
		}
	};
}
