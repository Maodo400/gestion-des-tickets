package com.brianaubry.helpdesk.repository;

import com.brianaubry.helpdesk.model.Role;
import com.brianaubry.helpdesk.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findById(int id);
    
}
