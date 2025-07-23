package lk.ijse.desktop.myfx.myfinalproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UserDto {
    private String id;
    private String userName;
    private String password;
    private String email;

    public UserDto(String id) {
        this.id = id;
    }
}
