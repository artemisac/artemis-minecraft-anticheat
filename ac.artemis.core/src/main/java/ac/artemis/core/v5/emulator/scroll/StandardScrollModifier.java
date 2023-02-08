package ac.artemis.core.v5.emulator.scroll;

import java.util.UUID;

public class StandardScrollModifier implements ScrollModifier {
    private UUID uuid;
    private String name;
    private int operation;
    private double amount;

    @Override
    public ScrollModifier id(final UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public ScrollModifier name(final String name) {
        this.name = name;
        return this;
    }

    @Override
    public ScrollModifier operation(final int operation) {
        this.operation = operation;
        return this;
    }

    @Override
    public ScrollModifier amount(final double amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOperation() {
        return operation;
    }

    @Override
    public double getAmount() {
        return amount;
    }
}
