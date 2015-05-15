package com.mydomain.lawn3_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.microbridge.server.Server;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Lawn3_Server extends Activity {
	
	private static final int MENU1 = Menu.FIRST;
	private static final int MENU2 = Menu.FIRST+1;
	
	// Create TCP server (based on MicroBridge LightWeight Server).
	// Note: This Server runs in a separate thread.
	Server mServer = null;
		
	private final String TAG = Lawn3_Server.class.getSimpleName();

	private void logAndDisplay(String str) {
		Log.d(TAG, str);
		textDisplay.append(str + System.getProperty("line.separator"));
	}
	
    protected void onStart() {
    	super.onStart();
    	logAndDisplay("onStart");
    }
	
    protected void onRestart() {
    	super.onRestart();
    	logAndDisplay("onRestart");
    }

    protected void onResume() {
    	super.onResume();
    	logAndDisplay("onResume");
    }

    protected void onPause() {
    	super.onPause();
    	logAndDisplay("onPause");    
    }

    protected void onStop() {
    	super.onStop();
    	logAndDisplay("onStop");    
    }

    protected void onDestroy() {
    	super.onDestroy();
    	logAndDisplay("onDestroy");    
    }

	Handler handler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  Bundle bundle = msg.getData();
			  String string = bundle.getString("myKey");
			  textDisplay.append("> " + string + System.getProperty("line.separator"));		     
		  }
	};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        textDisplay = (TextView) this.findViewById(R.id.text1);
        textDisplay.setText("");
 
		logAndDisplay("onCreate");
		//textDisplay.setMovementMethod(new ScrollingMovementMethod());
		
		// Create TCP server (based on MicroBridge LightWeight Server)
		try {
			mServer = new Server(4568); // Use ADK port
			mServer.start();
		} catch (IOException e) {
			logAndDisplay("Unable to start ADK TCP server");
			Log.e(TAG, "Unable to start TCP server", e);
			Log.e(TAG, "Unable to start TCP server2");
			System.exit(-1);
		}
		
        //runTcpServer();	
 		new Thread(new Runnable() {
	        public void run() {
	        	runTcpServer();
	        }
 		}).start();
    }
    
    private void myTextDisplay(String str){
    	Log.i(TAG, str);
	  	Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString("myKey", str);
	    msg.setData(bundle);
	    handler.sendMessage(msg);
    }
    
    private TextView textDisplay;
    private static final int TCP_SERVER_PORT = 21111;
    private void runTcpServer() {
    	
    	while(true)
    	{
        	ServerSocket ss = null;
        	myTextDisplay("Starting TCP Server, port = " + TCP_SERVER_PORT);
        	       	
        	try {
    			ss = new ServerSocket(TCP_SERVER_PORT);
    			//ss.setSoTimeout(10000);
    			//accept connections
    			Socket s = ss.accept();
    			myTextDisplay("Accepted socket connection");
    			
    			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    			
    			while (true){
    				//receive a message
    				String incomingMsg = in.readLine();
    				myTextDisplay("received: " + incomingMsg);
    				//String[] separated = incomingMsg.split("\\s+");    				
    				//ToDo: FIX CRASH if input is "space" only	    				    				
					out.write("echo: " + incomingMsg + "\r\n");
					out.flush();
					
					//finish(); //kill app
					//System.exit(-1); //kill this thread?
    				//SystemClock.sleep(5000);
    				try {
    					byte[] bytes = incomingMsg.getBytes();
    					myTextDisplay("numBytes = " + bytes.length);
    					// Send the state of each LED to ADK Main Board as a byte
    					//mServer.send(new byte[] { (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01 });
    					mServer.send(bytes);
    				} catch (IOException e) {
    					myTextDisplay("failed to send ADK message");
    				}
    				
    			}
    			//s.close(); //qleisan
    		} catch (InterruptedIOException e) {
    			//if timeout occurs
    			//e.printStackTrace();
    			myTextDisplay("qleisan ----- if timeout occurs....");
        	} catch (IOException e) {
    			e.printStackTrace();
    			myTextDisplay("qleisan2");
    		} finally {
    			if (ss != null) {
    				try {
    					ss.close();
    					myTextDisplay("qleisan3");
    					//System.exit(-1);
    				} catch (IOException e) {
    					e.printStackTrace();;
    					myTextDisplay("qleisan4");
    				}
    			}
    		}   		
    	}
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.d(TAG, "qleisan - optionsMenu");
    	menu.add(0, MENU1, 0, "Quit");
//    	menu.add(0, MENU2, 0, "StartT");
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG, "qleisan - optionsMenuItemSelected");
    	switch (item.getItemId()) {
    	case MENU1:
          	Log.d(TAG, "qleisan - MENU1");
        	textDisplay.append("qleisan - MENU1" + System.getProperty("line.separator"));
        	// ToDo: kill tcp-thread!
    		finish();
    		return true;
    	case MENU2:
          	Log.d(TAG, "qleisan - MENU2");
        	textDisplay.append("qleisan - MENU2" + System.getProperty("line.separator"));
    		return true;
    	}
    	return false;
    }
}