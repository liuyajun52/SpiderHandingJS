package cn.blacklighting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import cn.blacklighting.sevice.HtmlExtracterService;
import com.alibaba.fastjson.JSONObject;

/**
 * 本地HTML解析                               <br/>
 * Create Time	:2015年6月3日/下午1:43:11<br/>
 * Last Modified Time:2015年6月3日/下午1:43:11<br/>
 * @author  Yajun Liu <br/>
 */     
public class LocalExacterMain {
	/**
	 * HTML解析器
	 */
	static HtmlExtracterService extracter = null;
	/**
	 * HTML队列
	 */
	static LinkedBlockingQueue<Entry<String, String>> htmlQueue = null;
	public static void main(String[] args) throws IOException {
		if(args.length<3){
			System.err.println("Need 3 args templateFileName htmlFileName threadPoolSize");
			System.exit(0);
		}
		htmlQueue = new LinkedBlockingQueue<Map.Entry<String, String>>(100);
		final String templateFileName=args[0];
		int threadPoolSize=Integer.parseInt(args[2]);
		
		//初始书HTML解析器
		extracter = new HtmlExtracterService(templateFileName,
				htmlQueue, new HtmlExtracterService.OutJson() {
					//配置输出接口
					public void outJson(String url, JSONObject obj) {
							System.out.println(url + "\t"
								+ obj.toJSONString());
					}
				},threadPoolSize);
		
		extracter.startService();
		
		InputStream input=new FileInputStream(new File(args[1]));
		BufferedReader reader=new BufferedReader(new InputStreamReader(input));
		String line=null;
		
		while((line=reader.readLine())!=null){
			String[] info=line.split("\t");
			if(info.length<2)
				continue;
			String url=info[0];
			String html =info[1];
			try {
				htmlQueue.put(new SimpleEntry<String, String>(url,html));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		reader.close();
		
	}
	


}
