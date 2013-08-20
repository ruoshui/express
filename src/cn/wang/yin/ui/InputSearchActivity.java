package cn.wang.yin.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.tsz.afinal.FinalDb;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.shui.express.scan.hessian.bean.Express;
import cn.shui.express.scan.hessian.bean.ExpressData;
import cn.wang.yin.hessian.api.Remot;
import cn.wang.yin.personal.R;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonStringUtils;
import cn.wang.yin.utils.RemoteFactoryUtils;

import com.caucho.hessian.client.HessianProxyFactory;

public class InputSearchActivity extends Activity {
	EditText editText1;
	Button button1;
	String num = "";
	View express_info;
	TextView express_nu;
	TextView express_name;

	private ProgressDialog p_dialog;
	LinearLayout express_list;
	public static final int SUCCESS = 101;
	public static final int FAIL = 102;
	private static Express tmp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.express_search);

		editText1 = (EditText) findViewById(R.id.express_nu_input);
		button1 = (Button) findViewById(R.id.express_search_button);
		express_list = (LinearLayout) findViewById(R.id.express_list);

		express_info = findViewById(R.id.express_info);
		express_nu = (TextView) findViewById(R.id.express_nu);
		express_name = (TextView) findViewById(R.id.express_name);

		p_dialog = new ProgressDialog(this);
		p_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		p_dialog.setMessage("载入中……");
		p_dialog.setTitle("请等待");

		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String express_num = editText1.getText().toString();
				if (StringUtils.isNotBlank(express_num)) {
					num = express_num;
					p_dialog.show();
					new Thread(submitRunnable).start();
				}
			}
		});
	}

	public void fresh(Set<ExpressData> datas) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		express_list.removeAllViews();
		for (ExpressData bean : datas) {
			View child = inflater.inflate(R.layout.express_sinagle, null);
			TextView textView1 = (TextView) child
					.findViewById(R.id.express_detail_date);
			ImageView imageView1 = (ImageView) child
					.findViewById(R.id.imageView1);
			TextView textView2 = (TextView) child
					.findViewById(R.id.express_detail_content);
			textView1.setText(""
					+ PersonStringUtils.pareDateToString(bean.getFtime()));
			textView2
					.setText("" + bean.getContext().trim().replaceAll(" ", ""));
			express_list.addView(child);
		}

	}

	Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (p_dialog != null) {
				p_dialog.dismiss();
			}
			switch (msg.what) {
			case SUCCESS: {
				if (msg.obj != null) {
					Express bean = (Express) msg.obj;
					tmp = bean;
					express_info.setVisibility(View.VISIBLE);
					express_name.setText(bean.getExpressname());
					express_nu.setText(bean.getNu());

					fresh(bean.getExpressDatas());
					new Thread(saveRunnable).start();
				}
			}
				break;
			case FAIL: {
				AlertDialog dialog = new AlertDialog.Builder(
						getApplicationContext())
						.setTitle("提示")
						.setMessage("超时！请重试！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										new Thread(saveRunnable).start();
									}
								}).create();
				dialog.show();
			}
				break;
			}

			super.handleMessage(msg);
		}

	};

	Runnable saveRunnable = new Runnable() {

		@Override
		public void run() {
			if (tmp != null) {
				Log.e("save", "开始");
				FinalDb db = FinalDb.create(getApplicationContext());
				List<Express> l = db.findAllByWhere(Express.class,
						"nu=" + tmp.getNu());
				try {

					if (l != null && l.size() > 0) {
						Express bean = l.get(0);
						db.update(tmp, "id=" + bean.getId());
						LinkedHashSet lhs = (LinkedHashSet) tmp
								.getExpressDatas();
						Iterator it = lhs.iterator();
						while (it.hasNext()) {
							ExpressData data = (ExpressData) it.next();
							db.save(data);
						}

					} else {
						db.save(tmp);
						LinkedHashSet lhs = (LinkedHashSet) tmp
								.getExpressDatas();
						Iterator it = lhs.iterator();
						while (it.hasNext()) {
							ExpressData data = (ExpressData) it.next();
							db.save(data);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}

			}
		}
	};
	Runnable submitRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message msg = new Message();
			msg.what = SUCCESS;
			Remot remot = null;
			try {
				// remot = RemoteFactoryUtils.getReport();
				HessianProxyFactory factory = RemoteFactoryUtils.getFactory();
				remot = factory.create(Remot.class, PersonConstant.REMOTE_URL);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msg.what = FAIL;
				e.printStackTrace();
			}
			try {
				// 查询快递类型的地址
				// http://www.kuaidi100.com/autonumber/auto?num=5045205409800

				Express bean = remot.scanExpress(num);
				// Set<ExpressData> datas = bean.getExpressDatas();
				msg.obj = bean;
			} catch (Exception e) {
				msg.what = FAIL;
				e.printStackTrace();
			}finally{
				if(msg.obj==null){
					Express bean=new Express ();
					bean.setNu(num);
					bean.setUpdatetime(new Date());
					FinalDb db = FinalDb.create(getApplicationContext());
					db.save(bean);
				}
				
			}
			hand.sendMessage(msg);
		}

	};

	Runnable scanrunnable = new Runnable() {
		@Override
		public void run() {
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
