package ac.artemis.packet.modal;

public class WrappedBlock {
    private int id;
    private int data;

    public WrappedBlock(int id, int data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WrappedBlock that = (WrappedBlock) o;

        if (id != that.id) return false;
        return data == that.data;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + data;
        return result;
    }
}
