package dragor.com.webapp.controller;

import dragor.com.webapp.dto.UserDto;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user") // Fixed mapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Fixed injection issue

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDto) {
        System.out.println("Processing registration for user: " + userDto.getUsername()); // Debugging log
        return ResponseEntity.ok(userService.registerUser(userDto));
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        var authObject = userService.authenticateUser(userDto);
        var token = (String) authObject.get("token");
        System.out.println("Jwt token: " + token);
        return ResponseEntity.ok()
                .header("Authorization", token)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(authObject.get("user"));
    }
}
