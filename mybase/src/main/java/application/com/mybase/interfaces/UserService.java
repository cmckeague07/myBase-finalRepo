package application.com.mybase.interfaces;
import org.springframework.security.core.userdetails.UserDetailsService;

import application.com.mybase.User;
import application.com.mybase.UserRegistrationDto;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration);
}