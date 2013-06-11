package cn.wang.yin.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.wang.yin.personal.R;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class FragmentTabsSearch extends Fragment {
	private static final String[] CONTENT = new String[] { "…®¬Î≤È—Ø", " ‰»Î≤È—Ø" };
	private static final int[] ICONS = new int[] {
			R.drawable.bottom_bar_icon_set_normal,
			R.drawable.bottom_bar_icon_search_normal };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			return null;
		}
		// setContentView(R.layout.simple_tabs);

		LayoutInflater myInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater
				.inflate(R.layout.simple_tabs, container, false);

		FragmentPagerAdapter adapter = new GoogleMusicAdapter(
				FragmentMain.fm);

		ViewPager pager = (ViewPager) layout.findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) layout
				.findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		return layout;
	}

	class GoogleMusicAdapter extends FragmentPagerAdapter implements
			IconPagerAdapter {
		public GoogleMusicAdapter(){
			super(getFragmentManager());
		}
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment detail = null;
			if (position == 0) {
				detail = new FragmentExpress();

			} else if (position == 1) {
				detail = new FragmentSetting();

			}

			return detail;// TestFragment.newInstance(CONTENT[position %
							// CONTENT.length]);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getIconResId(int index) {
			return ICONS[index];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}
}
