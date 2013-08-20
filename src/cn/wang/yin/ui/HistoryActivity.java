package cn.wang.yin.ui;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.shui.express.scan.hessian.bean.Express;
import cn.wang.yin.personal.R;
import cn.wang.yin.personal.user.data.UserData;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonDbUtils;
import cn.wang.yin.utils.PersonStringUtils;

public class HistoryActivity extends Activity {
	private ListView listView;
	List<Express> lp = new ArrayList();
	public static final int SUCCESS_DATA = 100;
	public static final int EMEPTY_DATA = 102;
	public static final int INTENET_ERROR_DATA = 404;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		PersonDbUtils.init(
				getApplicationContext(),
				getSharedPreferences(PersonConstant.USER_AGENT_INFO,
						Context.MODE_PRIVATE));
		//
		setContentView(R.layout.history_express_list);
		listView = (ListView) findViewById(R.id.listView_expresslist);
		listView.setDividerHeight(0);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setScrollBarStyle(0);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				UserData.setExpress(lp.get(position));
				startActivityForResult((new Intent().setClass(
						getApplicationContext(), expressDetail.class)), 100);
			}
		});

	}

	Runnable readRunnable = new Runnable() {

		@Override
		public void run() {

			Message msg = new Message();
			FinalDb db = FinalDb.create(getApplicationContext());
			lp.clear();
			lp = db.findAll(Express.class);
			if (lp.size() > 0) {
				msg.obj = lp;
				msg.what = SUCCESS_DATA;
			} else {
				msg.what = EMEPTY_DATA;
			}
			hand.sendMessage(msg);

		}
	};
	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS_DATA: {
				if (msg.obj != null) {
					// Set<ExpressData> datas = (Set<ExpressData>) msg.obj;
					List<Express> lb = (List<Express>) msg.obj;
					listView.setAdapter(new TripAdapter(
							getApplicationContext(),
							android.R.layout.simple_list_item_1, lb));
				}
			}
				break;
			case EMEPTY_DATA: {
			}
				break;

			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onStart() {
		super.onStart();
		new Thread(readRunnable).start();
	}

	public void freshExpressData() {

	}

	public class TripAdapter extends ArrayAdapter<Express> {
		List<Express> items;
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 实际的padding的距离与界面上偏移距离的比例

		public void TripAdapter() {

		}

		public TripAdapter(Context context, int textViewResourceId,
				List<Express> items) {

			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.history_express_sinagle, null);
			}
			Express ep = items.get(position);
			TextView textView1 = (TextView) view.findViewById(R.id.epxress_nu);
			TextView textView2 = (TextView) view
					.findViewById(R.id.express_date);
			textView1.setText(ep.getNu());
			textView2.setText(PersonStringUtils.pareDateToString(ep
					.getUpdatetime()));
			return view;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		new Thread(readRunnable).start();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
