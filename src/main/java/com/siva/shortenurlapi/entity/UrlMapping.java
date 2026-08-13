package com.siva.shortenurlapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "url_mapping")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String longUrl;

    @Column(unique = true)
    private String alias;

    private LocalDateTime createdAt;

    private Integer expiryDays;

    private LocalDateTime expiryDate;

    private long clickCount;

    private LocalDateTime lastAccessedAt;

    private boolean expired;


}
