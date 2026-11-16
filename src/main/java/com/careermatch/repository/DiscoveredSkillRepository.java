
package com.careermatch.repository;

import com.careermatch.entity.DiscoveredSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscoveredSkillRepository
        extends JpaRepository<DiscoveredSkill, Long> {

    Optional<DiscoveredSkill> findByNormalizedName(String normalizedName);

    boolean existsByNormalizedName(String normalizedName);
}
