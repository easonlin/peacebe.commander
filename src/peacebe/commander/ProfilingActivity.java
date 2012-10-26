package peacebe.commander;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ScrollView;
import android.widget.TextView;

public class ProfilingActivity extends Activity {
	private Handler handler = new Handler();
	private FrameLayout paintFrame;
	private ListView playerList;
	private IPeaceBeServer srv = PeaceBeServer.factoryGet();
	private ProgressBar pgbWaiting;
	private Button nextButton;

	public class MySimpleArrayAdapter extends ArrayAdapter<Boolean> {
		private final Context context;
		private final Boolean[] values;
		private final String[] player_names = { "Player 1 (Male)",
				"Player 2 (Male)", "Player 3 (Male)", "Player 4 (Male)",
				"Player 5 (Female)", "Player 6 (Female)", "Player 7 (Female)",
				"Player 8 (Female)" };

		public MySimpleArrayAdapter(Context context, Boolean[] values) {
			super(context, R.layout.profiling_item_simple, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.profiling_item_simple,
					parent, false);
			TextView textView = (TextView) rowView
					.findViewById(R.id.profiling_boy);
			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.profiling_ok);
			textView.setText(player_names[position]);
			// Change the icon for Windows and iPhone
			if (values[position]) {
				imageView.setVisibility(ImageView.VISIBLE);
			} else {
				imageView.setVisibility(ImageView.INVISIBLE);
			}
			return rowView;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		paintFrame = (FrameLayout) findViewById(R.id.paintFrame);
		playerList = new ListView(paintFrame.getContext());

		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		Boolean[] OKs = { false, false, false, false, false, false, false,
				false };
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
				paintFrame.getContext(), OKs);

		// Assign adapter to ListView
		playerList.setAdapter(adapter);
		playerList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Boolean[] OKs = { false, false, false, false, false, false,
						false, false };
				OKs[position] = true;
				MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
						paintFrame.getContext(), OKs);
				ListView playerList = (ListView) parent;
				// Assign adapter to ListView
				playerList.setAdapter(adapter);

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
		handler.postDelayed(updateTimer, 200);
	}
	@Override
	public void finish(){
		Log.i("RUN","FINISH Profiling");
		srv.StartProfilingFinish();
		handler.removeCallbacks(updateTimer);
		super.finish();
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	private Runnable updateTimer = new Runnable() {
		public void run() {
			Boolean[] OKs = { false, false, false, false, false, false, false,
					false };
			JSONArray profiled = srv.getProfiled();
			for (int location = 0; location < 8; location++) {
				JSONObject player;
				String profileState = null;
				try {
					player = profiled.getJSONObject(location);
					profileState = player.getString("state");
					Log.i("paint", "i " + location + ",state " + profileState);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				// txt[i].setVisibility(TextView.GONE);
				if ("w_profiling".equals(profileState)) {
					OKs[location] = true;
				}
			}
			MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
					paintFrame.getContext(), OKs);
			// Assign adapter to ListView
			playerList.setAdapter(adapter);
			handler.postDelayed(this, 200);
		}
	};
}
