package etu.sprint.service;

import etu.sprint.entity.Blog;
import etu.sprint.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> findAllActive() {
        return blogRepository.findByActifTrueOrderByDatePublicationDesc();
    }

    public List<Blog> findRecent(int limit) {
        return blogRepository.findByActifTrueOrderByDatePublicationDesc(PageRequest.of(0, limit));
    }

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    public Optional<Blog> findById(Integer id) {
        return blogRepository.findById(id);
    }

    public Blog save(Blog blog) {
        return blogRepository.save(blog);
    }

    public void deleteById(Integer id) {
        blogRepository.deleteById(id);
    }
}
