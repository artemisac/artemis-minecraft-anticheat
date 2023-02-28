package cc.ghast.packet.buffer;

import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.exceptions.InvalidByteBufStructureException;
import cc.ghast.packet.nms.payload.MinecraftKey;
import cc.ghast.packet.wrapper.codec.StringPool;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.wrapper.netty.MutableByteBufAllocator;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;

@Getter
public class ProtocolByteBuf {
    private final MutableByteBuf byteBuf;
    private final ProtocolVersion version;
    private int id;

    public ProtocolByteBuf(final MutableByteBuf byteBuf, ProtocolVersion version) {
        this.byteBuf = byteBuf;
        this.version = version;
    }

    public ProtocolByteBuf(final MutableByteBuf byteBuf, int version) {
        this.byteBuf = byteBuf;
        this.version = ProtocolVersion.getVersion(version);
    }

    public int readVarInt() {
        return Converters.VAR_INT.read(this.byteBuf, version);
    }

    public void writeVarInt(final int i) {
        Converters.VAR_INT.write(this.byteBuf, i);
    }

    public String readString() {
        return Converters.STRING.read(byteBuf, version);
    }

    public void writeString(String s) {
        Converters.STRING.write(byteBuf, s);
    }

    public BlockPosition readBlockPositionFromLong() {
        return Converters.LOCATION_LONG.read(byteBuf, version);
    }

    public void writeBlockPositionIntoLong(BlockPosition position) {
        Converters.LOCATION_LONG.write(byteBuf, position);
    }

    public UUID readUUID(){
        return Converters.UUID.read(byteBuf, version);
    }

    public void writeUUID(UUID uuid){
        Converters.UUID.write(byteBuf, uuid);
    }

    public ItemStack readItem() {
        try {
            return Converters.ITEM_STACK.read(byteBuf, version);
        } catch (IOException e) {
            throw new InvalidByteBufStructureException(e);
        }
    }

    public void writeItem(ItemStack item) {
        Converters.ITEM_STACK.write(byteBuf, item);
    }

    public String readStringBuf(int i) {
        return Converters.STRING_POOL.read(byteBuf, version, i).getData();
    }

    public void writeStringBuf(String s, int size) {
        Converters.STRING_POOL.write(byteBuf, new StringPool(s, size));
    }

    public MinecraftKey readMinecraftKey() {
        return new MinecraftKey(this.readStringBuf(32767));
    }

    @SneakyThrows
    public CompoundTag readTag() {
        return Converters.NBT.read(byteBuf, version);
    }

    @SneakyThrows
    public void writeTag(CompoundTag compoundTag) {
        Converters.NBT.write(byteBuf, compoundTag);
    }

    public void readMinecraftKey(MinecraftKey key) {
        this.writeStringBuf(key.toString(), 32767);
    }

    public int capacity() {
        return this.byteBuf.capacity();
    }

    public MutableByteBuf capacity(final int i) {
        return this.byteBuf.capacity(i);
    }

    public int maxCapacity() {
        return this.byteBuf.maxCapacity();
    }

    public MutableByteBufAllocator alloc() {
        return this.byteBuf.alloc();
    }

    public ByteOrder order() {
        return this.byteBuf.order();
    }

    public MutableByteBuf order(final ByteOrder byteOrder) {
        return this.byteBuf.order(byteOrder);
    }

    public MutableByteBuf unwrap() {
        return this.byteBuf.unwrap();
    }

    public boolean isDirect() {
        return this.byteBuf.isDirect();
    }

    public int readerIndex() {
        return this.byteBuf.readerIndex();
    }

    public MutableByteBuf readerIndex(final int i) {
        return this.byteBuf.readerIndex(i);
    }

    public int writerIndex() {
        return this.byteBuf.writerIndex();
    }

    public MutableByteBuf writerIndex(final int i) {
        return this.byteBuf.writerIndex(i);
    }

    public MutableByteBuf setIndex(final int i, final int i1) {
        return this.byteBuf.setIndex(i, i1);
    }

    public int readableBytes() {
        return this.byteBuf.readableBytes();
    }

    public int writableBytes() {
        return this.byteBuf.writableBytes();
    }

    public int maxWritableBytes() {
        return this.byteBuf.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.byteBuf.isReadable();
    }

    public boolean isReadable(final int i) {
        return this.byteBuf.isReadable(i);
    }

    public boolean isWritable() {
        return this.byteBuf.isWritable();
    }

    public boolean isWritable(final int i) {
        return this.byteBuf.isWritable(i);
    }

    public MutableByteBuf clear() {
        return this.byteBuf.clear();
    }

