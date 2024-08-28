package snt.rmrt.models.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class User implements Serializable {

    @Id
    private String username;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private UserRoles role = UserRoles.ROLE_USER;

}

