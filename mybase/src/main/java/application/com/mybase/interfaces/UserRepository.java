package application.com.mybase.interfaces;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.com.mybase.User;

@Repository
public interface UserRepository extends JpaRepository < User, Long > {
    User findByEmail(String email);
}