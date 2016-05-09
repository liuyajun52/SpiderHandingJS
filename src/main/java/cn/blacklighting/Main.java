/**
 *
 */
package cn.blacklighting;

import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.models.UrlEntity;
import cn.blacklighting.sevice.*;
import cn.blacklighting.sevice.proxy.HtmlWriterProxy;
import cn.blacklighting.sevice.proxy.SchedulerProxy;
import cn.blacklighting.sevice.serviceinterface.HtmlWriter;
import cn.blacklighting.sevice.serviceinterface.Scheduler;
import cn.blacklighting.sevice.serviceinterface.UrlDistributer;
import cn.blacklighting.util.CrawlerUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.rmi.NotBoundException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author Yajun Liu
 */
public class Main {
    private static Logger logger=LogManager.getRootLogger();

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
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
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
        	try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            logger.info("Seed DB URL sum :" + seedNum);
        }
    }

    public static void main(final String[] args) throws IOException, ConfigurationException, NotBoundException {

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

            //get scheduler
            SchedulerProxy.registerOrGetService();
            Scheduler scheduler=SchedulerProxy.getScheduler();

            //get html writer
            HtmlWriterProxy.registerOrGetService();
            HtmlWriter writer=HtmlWriterProxy.getWriter();

            //init url distributer
            UrlDistributer urlDistributer=new DBUrlDistributerService();

            StaticPageCrawlerService crawler=new StaticPageCrawlerService(scheduler,urlDistributer,writer);
            crawler.start();
        } else {
            System.out.println("Unknown commend " + args[0]);
            printUsage();
            return;
        }

    }

}
