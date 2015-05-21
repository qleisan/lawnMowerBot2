package net.mitchtech.adb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import net.mitchtech.adb.Lawn3_client.R;

//import org.microbridge.server.Server;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class Lawn3clientActivity extends Activity {
	private final String TAG = Lawn3clientActivity.class.getSimpleName();
	
	private final byte PIN_OFF = 0x00;
	private final byte PIN_ON = 0x01;
	
	private final byte BUTTON_LED = 0x03;
	private final byte BUTTON_FWD2 = 0x04;
	private final byte SEEKBAR1 = 0x05;
	private final byte SEEKBAR2 = 0x06;
	private final byte BUTTON_FWD1 = 0x07;
	private final byte BUTTON_FWD0 = 0x08;
	private final byte BUTTON_ROT_CW = 0x09;
	private final byte BUTTON_ROT_CCW = 0x0A;
	private final byte BUTTON_CUTTER = 0x0B;
	
	private ToggleButton toggleLed;
	private ToggleButton toggleCutter;
	private ToggleButton toggleFwd2;
	private ToggleButton toggleFwd1;
	private ToggleButton toggleFwd0;
	private ToggleButton toggleCcw;
	private ToggleButton toggleCw;
	
	
	private SeekBar servoBar1;
	private SeekBar servoBar2;

	private OnCheckedChangeListener stateChangeListener = new StateChangeListener();
	private OnSeekBarChangeListener seekBarChangeListener = new SeekBarChangeListener();

	//Server server = null;
	
	//qleisan
	private static final int TCP_SERVER_PORT = 21111;
	
	public Socket s = null;
	public BufferedReader in = null;
	public BufferedWriter out = null;
	

	
	public void toggleGroup_enableChangeListener()
	{
		Log.i(TAG, "toggleGroup_enableChangeListener");
		toggleFwd2.setOnCheckedChangeListener(stateChangeListener);
		toggleFwd1.setOnCheckedChangeListener(stateChangeListener);
		toggleFwd0.setOnCheckedChangeListener(stateChangeListener);
		toggleCcw.setOnCheckedChangeListener(stateChangeListener);
		toggleCw.setOnCheckedChangeListener(stateChangeListener);
	}
	
	public void toggleGroup_disableChangeListener()
	{
		Log.i(TAG, "toggleGroup_disableChangeListener");
		toggleFwd2.setOnCheckedChangeListener(null);
		toggleFwd1.setOnCheckedChangeListener(null);
		toggleFwd0.setOnCheckedChangeListener(null);
		toggleCcw.setOnCheckedChangeListener(null);
		toggleCw.setOnCheckedChangeListener(null);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		toggleLed = (ToggleButton) findViewById(R.id.ToggleButton1);
		toggleLed.setTag((byte) BUTTON_LED);
		toggleLed.setOnCheckedChangeListener(stateChangeListener);

		toggleCutter = (ToggleButton) findViewById(R.id.toggleButton7);
		toggleCutter.setTag((byte) BUTTON_CUTTER);
		toggleCutter.setOnCheckedChangeListener(stateChangeListener);

		servoBar1 = (SeekBar) findViewById(R.id.SeekBarServo1);
		servoBar1.setTag((byte) SEEKBAR1);
		servoBar1.setOnSeekBarChangeListener(seekBarChangeListener);

		servoBar2 = (SeekBar) findViewById(R.id.SeekBarServo2);
		servoBar2.setTag((byte) SEEKBAR2);
		servoBar2.setOnSeekBarChangeListener(seekBarChangeListener);

		toggleFwd2 = (ToggleButton) findViewById(R.id.toggleButton2);
		toggleFwd2.setTag((byte) BUTTON_FWD2);
		//toggleFwd2.setOnCheckedChangeListener(stateChangeListener);

		toggleFwd1 = (ToggleButton) findViewById(R.id.toggleButton3);
		toggleFwd1.setTag((byte) BUTTON_FWD1);
		//toggleFwd1.setOnCheckedChangeListener(stateChangeListener);
		
		toggleFwd0 = (ToggleButton) findViewById(R.id.toggleButton4);
		toggleFwd0.setTag((byte) BUTTON_FWD0);
		//toggleFwd0.setOnCheckedChangeListener(stateChangeListener);
		
		toggleCcw = (ToggleButton) findViewById(R.id.toggleButton5);
		toggleCcw.setTag((byte) BUTTON_ROT_CCW);
		//toggleCcw.setOnCheckedChangeListener(stateChangeListener);

		toggleCw = (ToggleButton) findViewById(R.id.toggleButton6);
		toggleCw.setTag((byte) BUTTON_ROT_CW);
		//toggleCw.setOnCheckedChangeListener(stateChangeListener);
		
		toggleGroup_enableChangeListener();
		
		Log.i(TAG, "Open TCP connecttion 192.168.1.1");
	   	try {
				s = new Socket("192.168.1.1", TCP_SERVER_PORT); // <------ UGLY!
				in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		
	}

	private class StateChangeListener implements OnCheckedChangeListener {


		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			byte portByte = (Byte) buttonView.getTag();
			
			Log.i(TAG, "onCheckedChanged, portbyte=" + portByte);
			String outMsg = "";
			
			switch (portByte) {
			case BUTTON_LED:
				Log.i(TAG, "BUTTON_LED");
				if (isChecked) {
					outMsg = "ledon";
				} else {
					outMsg = "ledoff";
				}
				break;
			case BUTTON_CUTTER:
				Log.i(TAG, "BUTTON_CUTTER");
				if (isChecked) {
					outMsg = "cuton";
				} else {
					outMsg = "cutoff";
				}
				break;
			case BUTTON_FWD2:
				Log.i(TAG, "BUTTON_FWD2");
				toggleGroup_disableChangeListener();
				toggleFwd2.setChecked(true);
				toggleFwd1.setChecked(false);
				toggleFwd0.setChecked(false);
				toggleCcw.setChecked(false);
				toggleCw.setChecked(false);
				toggleGroup_enableChangeListener();
				outMsg = "fwd2";
				break;
			case BUTTON_FWD1:
				Log.i(TAG, "BUTTON_FWD1");
				toggleGroup_disableChangeListener();
				toggleFwd2.setChecked(false);
				toggleFwd1.setChecked(true);
				toggleFwd0.setChecked(false);
				toggleCcw.setChecked(false);
				toggleCw.setChecked(false);
				toggleGroup_enableChangeListener();
				outMsg = "fwd1";
				break;
			case BUTTON_FWD0:
				Log.i(TAG, "BUTTON_FWD0");
				toggleGroup_disableChangeListener();
				toggleFwd2.setChecked(false);
				toggleFwd1.setChecked(false);
				toggleFwd0.setChecked(true);
				toggleCcw.setChecked(false);
				toggleCw.setChecked(false);
				toggleGroup_enableChangeListener();
				outMsg = "fwd0";
				break;
			case BUTTON_ROT_CCW:
				Log.i(TAG, "BUTTON_ROT_CCW");
				toggleGroup_disableChangeListener();
				toggleFwd2.setChecked(false);
				toggleFwd1.setChecked(false);
				toggleFwd0.setChecked(false);
				toggleCcw.setChecked(true);
				toggleCw.setChecked(false);
				toggleGroup_enableChangeListener();
				outMsg = "ccw";
				break;
			case BUTTON_ROT_CW:
				Log.i(TAG, "BUTTON_ROT_CW");
				toggleGroup_disableChangeListener();
				toggleFwd2.setChecked(false);
				toggleFwd1.setChecked(false);
				toggleFwd0.setChecked(false);
				toggleCcw.setChecked(false);
				toggleCw.setChecked(true);
				toggleGroup_enableChangeListener();
				outMsg = "cw";
				break;
			}
			
			if (outMsg != "")
			{
				Log.i(TAG, "outMsg = " + outMsg);
				try {
					//send output msg			
					out.write(outMsg + System.getProperty("line.separator"));
					out.flush();
					Log.i(TAG, "sent: " + outMsg);
					//accept server response
					String inMsg = "NOTHING";
					inMsg = in.readLine() + System.getProperty("line.separator");
					Log.i(TAG, "received: " + inMsg);
					//close connection
					//s.close();
				} catch (IOException e) {
					Log.e(TAG, "ERROR!");
				}
			}
			
		}
	}

	private class SeekBarChangeListener implements OnSeekBarChangeListener {

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			byte portByte = (Byte) seekBar.getTag();
			
			Log.i(TAG, "qleisan - onProgressChanged()");
			Log.d(TAG, "progress = " + String.valueOf(progress));
			
//			try {
//				server.send(new byte[] { portByte, (byte) progress });
//			} catch (IOException e) {
//				Log.e("microbridge", "problem sending TCP message", e);
//			}
			
			try {
				//send output msg
				String outMsg = "motorcw " + String.valueOf(progress) + System.getProperty("line.separator"); 				
				out.write(outMsg);
				out.flush();
				Log.i(TAG, "sent: " + outMsg);
				//accept server response
				String inMsg = in.readLine() + System.getProperty("line.separator");
				Log.i(TAG, "received: " + inMsg);
				//close connection
				//s.close();
			} catch (IOException e) {
				Log.e(TAG, "qleisan - SeekBar ERROR!");
			}

			
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			Log.i(TAG, "qleisan - onStartTrackingTouch()");
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			Log.i(TAG, "qleisan - onStopTrackingTouch()");
		}
	}

}