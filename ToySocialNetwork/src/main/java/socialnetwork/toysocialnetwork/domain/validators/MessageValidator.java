package socialnetwork.toysocialnetwork.domain.validators;

import socialnetwork.toysocialnetwork.domain.Message;

public class MessageValidator implements Validator<Message>{
    /**
     * Overrides the validate method from Validator, adding functionality specific to Message objects.
     * @param entity = The entity to be validated.
     * @throws ValidationException if entity is not valid.
     */
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getContent().length() < 1)
            throw new ValidationException("Entity " + entity + " is not valid.");

    }
}
