package dev.oauth.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "resource_member_profile")
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate birthdate;

    protected MemberProfile() {
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }
}
