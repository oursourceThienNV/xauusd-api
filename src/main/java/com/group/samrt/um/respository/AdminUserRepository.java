package com.group.samrt.um.respository;

import com.group.samrt.um.domain.uml.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser,String>, JpaSpecificationExecutor<AdminUser> {
    AdminUser findByUsername(String username);
    Optional<AdminUser> findAdminUserById(Long id);
    Optional<AdminUser> findAdminUserByUsername(String username);
    Optional<AdminUser> findByUsernameAndStatus(String username,String status);
    @Query("""
    SELECT u
    FROM AdminUser u
    WHERE (
        (
            :role = '00'
            AND u.role IN ('02', '03')
        )
        OR
        (
            :role = '01'
            AND u.role = '03'
            AND u.createdBy = :createdBy
        )
    )
    AND (
        :keyword IS NULL
        OR :keyword = ''
        OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    ORDER BY u.username ASC
""")
    Page<AdminUser> findAccountList(
            @Param("keyword") String keyword,
            @Param("role") String role,
            @Param("createdBy") String createdBy,
            Pageable pageable
    );

    long countByStatus(String status);
    @Query("""
    SELECT COUNT(u)
    FROM AdminUser u
    WHERE u.licenseExpiredDt IS NOT NULL
      AND u.licenseExpiredDt < CURRENT_TIMESTAMP
""")
    long countExpiredAccounts();
}
