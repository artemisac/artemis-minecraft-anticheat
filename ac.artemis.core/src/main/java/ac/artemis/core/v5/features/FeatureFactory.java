package ac.artemis.core.v5.features;

public interface FeatureFactory<T> {
    /**
     * Builds the feature of type T using the parameters provided.
     * This class should be extended with an appropriate factory template.
     *
     * @return New instance of T
     */
    T build();
}
