package com.rambus.cmks.cli.repository;

import com.rambus.cmks.cli.domain.RedisUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/** UserRepository - used for storage and retrieval */
@Repository
public interface UserRepository extends CrudRepository<RedisUser, String> {

}
