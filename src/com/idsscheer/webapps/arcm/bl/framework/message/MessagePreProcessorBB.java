/**
 * 
 */
package com.idsscheer.webapps.arcm.bl.framework.message;


/**
 * Classes extending this interface are called during message prepare phase, allowing to customize message object state.
 * Before use any implementations must be declared with <code>messagePreProcessorClassMappings.xml</code>
 * Here's what declaration of a custom message pre-processor could look like.
 *
 * <pre>{@code
 *     <blClassMapping xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../xsd/blClassMappings.xsd">
 *         <mappingType name="MessagePreProcessor">
 *             <mapping name="default" clsName="com.example.MessagePreProcessor"/>
 *         </mappingType>
 *     </blClassMapping>
 * }</pre>
 *
 * Note that only single pre-processor is allowed and its name must be "default"
 *
 * @see IMessage
 * @see MessageFactory
 */
public class MessagePreProcessorBB implements IMessagePreProcessor {

	/* (non-Javadoc)
	 * @see com.idsscheer.webapps.arcm.bl.framework.message.IMessagePreProcessor#prepare(com.idsscheer.webapps.arcm.bl.framework.message.IMessage)
	 */
	@Override
	public void prepare(IMessage message) {
		

	}

}
