/**
 * 
 */
package cn.blacklighting.sevice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yajun Liu
 *
 */
public class FileWriteService {

	public static String OUTPUT_FILE_NAME = null;
	private static OutputStream out = null;
	private static BufferedWriter writer=null;
//	private static LinkedBlockingQueue<String> outQueue = null;
	private static LinkedBlockingQueue<String> outArrayQueue=null;
	static boolean stop = false;

	public static synchronized void writeOut(String data){
		if (out == null) {
			try {
				out = new FileOutputStream(new File(OUTPUT_FILE_NAME));
				writer=new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.err.println("can not create output file !!!");
				System.exit(0);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			outArrayQueue = new LinkedBlockingQueue<String>();
			new WriteByteFileThread().start();
		}
		try {
			outArrayQueue.put(data);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}
	
	public static synchronized void closeOut() {
		stop = true;
	}
	
	public static synchronized void setOutPutFile(String fileName){
		if(OUTPUT_FILE_NAME==null){
			OUTPUT_FILE_NAME=fileName;
		}
	}
	
	static class WriteByteFileThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				while ((!stop) || (!outArrayQueue.isEmpty())) {
					String outHtml = outArrayQueue.take();
					writer.write(outHtml);
					writer.flush();
				}
				writer.flush();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
