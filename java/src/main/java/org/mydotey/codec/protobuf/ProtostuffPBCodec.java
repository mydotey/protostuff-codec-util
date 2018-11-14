package org.mydotey.codec.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.mydotey.codec.CodecException;
import org.mydotey.codec.probotuf.ProtobufCodec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Created by Qiang Zhao on 10/05/2016.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProtostuffPBCodec extends ProtobufCodec {

    public static final ProtostuffPBCodec DEFAULT = new ProtostuffPBCodec();

    private ConcurrentHashMap<Class, Schema> _schemaMap = new ConcurrentHashMap<>();

    @Override
    protected byte[] doEncode(Object obj) {
        Schema schema = getSchema(obj.getClass());
        LinkedBuffer buffer = getBuffer();
        try {
            return ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            releaseBuffer(buffer);
        }
    }

    @Override
    protected <T> T doDecode(byte[] is, Class<T> clazz) {
        Schema schema = getSchema(clazz);
        try {
            T obj = clazz.newInstance();
            ProtobufIOUtil.mergeFrom(is, obj, schema);
            return obj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("class has no default public constructor: " + clazz);
        }
    }

    @Override
    protected void doEncode(OutputStream os, Object obj) {
        Schema schema = getSchema(obj.getClass());
        LinkedBuffer buffer = getBuffer();
        try {
            ProtobufIOUtil.writeTo(os, obj, schema, buffer);
        } catch (IOException e) {
            throw new CodecException(e);
        } finally {
            releaseBuffer(buffer);
        }
    }

    @Override
    protected <T> T doDecode(InputStream is, Class<T> clazz) {
        Schema schema = getSchema(clazz);
        try {
            T obj = clazz.newInstance();
            ProtobufIOUtil.mergeFrom(is, obj, schema);
            return obj;
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            throw new IllegalArgumentException("class has no default public constructor: " + clazz);
        }
    }

    protected <T> Schema<T> getSchema(Class<T> clazz) {
        return _schemaMap.computeIfAbsent(clazz, RuntimeSchema::getSchema);
    }

    protected LinkedBuffer getBuffer() {
        return LinkedBuffer.allocate();
    }

    protected void releaseBuffer(LinkedBuffer buffer) {

    }

}