    public MutableByteBuf markReaderIndex() {
        return this.byteBuf.markReaderIndex();
    }

    public MutableByteBuf resetReaderIndex() {
        return this.byteBuf.resetReaderIndex();
    }

    public MutableByteBuf markWriterIndex() {
        return this.byteBuf.markWriterIndex();
    }

    public MutableByteBuf resetWriterIndex() {
        return this.byteBuf.resetWriterIndex();
    }

    public MutableByteBuf discardReadBytes() {
        return this.byteBuf.discardReadBytes();
    }

    public MutableByteBuf discardSomeReadBytes() {
        return this.byteBuf.discardSomeReadBytes();
    }

    public MutableByteBuf ensureWritable(final int i) {
        return this.byteBuf.ensureWritable(i);
    }

    public int ensureWritable(final int i, final boolean b) {
        return this.byteBuf.ensureWritable(i, b);
    }

    public boolean getBoolean(final int i) {
        return this.byteBuf.getBoolean(i);
    }

    public byte getByte(final int i) {
        return this.byteBuf.getByte(i);
    }

    public short getUnsignedByte(final int i) {
        return this.byteBuf.getUnsignedByte(i);
    }

    public short getShort(final int i) {
        return this.byteBuf.getShort(i);
    }

    public int getUnsignedShort(final int i) {
        return this.byteBuf.getUnsignedShort(i);
    }

    public int getMedium(final int i) {
        return this.byteBuf.getMedium(i);
    }

    public int getUnsignedMedium(final int i) {
        return this.byteBuf.getUnsignedMedium(i);
    }

    public int getInt(final int i) {
        return this.byteBuf.getInt(i);
    }

    public long getUnsignedInt(final int i) {
        return this.byteBuf.getUnsignedInt(i);
    }

    public long getLong(final int i) {
        return this.byteBuf.getLong(i);
    }

    public char getChar(final int i) {
        return this.byteBuf.getChar(i);
    }

    public float getFloat(final int i) {
        return this.byteBuf.getFloat(i);
    }

    public double getDouble(final int i) {
        return this.byteBuf.getDouble(i);
    }

    public MutableByteBuf getBytes(final int i, final MutableByteBuf byteBuf) {
        return this.byteBuf.getBytes(i, byteBuf);
    }

    public MutableByteBuf getBytes(final int i, final MutableByteBuf byteBuf, final int i1) {
        return this.byteBuf.getBytes(i, byteBuf, i1);
    }

    public MutableByteBuf getBytes(final int i, final MutableByteBuf byteBuf, final int i1, final int i2) {
        return this.byteBuf.getBytes(i, byteBuf, i1, i2);
    }

    public MutableByteBuf getBytes(final int i, final byte[] bytes) {
        return this.byteBuf.getBytes(i, bytes);
    }

