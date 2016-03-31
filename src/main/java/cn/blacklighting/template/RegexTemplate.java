/**
 * 
 */
package cn.blacklighting.template;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author liuyajun01
 *
 */
public class RegexTemplate implements Template {
	public Pattern regx;
	public final Map<Integer,String> groupMap;
	
	public RegexTemplate(){
		groupMap=new HashMap<Integer, String>();
	}
	
	public int getTemplateType() {
		return TYPE_REGX;
	}

}
