package dev.oauth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class AuthMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    private String password; // 인증 서버에는 이 필드가 필수입니다.
    private String name;
    private String role;
    private String email;
    private String gender;
    private LocalDate birthdate;

}
