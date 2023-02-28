package cc.ghast.packet.wrapper.netty;

public interface MutableByteBufOutputStream {
    static MutableByteBufOutputStream build(MutableByteBuf byteBuf) {
        return null;
    }

    int readBytes();

    int available();

    void mark(int var1);

    boolean markSupported();

    int read();

    int read(byte[] var1, int var2, int var3);

    void reset();

    long skip(long var1);

    boolean readBoolean();

    byte readByte();

    char readChar();

    double readDouble();

    float readFloat();

    void readFully(byte[] var1);

    void readFully(byte[] var1, int var2, int var3);

    int readInt();

    String readLine();

    long readLong();

    short readShort();

    String readUTF();

    int readUnsignedByte();

    int readUnsignedShort();

    int skipBytes(int var1);

    void checkAvailable(int var1);

    Object getParent();

}
