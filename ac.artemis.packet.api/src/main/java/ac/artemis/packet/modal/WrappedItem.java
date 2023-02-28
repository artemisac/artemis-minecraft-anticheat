package ac.artemis.packet.modal;

import java.util.Objects;

/**
 * @author Ghast
 * @since 09-May-20
 */
public class WrappedItem {
    private short id;
    private byte amount;
    private short data;
    private Object tag;

    public WrappedItem(short id, byte amount, short data, Object tag) {
        this.id = id;
        this.amount = amount;
        this.data = data;
        this.tag = tag;
    }

    public WrappedItem(short id, byte amount, Object tag) {
        this.id = id;
        this.amount = amount;
        this.tag = tag;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getAmount() {
        return amount;
    }

    public void setAmount(byte amount) {
        this.amount = amount;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappedItem that = (WrappedItem) o;
        return id == that.id && amount == that.amount && data == that.data && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, data, tag);
    }

    @Override
    public String toString() {
        return "WrappedItem{" +
                "id=" + id +
                ", amount=" + amount +
                ", data=" + data +
                ", tag=" + tag +
                '}';
    }
}
