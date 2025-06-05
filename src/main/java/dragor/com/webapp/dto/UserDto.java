package dragor.com.webapp.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String uid;
    private String firstName;
    private String lastName;
    private  String username;
    private Date dob;
    private long tel;
    private String password;
    private String gender;
}
