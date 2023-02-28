package cc.ghast.packet.wrapper.netty.bytebuf;

import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.wrapper.netty.MutableByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

/**
 * @author Ghast
 * @since 18/08/2020
 * Artemis © 2020
 */
public class CurrentByteBuf implements MutableByteBuf {
    
    private final ByteBuf byteBuf;

    public CurrentByteBuf(Object object) {
        this.byteBuf = (ByteBuf) object;
        //this.allocator = MutableByteBufAllocator.translate(((ByteBuf) object).alloc());
    }

    @Override
    public Object getParent() {
        return byteBuf;
    }

    @Override
    public int capacity() {
        return this.byteBuf.capacity();
    }

    @Override
    public MutableByteBuf capacity(final int i) {
        this.byteBuf.capacity(i);
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.byteBuf.maxCapacity();
    }

    @Override
    public MutableByteBufAllocator alloc() {
        return MutableByteBufAllocator.translate(((ByteBuf) byteBuf).alloc());
    }

    @Override
    public ByteOrder order() {
        return this.byteBuf.order();
    }

    @Override
    public MutableByteBuf order(final ByteOrder byteOrder) {
        this.byteBuf.order(byteOrder);
        return this;
    }

    @Override
    public MutableByteBuf unwrap() {
        this.byteBuf.unwrap();
        return this;
    }

    @Override
    public boolean isDirect() {
        return this.byteBuf.isDirect();
    }

    @Override
    public int readerIndex() {
        return this.byteBuf.readerIndex();
    }

    @Override
    public MutableByteBuf readerIndex(final int i) {
        this.byteBuf.readerIndex(i);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.byteBuf.writerIndex();
    }

    @Override
    public MutableByteBuf writerIndex(final int i) {
        this.byteBuf.writerIndex(i);
        return this;
    }

    @Override
    public MutableByteBuf setIndex(final int i, final int i1) {
        this.byteBuf.setIndex(i, i1);
        return this;
    }

    @Override
    public int readableBytes() {
        return this.byteBuf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.byteBuf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.byteBuf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.byteBuf.isReadable();
    }

    @Override
    public boolean isReadable(final int i) {
        return this.byteBuf.isReadable(i);
    }

    @Override
    public boolean isWritable() {
        return this.byteBuf.isWritable();
    }

    @Override
    public boolean isWritable(final int i) {
        return this.byteBuf.isWritable(i);
    }

    @Override
    public MutableByteBuf clear() {
        this.byteBuf.clear();
        return this;
    }

    @Override
    public MutableByteBuf markReaderIndex() {
        this.byteBuf.markReaderIndex();
        return this;
    }

    @Override
    public MutableByteBuf resetReaderIndex() {
        this.byteBuf.resetReaderIndex();
        return this;
    }

    @Override
    public MutableByteBuf markWriterIndex() {
        this.byteBuf.markWriterIndex();
        return this;
    }

    @Override
    public MutableByteBuf resetWriterIndex() {
        this.byteBuf.resetWriterIndex();
        return this;
    }

    @Override
    public MutableByteBuf discardReadBytes() {
        this.byteBuf.discardReadBytes();
        return this;
    }

    @Override
    public MutableByteBuf discardSomeReadBytes() {
        this.byteBuf.discardSomeReadBytes();
        return this;
    }

    @Override
    public MutableByteBuf ensureWritable(final int i) {
        this.byteBuf.ensureWritable(i);
        return this;
    }

    @Override
    public int ensureWritable(final int i, final boolean b) {
        return this.byteBuf.ensureWritable(i, b);
    }

    @Override
    public boolean getBoolean(final int i) {
        return this.byteBuf.getBoolean(i);
    }

    @Override
    public byte getByte(final int i) {
        return this.byteBuf.getByte(i);
    }

    @Override
    public short getUnsignedByte(final int i) {
        return this.byteBuf.getUnsignedByte(i);
    }

    @Override
    public short getShort(final int i) {
        return this.byteBuf.getShort(i);
    }

    @Override
    public int getUnsignedShort(final int i) {
        return this.byteBuf.getUnsignedShort(i);
    }

    @Override
    public int getMedium(final int i) {
        return this.byteBuf.getMedium(i);
    }

    @Override
    public int getUnsignedMedium(final int i) {
        return this.byteBuf.getUnsignedMedium(i);
    }

