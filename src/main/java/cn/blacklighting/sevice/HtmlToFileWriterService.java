/**
 *
 */
package cn.blacklighting.sevice;

import cn.blacklighting.entity.UrlEntity;
import com.sun.deploy.net.URLEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Yajun Liu
 */
public class HtmlToFileWriterService implements HtmlWriter {

    private static org.apache.log4j.Logger logger = Logger.getLogger(HtmlToFileWriterService.class);
    public static final String DEFAULT_DIR_URL_ENCODE="UTF-8";
    private static final String dataDir = "D:\\crawler";
    private BlockingQueue<AbstractMap.SimpleEntry<UrlEntity, byte[]>> htmlQueue = null;
    private File dataRootDir;
    private AtomicBoolean shutDown;
    private DBService db;


    public HtmlToFileWriterService() {
        htmlQueue = new LinkedBlockingQueue<>();
        dataRootDir = new File(dataDir);
        shutDown = new AtomicBoolean(false);
        db=DBService.getInstance();
        if (!dataRootDir.exists()) {
            dataRootDir.mkdir();
        }
        new WriteThread().start();
    }

    public void writeHtml(UrlEntity url, byte[] html) {
        try {
            htmlQueue.put(new AbstractMap.SimpleEntry<>(url, html));
        } catch (InterruptedException e) {
            logger.fatal("Error on wait on HtmlToFileWriterService", e);
            e.printStackTrace();
        }
    }

    public void shutDown(){
        shutDown.set(false);
    }

    class WriteThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (shutDown.get()) {
                try {
                    AbstractMap.SimpleEntry en = htmlQueue.take();
                    UrlEntity url = (UrlEntity) en.getKey();
                    byte[] html = (byte[]) en.getValue();
                    String domain = url.getDomain();
                    String u = url.getUrl();
                    Path path = Paths.get(dataDir, URLEncoder.encode(domain, DEFAULT_DIR_URL_ENCODE)
                            ,URLEncoder.encode(u, DEFAULT_DIR_URL_ENCODE));
                    File out = path.toFile();
                    FileUtils.writeByteArrayToFile(out,html);

                    Session s=db.getSession();
                    s.beginTransaction();
                    url.setStatus(2);
                    s.merge(url);
                    s.beginTransaction().commit();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.fatal("Error on wait on HtmlToFileWriterService", e);
                }
            }
        }
    }

    //	public static String OUTPUT_FILE_NAME = null;
//	private static OutputStream out = null;
//	private static BufferedWriter writer=null;
////	private static LinkedBlockingQueue<String> outQueue = null;
//	private static LinkedBlockingQueue<String> outArrayQueue=null;
//	static boolean stop = false;
//
//	public static synchronized void writeOut(String data){
//		if (out == null) {
//			try {
//				out = new FileOutputStream(new File(OUTPUT_FILE_NAME));
//				writer=new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				System.err.println("can not create output file !!!");
//				System.exit(0);
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			outArrayQueue = new LinkedBlockingQueue<String>();
//			new WriteByteFileThread().start();
//		}
//		try {
//			outArrayQueue.put(data);
//		} catch (InterruptedException e) {
////			e.printStackTrace();
//		}
//	}
//
//	public static synchronized void closeOut() {
//		stop = true;
//	}
//
//	public static synchronized void setOutPutFile(String fileName){
//		if(OUTPUT_FILE_NAME==null){
//			OUTPUT_FILE_NAME=fileName;
//		}
//	}


//
//	static class WriteByteFileThread extends Thread {
//		@Override
//		public void run() {
//			super.run();
//			try {
//				while ((!stop) || (!outArrayQueue.isEmpty())) {
//					String outHtml = outArrayQueue.take();
//					writer.write(outHtml);
//					writer.flush();
//				}
//				writer.flush();
//				writer.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	}

}
