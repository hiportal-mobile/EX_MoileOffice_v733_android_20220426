package com.ex.group.approval.easy.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;


public class ExNetwork
{
	private final boolean DEBUG = true;
	private final String LOGTAG = "HttpPost";
	public String rcvString = "N/A";
	public StringBuilder rcvBuffer;
	public ArrayList<SendQueue> sndBuffer;
	
	private static final String BOUNDARY = "*****";
	private static final String ENDLINE = "\r\n";
	private static final String HYPHENS = "--";
	
	private static final String CS_UTF_8 = "UTF-8";
	
	public ExNetwork(){}
	
	public ExNetwork(ArrayList<SendQueue> _sndBuffer)
	{
		this.sndBuffer = _sndBuffer;
		
		rcvBuffer = new StringBuilder(2000);
		rcvString = "";
	}
	
	public void SendData()
	{
		try{
			URL url = new URL(sndBuffer.get(0).value);
			Log.d("LOGTAG","SendData URL : " + url.toString());
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			
			if( http == null )
			{
				if(DEBUG)
					Log.i(LOGTAG, "httpURLConnection is null");
				return;
			}
			
			//	set send mode
			http.setConnectTimeout(15000);  // 15초
			http.setReadTimeout(20000) ;  // 20초
			http.setDefaultUseCaches(false);                                            
			http.setDoInput(true);            // 서버에서 읽기 모드 지정 
			http.setDoOutput(true);           // 서버로 쓰기 모드 지정  
			http.setRequestMethod("POST");    // 전송 방식은 POST 
			
			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
			http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
			
			//	send to server
			StringBuffer buffer = new StringBuffer();
			for( int i=1 ; i<sndBuffer.size() ; i++ )
			{
				buffer.append(sndBuffer.get(i).key).append('=');
				buffer.append(sndBuffer.get(i).value).append('&');
			}			
			Log.i(LOGTAG, "SendData buffer : " + buffer);
			
			OutputStream os = http.getOutputStream();
			if( os == null )
			{
				if(DEBUG)
					Log.i(LOGTAG, "OutputStream is null");
				return;
			}
			
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");			
			PrintWriter writer = new PrintWriter(osw);
			writer.write(buffer.toString());
			writer.flush();
			
			//	receive from server
			InputStream is = http.getInputStream();
			if( is == null )
			{
				if(DEBUG)
					Log.i(LOGTAG, "InputStream is null");
				return;
			}
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			String str;
			while((str = reader.readLine()) != null )
			{
				rcvBuffer.append(str + "\n");
			}
			rcvString = rcvBuffer.toString().trim();
		}catch(MalformedURLException e){
			Log.i(LOGTAG, "MalformedURLException is " + e.getMessage());
			rcvString = "N/A";
		}catch(IOException e){
			Log.i(LOGTAG, "IOException is " + e.getMessage());
			rcvString = "N/A";
		}catch(Exception e){
			Log.i(LOGTAG, "Excetion is " + e.getMessage());
			rcvString = "N/A";
		}finally{
			if(DEBUG)
				Log.i(LOGTAG, rcvString);
		}
	}
	
	public void fileUpload(HashMap<String, Object> files)
	{
		try{
			
			URL url = new URL(sndBuffer.get(0).value);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			
			if( http == null )
			{
				if(DEBUG)
					Log.i(LOGTAG, "httpURLConnection is null");
				return;
			}
			
			//	set send mode
			http.setConnectTimeout(5000);  // 5초
			http.setReadTimeout(10000) ;  // 10초
			http.setDefaultUseCaches(false);
			http.setDoInput(true);            // 서버에서 읽기 모드 지정 
			http.setDoOutput(true);           // 서버로 쓰기 모드 지정
			http.setUseCaches(false);
			http.setRequestMethod("POST");    // 전송 방식은 POST
			http.setRequestProperty("Connection", "Keep-Alive");
			http.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			
			DataOutputStream dos = new DataOutputStream(http.getOutputStream());
			
			/**	parameter setting	*/
			for( int i=1 ; i<sndBuffer.size() ; i++ )
			{
				//buffer.append(sndBuffer.get(i).key).append("=");
				//buffer.append(sndBuffer.get(i).value).append("&");
				
				dos.writeBytes(HYPHENS + BOUNDARY + ENDLINE);
				dos.writeBytes("Content_Disposition: form-data; name=\"" + sndBuffer.get(i).key + "\"" + ENDLINE);
				dos.writeBytes(ENDLINE);
				dos.writeBytes(sndBuffer.get(i).value);
				dos.writeBytes(ENDLINE);
				if(DEBUG)
				{
					Log.i(LOGTAG, "sndBuffer.get(i).key="+sndBuffer.get(i).key);
					Log.i(LOGTAG, "sndBuffer.get(i).value="+sndBuffer.get(i).value);
				}
				
			}
			
			FileInputStream fis = null;
			/**	file setting	*/
			for( Iterator<String> i = files.keySet().iterator() ; i.hasNext() ; )
			{
				String key = i.next();
				String fileNm = String.valueOf(files.get(key));
				
				fis = new FileInputStream(fileNm);
				
				dos.writeBytes(HYPHENS + BOUNDARY + ENDLINE);
				dos.writeBytes("Content-Disposition: form-datal; name=\"" + key + "\";filename=\"" + fileNm +"\"" + ENDLINE);
				dos.writeBytes(ENDLINE);
				
				int bytesAvailable = fis.available();
				int maxSize = 1024;
				int bufferSize = Math.min(bytesAvailable, maxSize);
				
				byte[] buffer = new byte[bufferSize];
				int bytesRead = fis.read(buffer, 0, bufferSize);
				
				while(bytesRead > 0 )
				{
					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fis.available();
					bufferSize = Math.min(bytesAvailable, maxSize);
					bytesRead = fis.read(buffer, 0, bufferSize);
				}
				
				dos.writeBytes(ENDLINE);
				dos.writeBytes(HYPHENS + BOUNDARY + HYPHENS + ENDLINE);
				
				fis.close();
				dos.flush();
			}
			
			InputStream is = http.getInputStream();
			if( is == null )
			{
				if(DEBUG)
					Log.i(LOGTAG, "InputStream is null");
				return;
			}
			InputStreamReader isr = new InputStreamReader(is, CS_UTF_8);
			BufferedReader reader = new BufferedReader(isr);
			
			String str;
			while((str = reader.readLine()) != null )
			{
				rcvBuffer.append(str + "\n");
			}
			rcvString = rcvBuffer.toString().trim();
		}catch(MalformedURLException e){
			Log.i(LOGTAG, "MalformedURLException is " + e.getMessage());
			rcvString = "N/A";
		}catch(IOException e){
			Log.i(LOGTAG, "IOException is " + e.getMessage());
			rcvString = "N/A";
		}catch(Exception e){
			Log.i(LOGTAG, "Excetion is " + e.getMessage());
			rcvString = "N/A";
		}finally{
			if(DEBUG)
				Log.i(LOGTAG, rcvString);
		}	
	}
	
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(sndBuffer.get(0).value).append('?');
		for( int i=1 ; i<sndBuffer.size() ; i++ )
		{
			buf.append(sndBuffer.get(i).key).append('=');
			buf.append(sndBuffer.get(i).value).append('&');
		}
		
		return buf.toString();
	}
	
}