package org.ead.identitymanagement.repository;

import org.bson.types.ObjectId;
import org.ead.identitymanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);

    Boolean deleteUserByEmail(String email);
}
