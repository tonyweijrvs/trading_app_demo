package ca.jrvs.apps.trading.dao;

/**
 * Interface for generic CRUD operations on a repository for a specific type.
 */
public interface CrudRepository<E, ID> {

  /**
   * Create a given entity. Return saved entity.
   *
   * @param entity must not be {@literal null}.
   * @return the saved entity will never be {@literal null}.
   * @throws IllegalArgumentException if entity is invalid
   * @throws java.sql.SQLException if sql execution failed
   */
  E save(E entity);

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or null if none found
   * @throws IllegalArgumentException if {@code id} is {@literal null}.
   * @throws java.sql.SQLException if sql execution failed
   * @throws ResourceNotFoundException if no entity is found in db
   */
  E findById(ID id);

  /**
   * Returns whether an entity with the given id exists.
   *
   * @param id must not be {@literal null}.
   * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
   * @throws IllegalArgumentException if {@code id} is {@literal null}.
   * @throws java.sql.SQLException if sql execution failed
   * @throws ResourceNotFoundException if no entity is found in db
   */
  boolean existsById(ID id);

  /**
   * Deletes the entity with the given id.
   *
   * @param id must not be {@literal null}.
   * @throws IllegalArgumentException if {@code id} is {@literal null}.
   * @throws java.sql.SQLException if sql execution failed
   * @throws ResourceNotFoundException if no entity is found in db
   */
  void deleteById(ID id);

}
