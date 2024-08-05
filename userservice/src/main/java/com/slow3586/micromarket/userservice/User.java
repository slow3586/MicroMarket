package com.slow3586.micromarket.userservice;

import com.slow3586.micromarket.api.audit.AuditEntityListener;
import com.slow3586.micromarket.api.spring.DefaultEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity(name = "`user`")
@EntityListeners(AuditEntityListener.class)
public class User extends DefaultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    UUID id;
    @Length(min = 4, max = 16)
    @Column(unique=true)
    String login;
    @NotBlank
    String password;
    @Length(min = 4, max = 16)
    String name;
}
