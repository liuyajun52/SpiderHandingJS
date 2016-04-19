/**
 *
 */
package cn.blacklighting;

import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.entity.UrlEntity;
import cn.blacklighting.sevice.*;
import cn.blacklighting.util.CrawlerUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yajun Liu
 */
public class Main {

    private static org.apache.log4j.Logger logger = Logger.getLogger(Main.class);

    public static int THRED_POOL_SIZE = 0;
    static HtmlExtracterService extracter = null;
    static LinkedBlockingQueue<Entry<String, String>> htmlQueue = null;
    public static boolean needProxy = true;
    public static String fileEncoding = "utf8";


    static void printUsage() {
        System.out.println("Usage: seedDB| crawl");
        System.out.println("       seedDB:use give text file to insrt to the url DB,the  file given should " +
                "be split by \\t and fields are url ,[need_hand_JS(default 0 for false),weight(0-10,default 5 " +
                "for seed) ,max_deepth(default 3)");
        System.out.println("       crawl [seedFileName]:use seeds in db or given seed file to crawl page");
    }

    static Object getArrayIndexOrDef(Object[] l, int index, Object def) {
        if (index > 0 && index < l.length) {
            return l[index];
        }
        return def;
    }


    static void seedDBUsingFile(String fileName) {

        Session s=null;
        UrlDao urlDao=new UrlDao();
        int seedNum = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] infos = line.split("\t");
                UrlEntity url = new UrlEntity();

                url.setUrl(infos[0]);
                url.setNeedHandJs(Byte.parseByte((String) getArrayIndexOrDef(infos,
                        1, "0")));
                //default weight for seed is 10
                url.setWeight(Integer.parseInt((String) getArrayIndexOrDef(infos,
                        2, "5")));
                url.setMaxDeepth(Integer.parseInt((String) getArrayIndexOrDef(infos,
                        3, "3")));

                url.setIsSeed((byte) 1);
                url.setDeepth(0);
                url.setRetryTime(0);
                url.setStatus(0);
                url.setMd5(Md5Crypt.md5Crypt(infos[0].getBytes()));
                url.setDomain(CrawlerUtil.getDomainName(infos[0]));
                urlDao.add(url);
                seedNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.fatal("IO error while seed DB", e);
        } finally {
            s.close();
            logger.info("Seed DB URL sum :" + seedNum);
        }
    }

    public static void main(final String[] args) throws FileNotFoundException {

        BasicConfigurator.configure();

        System.out.println("Spider Handing JS V1.0");
        if (args.length == 0) {
            printUsage();
            return;
        }


        if (args[0].equals("seedDB")) {
            if (args.length < 2) {
                System.out.println("Need seed file name");
            }
            seedDBUsingFile(args[1]);
        } else if (args[0].equals("crawl")) {
            if(args.length>1){
                seedDBUsingFile(args[1]);
            }
            UrlDistributer urlDistributer=new DBUrlDistributer();
            HtmlWriter writer=new HtmlToFileWriterService();
            PageCrawlingService crawler=new PageCrawlingService(urlDistributer,writer);
            crawler.start();
        } else {
            System.out.println("Unknown commend " + args[0]);
            printUsage();
            return;
        }


//        DBService db = DBService.getInstance();
//        Session session = db.getSession();
//        Transaction tx = session.beginTransaction();
//
//
//        if (args.length < 3) {
//            System.out
//                    .println("3 arg needed : urlFileNname htmlOutputFileName,threadPoolSize [templateFile(null for not needed),proxy(1 or 0)]");
//            return;
//        }
//
//        HtmlToFileWriterService.setOutPutFile(args[1]);
//        THRED_POOL_SIZE = Integer.parseInt(args[2]);
//        System.setProperty("org.apache.commons.logging.Log",
//                "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty(
//                "org.apache.commons.logging.simplelog.log.org.apache.http",
//                "warn");
//
//        if (args.length >= 4 && !args[3].equals("null")) {
//            String templateFileName = args[3];
//            try {
//                htmlQueue = new LinkedBlockingQueue<Map.Entry<String, String>>();
//                extracter = new HtmlExtracterService(templateFileName,
//                        htmlQueue, new HtmlExtracterService.OutJson() {
//
//                    public void outJson(String url, JSONObject obj) {
//                        System.out.println(url + "\t"
//                                + obj.toJSONString());
////								HtmlToFileWriterService.writeOut((url + "\t"
////										+ obj.toJSONString() + "\n").getBytes());
//                    }
//                });
//                extracter.startService();
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.exit(0);
//            }
//        }
//
//        if (args.length >= 5) {
//            if (Integer.parseInt(args[4]) == 0) {
//                needProxy = false;
//            }
//        }
//
//        if (args.length >= 6) {
//            fileEncoding = args[5];
//        }
//
//        new PageDownService(
//                new InputStreamReader(new FileInputStream(args[0])),
//                new PageDownService.OutPutHtml() {
//
//                    public void writeHtml(String url, String html) {
//                        HtmlToFileWriterService.writeOut(url + "\t"
//                                + html + "\n");
//                        if (args.length < 4 || args[3].equals("null")) {
//                            ;
//                        } else {
//                            AbstractMap.SimpleEntry<String, String> urlHtmlPair = new AbstractMap.SimpleEntry<String, String>(
//                                    url, new String(html));
//                            try {
//                                htmlQueue.put(urlHtmlPair);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                }, THRED_POOL_SIZE).startSpider();
    }

}
