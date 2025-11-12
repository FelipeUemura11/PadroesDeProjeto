package program3;
import java.time.Duration;

public final class SensorSnapshot {
    private final double temperatureCelsius;
    private final double pressureBar;
    private final double radiationSievert;
    private final boolean coolingSystemFailure;
    private final Duration timeAbove400C;

    private SensorSnapshot(Builder builder) {
        this.temperatureCelsius = builder.temperatureCelsius;
        this.pressureBar = builder.pressureBar;
        this.radiationSievert = builder.radiationSievert;
        this.coolingSystemFailure = builder.coolingSystemFailure;
        this.timeAbove400C = builder.timeAbove400C;
    }

    public double temperatureCelsius() {
        return temperatureCelsius;
    }

    public double pressureBar() {
        return pressureBar;
    }

    public double radiationSievert() {
        return radiationSievert;
    }

    public boolean coolingSystemFailure() {
        return coolingSystemFailure;
    }

    public Duration timeAbove400C() {
        return timeAbove400C;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private double temperatureCelsius;
        private double pressureBar;
        private double radiationSievert;
        private boolean coolingSystemFailure;
        private Duration timeAbove400C = Duration.ZERO;

        private Builder() {
        }

        public Builder temperatureCelsius(double value) {
            this.temperatureCelsius = value;
            return this;
        }

        public Builder pressureBar(double value) {
            this.pressureBar = value;
            return this;
        }

        public Builder radiationSievert(double value) {
            this.radiationSievert = value;
            return this;
        }

        public Builder coolingSystemFailure(boolean value) {
            this.coolingSystemFailure = value;
            return this;
        }

        public Builder timeAbove400C(Duration value) {
            if (value == null) {
                throw new IllegalArgumentException("timeAbove400C nao pode ser nulo");
            }
            this.timeAbove400C = value;
            return this;
        }

        public SensorSnapshot build() {
            return new SensorSnapshot(this);
        }
    }
}

