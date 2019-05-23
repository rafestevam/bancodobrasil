package com.idsscheer.webapps.arcm.bl.authorization.rights.runtime.accesscontrol.standard;

import com.idsscheer.webapps.arcm.bl.authorization.rights.runtime.accesscontrol.IAccessControlContext;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.IAppObj;
import com.idsscheer.webapps.arcm.common.constants.metadata.Enumerations;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.IRight;
import com.idsscheer.webapps.arcm.config.metadata.rights.roles.ObjectRight;

/**
 * <p>Standard class overridden to provide ISSUEMANAGER access to every issue.</p>
 *
 * @author SAG RnD by support call 5268070 (ACR-7146)
 * @since 15. February 2017
 */

public class IssueAccessControlBB extends IssueAccessControl {

    @Override
    public boolean hasOwnerRight(IRight right, IAppObj appObj, IAccessControlContext context) {
        if (right == ObjectRight.WRITE) {
            if (context.hasRole(Enumerations.USERROLE_TYPE.ISSUEMANAGER)) {
                return true;
            }
        }
        return super.hasOwnerRight(right, appObj, context);
    }
}
