package org.ead.identitymanagement.repository;

import org.bson.types.ObjectId;
import org.ead.identitymanagement.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;



import java.util.Optional;


public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);

    void deleteUserByEmail(String email);
}
