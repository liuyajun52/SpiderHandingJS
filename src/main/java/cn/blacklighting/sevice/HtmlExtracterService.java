/**
 * 
 */
package cn.blacklighting.sevice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import cn.blacklighting.template.RegexTemplate;
import cn.blacklighting.template.Template;
import cn.blacklighting.template.XpathNode;
import cn.blacklighting.template.XpathTemplate;

/**
 * Html解析服务 <br/>
 * Create Time :2015年5月9日/下午3:08:56<br/>
 * Last Modified Time:2015年5月9日/下午3:08:56<br/>
 * 
 * @version
 * @author Yajun Liu<br/>
 */
public class HtmlExtracterService {

	// xpath 模板全局变量
	public final static String XPATH_TPL = "xpath";
	// regex全局变量
	public final static String REGEX_TPL = "regex";

	Map<Integer, Template> templateMap;
	String templateFileName;

	LinkedBlockingQueue<Entry<String, String>> htmlQueue;
	OutJson outer;
	ExecutorService threadPool;
	int threadPoolSize;

	public HtmlExtracterService(String templateFileName,
			LinkedBlockingQueue<Entry<String, String>> htmlQueue, OutJson outer)
			throws IOException {
		this(templateFileName, htmlQueue, outer, 20);
	}

	public HtmlExtracterService(String templateFileName,
			LinkedBlockingQueue<Entry<String, String>> htmlQueue,
			OutJson outer, int threadPoolSize) throws IOException {
		this.templateFileName = templateFileName;
		this.htmlQueue = htmlQueue;
		this.outer = outer;
		this.threadPoolSize = threadPoolSize;
		templateMap = new LinkedHashMap<Integer, Template>();
		htmlQueue = new LinkedBlockingQueue<Map.Entry<String, String>>();
		threadPool = Executors.newFixedThreadPool(threadPoolSize);
		init();
	}

	public void startService() {
		for (int i = 0; i < threadPoolSize; i++) {
			threadPool.execute(new ExtractThread());
		}
		// new ExtractThread().start();
	}

	class ExtractThread implements Runnable {

