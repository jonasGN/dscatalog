package jonas.gn.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jonas.gn.dscatalog.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}