/**
 * 
 */
package cn.blacklighting.template;

/**
 * @author liuyajun01
 *
 */
public interface  Template {
	int TYPE_XPATH=0;
	int TYPE_REGX=1;
	/**
	 * @return 模板类型，0 正则模板 1 Xpath模板 
	 */
	public int getTemplateType();
}
