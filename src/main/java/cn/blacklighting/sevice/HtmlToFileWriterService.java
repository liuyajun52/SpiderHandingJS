/**
 *
 */
package cn.blacklighting.sevice;

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

                    PageEntity page=new PageEntity();
                    page.setDocType("html");
                    page.setJsHandled(url.getNeedHandJs());
                    page.setPagePath(path.toString());
                    page.setUrlId(url.getId());
                    s.save(page);

                    s.getTransaction().commit();
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.fatal("Error on wait on HtmlToFileWriterService", e);
                }
            }
        }
    }


}
