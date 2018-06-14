package org.elsquatrecaps.jig.sdl.persistence;

import java.util.List;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.SearchAndCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long>{
    
    @Query("SELECT s as search, COUNT(r) as count FROM Search s JOIN s.resources r GROUP BY s.id")
    List<SearchAndCount> findAllWithResourcesCount();
    
    @Query("SELECT s FROM Search s WHERE s.repository = :repository AND s.searchCriteria = :searchCriteria")
    Optional<Search> findOne(@Param("repository") String repository, @Param("searchCriteria") String searchCriteria);
    
    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    List<Resource> findResourcesBySearchId(@Param("searchId") Long id);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    Page<Resource> findResourcesBySearchId(@Param("searchId") Long id, Pageable pageable);
}
