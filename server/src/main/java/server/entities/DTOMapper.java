package server.entities;

import server.service.exceptions.NotFoundInDatabaseException;

public interface DTOMapper<ENTITY, DTO> {

    /**
     * Transforms entity to corresponding DTO
     * @param entity entity to transform
     * @return corresponding DTO
     */
    DTO toDTO(ENTITY entity);

    /**
     * Fetches entity corresponding to the DTO from the server
     * @param dto DTO to transform
     * @param args additional context arguments
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if a entity is not present in the database
     */
    ENTITY getEntity(DTO dto, Object ...args) throws NotFoundInDatabaseException;

    /**
     * Transforms DTO to corresponding entity
     * @param dto DTO to transform
     * @param args additional context arguments
     * @return corresponding entity
     * @throws NotFoundInDatabaseException if some dependant object is not present in the database
     */
    ENTITY newEntity(DTO dto, Object ...args) throws NotFoundInDatabaseException;
}
