/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.framework.renderer;

/**
 * @author Administrator
 *
 */
public class TechSupportNeededRendererBB extends AbstractBooleanRenderer {

	private static final String ATBREPORT_TECHAPPROVER_PROPERTY_KEY = "html.atbreport.techapprover.short.DBI";
	private static final String ATBREPORT_IMPLAPPROVER_PROPERTY_KEY = "html.atbreport.implapprover.short.DBI";
	
	@Override
	protected void renderReadonly() {
		result.append("<span><nobr>");
		if (data) {
			result.append(getMessage(ATBREPORT_TECHAPPROVER_PROPERTY_KEY));
		} else {
			result.append(getMessage(ATBREPORT_IMPLAPPROVER_PROPERTY_KEY));
		}
		result.append("</nobr></span>");
	}
	
	@Override
	protected void renderWritable() {
		renderReadonly();
		
	}


}
