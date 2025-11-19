package net.devlord.trendy.repository;

import net.devlord.trendy.model.entity.TrendExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrendExampleRepository extends JpaRepository<TrendExample, Long> {
    
    List<TrendExample> findByTrendIdOrderByDisplayOrderAsc(Long trendId);
    
    void deleteByTrendId(Long trendId);
}

