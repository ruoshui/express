package cn.wang.yin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import cn.wang.yin.personal.user.data.UserData;
import cn.wang.yin.utils.ExpressType;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonStringUtils;
import cn.wang.yin.utils.RemoteFactoryUtils;

import com.caucho.hessian.client.HessianProxyFactory;

public class expressDetail extends Activity {
	String num = "";
	private ProgressDialog p_dialog;
	LinearLayout express_list;
	public static final int SUCCESS = 101;
	public static final int FAIL = 102;
	List<String> all = new ArrayList();
	TextView express_nu;
	TextView express_name;
	Button back_up_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.express_detail);
		express_list = (LinearLayout) findViewById(R.id.express_list);
		express_nu = (TextView) findViewById(R.id.express_nu);
		express_name = (TextView) findViewById(R.id.express_name);
		back_up_button = (Button) findViewById(R.id.back_up_button);

		p_dialog = new ProgressDialog(this);
		p_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		p_dialog.setMessage(getResources().getString(R.string.dialog_loadding));
		p_dialog.setTitle(getResources().getString(R.string.dialog_title_wait));
		back_up_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void fresh(Set<ExpressData> datas) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		express_list.removeAllViews();
		for (ExpressData bean : datas) {
			View child = inflater.inflate(R.layout.express_sinagle, null);
			TextView textView1 = (TextView) child
					.findViewById(R.id.express_detail_date);
			TextView textView2 = (TextView) child
					.findViewById(R.id.express_detail_content);
			textView1.setText(""
					+ PersonStringUtils.pareDateToString16(bean.getFtime()));
			textView2.setText("" + bean.getContext().replaceAll(" ", ""));
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
					Set<ExpressData> datas = bean.getExpressDatas();
					// bean.getCom()
					express_name.setText(ExpressType.getMap()
							.get(bean.getCom()));
					express_nu.setText(bean.getNu());

					fresh(datas);
				}
			}
				break;
			case FAIL: {
				AlertDialog dialog = new AlertDialog.Builder(expressDetail.this)
						.setTitle("提示")
						.setMessage("需要网络，您的手机当前网络不可用，请设置您的网络")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
				break;
			}

			super.handleMessage(msg);
		}

	};
	Runnable submitRunnnable = new Runnable() {

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
				Express bean = remot.scanExpress(num);
				// Set<ExpressData> datas = bean.getExpressDatas();
				msg.obj = bean;
			} catch (Exception e) {
				msg.what = FAIL;
				e.printStackTrace();
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
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (p_dialog == null) {
			p_dialog = new ProgressDialog(this);
			p_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			p_dialog.setMessage("载入中……");
			p_dialog.setTitle("请等待");
		}
		if (UserData.getExpress() != null) {
			num = UserData.getExpress().getNu();
			p_dialog.show();
			new Thread(submitRunnnable).start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

}