    public MutableByteBuf getBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        return this.byteBuf.getBytes(i, bytes, i1, i2);
    }

    public MutableByteBuf getBytes(final int i, final ByteBuffer byteBuffer) {
        return this.byteBuf.getBytes(i, byteBuffer);
    }

    public MutableByteBuf getBytes(final int i, final OutputStream outputStream, final int i1) throws IOException {
        return this.byteBuf.getBytes(i, outputStream, i1);
    }

    public int getBytes(final int i, final GatheringByteChannel gatheringByteChannel, final int i1) throws IOException {
        return this.byteBuf.getBytes(i, gatheringByteChannel, i1);
    }


    public MutableByteBuf setBoolean(final int i, final boolean b) {
        return this.byteBuf.setBoolean(i, b);
    }

    public MutableByteBuf setByte(final int i, final int i1) {
        return this.byteBuf.setByte(i, i1);
    }

    public MutableByteBuf setShort(final int i, final int i1) {
        return this.byteBuf.setShort(i, i1);
    }

    public MutableByteBuf setMedium(final int i, final int i1) {
        return this.byteBuf.setMedium(i, i1);
    }

    public MutableByteBuf setInt(final int i, final int i1) {
        return this.byteBuf.setInt(i, i1);
    }

    public MutableByteBuf setLong(final int i, final long l) {
        return this.byteBuf.setLong(i, l);
    }

    public MutableByteBuf setChar(final int i, final int i1) {
        return this.byteBuf.setChar(i, i1);
    }

    public MutableByteBuf setFloat(final int i, final float v) {
        return this.byteBuf.setFloat(i, v);
    }

    public MutableByteBuf setDouble(final int i, final double v) {
        return this.byteBuf.setDouble(i, v);
    }

    public MutableByteBuf setBytes(final int i, final MutableByteBuf byteBuf) {
        return this.byteBuf.setBytes(i, byteBuf);
    }

    public MutableByteBuf setBytes(final int i, final MutableByteBuf byteBuf, final int i1) {
        return this.byteBuf.setBytes(i, byteBuf, i1);
    }

    public MutableByteBuf setBytes(final int i, final MutableByteBuf byteBuf, final int i1, final int i2) {
        return this.byteBuf.setBytes(i, byteBuf, i1, i2);
    }

    public MutableByteBuf setBytes(final int i, final byte[] bytes) {
        return this.byteBuf.setBytes(i, bytes);
    }

    public MutableByteBuf setBytes(final int i, final byte[] bytes, final int i1, final int i2) {
        return this.byteBuf.setBytes(i, bytes, i1, i2);
    }

    public MutableByteBuf setBytes(final int i, final ByteBuffer byteBuffer) {
        return this.byteBuf.setBytes(i, byteBuffer);
    }

    public int setBytes(final int i, final InputStream inputStream, final int i1) throws IOException {
        return this.byteBuf.setBytes(i, inputStream, i1);
    }

    public int setBytes(final int i, final ScatteringByteChannel scatteringByteChannel, final int i1) throws IOException {
        return this.byteBuf.setBytes(i, scatteringByteChannel, i1);
    }

    public MutableByteBuf setZero(final int i, final int i1) {
        return this.byteBuf.setZero(i, i1);
    }

    public boolean readBoolean() {
        return this.byteBuf.readBoolean();
    }

    public byte readByte() {
        return this.byteBuf.readByte();
    }

    public short readUnsignedByte() {
        return this.byteBuf.readUnsignedByte();
    }

    public short readShort() {
        return this.byteBuf.readShort();
    }

    public int readUnsignedShort() {
        return this.byteBuf.readUnsignedShort();
    }

    public int readMedium() {
        return this.byteBuf.readMedium();
    }

    public int readUnsignedMedium() {
        return this.byteBuf.readUnsignedMedium();
    }

    public int readInt() {
        return this.byteBuf.readInt();
    }

    public long readUnsignedInt() {
        return this.byteBuf.readUnsignedInt();
    }

    public long readLong() {
        return this.byteBuf.readLong();
    }

    public char readChar() {
        return this.byteBuf.readChar();
    }

    public float readFloat() {
        return this.byteBuf.readFloat();
    }

    public double readDouble() {
        return this.byteBuf.readDouble();
    }

    public MutableByteBuf readBytes(final int i) {
        return this.byteBuf.readBytes(i);
    }

    public MutableByteBuf readSlice(final int i) {
        return this.byteBuf.readSlice(i);
    }

    public MutableByteBuf readBytes(final MutableByteBuf byteBuf) {
        return this.byteBuf.readBytes(byteBuf);
    }

    public MutableByteBuf readBytes(final MutableByteBuf byteBuf, final int i) {
        return this.byteBuf.readBytes(byteBuf, i);
    }

    public MutableByteBuf readBytes(final MutableByteBuf byteBuf, final int i, final int i1) {
        return this.byteBuf.readBytes(byteBuf, i, i1);
    }

    public MutableByteBuf readBytes(final byte[] bytes) {
        return this.byteBuf.readBytes(bytes);
    }

    public MutableByteBuf readBytes(final byte[] bytes, final int i, final int i1) {
        return this.byteBuf.readBytes(bytes, i, i1);
    }

    public MutableByteBuf readBytes(final ByteBuffer byteBuffer) {
        return this.byteBuf.readBytes(byteBuffer);
    }

    public MutableByteBuf readBytes(final OutputStream outputStream, final int i) throws IOException {
        return this.byteBuf.readBytes(outputStream, i);
    }

    public int readBytes(final GatheringByteChannel gatheringByteChannel, final int i) throws IOException {
        return this.byteBuf.readBytes(gatheringByteChannel, i);
    }

    public MutableByteBuf skipBytes(final int i) {
        return this.byteBuf.skipBytes(i);
    }

    public MutableByteBuf writeBoolean(final boolean b) {
        return this.byteBuf.writeBoolean(b);
    }

    public MutableByteBuf writeByte(final int i) {
        return this.byteBuf.writeByte(i);
    }

    public MutableByteBuf writeShort(final int i) {
        return this.byteBuf.writeShort(i);
    }

    public MutableByteBuf writeMedium(final int i) {
        return this.byteBuf.writeMedium(i);
    }

    public MutableByteBuf writeInt(final int i) {
        return this.byteBuf.writeInt(i);
    }

    public MutableByteBuf writeLong(final long l) {
        return this.byteBuf.writeLong(l);
    }

    public MutableByteBuf writeChar(final int i) {
        return this.byteBuf.writeChar(i);
    }

    public MutableByteBuf writeFloat(final float v) {
        return this.byteBuf.writeFloat(v);
    }

    public MutableByteBuf writeDouble(final double v) {
        return this.byteBuf.writeDouble(v);
    }

    public MutableByteBuf writeBytes(final MutableByteBuf byteBuf) {
        return this.byteBuf.writeBytes(byteBuf);
    }

    public MutableByteBuf writeBytes(final MutableByteBuf byteBuf, final int i) {
        return this.byteBuf.writeBytes(byteBuf, i);
    }

    public MutableByteBuf writeBytes(final MutableByteBuf byteBuf, final int i, final int i1) {
        return this.byteBuf.writeBytes(byteBuf, i, i1);
    }

    public MutableByteBuf writeBytes(final byte[] bytes) {
        return this.byteBuf.writeBytes(bytes);
    }

    public MutableByteBuf writeBytes(final byte[] bytes, final int i, final int i1) {
        return this.byteBuf.writeBytes(bytes, i, i1);
    }

    public MutableByteBuf writeBytes(final ByteBuffer byteBuffer) {
        return this.byteBuf.writeBytes(byteBuffer);
    }

    public int writeBytes(final InputStream inputStream, final int i) throws IOException {
        return this.byteBuf.writeBytes(inputStream, i);
    }

    public int writeBytes(final ScatteringByteChannel scatteringByteChannel, final int i) throws IOException {
        return this.byteBuf.writeBytes(scatteringByteChannel, i);
    }

    public MutableByteBuf writeZero(final int i) {
        return this.byteBuf.writeZero(i);
    }

    public int indexOf(final int i, final int i1, final byte b) {
        return this.byteBuf.indexOf(i, i1, b);
    }

    public int bytesBefore(final byte b) {
        return this.byteBuf.bytesBefore(b);
    }

    public int bytesBefore(final int i, final byte b) {
        return this.byteBuf.bytesBefore(i, b);
    }

    public int bytesBefore(final int i, final int i1, final byte b) {
        return this.byteBuf.bytesBefore(i, i1, b);
    }

    public ProtocolByteBuf copy() {
        return new ProtocolByteBuf(this.byteBuf.copy(), version);
    }

    public MutableByteBuf copy(final int i, final int i1) {
        return this.byteBuf.copy(i, i1);
    }

    public MutableByteBuf slice() {
        return this.byteBuf.slice();
    }

    public MutableByteBuf slice(final int i, final int i1) {
        return this.byteBuf.slice(i, i1);
    }

    public MutableByteBuf duplicate() {
        return this.byteBuf.duplicate();
    }

    public int nioBufferCount() {
        return this.byteBuf.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.byteBuf.nioBuffer();
    }

    public ByteBuffer nioBuffer(final int i, final int i1) {
        return this.byteBuf.nioBuffer(i, i1);
    }

    public ByteBuffer internalNioBuffer(final int i, final int i1) {
        return this.byteBuf.internalNioBuffer(i, i1);
    }

    public ByteBuffer[] nioBuffers() {
        return this.byteBuf.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(final int i, final int i1) {
        return this.byteBuf.nioBuffers(i, i1);
    }

    public boolean hasArray() {
        return this.byteBuf.hasArray();
    }

    public byte[] array() {
        return this.byteBuf.array();
    }

    public int arrayOffset() {
        return this.byteBuf.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.byteBuf.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.byteBuf.memoryAddress();
    }

    public String toString(final Charset charset) {
        return this.byteBuf.toString(charset);
    }

    public String toString(final int i, final int i1, final Charset charset) {
        return this.byteBuf.toString(i, i1, charset);
    }

    public int hashCode() {
        return this.byteBuf.hashCode();
    }

    public boolean equals(final Object o) {
        return this.byteBuf.equals(o);
    }

    public int compareTo(final MutableByteBuf byteBuf) {
        return this.byteBuf.compareTo(byteBuf);
    }

    public String toString() {
        return this.byteBuf.toString();
    }

    public MutableByteBuf retain(final int i) {
        return this.byteBuf.retain(i);
    }

    public MutableByteBuf retain() {
        return this.byteBuf.retain();
    }

    public int refCnt() {
        return this.byteBuf.refCnt();
    }

    public boolean release() {
        return this.byteBuf.release();
    }

    public boolean release(final int i) {
        return this.byteBuf.release(i);
    }

    public MutableByteBuf getByteBuf() {
        return this.byteBuf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
