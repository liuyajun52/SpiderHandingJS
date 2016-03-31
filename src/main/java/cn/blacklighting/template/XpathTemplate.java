/**
 * 
 */
package cn.blacklighting.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author liuyajun01
 *
 */
public class XpathTemplate implements Template ,Iterable<XpathNode> {
	private List<XpathNode> pathList;
	private String name;
	public XpathTemplate(){
		pathList=new ArrayList<XpathNode>();
	}
	
	public void addXpathNode(XpathNode node){
		pathList.add(node);
	}
	
	public Iterator<XpathNode> iterator() {
		return pathList.iterator();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTemplateType() {
		return TYPE_XPATH;
	}

}
