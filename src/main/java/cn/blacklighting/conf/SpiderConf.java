package cn.blacklighting.conf;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


/**
 * Created by zybang on 2016/4/21.
 */
public class SpiderConf {

    private static PropertiesConfiguration config=null;

    public static PropertiesConfiguration getConf() throws ConfigurationException {
        if(config==null) {
            config=new PropertiesConfiguration();
            config.load(SpiderConf.class.getResourceAsStream("/spider.properties"));
        }
        return config;
    }

}
