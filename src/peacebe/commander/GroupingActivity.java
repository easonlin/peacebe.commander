package peacebe.commander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import peacebe.commander.R;
import peacebe.common.IPeaceBeServer;
import peacebe.common.PeaceBeServer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

public class GroupingActivity extends Activity {
	private TableLayout tabGrouping;
	private TextView txtGrouping;
	private Button btnNext;
	private ImageView[] imgOK = new ImageView[8];
	private TextView[] txt = new TextView[8];
	private Handler handler = new Handler();
	private ProgressBar pgbLoading;
	private IPeaceBeServer srv = PeaceBeServer.factoryGet();
	private int mClientState = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grouping);

		tabGrouping = (TableLayout) findViewById(R.id.tabGrouping);
		txtGrouping = (TextView) findViewById(R.id.labGrouping);
		pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);

		imgOK[0] = (ImageView) findViewById(R.id.imgOk00);
		imgOK[1] = (ImageView) findViewById(R.id.imgOk10);
		imgOK[2] = (ImageView) findViewById(R.id.imgOk20);
		imgOK[3] = (ImageView) findViewById(R.id.imgOk30);
		imgOK[4] = (ImageView) findViewById(R.id.imgOk01);
		imgOK[5] = (ImageView) findViewById(R.id.imgOk11);
		imgOK[6] = (ImageView) findViewById(R.id.imgOk21);
		imgOK[7] = (ImageView) findViewById(R.id.imgOk31);

		txt[0] = (TextView) findViewById(R.id.txt00);
		txt[1] = (TextView) findViewById(R.id.txt10);
		txt[2] = (TextView) findViewById(R.id.txt20);
		txt[3] = (TextView) findViewById(R.id.txt30);
		txt[4] = (TextView) findViewById(R.id.txt01);
		txt[5] = (TextView) findViewById(R.id.txt11);
		txt[6] = (TextView) findViewById(R.id.txt21);
		txt[7] = (TextView) findViewById(R.id.txt31);

		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mClientState == 0) {
					srv.StartVote();
					mClientState = 1;
				} else if (mClientState == 1) {
					srv.StartResult();
					mClientState = 2;
				} else {
					finish();
				}
				setViewByState(mClientState);
			}
		});
		Bundle bundle = getIntent().getExtras();
		String state = bundle.getString("state");
		if ("start".equals(state)) {
			mClientState = 0;
		} else if ("vote".equals(state)) {
			mClientState = 1;
		} else if ("result".equals(state)) {
			mClientState = 2;
		}
		setViewByState(mClientState);
		handler.postDelayed(updateTimer, 200);
	}
	@Override
	public void finish(){
		Log.i("RUN","FINISH");
		srv.StartFinish();
		handler.removeCallbacks(updateTimer);
		super.finish();
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	private void setViewByState(int state) {
		// TODO Auto-generated method stub
		if (state == 1) {
			txtGrouping.setText(getString(R.string.labGroupingWaitVote));
			toVote();
		} else if (state == 2) {
			txtGrouping.setText(getString(R.string.labGroupingResult));
			toResult();
			pgbLoading.setVisibility(ProgressBar.VISIBLE);
			// handler.removeCallbacks(updateTimer);
			// handler.postDelayed(updateTimer, 200);
		} else if (state == 0) {
			toPaint();
			pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);
			// handler.removeCallbacks(updateTimer);
			// handler.postDelayed(updateTimer, 200);
		}
	}

	boolean isVoteResultShow = false;

	private Runnable updateTimer = new Runnable() {
		public void run() {
			switch (mClientState) {
			case 0:
				JSONArray painted = srv.getPainted();
				LoadIsPaint(painted);
				break;
			case 1:
				JSONArray voted = srv.getVoted();
				LoadVote(voted);
				break;
			case 2:
			default:
				JSONArray totalResult = srv.getTotalResult();
				if (!isVoteResultShow) {
					pgbLoading.setVisibility(ProgressBar.INVISIBLE);
					LoadResult(totalResult);
					isVoteResultShow = true;
				}
				break;
			}

			handler.postDelayed(this, 200);
		}
	};
	public ArrayList<JSONObject> SimpleSort(JSONArray jsonArray){
		ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
		if (jsonArray == null) {
			return arrayList;
		}
		int len = jsonArray.length();
		for (int i = 0; i < len; i++) {
			try {
				arrayList.add(jsonArray.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		Collections.sort(arrayList, new Comparator<JSONObject>() {
			public int compare(JSONObject lhs, JSONObject rhs) {
				String lid;
				String rid;
				String lgroup;
				String rgroup;
				try {
					lid = lhs.getString("id");
					lgroup = lhs.getString("boy");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
				try {
					rid = rhs.getString("id");
					rgroup = lhs.getString("boy");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 1;
				}
				int result = lgroup.compareTo(rgroup);
				if (result == 0){
					result = lid.compareTo(rid);
				}
				return result;
			}
		});
		return arrayList;
	}
	public void LoadIsPaint(JSONArray jsonPainted){
		// set table to is paint mode
		tabGrouping.setStretchAllColumns(false);
		tabGrouping.setColumnStretchable(0, true);
		tabGrouping.setColumnStretchable(1, true);
		ArrayList<JSONObject> painted = SimpleSort(jsonPainted);
		int len = painted.size();
		for (int location = 0; location < len; location++) {
			JSONObject player;
			String paintState = null;
			try {
				player = painted.get(location);
				paintState = player.getString("state");
				Log.i("paint", "i " + location + "state" + paintState);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			// txt[i].setVisibility(TextView.GONE);
			if ("w_painting".equals(paintState)) {
				imgOK[location].setVisibility(ImageView.VISIBLE);
			} else {
				imgOK[location].setVisibility(ImageView.INVISIBLE);
			}
		}
	}

	public void toVote() {
		for (int i = 0; i < 8; i++) {
			imgOK[i].setVisibility(TextView.GONE);
			txt[i].setVisibility(TextView.INVISIBLE);
		}
	}

	public void toPaint() {
		for (int i = 0; i < 8; i++) {
			imgOK[i].setVisibility(TextView.INVISIBLE);
			txt[i].setVisibility(TextView.GONE);
		}
	}

	public void toResult() {
		for (int i = 0; i < 8; i++) {
			imgOK[i].setVisibility(TextView.GONE);
			txt[i].setVisibility(TextView.INVISIBLE);
		}
	}

	public void LoadVote(JSONArray jsonVoted) {
		// set table to is vote mode
		tabGrouping.setStretchAllColumns(false);
		tabGrouping.setColumnStretchable(2, true);
		tabGrouping.setColumnStretchable(3, true);
		ArrayList<JSONObject> totalResult = SimpleSort(jsonVoted);
		int len = totalResult.size();
		for (int location = 0; location < len; location++) {
			JSONObject player;
			String voteResult = null;
			try {
				player = totalResult.get(location);
				voteResult = player.getString("vote");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.i("VOTE",location + " still not vote");
				continue;
			}
			Log.i("paint", "i " + location + ", voteResult" + voteResult);
			// imgOK[i].setVisibility(TextView.GONE);

			if (voteResult!=null) {
				txt[location].setText(voteResult);
				txt[location].setVisibility(TextView.VISIBLE);
			} else {
				txt[location].setVisibility(TextView.INVISIBLE);
			}
		}
	}

	public void LoadResult(JSONArray jsonTotalResult) {
		// set table to is vote mode
		tabGrouping.setStretchAllColumns(false);
		tabGrouping.setColumnStretchable(2, true);
		tabGrouping.setColumnStretchable(3, true);
		ArrayList<JSONObject> totalResult = SimpleSort(jsonTotalResult);
		int len = totalResult.size();
		for (int location = 0; location < len; location++) {

			JSONObject player;
			String voteResult = "";
			try {
				player = totalResult.get(location);
				voteResult = player.getString("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// imgOK[i].setVisibility(TextView.GONE);
			txt[location].setText(voteResult);
			txt[location].setVisibility(TextView.VISIBLE);
		}
	}
}
