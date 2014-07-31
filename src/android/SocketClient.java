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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class SocketClient extends CordovaPlugin{
	
	private HashMap<String,Socket> socketMap = new HashMap<String,Socket>();
	private JSONArray resultList = new JSONArray();
	private static final int SERVERPORT = 5000;
	
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
	    super.initialize(cordova, webView);
	}
	
	 @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("connect".equals(action)) {
            this.connect(args,callbackContext);
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
	        	 ArrayList<Thread> threadList = new ArrayList<Thread>();
	        	 for(int index=0; index<args.length(); index++){
	        		 String ip = args.getString(index);
	        		 threadList.add(new Thread(new ClientThread(ip)));
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
	        	 String serverIp = args.getString(0);
	        	 String message = args.getString(1);
	        	 Socket socket = socketMap.get(serverIp);
	        	 PrintWriter out = new PrintWriter(new BufferedWriter(
	 					new OutputStreamWriter(socket.getOutputStream())),
	 					true);
	 			 out.println(message);
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

		ClientThread(String serverIp){
			this.serverIp = serverIp; 
		}
		
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				java.net.Socket socket = new java.net.Socket(serverAddr, SERVERPORT);
				socketMap.put(serverIp, socket);
				
				resultList.put("Socket connection with "+ serverIp +" is established.");
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
