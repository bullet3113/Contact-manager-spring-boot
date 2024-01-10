package com.smartcontactmanagaer.SCMApplication.Repository;

import com.smartcontactmanagaer.SCMApplication.Entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

//    @Query(value = "select * from Contact where user_u_id = :userId", nativeQuery = true)
//    public List<Contact> findContactsByUser(@Param("userId") int userId);

    // pagination
    // pageable object contains
    // current page
    // contacts per page
    @Query(value = "select * from Contact where user_u_id = :userId", nativeQuery = true)
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);

    public Contact findByEmail(String email);
}
