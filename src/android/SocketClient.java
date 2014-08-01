package com.example.myPlugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class SocketClient extends CordovaPlugin{
	
	private HashMap<String,Socket> socketMap = new HashMap<String,Socket>();
	private JSONArray resultList = new JSONArray();
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
	    super.initialize(cordova, webView);
	}
	
	 @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("connect".equals(action)) {
        	cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    connect(args,callbackContext);
                }
            });
        	return true;
        }
        if ("send".equals(action)) {
            this.send(args,callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }
	 
	 @Override
    public void onDestroy() {
		 resultList = new JSONArray();
    }
	 
	 @Override
    public void onReset() {
		 resultList = new JSONArray();
    }
	 
	 private void connect(JSONArray args, CallbackContext callbackContext) {
		 try{
	         if (args != null && args.length() > 0) {
	        	 resultList = new JSONArray();
	        	 ArrayList<Thread> threadList = new ArrayList<Thread>();
	        	 for(int index=0; index<args.length(); index = index+2){
	        		 String ip = args.getString(index);
	        		 int port = args.getInt(index+1);
	        		 Thread thread = new Thread(new ClientThread(ip,port));
	        		 thread.start();
	        		 threadList.add(thread);
	        	 }
	        	 for(int i = 0; i < threadList.size(); i++){
	        		 threadList.get(i).join();
	        	 }
	        	 callbackContext.success(resultList);
	         } else {
	             callbackContext.error("No IP addresses are input.");
	         }			 
		 }catch(Exception e){
			 callbackContext.error(e.getMessage());
		 }
     }
	 
	 private void send(JSONArray args, CallbackContext callbackContext) {
		 try{
	         if (args != null && args.length() > 0) {
	        	 String message = args.getString(0);
	        	 for(Map.Entry<String, Socket> entry:socketMap.entrySet()){
	        		 Socket socket = (Socket)entry.getValue();
		        	 PrintWriter out = new PrintWriter(new BufferedWriter(
			 					new OutputStreamWriter(socket.getOutputStream())),
			 					true);
		        	 out.println(message);
	        	 }

	        	 callbackContext.success("Message: "+message+"successfully sent.");
	         } else {
	             callbackContext.error("No args are input.");
	         }			 
		 }catch (UnknownHostException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			 e.printStackTrace();
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
     }
	 
	 class ClientThread implements Runnable{
		
		private String serverIp;
		private int serverPort;

		ClientThread(String serverIp, int serverPort){
			this.serverIp = serverIp;
			this.serverPort = serverPort;
		}
		
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				Socket socket = new Socket(serverAddr, serverPort);
				socketMap.put(serverIp+":"+serverPort, socket);
				resultList.put("Socket connection with "+ serverIp+":"+serverPort+" is established.");
				//callbackContext.success(resultList);
			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
				resultList.put("Socket connection with "+ serverIp +" failed.");
			} catch (IOException e) {
				System.out.println(e.getMessage());
				resultList.put("Socket connection with "+ serverIp +" failed.");
			}
		}
	}
}
