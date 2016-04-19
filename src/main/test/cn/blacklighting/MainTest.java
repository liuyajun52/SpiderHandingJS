package cn.blacklighting;

import cn.blacklighting.util.CrawlerUtil;
import junit.framework.TestCase;

/**
 * Created by zybang on 2016/4/5.
 */
public class MainTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }


    public void testGetArrayIndexOrDef() throws Exception {
        String[] arr={"0","1","2"};
        assertEquals((String)Main.getArrayIndexOrDef(arr,1,"0"),"1");
        assertEquals((String)Main.getArrayIndexOrDef(arr,3,"10"),"10");
    }

    public void testGetDomainName() throws Exception {
        assertEquals(CrawlerUtil.getDomainName("http://baidu.com"),"baidu.com");
        assertEquals(CrawlerUtil.getDomainName("www.baidu.com"),"baidu.com");
        assertEquals(CrawlerUtil.getDomainName("https://www.baidu.com"),"baidu.com");
    }

    public void testSeedDBUsingFile() throws Exception {

    }

}