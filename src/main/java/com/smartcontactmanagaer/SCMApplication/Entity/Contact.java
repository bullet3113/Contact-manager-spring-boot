package com.smartcontactmanagaer.SCMApplication.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cId;

    @NotBlank(message = "Name Required!")
    @Size(min = 2, max = 20, message = "min 2 and max 20 characters allowed!")
    private String name;
    private String nickName;
    private String work;

    @Size(min = 10, max = 10, message = "10 digit phone required! Exclude country code.")
    @NotBlank(message = "Phone Required!")
    private String phone;

    @Column(unique = true)
    @NotBlank(message = "Email Required!")
    private String email;

    @Column(length = 500)
    @Size(max = 500, message = "Word limit 500 characters")
    private String description;
    private String imageUrl;

    @ManyToOne
    private User user;
}
