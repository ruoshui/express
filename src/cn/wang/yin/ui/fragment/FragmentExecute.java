package cn.wang.yin.ui.fragment;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.tsz.afinal.FinalDb;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cn.wang.yin.utils.PersonDbUtils;
import cn.wang.yin.utils.PersonStringUtils;
import cn.wang.yin.utils.RemoteFactoryUtils;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * ��Ҫʹ�ò��������Ĺ�����������ʹ��getActivity()�滻context���� ������Ļ����ת��ʱ����׳��쳣�� Caused by:
 * java.lang.InstantiationException: can't instantiate class
 * com.michael.fragment.FragmentExecute; no empty constructor
 * 
 * @see http
 *      ://stackoverflow.com/questions/7016632/unable-to-instantiate-fragment
 * */
public class FragmentExecute extends Fragment {
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

	public FragmentExecute() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null) {
			return null;
		}

		LayoutInflater myInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = myInflater.inflate(R.layout.express_search, container,
				false);

		// layout.setContentView(R.layout.express);
		editText1 = (EditText) layout.findViewById(R.id.express_nu_input);
		button1 = (Button) layout.findViewById(R.id.express_search_button);
		express_list = (LinearLayout) layout.findViewById(R.id.express_list);

		express_info = layout.findViewById(R.id.express_info);
		express_nu = (TextView) layout.findViewById(R.id.express_nu);
		express_name = (TextView) layout.findViewById(R.id.express_name);

		p_dialog = new ProgressDialog(getActivity());
		p_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		p_dialog.setMessage("�����С���");
		p_dialog.setTitle("��ȴ�");

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

		return layout;
	}

	public void fresh(Set<ExpressData> datas) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
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
				AlertDialog dialog = new AlertDialog.Builder(getActivity())
						.setTitle("��ʾ")
						.setMessage("��ʱ�������ԣ�")
						.setPositiveButton("ȷ��",
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

	Runnable saveRunnable = new Runnable() {

		@Override
		public void run() {
			if (tmp != null) {
				Log.e("save", "��ʼ");
				FinalDb db = FinalDb.create(getActivity());
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
				// ��ѯ������͵ĵ�ַ
				// http://www.kuaidi100.com/autonumber/auto?num=5045205409800

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
}
