package cn.wang.yin.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.wang.yin.personal.R;

public class fragmentPaper extends Fragment {
	ViewPager viewPager;
	// 每个页面的Fragment
	List<Fragment> fragments = new ArrayList<Fragment>();
	// 每个Fragment对应的title
	List<String> fragmentTtile = new ArrayList<String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			return null;
		}
		View layout = inflater.inflate(R.layout.fragment_pager, container,
				false);
		viewPager = (ViewPager) layout.findViewById(R.id.viewPager);
		viewPager.clearDisappearingChildren();
		fragments.add(new FragmentSetting());
		fragments.add(new FragmentExpress());
		fragmentTtile.add("常联系的人");
		fragmentTtile.add("陌生人");
		viewPager.setAdapter(new MyFragmentAdapter(getFragmentManager(), fragments,
				fragmentTtile));
		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
	
	}

	class MyFragmentAdapter extends FragmentPagerAdapter {
		List<Fragment> frags;
		List<String> fragTitles;

		public MyFragmentAdapter(FragmentManager fm, List<Fragment> frag,
				List<String> title) {
			super(fm);
			this.frags = frag;
			this.fragTitles = title;
		}

		@Override
		public Fragment getItem(int arg0) {
			return frags.get(arg0);
		}

		@Override
		public int getCount() {
			return frags.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragTitles.get(position);
		}
	}
}
