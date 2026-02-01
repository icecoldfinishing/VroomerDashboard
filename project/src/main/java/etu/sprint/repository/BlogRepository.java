package etu.sprint.repository;

import etu.sprint.entity.Blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    
    List<Blog> findByActifTrueOrderByDatePublicationDesc();
    
    List<Blog> findByActifTrueOrderByDatePublicationDesc(Pageable pageable);
}
