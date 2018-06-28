/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.services;

import java.util.List;
import java.util.Optional;
import org.elsquatrecaps.jig.sdl.exception.EntityNotFoundException;
import org.elsquatrecaps.jig.sdl.model.Resource;
import org.elsquatrecaps.jig.sdl.model.Search;
import org.elsquatrecaps.jig.sdl.model.SearchAndCount;
import org.elsquatrecaps.jig.sdl.model.SearchId;
import org.elsquatrecaps.jig.sdl.model.SearchResource;
import org.elsquatrecaps.jig.sdl.model.SearchResourceId;
import org.elsquatrecaps.jig.sdl.persistence.ResourceRepository;
import org.elsquatrecaps.jig.sdl.persistence.SearchRepository;
import org.elsquatrecaps.jig.sdl.searcher.SearcherResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.elsquatrecaps.jig.sdl.persistence.SearchResourceRepository;

@Service
public class PersistenceService {
    ResourceRepository resourceRepository;
    SearchResourceRepository searchResourceRepository;
    SearchRepository searchRepository;
    PlatformTransactionManager transactionManager;
    

    public PersistenceService(ResourceRepository resourceRepository, SearchResourceRepository searchResourceRepository, SearchRepository searchRepository, PlatformTransactionManager transactionManager) {
        this.resourceRepository = resourceRepository;
        this.searchResourceRepository = searchResourceRepository;
        this.searchRepository = searchRepository;
        this.transactionManager = transactionManager;
    }
    
    //searchs
    public List<Search> findAllSearch(){
        List<Search> list = searchRepository.findAll();
        return list;
    }
    
    public List<SearchAndCount> findAllSearchWithResourceCounter(){
        List<SearchAndCount> list = searchRepository.findAllWithResourcesCount();
        return list;
    }
    
    public Page<Search> findAllSearch(int pageNum,  int maxElements){
        return searchRepository.findAll(PageRequest.of(pageNum, maxElements));
    }
    
    private void saveResources(List<SearchResource> resources){
        if(resources!=null && resources.size()>0){
            for(SearchResource sr: resources){
                String rId = sr.getResource().getId();
                if(!resourceRepository.existsById(rId)){
                    resourceRepository.saveAndFlush(sr.getResource());
                }
            }
        }
    }

    public Search saveSearch(Search search){
        saveResources(search.getResourceList());
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        return transaction.execute((TransactionStatus status) -> {
            Search toSave;
            Optional<Search> optional = searchRepository.findOne(search.getRepository(), search.getSearchCriteria());
            if(optional.isPresent()){
                String updateDate = search.getOriginalDate();
                List<SearchResource> resources = search.getResourceList();
                toSave = optional.get();
                toSave.setUpdateDate(updateDate);
                if(resources!=null && resources.size()>0){
                    for(SearchResource sr: resources){
                        toSave.addResource(sr);
                        status.flush();
                    }
                }
            }else{
                toSave = search;
                for(SearchResource sr: toSave.getResourceList()){
                    resourceRepository.saveAndFlush(sr.getResource());
                }
                searchRepository.saveAndFlush(toSave);
            }
            return toSave;
        });
    }
    
    //resources
    public List<SearchResource> findAllResourceBySearch(SearchId id){
        return searchResourceRepository.findBySearchId(id);
    }

    public Page<SearchResource> findAllResourceBySearch(SearchId id, int pageNum,  int maxElements){
        return searchResourceRepository.findBySearchId(id, PageRequest.of(pageNum, maxElements));
    }

    public List<SearchResource> findAllResourceBySearch(Search search){
        return searchResourceRepository.findBySearchId(search.getId());
    }

    public Page<SearchResource> findAllResourceBySearch(Search search, int pageNum,  int maxElements){
        return searchResourceRepository.findBySearchId(search.getId(), PageRequest.of(pageNum, maxElements));
    }
    
    public SearchResource findResourceById(SearchResourceId id){
        SearchResource ret;
        Optional<SearchResource> optional = searchResourceRepository.findById(id);
        if(optional.isPresent()){
            ret = optional.get();
        }else{
            throw new EntityNotFoundException("SearchResource", "id", id);
        }
        return ret;
    }
    
    //searchs
    public Search findSearchById(SearchId id){
        Search ret;
        Optional<Search> optional = searchRepository.findById(id);
        if(optional.isPresent()){
            ret = optional.get();
        }else{
            throw new EntityNotFoundException("Search", "id", id);
        }
        return ret;       
    }
    
    public Optional<Search> findOne(String repository, String searchCriteria){
        return searchRepository.findOne(repository, searchCriteria);
    }
}
