package socialnetwork.toysocialnetwork.domain.validators;


import socialnetwork.toysocialnetwork.domain.Friendship;

/**
 * A validator for objects of type Friendship, implementing the Validator interface
 */
public class FriendshipValidator implements Validator<Friendship>{
    /**
     * Overrides the validate method from Validator, adding functionality specific to Friendship objects.
     * @param entity = The entity to be validated.
     * @throws ValidationException if entity is not valid.
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getU1ID() == null || entity.getU1ID() < 1
                ||entity.getU2ID() == null || entity.getU2ID() < 1)
            throw new ValidationException("Entity " + entity + " is not valid.");
    }
}
