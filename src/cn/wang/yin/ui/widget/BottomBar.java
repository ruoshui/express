package cn.wang.yin.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.wang.yin.personal.R;

public class BottomBar extends LinearLayout implements OnClickListener {

	private static final int TAG_0 = 0;
	private static final int TAG_1 = 1;
	private static final int TAG_2 = 2;
	private Context mContext;

	public BottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public BottomBar(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private List<View> itemList;

	private void init() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.bottom_bar, null);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
		Button btnOne = (Button) layout.findViewById(R.id.btn_item_one);
		Button btnTwo = (Button) layout.findViewById(R.id.btn_item_two);
		Button btnFive = (Button) layout.findViewById(R.id.btn_item_five);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnOne.setTag(TAG_0);
		btnTwo.setTag(TAG_1);
		btnFive.setTag(TAG_2);
		itemList = new ArrayList<View>();
		itemList.add(btnOne);
		itemList.add(btnTwo);
		itemList.add(btnFive);
		this.addView(layout);
	}

	@Override
	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		
		setNormalState(lastButton);
		setSelectedState(tag);
	}

	private int lastButton = -1;

	public void setSelectedState(int index) {
		if (index != -1 && onItemChangedListener != null) {
			if (index > itemList.size()) {
				throw new RuntimeException(
						"the value of default bar item can not bigger than string array's length");
			}
			itemList.get(index).setSelected(true);
			onItemChangedListener.onItemChanged(index);
			lastButton = index;
		}
	}

	private void setNormalState(int index) {
		if (index != -1) {
			if (index > itemList.size()) {
				throw new RuntimeException(
						"the value of default bar item can not bigger than string array's length");
			}
			itemList.get(index).setSelected(false);
		}
	}

	public interface OnItemChangedListener {
		public void onItemChanged(int index);
	}

	private OnItemChangedListener onItemChangedListener;

	public void setOnItemChangedListener(
			OnItemChangedListener onItemChangedListener) {
		this.onItemChangedListener = onItemChangedListener;
	}
}
