/**
 * 
 */
package cn.blacklighting;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import cn.blacklighting.sevice.DBService;
import cn.blacklighting.sevice.FileWriteService;
import cn.blacklighting.sevice.HtmlExtracterService;
import cn.blacklighting.sevice.PageDownService;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.*;

/**
 * @author Yajun Liu
 * @modify time 2016/3/31
 */
public class Main {



	public static int THRED_POOL_SIZE = 0;
	static HtmlExtracterService extracter = null;
	static LinkedBlockingQueue<Entry<String, String>> htmlQueue = null;
	public static boolean needProxy=true;
	public static String fileEncoding="utf8";


	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException {

		DBService db= DBService.getInstance();
		Session session=db.getSession();
		Transaction tx=session.beginTransaction();

		UrlseedEntity seed=new UrlseedEntity();


		if (args.length < 3) {
			System.out
					.println("3 arg needed : urlFileNname htmlOutputFileName,threadPoolSize [templateFile(null for not needed),proxy(1 or 0)]");
			return;
		}

		FileWriteService.setOutPutFile(args[1]);
		THRED_POOL_SIZE = Integer.parseInt(args[2]);
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http",
				"warn");

		if (args.length >= 4&&!args[3].equals("null")) {
			String templateFileName = args[3];
			try {
				htmlQueue = new LinkedBlockingQueue<Map.Entry<String, String>>();
				extracter = new HtmlExtracterService(templateFileName,
						htmlQueue, new HtmlExtracterService.OutJson() {

							public void outJson(String url, JSONObject obj) {
								System.out.println(url + "\t"
										+ obj.toJSONString());
//								FileWriteService.writeOut((url + "\t"
//										+ obj.toJSONString() + "\n").getBytes());
							}
						});
				extracter.startService();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		if(args.length >= 5){
			if(Integer.parseInt(args[4])==0){
				needProxy=false;
			}
		}
		
		if(args.length>=6){
			fileEncoding=args[5];
		}
		
		new PageDownService(
				new InputStreamReader(new FileInputStream(args[0])),
				new PageDownService.OutPutHtml() {

					public void writeHtml(String url, String html) {
						FileWriteService.writeOut(url + "\t"
								+ html+"\n");
						if (args.length < 4||args[3].equals("null")) {
							;
						} else {
							SimpleEntry<String, String> urlHtmlPair = new SimpleEntry<String, String>(
									url, new String(html));
							try {
								htmlQueue.put(urlHtmlPair);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
				}, THRED_POOL_SIZE).startSpider();
	}

}
