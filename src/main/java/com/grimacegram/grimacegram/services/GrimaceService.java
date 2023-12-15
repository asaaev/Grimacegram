package com.grimacegram.grimacegram.services;

import com.grimacegram.grimacegram.grimace.Grimace;
import com.grimacegram.grimacegram.model.User;
import com.grimacegram.grimacegram.repository.FileAttachmentRepository;
import com.grimacegram.grimacegram.repository.GrimaceRepository;
import com.grimacegram.grimacegram.shared.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Service
public class GrimaceService {

    GrimaceRepository grimaceRepository;
    UserService userService;
    FileAttachmentRepository fileAttachmentRepository;

    FileService fileService;
    public GrimaceService (GrimaceRepository grimaceRepository, UserService userService, FileAttachmentRepository fileAttachmentRepository,
                           FileService fileService){
        super();
        this.grimaceRepository = grimaceRepository;
        this.userService = userService;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.fileService = fileService;
    }

    public Grimace save(User user, Grimace grimace) {
        grimace.setTimestamp(new Date());
        grimace.setUser(user);
        if(grimace.getAttachment() != null) {
            FileAttachment inDB = fileAttachmentRepository.findById(grimace.getAttachment().getId()).get();
            inDB.setGrimace(grimace);
            grimace.setAttachment(inDB);
        }
        return grimaceRepository.save(grimace);
    }

    public Page<Grimace> getAllGrimaces(Pageable pageable) {
        return grimaceRepository.findAll(pageable);
    }

    public Page<Grimace> getGrimaceOfUser(String username, Pageable pageable) {
        User inDB = userService.getByUsername(username);
        return grimaceRepository.findByUser(inDB, pageable);
    }

    public Page<Grimace> getOldGrimaces(long id, String username, Pageable pageable) {
        Specification<Grimace> spec = Specification.where(idLessThan(id));
        if(username != null) {
            User inDB = userService.getByUsername(username);
            spec = spec.and(userIs(inDB));
        }

        return grimaceRepository.findAll(spec, pageable);

    }


    public List<Grimace> getNewGrimace(long id, String username, Pageable pageable) {
        Specification<Grimace> spec = Specification.where(idGreaterThan(id));
        if(username != null) {
            User inDB = userService.getByUsername(username);
            spec = spec.and(userIs(inDB));
        }
        return grimaceRepository.findAll(spec, pageable.getSort());
    }


    public long getNewGrimacesCount(long id, String username) {
        Specification<Grimace> spec = Specification.where(idGreaterThan(id));
        if(username != null) {
            User inDB = userService.getByUsername(username);
            spec = spec.and(userIs(inDB));
        }
        return grimaceRepository.count(spec);
    }

    private Specification<Grimace> userIs(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }
    private Specification<Grimace> idLessThan(long id){
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), id);
    }
    private Specification<Grimace> idGreaterThan(long id){
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }

    public void deleteGrimace(long id) {
        Grimace grimace = grimaceRepository.getOne(id);
        if(grimace.getAttachment() != null) {
            fileService.deleteAttachmentImage(grimace.getAttachment().getName());
        }
        grimaceRepository.deleteById(id);
    }
}
