package peacebe.commander;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import peacebe.common.Helper;
import peacebe.common.IPeaceBeServer;
import peacebe.common.PeaceBeServer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ProfilingActivity extends Activity {
	private Handler handler = new Handler();
	private FrameLayout paintFrame;
	private ListView playerList;
	private IPeaceBeServer srv = PeaceBeServer.factoryGet();
	private ProgressBar pgbWaiting;
	private Button nextButton;
	private boolean isUpdateRunnerClosed = false;

	public class ProfilingArrayAdapter extends ArrayAdapter<JSONObject> {
		private final JSONObject [] values;
		/*private final String[] player_names = { "Player 1 (Male)",
				"Player 2 (Male)", "Player 3 (Male)", "Player 4 (Male)",
				"Player 5 (Female)", "Player 6 (Female)", "Player 7 (Female)",
				"Player 8 (Female)" };
		private final String[] player_names = { "M",
				"M", "M", "M",
				"F", "F", "F",
				"F" };*/

		public ProfilingArrayAdapter(Context context, JSONObject [] profiled) {
			super(context, R.layout.profiling_item_simple, profiled);
			this.values = profiled;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.profiling_item_simple,
					parent, false);
			TextView textBoy = (TextView) rowView.findViewById(R.id.profiling_boy);
			ImageView imageOk = (ImageView) rowView.findViewById(R.id.profiling_ok);

			imageOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	String id = (String) v.getTag();
                	srv.sendProfileVerifyOk(id);
                }
            });
			ImageView imageDeny = (ImageView) rowView.findViewById(R.id.profiling_deny);
			imageDeny.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                	String id = (String) v.getTag();
                	srv.sendProfileVerifyDeny(id);
                }
            });
			ImageView imagePhoto = (ImageView) rowView.findViewById(R.id.profiling_photo);
			JSONObject player = values[position];
			String profileState="";
			try {
				textBoy.setText(player.getString("boy"));
				imageOk.setTag(player.getString("id"));
				imageDeny.setTag(player.getString("id"));
				profileState = player.getString("state");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				imagePhoto.setImageBitmap(Helper.getBitmapFromString(player.getString("photo")));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ("w_profiling".equals(profileState)) {
				imageOk.setVisibility(ImageView.INVISIBLE);
				imageDeny.setVisibility(ImageView.VISIBLE);
			} else if ("v_profiling".equals(profileState)) {
				imageOk.setVisibility(ImageView.VISIBLE);
				imageDeny.setVisibility(ImageView.VISIBLE);
			} else{
				imageOk.setVisibility(ImageView.INVISIBLE);
				imageDeny.setVisibility(ImageView.INVISIBLE);
			}
			return rowView;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Bundle bundle = getIntent().getExtras();
		srv.setTeam(bundle.getString("team"));
		paintFrame = (FrameLayout) findViewById(R.id.paintFrame);
		playerList = new ListView(paintFrame.getContext());
		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		JSONObject [] profiled = { };
		ProfilingArrayAdapter profilingAdapter = new ProfilingArrayAdapter(
				paintFrame.getContext(), profiled);

		// Assign adapter to ListView
		playerList.setAdapter(profilingAdapter);
		playerList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				/*
				Boolean[] OKs = { false, false, false, false, false, false,
						false, false };
				OKs[position] = true;
				MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
						paintFrame.getContext(), OKs);
				ListView playerList = (ListView) parent;
				// Assign adapter to ListView
				playerList.setAdapter(adapter);
				*/
			}
		});
		pgbWaiting = (ProgressBar) findViewById(R.id.pgbWaiting);
		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		//ScrollView scrollView = new ScrollView(paintFrame.getContext());
		//scrollView.addView(playerList);
		paintFrame.addView(playerList);
		nextButton.setVisibility(Button.VISIBLE);
		pgbWaiting.setVisibility(ProgressBar.GONE);
		handler.postDelayed(updateTimer, 800);
	}
	@Override
	public void finish(){
		Log.i("RUN","FINISH Profiling");
		srv.StartProfilingFinish();
		handler.removeCallbacks(updateTimer);
		isUpdateRunnerClosed = true;
		super.finish();
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	private int updateDelay=2000;
	private Runnable updateTimer = new Runnable() {
		public void run() {
			if (isUpdateRunnerClosed){
				Log.i("run","isUpdateRunnerClosed, return.");
				return;
			}
			JSONArray profiled = srv.getProfiled();
			if (profiled == null){
				handler.postDelayed(this, updateDelay);
				return;
			}
			int len = profiled.length();
			JSONObject [] arrayProfiled=new JSONObject [len];
			for(int i = 0; i < len; i++){
				try {
					arrayProfiled[i]=profiled.getJSONObject(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					arrayProfiled[i]=new JSONObject();
				}
			}
			ProfilingArrayAdapter adapter = new ProfilingArrayAdapter(
					paintFrame.getContext(), arrayProfiled);
			// Assign adapter to ListView
			playerList.setAdapter(adapter);
			handler.postDelayed(this, updateDelay);
		}
	};
}
