package com.group.samrt.um.client.client.request;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLogRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;
    @Column(columnDefinition = "TEXT")
    private String logs;

    private LocalDateTime createdDt;
}