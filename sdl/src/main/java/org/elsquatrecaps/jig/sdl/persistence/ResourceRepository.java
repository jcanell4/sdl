/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.persistence;

import java.util.List;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String>{

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    List<Resource> findBySearchId(@Param("searchId") Long id);

    @Query("SELECT r FROM Search s INNER JOIN  s.resources r WHERE s.id = :searchId")
    Page<Resource> findBySearchId(@Param("searchId") Long id, Pageable pageable);
}
