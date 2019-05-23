package com.aris.arcm.transfer.parser.dynamic;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.idsscheer.webapps.arcm.bl.authentication.context.ContextFactory;
import com.idsscheer.webapps.arcm.bl.authentication.context.IUserContext;
import com.idsscheer.webapps.arcm.common.constants.metadata.ObjectType;

@SuppressWarnings("serial")
public class PostProcessorBB extends DebuggingPostProcessor {
	protected final Log log = LogFactory.getLog(PostProcessorBB.class);
	
	protected IUserContext userContext;

    @Override
    public void execute() {
    	super.execute();
    	userContext = ContextFactory.getFullReadAccessUserContext(Locale.ENGLISH);
    	for (ProcessedObject validObject : getSuccessfulObjects()) {
    		if (validObject.getObjectType().equals(ObjectType.USERGROUP.getId())) {
    			UserGroupPostProcessorEngine engine = new UserGroupPostProcessorEngine(userContext, validObject.getGuid(), validObject.getChangeType());
    			try {
					engine.execute();
				} catch (Exception e) {
					log.error("Error - object with guid '" + validObject.getGuid() + "'", e);
				}
    		}
/*    		Não será enviado mensagens de perdas conforme solicitado pelo cliente BB
 * 			else {
        		if (validObject.getObjectType().equals(ObjectType.LOSS.getId())) {
        			LossPostProcessorEngine engine = new LossPostProcessorEngine(userContext, validObject.getGuid(), validObject.getChangeType());
        			try {
    					engine.execute();
    				} catch (Exception e) {
    					log.error("Error - object with guid '" + validObject.getGuid() + "'", e);
    				}
        		}
    			
    		}*/
    	}
    }


}