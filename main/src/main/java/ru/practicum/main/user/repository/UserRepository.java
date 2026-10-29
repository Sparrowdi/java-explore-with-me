package ru.practicum.main.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT u 
            FROM User u 
            WHERE :ids IS NULL OR u.id IN :ids""")
    List<User> findUsersByIds(@Param("ids") List<Long> ids, Pageable pageable);
}