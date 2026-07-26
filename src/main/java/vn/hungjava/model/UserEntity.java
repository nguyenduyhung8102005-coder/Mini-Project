package vn.hungjava.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.hungjava.common.Gender;
import vn.hungjava.common.UserStatus;
import vn.hungjava.common.UserType;

import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class UserEntity extends AbstractEntity<Long> implements UserDetails, Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth_day")
    @Temporal(TemporalType.DATE)
    private Date birthDay;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone",  length = 15)
    private String phone;

    @Column(name = "username", unique = true, nullable = true ,length = 255)
    private String username;

    @Column(name = "password",  length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type")
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private UserStatus status;

//    @Column(name = "created_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    @CreationTimestamp
//    private String createdAt;
//
//    @Column(name = "update_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    @UpdateTimestamp
//    private String updateAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserHasRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<GroupHasUser> groups = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //1. Get role
        List<Role> roleList = roles.stream().map(UserHasRole::getRole).toList();

        //2. Get role name
        List<String> roleNames = roleList.stream().map(Role::getName).toList();

        //3. Add role name to Authority
        return roleNames.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(status);
    }
}