    @Override
    public int getInt(final int i) {
        return this.byteBuf.getInt(i);
    }

    @Override
    public long getUnsignedInt(final int i) {
        return this.byteBuf.getUnsignedInt(i);
    }

    @Override
    public long getLong(final int i) {
        return this.byteBuf.getLong(i);
    }

    @Override
    public char getChar(final int i) {
        return this.byteBuf.getChar(i);
    }

    @Override
    public float getFloat(final int i) {
        return this.byteBuf.getFloat(i);
    }

    @Override
    public double getDouble(final int i) {
        return this.byteBuf.getDouble(i);
    }

    @Override
    public MutableByteBuf getBytes(final int i, final MutableByteBuf mutableByteBuf) {
        this.byteBuf.getBytes(i, (ByteBuf) mutableByteBuf.getParent());
        return this;
    }

    @Override
    public MutableByteBuf getBytes(final int i, final MutableByteBuf mutableByteBuf, final int i1) {
        this.byteBuf.getBytes(i, (ByteBuf) mutableByteBuf.getParent(), i1);
        return this;
    }

    @Override
    public MutableByteBuf getBytes(final int i, final MutableByteBuf mutableByteBuf, final int i1, final int i2) {
        this.byteBuf.getBytes(i, (ByteBuf) mutableByteBuf.getParent(), i1, i2);
        return this;
    }

    @Override
    public MutableByteBuf getBytes(final int i, final byte[] bytes) {
        this.byteBuf.getBytes(i, bytes);
        return this;
    }

