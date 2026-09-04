package vn.hungjava.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.hungjava.model.UserEntity;
import vn.hungjava.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceDetail implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "User not found: " + username);
        }

        return user;
    }

    public UserDetails loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User not found with ID: " + userId
                        )
                );
    }
}
