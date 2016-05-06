/**
 *
 */
package cn.blacklighting.sevice;

import cn.blacklighting.dao.PageDao;
import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.models.PageEntity;
import cn.blacklighting.models.UrlEntity;
import cn.blacklighting.sevice.serviceinterface.HtmlWriter;
import cn.blacklighting.util.CrawlerUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Yajun Liu
 */
public class HtmlToFileWriterService extends UnicastRemoteObject implements HtmlWriter {

    private static Logger logger= LogManager.getRootLogger();
    public static final String DEFAULT_DIR_URL_ENCODE="UTF-8";
    private static final String dataDir = "D:\\crawler";
    private ExecutorService writerPool;
    public static int WRITE_POOL_SIZE=5;
    private BlockingQueue<AbstractMap.SimpleEntry<UrlEntity, byte[]>> htmlQueue = null;
    private File dataRootDir;
    private AtomicBoolean shutDown;

    private UrlDao urlDao;
    private PageDao pageDao;


    public HtmlToFileWriterService() throws RemoteException{
        htmlQueue = new LinkedBlockingQueue<>();
        dataRootDir = new File(dataDir);
        shutDown = new AtomicBoolean(false);
        urlDao=new UrlDao();
        pageDao=new PageDao();
        if (!dataRootDir.exists()) {
            dataRootDir.mkdir();
        }
        writerPool = Executors.newFixedThreadPool(WRITE_POOL_SIZE);
        for (int i = 0; i < WRITE_POOL_SIZE; i++) {
            writerPool.execute(new WriteThread());
        }
    }

    public void writeHtml(UrlEntity url, byte[] html) throws RemoteException{
        try {
            htmlQueue.put(new AbstractMap.SimpleEntry<>(url, html));
        } catch (InterruptedException e) {
            logger.fatal("Error on wait on HtmlToFileWriterService", e);
            e.printStackTrace();
        }
    }

    public void shutDown() throws RemoteException {
        shutDown.set(true);
    }

    class WriteThread implements Runnable {
        @Override
        public void run() {
            while (!shutDown.get()||!htmlQueue.isEmpty()) {
                try {

                    SimpleEntry<UrlEntity, byte[]> en = htmlQueue.take();
                    UrlEntity url = (UrlEntity) en.getKey();
                    byte[] html = (byte[]) en.getValue();
                    String domain = url.getDomain();
                    String u = url.getUrl();
                    Path path = Paths.get(dataDir, URLEncoder.encode(domain, DEFAULT_DIR_URL_ENCODE)
                            , CrawlerUtil.md5(u));
                    File out = path.toFile();
                    FileUtils.writeByteArrayToFile(out,html);

                    url.setStatus(UrlEntity.STATUS_CRAWED);
                    urlDao.update(url);

                    PageEntity page=new PageEntity();
                    page.setDocType("html");
                    page.setJsHandled(url.getNeedHandJs());
                    page.setPagePath(path.toString());
                    page.setUrlId(url.getId());
                    pageDao.add(page);

                    logger.info("HTML WRITE TO "+path.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.fatal("Error on wait on HtmlToFileWriterService", e);
                }
            }
        }
    }


}
