/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.components.common.renderer;

import com.idsscheer.webapps.arcm.ui.framework.support.StringUtils;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLImage;

/**
 * @author Administrator
 *
 */
public class ExternalLinkRendererBB extends ExternalLinkRenderer {

	@Override
	protected void renderAnchor(String p_url, String p_name) {
		HTMLImage goToIcon = getHTMLHelper().createImage("iconlib/icons/go_to_16.png").setClassName("ICON");
		this.result.append("<table class=\"RELATION\">");
		this.result.append("<td width=\"18px\">").append(goToIcon).append("</td>");
		this.result.append("<td><a class=\"anchor_text\" href=\"").append(escape(p_url)).append("\" target=\"_blank\" ");
		renderJSEvents();
		this.result.append(" ><div>").append(StringUtils.escapeBR(p_name)).append("</div></a></td>");
		this.result.append("</table>");
	}
}