    @Override
    public MutableByteBuf getBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        this.byteBuf.getBytes(i, bytes, i1, i2);
        return this;
    }

    @Override
    public MutableByteBuf getBytes(final int i, final ByteBuffer byteBuffer) {
        this.byteBuf.getBytes(i, byteBuffer);
        return this;
    }

    @Override
    public MutableByteBuf getBytes(final int i, final OutputStream outputStream, final int i1) throws IOException {
        this.byteBuf.getBytes(i, outputStream, i1);
        return this;
    }

    @Override
    public int getBytes(final int i, final GatheringByteChannel gatheringByteChannel, final int i1) throws IOException {
        return this.byteBuf.getBytes(i, gatheringByteChannel, i1);
    }

    @Override
    public MutableByteBuf setBoolean(final int i, final boolean b) {
        this.byteBuf.setBoolean(i, b);
        return this;
    }

    @Override
    public MutableByteBuf setByte(final int i, final int i1) {
        this.byteBuf.setByte(i, i1);
        return this;
    }

    @Override
    public MutableByteBuf setShort(final int i, final int i1) {
        this.byteBuf.setShort(i, i1);
        return this;
    }

    @Override
    public MutableByteBuf setMedium(final int i, final int i1) {
        this.byteBuf.setMedium(i, i1);
        return this;
    }

    @Override
    public MutableByteBuf setInt(final int i, final int i1) {
        this.byteBuf.setInt(i, i1);
        return this;
    }

    @Override
    public MutableByteBuf setLong(final int i, final long l) {
        this.byteBuf.setLong(i, l);
        return this;
    }

    @Override
    public MutableByteBuf setChar(final int i, final int i1) {
        this.byteBuf.setChar(i, i1);
        return this;
    }

    @Override
    public MutableByteBuf setFloat(final int i, final float v) {
        this.byteBuf.setFloat(i, v);
        return this;
    }

    @Override
    public MutableByteBuf setDouble(final int i, final double v) {
        this.byteBuf.setDouble(i, v);
        return this;
    }

    @Override
    public MutableByteBuf setBytes(final int i, final MutableByteBuf mutableByteBuf) {
        this.byteBuf.setBytes(i, (ByteBuf) mutableByteBuf.getParent());
        return this;
    }

    @Override
    public MutableByteBuf setBytes(final int i, final MutableByteBuf mutableByteBuf, final int i1) {
        this.byteBuf.setBytes(i, (ByteBuf) mutableByteBuf.getParent(), i1);
        return this;
    }

    @Override
    public MutableByteBuf setBytes(final int i, final MutableByteBuf mutableByteBuf, final int i1, final int i2) {
        this.byteBuf.setBytes(i, (ByteBuf) mutableByteBuf.getParent(), i1, i2);
        return this;
    }

    @Override
    public MutableByteBuf setBytes(final int i, final byte[] bytes) {
        this.byteBuf.setBytes(i, bytes);
        return this;
    }

    @Override
    public MutableByteBuf setBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        this.byteBuf.setBytes(i, bytes, i1, i2);
        return this;
    }

    @Override
    public MutableByteBuf setBytes(final int i, final ByteBuffer buffer) {
        this.byteBuf.setBytes(i, buffer);
        return this;
    }

    @Override
    public int setBytes(final int i, final InputStream inputStream, final int i1) throws IOException {
        return this.byteBuf.setBytes(i, inputStream, i1);
    }

    @Override
    public int setBytes(final int i, final ScatteringByteChannel scatteringByteChannel, final int i1) throws IOException {
        return this.byteBuf.setBytes(i, scatteringByteChannel, i1);
    }

    @Override
    public MutableByteBuf setZero(final int i, final int i1) {
        this.byteBuf.setZero(i, i1);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return this.byteBuf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.byteBuf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.byteBuf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.byteBuf.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.byteBuf.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return this.byteBuf.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.byteBuf.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        return this.byteBuf.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.byteBuf.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return this.byteBuf.readLong();
    }

    @Override
    public char readChar() {
        return this.byteBuf.readChar();
    }

    @Override
    public float readFloat() {
        return this.byteBuf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.byteBuf.readDouble();
    }

    @Override
    public MutableByteBuf readBytes(final int i) {
        return MutableByteBuf.translate(this.byteBuf.readBytes(i));
    }

    @Override
    public MutableByteBuf readSlice(final int i) {
        this.byteBuf.readSlice(i);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final MutableByteBuf MutableByteBuf) {
        this.byteBuf.readBytes(byteBuf);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final MutableByteBuf MutableByteBuf, final int i) {
        this.byteBuf.readBytes(byteBuf, i);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final MutableByteBuf MutableByteBuf, final int i, final int i1) {
        this.byteBuf.readBytes(byteBuf, i, i1);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final byte[] bytes) {
        this.byteBuf.readBytes(bytes);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final byte[] bytes, final int i, final int i1) {
        this.byteBuf.readBytes(bytes, i, i1);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final ByteBuffer byteBuffer) {
        this.byteBuf.readBytes(byteBuffer);
        return this;
    }

    @Override
    public MutableByteBuf readBytes(final OutputStream outputStream, final int i) throws IOException {
        this.byteBuf.readBytes(outputStream, i);
        return this;
    }

    @Override
    public int readBytes(final GatheringByteChannel gatheringByteChannel, final int i) throws IOException {
        return this.byteBuf.readBytes(gatheringByteChannel, i);
    }

    @Override
    public MutableByteBuf skipBytes(final int i) {
        this.byteBuf.skipBytes(i);
        return this;
    }

    @Override
    public MutableByteBuf writeBoolean(final boolean b) {
        this.byteBuf.writeBoolean(b);
        return this;
    }

    @Override
    public MutableByteBuf writeByte(final int i) {
        this.byteBuf.writeByte(i);
        return this;
    }

    @Override
    public MutableByteBuf writeShort(final int i) {
        this.byteBuf.writeShort(i);
        return this;
    }

    @Override
    public MutableByteBuf writeMedium(final int i) {
        this.byteBuf.writeMedium(i);
        return this;
    }

    @Override
    public MutableByteBuf writeInt(final int i) {
        this.byteBuf.writeInt(i);
        return this;
    }

    @Override
    public MutableByteBuf writeLong(final long l) {
        this.byteBuf.writeLong(l);
        return this;
    }

    @Override
    public MutableByteBuf writeChar(final int i) {
        this.byteBuf.writeChar(i);
        return this;
    }

    @Override
    public MutableByteBuf writeFloat(final float v) {
        this.byteBuf.writeFloat(v);
        return this;
    }

    @Override
    public MutableByteBuf writeDouble(final double v) {
        this.byteBuf.writeDouble(v);
        return this;
    }

    @Override
    public MutableByteBuf writeBytes(final MutableByteBuf mutableByteBuf) {
        this.byteBuf.writeBytes((ByteBuf) mutableByteBuf.getParent());
        return this;
    }

    @Override
    public MutableByteBuf writeBytes(final MutableByteBuf mutableByteBuf, final int i) {
        this.byteBuf.writeBytes((ByteBuf) mutableByteBuf.getParent(), i);
        return this;
    }

    @Override
    public MutableByteBuf writeBytes(final MutableByteBuf mutableByteBuf, final int i, final int i1) {
        this.byteBuf.writeBytes((ByteBuf) mutableByteBuf.getParent(), i, i1);
        return this;
    }

    @Override
    public MutableByteBuf writeBytes(final byte[] bytes) {
        this.byteBuf.writeBytes(bytes);
        return this;
    }

    @Override
    public MutableByteBuf writeBytes(final byte[] bytes, final int i, final int i1) {
        this.byteBuf.writeBytes(bytes, i, i1);
        return this;
    }

    @Override
    public MutableByteBuf writeBytes(final ByteBuffer byteBuffer) {
        this.byteBuf.writeBytes(byteBuffer);
        return this;
    }

    @Override
    public int writeBytes(final InputStream inputStream, final int i) throws IOException {
        return this.byteBuf.writeBytes(inputStream, i);
    }

    @Override
    public int writeBytes(final ScatteringByteChannel scatteringByteChannel, final int i) throws IOException {
        return this.byteBuf.writeBytes(scatteringByteChannel, i);
    }


    @Override
    public MutableByteBuf writeZero(final int i) {
        this.byteBuf.writeZero(i);
        return this;
    }

    @Override
    public int indexOf(final int i, final int i1, final byte b) {
        return this.byteBuf.indexOf(i, i1, b);
    }

    @Override
    public int bytesBefore(final byte b) {
        return this.byteBuf.bytesBefore(b);
    }

    @Override
    public int bytesBefore(final int i, final byte b) {
        return this.byteBuf.bytesBefore(i, b);
    }

    @Override
    public int bytesBefore(final int i, final int i1, final byte b) {
        return this.byteBuf.bytesBefore(i, i1, b);
    }

    @Override
    public MutableByteBuf copy() {
        return MutableByteBuf.translate(this.byteBuf.copy());
    }

    @Override
    public MutableByteBuf copy(final int i, final int i1) {
        return MutableByteBuf.translate(this.byteBuf.copy(i, i1));
    }

    @Override
    public MutableByteBuf slice() {
        this.byteBuf.slice();
        return this;
    }

    @Override
    public MutableByteBuf slice(final int i, final int i1) {
        this.byteBuf.slice(i, i1);
        return this;
    }

    @Override
    public MutableByteBuf duplicate() {
        return MutableByteBuf.translate(this.byteBuf.duplicate());
    }

    @Override
    public int nioBufferCount() {
        return this.byteBuf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.byteBuf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(final int i, final int i1) {
        return this.byteBuf.nioBuffer(i, i1);
    }

    @Override
    public ByteBuffer internalNioBuffer(final int i, final int i1) {
        return this.byteBuf.internalNioBuffer(i, i1);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.byteBuf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(final int i, final int i1) {
        return this.byteBuf.nioBuffers(i, i1);
    }

    @Override
    public boolean hasArray() {
        return this.byteBuf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.byteBuf.array();
    }

    @Override
    public int arrayOffset() {
        return this.byteBuf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.byteBuf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.byteBuf.memoryAddress();
    }

    @Override
    public String toString(final Charset charset) {
        return this.byteBuf.toString(charset);
    }

    @Override
    public String toString(final int i, final int i1, final Charset charset) {
        return this.byteBuf.toString(i, i1, charset);
    }

    @Override
    public int hashCode() {
        return this.byteBuf.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return this.byteBuf.equals(o);
    }

    @Override
    public int compareTo(final MutableByteBuf mutableByteBuf) {
        return this.byteBuf.compareTo((ByteBuf) mutableByteBuf.getParent());
    }

    @Override
    public String toString() {
        return this.byteBuf.toString();
    }

    @Override
    public MutableByteBuf retain(final int i) {
        this.byteBuf.retain(i);
        return this;
    }

    @Override
    public MutableByteBuf retain() {
        this.byteBuf.retain();
        return this;
    }

   @Override
    public int refCnt() {
        return this.byteBuf.refCnt();
    }

    @Override
    public boolean release() {
        return this.byteBuf.release();
    }

    @Override
    public boolean release(final int i) {
        return this.byteBuf.release(i);
    }

    @Override
    public MutableByteBuf getByteBuf() {
        return this;
    }
    
}
