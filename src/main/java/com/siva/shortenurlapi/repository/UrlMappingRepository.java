package com.siva.shortenurlapi.repository;

import com.siva.shortenurlapi.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    boolean existsByAlias(String alias);
    Optional<UrlMapping> findByAlias(String alias);

}
