package com.example.myPlugin;

import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class Socket extends CordovaPlugin{
	
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
            this.connect(args,callbackContext);
            return true;
        }
        return false;  // Returning false results in a "MethodNotFound" error.
    }
	 
	 @Override
    public void onDestroy() {
		try {
			resultList = new JSONArray();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	 
	 @Override
    public void onReset() {
		try {
			resultList = new JSONArray();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	 
	 private void connect(String args, CallbackContext callbackContext) {
		 try{
	         if (args != null && args.length() > 0) {
	        	 AarryList<Thread> threadList = new AarryList<Thread>();
	        	 for(int index=0; args.index<length; index++){
	        		 String ip = args.getString(index);
	        		 threadList.put(new Thread(new ClientThread(ip)));
	        	 }
	        	 for(int i = 0; i < threadList.length; i++){
	        		 threadList[i].join();
	        	 }
	        	 callbackContext.success(resultList);
	         } else {
	             callbackContext.error("No IP addresses are input.");
	         }			 
		 }catch(Exception e){
			 callbackContext.error(e.getMessage());
		 }
     }

	 private void send(String args, CallbackContext callbackContext) {
		 try{
	         if (args != null && args.length() > 0) {
	        	 String serverIp = args.getString(0);
	        	 String message = args.getString(1);
	        	 Socket socket = socketMap.get(serverIp);
	        	 PrintWriter out = new PrintWriter(new BufferedWriter(
	 					new OutputStreamWriter(socket.getOutputStream())),
	 					true);
	 			 out.println(str);
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
		 }catch(Exception e){
			 callbackContext.error(e.getMessage());
		 }
     }
	 
	 class ClientThread implements Runnable throws Exception{
		
		private String serverIp;

		ClientThread(String serverIp){
			this.ip = ip; 
		}
		
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIp);
				Socket socket = new Socket(serverAddr, SERVERPORT);
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
