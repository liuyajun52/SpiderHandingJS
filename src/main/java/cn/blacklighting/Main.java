/**
 *
 */
package cn.blacklighting;

import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.entity.UrlEntity;
import cn.blacklighting.sevice.*;
import cn.blacklighting.util.CrawlerUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * @author Yajun Liu
 */
public class Main {
    private static Logger logger=LogManager.getRootLogger();
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
                url.setMd5(CrawlerUtil.md5(infos[0].toLowerCase()));
                url.setDomain(CrawlerUtil.getDomainName(infos[0]));
                urlDao.add(url);
                seedNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.fatal("IO error while seed DB", e);
        } finally {
            logger.info("Seed DB URL sum :" + seedNum);
        }
    }

    public static void main(final String[] args) throws IOException {

        Properties props = new Properties();
        props.load(Main.class.getResourceAsStream("/log4j.properties"));
        PropertyConfigurator.configure(props);
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
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
            System.exit(0);
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

    }

}
