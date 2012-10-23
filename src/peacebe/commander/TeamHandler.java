package peacebe.commander;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class TeamHandler {
	private HandlerThread mTaskThread;
	private Handler mHandler;
	private DatagramSocket mSocket;
	private InetAddress mAddress;
	public TeamHandler(WifiManager wifi){
		mTaskThread = new HandlerThread("task");
		mTaskThread.start();
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    try {
			mAddress = InetAddress.getByAddress(quads);
			mSocket = new DatagramSocket();
			mSocket.setBroadcast(true);
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	mHandler = new Handler(mTaskThread.getLooper());
	}
	private String mId="1";
	private Runnable mainTimer = new Runnable() {
		public void run() {
			Log.i("UDP","sendPacket");
			try {
				sendPacket(mId+":1024:");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private int mDiscoveryPort = 11233;
	public void sendPacket(String data) throws IOException{
		DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), mAddress, mDiscoveryPort);
		mSocket.send(packet);
	}
	public void invitePlayer(String id){
		mId=id;
		mHandler.post(mainTimer);
	}
}
