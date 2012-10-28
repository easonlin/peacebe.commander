package peacebe.commander;
import org.json.JSONException;
import org.json.JSONObject;
import peacebe.commander.R;
import peacebe.common.IPeaceBeServer;
import peacebe.common.PeaceBeServer;
import peacebe.common.Setting;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CommanderActivity extends Activity {
    /** Called when the activity is first created. */
	private Button btnGrouping;
	private Button btnPhoto;
	private Button btnPhotoTogether;
	private Button btnSingingTogether;
	private Setting mSetting;
	private String mTid;
    //TeamHandler teamHandler;
	private IPeaceBeServer srv =  PeaceBeServer.factoryGet();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
		//WifiManager wifi = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        //teamHandler = new TeamHandler(wifi);
        mSetting = new Setting(this);
        mTid=mSetting.getTeam();
        srv.setTeam(mTid);
        JSONObject state = srv.getTeamState();
        changeViewByState(state);
        btnGrouping = (Button)findViewById(R.id.btnGrouping);
        btnGrouping.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) {
				JSONObject rc = srv.StartGrouping();
				if (rc==null){
					return;
				}
				Intent intent = new Intent(CommanderActivity.this, GroupingActivity.class);
				intent.putExtra("state", "start");
				intent.putExtra("team", mTid);
				startActivity(intent);
			}
        });

        btnGrouping = (Button)findViewById(R.id.btnProfiling);
        btnGrouping.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) {
				srv.StartProfiling();
				Intent intent = new Intent(CommanderActivity.this, ProfilingActivity.class);
				intent.putExtra("state", "start");
				intent.putExtra("team", mTid);
				startActivity(intent);
			}
        });
		
        btnPhoto = (Button)findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Not Implement Yet!!", Toast.LENGTH_SHORT).show();
			}
        });
        
        btnPhotoTogether = (Button)findViewById(R.id.btnPhotoTogether);
        btnPhotoTogether.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "Not Implement Yet!!", Toast.LENGTH_SHORT).show();
			}
        });
        
        btnSingingTogether = (Button)findViewById(R.id.btnSingingTogether);
        btnSingingTogether.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) {
				//teamHandler.invitePlayer("1");		
				Toast.makeText(v.getContext(), "Not Implement Yet!!", Toast.LENGTH_SHORT).show();
			}
        });
        
    }
	private void changeViewByState(JSONObject result) {
		// TODO Auto-generated method stub
		String app = null;
		String state = null;
		if (result == null){
			return;
		}
		try {
			app = result.getString("app");
			state = result.getString("state");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Intent intent = null;
		if ("grouping".equals(app)){
			intent = new Intent(CommanderActivity.this, GroupingActivity.class);
		} else if ("profiling".equals(app)){
			intent = new Intent(CommanderActivity.this, ProfilingActivity.class);
		} else {
			return;
		}
		intent.putExtra("state", state);
		intent.putExtra("team", mTid);
		startActivity(intent);
	}
	@Override
	public void onBackPressed() {
		finish();
	}
	private static final int LEAVE_MENU_ID = Menu.FIRST + 1;
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, LEAVE_MENU_ID, 0, "Team").setShortcut('6', 'l');
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case LEAVE_MENU_ID:
			resetTeam();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public boolean resetTeam() {
		final CharSequence[] items = { "Team1"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a Team");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int item) {
				Toast.makeText(getApplicationContext(), items[item],
						Toast.LENGTH_SHORT).show();
				int team = item + 1;
				mTid = Integer.toString(team);
				srv.setTeam(mTid);
				return;
			}
		});
		builder.show();
		return false;
	}
}