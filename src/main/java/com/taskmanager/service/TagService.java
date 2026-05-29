package com.taskmanager.service;

import com.taskmanager.exception.DuplicateResourceException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.model.Tag;
import com.taskmanager.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag create(String name, String color, String description) {
        log.info("[CREATE] Tag nou: {}", name);
        if (tagRepository.existsByName(name)) {
            throw new DuplicateResourceException("Tag-ul '" + name + "' exista deja.");
        }
        Tag tag = Tag.builder()
                .name(name)
                .color(color != null ? color : "#6c757d")
                .description(description)
                .build();
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", id));
    }

    public Tag update(Long id, String name, String color, String description) {
        Tag tag = findById(id);
        tag.setName(name);
        tag.setColor(color);
        tag.setDescription(description);
        return tagRepository.save(tag);
    }

    public void delete(Long id) {
        Tag tag = findById(id);
        tagRepository.delete(tag);
        log.info("[DELETE] Tag id={} sters", id);
    }
}
