package socialnetwork.toysocialnetwork.domain.validators;

/**
 * An interface representing a template for validators.
 * @param <T> The type of object the validator will operate on.
 */
public interface Validator<T> {
    /**
     * The abstract method that can be expanded to accommodate any validation strategy for different types of objects.
     * @param entity = The entity to be validated.
     * @throws ValidationException if entity is NOT valid.
     */
    void validate(T entity) throws ValidationException;
}
