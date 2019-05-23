/**
 * 
 */
package com.idsscheer.webapps.arcm.ui.framework.renderer;

import java.util.Locale;

import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;
import com.idsscheer.webapps.arcm.common.constants.metadata.attribute.IObjectcontainerAttributeType;
import com.idsscheer.webapps.arcm.config.Metadata;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IObjectType;
import com.idsscheer.webapps.arcm.ui.framework.actioncommands.ActionCommandIds;
import com.idsscheer.webapps.arcm.ui.framework.support.ActionCommandUtils;
import com.idsscheer.webapps.arcm.ui.framework.support.WindowType;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLAnchor;
import com.idsscheer.webapps.arcm.ui.web.html.HTMLImage;

/**
 * @author Administrator
 *
 */
public class AdvancedRelationRendererBB extends AdvancedRelationRenderer {

	public AdvancedRelationRendererBB() {}
	
	@Override
	protected void renderPrimaryColumnContent(IAppObj p_appObj) {
		boolean isObjectContainer = p_appObj.getObjectType().hasParentType(ObjectType.OBJECTCONTAINER.getId());
		
		IObjectType objType;
		if (isObjectContainer) {
			objType = Metadata.getMetadata().getObjectType(p_appObj.getAttribute(IObjectcontainerAttributeType.ATTR_OBJECT_OBJTYPE).getRawValue());
		} else {
			objType = this.relatedObjectType;
		}

		HTMLImage image = getPrimaryColumnImage(p_appObj, objType);
		this.result.append("<table class=\"RELATION\"><tr>").append("<td width=\"18px\">").append(image).append("</td><td>");

		if ((this.renderAsAnchor) && (!isAccessToRelationsRestricted())) {
			String ovid;
			if (isObjectContainer) {
				ovid = p_appObj.getAttribute(IObjectcontainerAttributeType.ATTR_OBJECT_OVID).getRawValue();
			} else {
				if (this.listAttributeType.isVersionizedRelation()) {
					ovid = p_appObj.getVersionData().getOVID().getAsString();
				} else {
					ovid = p_appObj.getVersionData().getHeadOVID().getAsString();
				}
			}

			boolean forcePopup = WindowType.POPUP == getWindowType();
			boolean canEdit = (!forcePopup) && (ActionCommandIds.EDIT.equals(this.command));
			boolean canView = (!forcePopup) && ((ActionCommandIds.OPEN_BY_PERMISSION.equals(this.command)) || (ActionCommandIds.VIEW.equals(this.command)));
			String url;
			if (canEdit) {
				url = "javascript:"+ resolveProperties(p_appObj, this.rowButtons.getButtonPrototype("edit"));
			} else {
				if (canView) {
					if (ActionCommandIds.VIEW.equals(this.command)) {
						url = "javascript:"+ resolveProperties(p_appObj, this.rowButtons.getButtonPrototype("open"));
					} else
						url = "javascript:"+ resolveProperties(p_appObj, this.rowButtons.getButtonPrototype("open_by_permission"));
				} else {
					if ((ActionCommandIds.POPUP.equals(this.command)) || (!canEdit) || (forcePopup)) {
						String forward = ActionCommandUtils.getObjectCommandForward(this.context.getUIEnvironment().getContextPath(), objType.getId().toLowerCase(Locale.ENGLISH), 
								ActionCommandIds.POPUP, "&__object=" + ovid+ "&__windowType=popup").getPath();
						url = "javascript:aam_openPopUp('" + forward+ "','object')";
					} else {
						url = "javascript:";
					}
				}
			}
			HTMLAnchor htmlAnchor = this.htmlHelper.createAnchor(url, "<div>"+ getStringRepresentation(p_appObj) + "</div>");
			htmlAnchor.setInline(false);
			htmlAnchor.setSpan(false);
			htmlAnchor.setClassName("TOOLBAR_BUTTON");
			htmlAnchor.setId("popup_" + ovid);
			if (isDeactivated(p_appObj)) {
				htmlAnchor.setClassName("DEACTIVATED");
			}
			this.result.append(htmlAnchor);
		} else {
			this.result.append("<div");
			if (isDeactivated(p_appObj)) {
				this.result.append(" class=\"DEACTIVATED\"");
			}
			this.result.append(">").append(getStringRepresentation(p_appObj)).append("</div>");
		}

		this.result.append("</td></tr></table>");
	}

}
