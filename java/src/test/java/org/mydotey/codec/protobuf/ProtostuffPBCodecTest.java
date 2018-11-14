package org.mydotey.codec.protobuf;

import org.mydotey.codec.Codec;

/**
 * @author koqizhao
 *
 * Nov 8, 2018
 */
public class ProtostuffPBCodecTest extends ProtobufCodecTest {

    @Override
    protected Codec getCodec() {
        return ProtostuffPBCodec.DEFAULT;
    }

}
