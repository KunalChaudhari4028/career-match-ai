
package com.careermatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discovered_skills")
@Getter
@Setter
@NoArgsConstructor
public class DiscoveredSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String normalizedName;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    private LocalDateTime discoveredAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "discovered_skill_resumes",
            joinColumns = @JoinColumn(name = "discovered_skill_id"),
            inverseJoinColumns = @JoinColumn(name = "resume_id")
    )
    private Set<Resume> resumes = new HashSet<>();
}
