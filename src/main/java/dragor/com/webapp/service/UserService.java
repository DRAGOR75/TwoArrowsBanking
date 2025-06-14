package dragor.com.webapp.service;

import dragor.com.webapp.dto.UserDto;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository; // Inject UserRepository
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final UserDetailsService userDetailsService; // Inject UserDetailsService
    private final AuthenticationManager authenticationManager; // Inject AuthenticationManager
    private final JwtService jwtService; // Inject JwtService

    /**
     * Registers a new user.
     *
     * @param userDto The user data transfer object containing the user's details.
     * @return The registered user.
     */
    public User registerUser(UserDto userDto) {
        User user = mapToUser(userDto);
        return userRepository.save(user);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param userDto The user data transfer object containing the user's credentials.
     * @return A map containing the JWT token and the authenticated user.
     * @throws UsernameNotFoundException If the user is not found.
     */
    public Map<String, Object> authenticateUser(UserDto userDto) {
        Map<String, Object> authObject = new HashMap<String, Object>();
        User user = (User) userDetailsService.loadUserByUsername(userDto.getUsername());
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        authObject.put("token", "Bearer ".concat(jwtService.generateToken(userDto.getUsername())));
        authObject.put("user", user);
        return authObject;
    }

    /**
     * Maps a UserDto to a User entity.
     *
     * @param dto The user data transfer object.
     * @return The mapped User entity.
     */
    private User mapToUser(UserDto dto){
        return User.builder()
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .dob(dto.getDob())
                .roles(List.of("USER"))
                .tag("ta_" + dto.getUsername())
                .build();
    }

}