		public void run() {
			while (true) {
				try {
					Entry<String, String> urlHtmlPair = htmlQueue.take();
					JSONObject result = extract(urlHtmlPair.getValue());
					outer.outJson(urlHtmlPair.getKey(), result);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private JSONObject extract(String html) {
		JSONObject result = null;
		for (int priority = 1;; priority++) {
			Template template = templateMap.get(priority);
			if (template == null) {
				// System.err.println("please check your template");
				break;
			}
			switch (template.getTemplateType()) {
			case Template.TYPE_XPATH:
				result = parseHtmlUseXpth(template, html);
				break;
			case Template.TYPE_REGX:
				result = parseHtmlUseRegx(template, html);
			}
			if (!result.isEmpty()) {
				break;
			}
		}
		return result;
	}

	private void init() throws IOException {
		InputStream input = new FileInputStream(templateFileName);
		String templateStr = IOUtils.toString(input);
		initExtracterTemplate(templateStr);
	}

	private void initExtracterTemplate(String templateStr) {
		templateMap = new HashMap<Integer, Template>();
		Document templateDoc = Jsoup.parse(templateStr);
		for (Node node : templateDoc.select("template")) {
			String priority = node.attr("priority");
			Template template = null;
			if (node.attr("type").equals(XPATH_TPL)) {
				template = parseXpathTpl(node);

			} else if (node.attr("type").equals(REGEX_TPL)) {
				template = parseRegexTpl(node);
			}
			if (template != null)
				templateMap.put(Integer.parseInt(priority), template);
			else {
				// TODO
			}
		}

	}

	/**
	 * 
	 * @Title: parseXpathTpl
	 * @Description: xpath解析
	 * @param @param n
	 * @param @return 设定文件
	 * @return XpathTemplate 返回类型
	 * @throws
	 */
	private XpathTemplate parseXpathTpl(Node n) {
		XpathTemplate tpl = new XpathTemplate();
		for (Node c : n.childNodes()) {
			if (c.nodeName().equals("node")) {
				XpathNode pxNode = new XpathNode();
				Element el = (Element) c;
				pxNode.name = el.attr("name");
				pxNode.path = el.ownText();
				pxNode.isArray = el.attr("isarr").equals("true");
				pxNode.nodeNeedHtml = el.attr("type").equals("html");
				tpl.addXpathNode(pxNode);
			}
		}
		return tpl;
	}

	/**
	 * @param n
	 * @return
	 */
	private RegexTemplate parseRegexTpl(Node n) {
		RegexTemplate tpl = new RegexTemplate();
		Element el = (Element) n;
		Elements regex = el.select("regex");
		if (regex.size() != 1) {
			// try {
			// throw new TemplatePraseException(
			// "Only one regx string is allowed in one regx template!!!\n"
			// +
			// "Other regx will be ignored! The prase will be continued , but check your template!\n"
			// + n.outerHtml());
			// } catch (TemplatePraseException e) {
			// // TODO
			// e.printStackTrace();
			// }
		}
		Element regxNode = regex.first();
		String regxString = regxNode.ownText();
		tpl.regx = Pattern.compile(regxString);
		for (Element e : regxNode.select("group")) {
			tpl.groupMap.put(Integer.parseInt(e.ownText()), e.attr("name"));
		}
		return tpl;
	}

	/**
	 * 使用xpath解析文本
	 * 
	 * @param template
	 * @param doc
	 * @return
	 */
	private JSONObject parseHtmlUseXpth(Template template, String html) {
		HtmlCleaner htmlClener = new HtmlCleaner();
		TagNode doc = htmlClener.clean(html);
		XpathTemplate xpathTemplate = (XpathTemplate) template;
		JSONObject resultObject = new JSONObject();
		Iterator<XpathNode> xpathNode = xpathTemplate.iterator();
		while (xpathNode.hasNext()) {
			XpathNode node = xpathNode.next();
			// 打印模板值
			// System.out.println(node.path);
			Object[] es = null;
			try {
				es = doc.evaluateXPath(node.path);
				if (es == null) {
					continue;
				}

				if (node.isArray) {
					List<String> tempList = new ArrayList<String>();
					for (Object e : es) {
						if (e instanceof TagNode) {
							TagNode tn = (TagNode) e;
							if (node.nodeNeedHtml) {
								tempList.add(htmlClener.getInnerHtml(tn));
							} else {
								tempList.add(tn.getText().toString());
							}

						} else {
							tempList.add(e.toString());
						}
					}
					node.node = tempList;
					if (!tempList.isEmpty()) {
						resultObject.put(node.name, tempList);
					}
				} else {
					if (es.length > 0) {
						String value = null;
						if (es[0] instanceof TagNode) {
							TagNode tn = (TagNode) es[0];

							if (node.nodeNeedHtml) {
								value = htmlClener.getInnerHtml(tn);
							} else {
								value = tn.getText().toString();
							}

						} else {
							value = es[0].toString();
						}
						node.node = value;
						if (value != null && !value.isEmpty())
							resultObject.put(node.name, node.node);
					}
				}
			} catch (XPatherException e1) {
			}

		}
		return resultObject;
	}

	private JSONObject parseHtmlUseRegx(Template template, String html) {
		RegexTemplate regxTemplate = (RegexTemplate) template;
		Matcher matcher = regxTemplate.regx.matcher(html);
		JSONObject resultObject = new JSONObject();
		if (matcher.find()) {
			for (int groupIndex : regxTemplate.groupMap.keySet()) {
				if (groupIndex > matcher.groupCount()) {

					// TODO
					continue;
				}
				resultObject.put(regxTemplate.groupMap.get(groupIndex),
						matcher.group(groupIndex));
			}
		}
		return resultObject;
	}

	public interface OutJson {
		void outJson(String url, JSONObject obj);
	}

}
