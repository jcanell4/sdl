package org.elsquatrecaps.jig.sdl.persistence;

import java.util.List;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.SearchAndCount;
import org.elsquatrecaps.jig.sdl.model.SearchId;
import org.elsquatrecaps.jig.sdl.model.SearchResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<Search, SearchId>{
    
    @Query("SELECT s as search, COUNT(r) as count FROM Search s INNER JOIN s.resources r GROUP BY s.id")
    List<SearchAndCount> findAllWithResourcesCount();
    
    @Query("SELECT s FROM Search s WHERE s.id.repository = lower(:repository) AND s.id.searchCriteria = lower(:searchCriteria)")
    Optional<Search> findOne(@Param("repository") String repository, @Param("searchCriteria") String searchCriteria);
    
//    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
//    List<SearchResource> findResourcesBySearchId(@Param("searchId") Long id);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    Page<SearchResource> findResourcesBySearchId(@Param("searchId") Long id, Pageable pageable);
}
