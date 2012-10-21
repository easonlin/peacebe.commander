package peacebe.commander;

import org.json.JSONException;
import org.json.JSONObject;

import peacebe.commander.R;
import peacebe.common.IPeaceBeServer;
import peacebe.common.PeaceBeServer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	private IPeaceBeServer srv =  PeaceBeServer.factoryGet();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);
        String guid = "guid";
        JSONObject tidObj = srv.getTeamByGUID(guid);
        String tid = null;
		try {
			tid = tidObj.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        srv.setTeam(tid);
        JSONObject state = srv.getTeamState();
        changeViewByState(state);
        
        
        btnGrouping = (Button)findViewById(R.id.btnGrouping);
        btnGrouping.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) {
				srv.StartGrouping();
				Intent intent = new Intent(CommanderActivity.this, GroupingActivity.class);
				intent.putExtra("state", "start");
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
				Toast.makeText(v.getContext(), "Not Implement Yet!!", Toast.LENGTH_SHORT).show();
			}
        });
        
    }

	private void changeViewByState(JSONObject result) {
		// TODO Auto-generated method stub
		String app = null;
		String state = null;
		try {
			app = result.getString("app");
			state = result.getString("state");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		intent.putExtra("id", "1");
		startActivity(intent);
	}
}