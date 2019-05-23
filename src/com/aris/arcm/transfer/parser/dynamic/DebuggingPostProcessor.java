package com.aris.arcm.transfer.parser.dynamic;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.aris.arcm.transfer.AbstractPostProcessor;
import com.idsscheer.annotations.Since;
import com.idsscheer.webapps.arcm.bl.models.objectmodel.attribute.IAttribute;
import com.idsscheer.webapps.arcm.common.constants.Versions;

/**
 * Simple example how to implement a custom post processing.<br>
 *
 * @author y9086
 * @since 9.8.4 02.02.2016
 */
@SuppressWarnings("serial")
@Since(Versions.ARCM_9_8_2_1)
public class DebuggingPostProcessor extends AbstractPostProcessor {

    @Override
    public void execute() {

        System.out.println("\n\nSuccessful objects:");
        for (ProcessedObject validObject : getSuccessfulObjects()) {
            System.out.println(validObject.getObjectType()+ " '"+ validObject.getChangeType().name() + "' "+ validObject.getGuid());
            if(!validObject.getAddedAttributeRefEntries().isEmpty()) {
                System.out.println("\tAdded references:");
                for (Map.Entry<String, List<String>> entry : validObject.getAddedAttributeRefEntries().entrySet()) {
                    System.out.println("\t" + entry.getKey());
                    System.out.println("\t\t" + entry.getValue());
                }
            }
            if(!validObject.getRemovedAttributeRefEntries().isEmpty()) {
                System.out.println("\tRemoved references:");
                for (Map.Entry<String, List<String>> entry : validObject.getRemovedAttributeRefEntries().entrySet()) {
                    System.out.println("\t" + entry.getKey());
                    System.out.println("\t\t" + entry.getValue());
                }
            }
        }

        System.out.println("\n\nFailed objects:");
        for (ProcessedObject obj : getFailedObjects()) {
            System.out.println(obj.getObjectType() + " " + obj.getGuid());
            for (IAttribute entry: obj.getInvalidAttributes()) {
                System.out.println("\t"+entry.getAttributeType().getId()+" - "+entry.getValidationMessages().getAsString(Locale.ENGLISH,false));
            }
        }
    }
}
