package com.carlosvc.repaso_springboot.users.repositories;

import com.carlosvc.repaso_springboot.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);

    @Modifying
    @Query("UPDATE User u SET u.isDeleted =true WHERE u.id = :id")
    void updateIsDeletedToTrueById(Long id);
    
    List<User> findAllByIsDeletedFalse();
}
