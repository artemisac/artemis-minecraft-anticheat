package cc.ghast.packet.wrapper.netty;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.wrapper.netty.bytebuf.CurrentByteBuf;
import cc.ghast.packet.wrapper.netty.bytebuf.LegacyByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public interface MutableByteBuf {

    Object getParent();
    
    static MutableByteBuf translate(Object byteBuf) {
        return ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_8) ? new CurrentByteBuf(byteBuf) : new LegacyByteBuf(byteBuf);
    }

    int capacity() ;

    MutableByteBuf capacity(final int i) ;

    int maxCapacity() ;

    MutableByteBufAllocator alloc() ;

    ByteOrder order() ;

    MutableByteBuf order(final ByteOrder byteOrder) ;

    MutableByteBuf unwrap() ;

    boolean isDirect() ;

    int readerIndex() ;

    MutableByteBuf readerIndex(final int i) ;

    int writerIndex() ;

    MutableByteBuf writerIndex(final int i) ;

    MutableByteBuf setIndex(final int i, final int i1) ;

    int readableBytes() ;

    int writableBytes() ;

    int maxWritableBytes() ;

    boolean isReadable() ;

    boolean isReadable(final int i) ;

    boolean isWritable() ;

    boolean isWritable(final int i) ;

    MutableByteBuf clear() ;

    MutableByteBuf markReaderIndex() ;

    MutableByteBuf resetReaderIndex() ;

    MutableByteBuf markWriterIndex() ;

    MutableByteBuf resetWriterIndex() ;

    MutableByteBuf discardReadBytes() ;

    MutableByteBuf discardSomeReadBytes() ;

    MutableByteBuf ensureWritable(final int i) ;

    int ensureWritable(final int i, final boolean b) ;

    boolean getBoolean(final int i) ;

    byte getByte(final int i) ;

    short getUnsignedByte(final int i) ;

    short getShort(final int i) ;

    int getUnsignedShort(final int i) ;

    int getMedium(final int i) ;

    int getUnsignedMedium(final int i) ;

    int getInt(final int i) ;

    long getUnsignedInt(final int i) ;

    long getLong(final int i) ;

    char getChar(final int i) ;

    float getFloat(final int i) ;

    double getDouble(final int i) ;

    MutableByteBuf getBytes(final int i, final MutableByteBuf byteBuf) ;

    MutableByteBuf getBytes(final int i, final MutableByteBuf byteBuf, final int i1) ;

    MutableByteBuf getBytes(final int i, final MutableByteBuf byteBuf, final int i1, final int i2) ;

    MutableByteBuf getBytes(final int i, final byte[] bytes) ;

    MutableByteBuf getBytes(final int i, final byte[] bytes, final int i1, final int i2) ;

    MutableByteBuf getBytes(final int i, final ByteBuffer byteBuffer) ;

    MutableByteBuf getBytes(final int i, final OutputStream outputStream, final int i1) throws IOException ;

    int getBytes(final int i, final GatheringByteChannel gatheringByteChannel, final int i1) throws IOException ;

    MutableByteBuf setBoolean(final int i, final boolean b) ;

    MutableByteBuf setByte(final int i, final int i1) ;

    MutableByteBuf setShort(final int i, final int i1) ;

    MutableByteBuf setMedium(final int i, final int i1) ;

    MutableByteBuf setInt(final int i, final int i1) ;

    MutableByteBuf setLong(final int i, final long l) ;

    MutableByteBuf setChar(final int i, final int i1) ;

    MutableByteBuf setFloat(final int i, final float v) ;

    MutableByteBuf setDouble(final int i, final double v) ;

    MutableByteBuf setBytes(final int i, final MutableByteBuf byteBuf) ;

    MutableByteBuf setBytes(final int i, final MutableByteBuf byteBuf, final int i1) ;

    MutableByteBuf setBytes(final int i, final MutableByteBuf byteBuf, final int i1, final int i2) ;

    MutableByteBuf setBytes(final int i, final byte[] bytes) ;

    MutableByteBuf setBytes(final int i, final byte[] bytes, final int i1, final int i2) ;

    MutableByteBuf setBytes(final int i, final ByteBuffer byteBuffer) ;

    int setBytes(final int i, final InputStream inputStream, final int i1) throws IOException ;

    int setBytes(final int i, final ScatteringByteChannel scatteringByteChannel, final int i1) throws IOException ;

    MutableByteBuf setZero(final int i, final int i1) ;

    boolean readBoolean() ;

    byte readByte() ;

    short readUnsignedByte() ;

    short readShort() ;

    int readUnsignedShort() ;

    int readMedium() ;

    int readUnsignedMedium() ;

    int readInt() ;

    long readUnsignedInt() ;

    long readLong() ;

    char readChar() ;

    float readFloat() ;

    double readDouble() ;

    MutableByteBuf readBytes(final int i) ;

    MutableByteBuf readSlice(final int i) ;

    MutableByteBuf readBytes(final MutableByteBuf byteBuf) ;

    MutableByteBuf readBytes(final MutableByteBuf byteBuf, final int i) ;

    MutableByteBuf readBytes(final MutableByteBuf byteBuf, final int i, final int i1) ;

    MutableByteBuf readBytes(final byte[] bytes) ;

    MutableByteBuf readBytes(final byte[] bytes, final int i, final int i1) ;

    MutableByteBuf readBytes(final ByteBuffer byteBuffer) ;

    MutableByteBuf readBytes(final OutputStream outputStream, final int i) throws IOException ;

    int readBytes(final GatheringByteChannel gatheringByteChannel, final int i) throws IOException ;

    MutableByteBuf skipBytes(final int i) ;

    MutableByteBuf writeBoolean(final boolean b) ;

    MutableByteBuf writeByte(final int i) ;

    MutableByteBuf writeShort(final int i) ;

    MutableByteBuf writeMedium(final int i) ;

    MutableByteBuf writeInt(final int i) ;

    MutableByteBuf writeLong(final long l) ;

    MutableByteBuf writeChar(final int i) ;

    MutableByteBuf writeFloat(final float v) ;

    MutableByteBuf writeDouble(final double v) ;

    MutableByteBuf writeBytes(final MutableByteBuf byteBuf) ;

    MutableByteBuf writeBytes(final MutableByteBuf byteBuf, final int i) ;

    MutableByteBuf writeBytes(final MutableByteBuf byteBuf, final int i, final int i1) ;

    MutableByteBuf writeBytes(final byte[] bytes) ;

    MutableByteBuf writeBytes(final byte[] bytes, final int i, final int i1) ;

    MutableByteBuf writeBytes(final ByteBuffer byteBuffer) ;

    int writeBytes(final InputStream inputStream, final int i) throws IOException ;

    int writeBytes(final ScatteringByteChannel scatteringByteChannel, final int i) throws IOException ;

    MutableByteBuf writeZero(final int i) ;

    int indexOf(final int i, final int i1, final byte b) ;

    int bytesBefore(final byte b) ;

    int bytesBefore(final int i, final byte b) ;

    int bytesBefore(final int i, final int i1, final byte b) ;

    MutableByteBuf copy() ;

    MutableByteBuf copy(final int i, final int i1) ;

    MutableByteBuf slice() ;

    MutableByteBuf slice(final int i, final int i1) ;

    MutableByteBuf duplicate() ;

    int nioBufferCount() ;

    ByteBuffer nioBuffer() ;

    ByteBuffer nioBuffer(final int i, final int i1) ;

    ByteBuffer internalNioBuffer(final int i, final int i1) ;

    ByteBuffer[] nioBuffers() ;

    ByteBuffer[] nioBuffers(final int i, final int i1) ;

    boolean hasArray() ;

    byte[] array() ;

    int arrayOffset() ;

    boolean hasMemoryAddress() ;

    long memoryAddress() ;

    String toString(final Charset charset) ;

    String toString(final int i, final int i1, final Charset charset) ;

    int hashCode() ;

    boolean equals(final Object o) ;

    int compareTo(final MutableByteBuf byteBuf) ;

    String toString() ;

    MutableByteBuf retain(final int i) ;

    MutableByteBuf retain() ;

    int refCnt() ;

    boolean release() ;

    boolean release(final int i) ;

    MutableByteBuf getByteBuf() ;
}
