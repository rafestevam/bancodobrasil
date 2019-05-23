/**
 * 
 */
package com.idsscheer.webapps.arcm.common.constants.metadata.attribute;

import com.idsscheer.webapps.arcm.config.metadata.objecttypes.IDoubleAttributeType;
import com.idsscheer.webapps.arcm.config.metadata.objecttypes.ILongAttributeType;

import static com.idsscheer.webapps.arcm.common.constants.metadata.attribute.MetadataConstantsUtil.*;

/**
 * <p>  this interface defines all attribute-types and attribute-names of LOSS IAppObj implementation.
 *      names of common attributes e.g. OBJ_ID and GUID are defined in {@link IObjectAttributeType} 
 *      additional attribute-names of versionized objects are defined in {@link IVersionAttributeType}
 * </p>
 * <p> This is a customization for <b><code>Banco do Brasil Project</code></b> </p>
 *@author brmob
 *@since v20160711-UPGD-Upgrade982To985
 *@see ILossAttributeType
 */
public interface ILossAttributeTypeBB extends ILossAttributeType {
	
	public static final String STR_QTDE_EVENTOS = "bb_qtdeEventos_int";
	public static final String STR_QTDE_PERDAS_RELEVANTES = "bb_qtdePerdasRelevantes_int";
	public static final String STR_PENDING_LOSS = "pendingLoss";
	
	public static final ILongAttributeType ATTR_QTDE_EVENTOS = create(OBJECT_TYPE, STR_QTDE_EVENTOS);
	public static final ILongAttributeType ATTR_QTDE_PERDAS_RELEVANTES = create(OBJECT_TYPE, STR_QTDE_PERDAS_RELEVANTES);
	public static final IDoubleAttributeType ATTR_PENDING_LOSS = create(OBJECT_TYPE, STR_PENDING_LOSS);

}
