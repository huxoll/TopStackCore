/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.msi.tough.query;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.protobuf.Message;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

/**
 * @author jgardner
 *
 */
public class ProtobufUtil {

    /**
     *
     */
    public ProtobufUtil() {
    }

    /**
     * Setter to inject a required property into a message (via reflection since
     * protobuf has no enforced interface/base class).
     *
     * Handles both real protobufs and our mocks.
     *
     * @param message
     * @param fieldName
     * @param value
     */
    public static <U extends Message> U setRequiredField(U message,
            String fieldName, Object value) {
        Message.Builder builder = message.newBuilderForType();
        if (builder != null) {
            Descriptor d = builder.getDescriptorForType();
            FieldDescriptor fd = d.findFieldByName(fieldName);
            builder.setField(fd, value);
            @SuppressWarnings("unchecked")
            U built = (U) builder.mergeFrom(message).buildPartial();
            return built;
        }
        try {
            PropertyUtils.setProperty(message, fieldName, value);
        } catch (Exception e) {
            // e may be: IllegalAccessException, InvocationTargetException,
            // NoSuchMethodException
            e.printStackTrace();
            throw new IllegalArgumentException("Message must have "
                    + "required property " + fieldName);
        }
        return message;
    }

    /**
     * Getter to retrieve a required property from a message (via reflection
     * since protobuf has no enforced interface/base class).
     * @param message
     * @param fieldName
     * @param value
     */
    public static <U extends Message> Object getRequiredField(U message,
            String fieldName) {
        try {
            return PropertyUtils.getProperty(message, fieldName);
        } catch (Exception e) {
            // e may be: IllegalAccessException, InvocationTargetException,
            // NoSuchMethodException
            throw new IllegalArgumentException("Message must have " +
                    "required property " + fieldName);
        }
    }

    /**
     * Getter to retrieve a required property from a message (via reflection
     * since protobuf has no enforced interface/base class).
     * @param message
     * @param fieldName
     * @param value
     */
    public static <U extends Message> Object getOptionalField(U message,
            String fieldName) {
        try {
            return PropertyUtils.getProperty(message, fieldName);
        } catch (Exception e) {
            // e may be: IllegalAccessException, InvocationTargetException,
            // NoSuchMethodException
            return null;
        }
    }

}
