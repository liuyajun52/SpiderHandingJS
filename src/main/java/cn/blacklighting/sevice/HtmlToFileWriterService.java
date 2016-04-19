/**
 *
 */
package cn.blacklighting.sevice;

import cn.blacklighting.dao.PageDao;
import cn.blacklighting.dao.UrlDao;
import cn.blacklighting.entity.PageEntity;
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

    private UrlDao urlDao;
    private PageDao pageDao;


    public HtmlToFileWriterService() {
        htmlQueue = new LinkedBlockingQueue<>();
        dataRootDir = new File(dataDir);
        shutDown = new AtomicBoolean(false);
        urlDao=new UrlDao();
        pageDao=new PageDao();
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

                    url.setStatus(UrlEntity.STATUS_CRAWED);
                    urlDao.update(url);

                    PageEntity page=new PageEntity();
                    page.setDocType("html");
                    page.setJsHandled(url.getNeedHandJs());
                    page.setPagePath(path.toString());
                    page.setUrlId(url.getId());
                    pageDao.add(page);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.fatal("Error on wait on HtmlToFileWriterService", e);
                }
            }
        }
    }


}
