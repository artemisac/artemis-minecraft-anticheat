package ac.artemis.core.v5.emulator.datawatcher;

public class DataKey<T> {
    private final int id;
    private final DataSerializer<T> serializer;

    public DataKey(int idIn, DataSerializer<T> serializerIn) {
        this.id = idIn;
        this.serializer = serializerIn;
    }

    public int getId() {
        return this.id;
    }

    public DataSerializer<T> getSerializer() {
        return this.serializer;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            DataKey<?> dataparameter = (DataKey) object;
            return this.id == dataparameter.id;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "<entity data: " + this.id + ">";
    }
}
