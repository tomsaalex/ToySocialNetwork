package socialnetwork.toysocialnetwork.domain.validators;


import socialnetwork.toysocialnetwork.domain.User;

/**
 * A validator for objects of type User, implementing the Validator interface
 */
public class UserValidator implements Validator<User>{
    /**
     * Overrides the validate method from Validator, adding functionality specific to User objects.
     * @param entity = The entity to be validated.
     * @throws ValidationException if entity is not valid.
     */
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName().length() == 0 ||
           entity.getLastName().length() == 0)
            throw new ValidationException("Entity " + entity + " is not valid.");
    }
}
