package socialnetwork.toysocialnetwork.repository.memory;

import socialnetwork.toysocialnetwork.domain.Entity;
import socialnetwork.toysocialnetwork.domain.validators.Validator;
import socialnetwork.toysocialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private final Validator<E> validator;
    private final Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }

    @Override
    public E save(E newEntity) {
        if(newEntity == null)
        {
            throw new IllegalArgumentException();
        }

        validator.validate(newEntity);

        for(E entity: findAll())
        {
            if(entity.equals(newEntity))
                return entity;
        }

        entities.put(newEntity.getId(), newEntity);
        return null;
    }

    @Override
    public E findOne(ID entityID) {
        if(entityID == null)
            throw new IllegalArgumentException();

        return entities.get(entityID);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E update(E newEntity) {
        if(newEntity == null)
            throw new IllegalArgumentException();

        validator.validate(newEntity);

        //TODO: I added this check for uniqueness when updating just like it is for saving. I hope this is good.
        E entityToUpdate = findOne(newEntity.getId());
        for(E entity: findAll())
        {
            if(entity.equals(newEntity) && entity.getId() != entityToUpdate.getId())
                return newEntity;
        }

        //entities.put(newEntity.getId(), newEntity); TODO: de ce e nevoie de linia asta? Nu strica tot?

        if(entities.get(newEntity.getId()) != null) {
            entities.put(newEntity.getId(), newEntity);
            return null;
        }
        return newEntity;

    }

    @Override
    public E delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException();
        if(entities.get(id) == null)
        {
            return null;
        }
        return entities.remove(id);
    }


}
