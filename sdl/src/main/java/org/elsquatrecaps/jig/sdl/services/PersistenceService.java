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
public class PersistenceService {
    ResourceRepository resourceRepository;
    SearchRepository searchRepository;
    PlatformTransactionManager transactionManager;
    

    public PersistenceService(ResourceRepository resourceRepository, SearchRepository searchRepository, PlatformTransactionManager transactionManager) {
        this.resourceRepository = resourceRepository;
        this.searchRepository = searchRepository;
        this.transactionManager = transactionManager;
    }
    
    //searchs
    public List<Search> findAllSearch(){
        List<Search> list = searchRepository.findAll();
        return list;
    }
    
    public Page<Search> findAllSearch(int pageNum,  int maxElements){
        return searchRepository.findAll(PageRequest.of(pageNum, maxElements));
    }

    public Search saveSearch(Search search){
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        return transaction.execute((TransactionStatus status) -> {
            Search toSave;
            Optional<Search> optional = searchRepository.findOne(search.getRepository(), search.getSearchCriteria());
            if(optional.isPresent()){
                String updateDate = search.getOriginalDate();
                List<Resource> resources = search.getResourceList();
                toSave = optional.get();
                toSave.setUpdateDate(updateDate);
                if(resources!=null && resources.size()>0){
                    toSave.addAllResources(resources);
                }
            }else{
                toSave = search;
                searchRepository.saveAndFlush(toSave);
            }
            return toSave;
        });
    }
    
    //resources
    public List<Resource> findAllResourceBySerach(Long id){
        return resourceRepository.findBySearchId(id);
    }

    public Page<Resource> findAllResourceBySerach(Long id, int pageNum,  int maxElements){
        return resourceRepository.findBySearchId(id, PageRequest.of(pageNum, maxElements));
    }

    public List<Resource> findAllResourceBySerach(Search search){
        return resourceRepository.findBySearchId(search.getId());
    }

    public Page<Resource> findAllResourceBySerach(Search search, int pageNum,  int maxElements){
        return resourceRepository.findBySearchId(search.getId(), PageRequest.of(pageNum, maxElements));
    }
    
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
    
    //searchs
    public Search findSearchById(long id){
        Search ret;
        Optional<Search> optional = searchRepository.findById(id);
        if(optional.isPresent()){
            ret = optional.get();
        }else{
            throw new EntityNotFoundException("Search", "id", id);
        }
        return ret;       
    }
}
