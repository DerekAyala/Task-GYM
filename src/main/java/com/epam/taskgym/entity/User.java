package com.epam.taskgym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "first_name", nullable=false)
    @NonNull
    private String firstName;
    @Column(name = "last_name", nullable=false)
    @NonNull
    private String lastName;
    @Column(name = "username", unique = true, nullable=false)
    @NonNull
    private String username;
    @Column(name = "password", nullable=false)
    @NonNull
    @JsonIgnore
    private String password;
    @Column(name = "is_active", nullable=false)
    @NonNull
    private Boolean isActive;
    @JsonIgnore
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        if (!username.equals(user.username)) return false;
        if (!password.equals(user.password)) return false;
        return isActive.equals(user.isActive);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + isActive.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "First Name='" + firstName +
                ", Last Name='" + lastName +
                ", Username='" + username +
                ", Is Active=" + isActive +
                '}';
    }
}
