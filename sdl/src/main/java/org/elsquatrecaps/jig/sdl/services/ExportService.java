/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.services;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.elsquatrecaps.jig.sdl.exception.EntityNotFoundException;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
import org.elsquatrecaps.jig.sdl.persistence.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ExportService {
    ResourceRepository resourceRepository;
    //SearchRepository searchRepository;
    //PlatformTransactionManager transactionManager;
    

    public ExportService(ResourceRepository resourceRepository /*, SearchRepository searchRepository, PlatformTransactionManager transactionManager*/) {
        this.resourceRepository = resourceRepository;
        //this.searchRepository = searchRepository;
        //this.transactionManager = transactionManager;
    }
    
    
    // El resource es pot treure del persistence service
    public Resource findResourceById(String id){
        Resource ret;
        Optional<Resource> optional = resourceRepository.findById(id);
        if(optional.isPresent()){
            ret = optional.get();
        }else{
            throw new EntityNotFoundException("Resource", "id", id);
        }
        return ret;
    }
    
}
