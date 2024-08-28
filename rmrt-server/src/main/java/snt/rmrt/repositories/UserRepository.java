package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
