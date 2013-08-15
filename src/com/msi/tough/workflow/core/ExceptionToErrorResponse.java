/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */

package com.msi.tough.workflow.core;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.component.ComponentException;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;

import com.msi.tough.query.ErrorResponse;

/**
 * <code>ExceptionToErrorResponse</code> converts an exception to an error
 * response, suitable to return to the user.
 */
public class ExceptionToErrorResponse extends AbstractMessageTransformer
{

    public ExceptionToErrorResponse()
    {
        super();
        // This should be com.google.protobuf.Message, but for some reason
        // transformer is invoked twice, the second time with an already
        // transformed object.
        this.registerSourceType(DataTypeFactory.create(Object.class));
        this.setReturnDataType(DataTypeFactory.create(ErrorResponse.class));
    }

    /* (non-Javadoc)
     * @see org.mule.transformer.AbstractMessageTransformer#transformMessage(org.mule.api.MuleMessage, java.lang.String)
     */
    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding)
            throws TransformerException {
        Throwable throwable = message.getExceptionPayload().getException();
        if (throwable instanceof ErrorResponse) {
            return (ErrorResponse) throwable;
        }
        if (throwable instanceof ComponentException) {
            ComponentException ce = (ComponentException) throwable;
            ce.setHandled(true);
            if (ce.getCause() instanceof ErrorResponse) {
                return (ErrorResponse) ce.getCause();
            }
        }
        return new ErrorResponse(
                "Provider",
                throwable.getMessage(),
                "InternalFailure", 500);
    }

}
