package com.smartcontactmanagaer.SCMApplication.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uId;

    @NotBlank(message = "Name Required!")
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters allowed!")
    private String name;


    @Column(unique = true)
    @NotBlank(message = "Email Required!")
    private String email;

    @NotBlank(message = "Password Required!")
    private String password;

    @Column(length = 500)
    @Size(max = 500, message = "Word limit 500 characters")
    private String description;
    private String role;
    private boolean isActive;
    private String imageUrl;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Contact> contactList;
}
