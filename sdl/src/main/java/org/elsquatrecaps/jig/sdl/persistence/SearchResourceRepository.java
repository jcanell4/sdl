/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.persistence;

import java.util.List;
import org.elsquatrecaps.jig.sdl.model.SearchId;
import org.elsquatrecaps.jig.sdl.model.SearchResource;
import org.elsquatrecaps.jig.sdl.model.SearchResourceId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchResourceRepository extends JpaRepository<SearchResource, SearchResourceId>{

    @Query("SELECT sr FROM SearchResource sr WHERE sr.resource.id= :resourceid")
    List<SearchResource> findByResourceId(@Param("resourceid") String id);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    List<SearchResource> findBySearchId(@Param("searchId") SearchId id);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    Page<SearchResource> findBySearchId(@Param("searchId") SearchId id, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Search s INNER JOIN s.resources r WHERE s.id = :searchId")
    int countResourcesFromSearch(@Param("searchId") SearchId id);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id.repository = :repository AND s.id.searchCriteria = :searchCriteria")
    List<SearchResource> findBySearchId(@Param("repository") String repository, @Param("searchCriteria") String searchCriteria);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id.repository = :repository AND s.id.searchCriteria = :searchCriteria")
    Page<SearchResource> findBySearchId(@Param("repository") String repository, @Param("searchCriteria") String searchCriteria, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Search s INNER JOIN s.resources r WHERE s.id.repository = :repository AND s.id.searchCriteria = :searchCriteria")
    int countResourcesFromSearch(@Param("repository") String repository, @Param("searchCriteria") String searchCriteria);
}
