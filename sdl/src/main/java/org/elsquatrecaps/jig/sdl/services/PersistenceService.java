/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elsquatrecaps.jig.sdl.services;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.File;
import java.util.ArrayList;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.elsquatrecaps.jig.sdl.persistence.SearchResourceRepository;

@Service
public class PersistenceService {
    ResourceRepository resourceRepository;
    SearchResourceRepository searchResourceRepository;
    SearchRepository searchRepository;
    PlatformTransactionManager transactionManager;
    private String fileRepositoryPath=null;
    

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
    
    private void saveResource(SearchResource sr){
        String rId = sr.getResource().getId();
        if(!resourceRepository.existsById(rId)){
            resourceRepository.saveAndFlush(sr.getResource());
        }else{
            Resource oldResource = resourceRepository.findById(rId).get();
            Resource newResource = sr.getResource();
            boolean updateFormats = false;
            if(this.fileRepositoryPath!=null){
                String[] oldFormats = oldResource.getSupportedFormats();
                List<String> newFormats = Arrays.asList(newResource.getSupportedFormats());

                updateFormats = !newFormats.isEmpty();
                if(updateFormats){
                    for(int i=0; i<oldFormats.length; i++){
                        String oldFileName = oldResource.getFileName(oldFormats[i]);
                        if(newFormats.contains(oldFormats[i])){
                            String newFilename = newResource.getFileName(oldFormats[i]);
                            if(!oldFileName.equals(newFilename)){
                                File f = new File(this.fileRepositoryPath, oldFileName.concat(".").concat(oldFormats[i]));
                                f.delete();
                            }                            
                        }else{
                            File f = new File(this.fileRepositoryPath, oldFileName.concat(".").concat(oldFormats[i]));
                            f.delete();                            
                        }                        
                    }                    
                }
            }
            oldResource.updateSingleData(sr.getResource(), updateFormats);
            resourceRepository.saveAndFlush(oldResource);
        }
    }
     
    public void saveSearchResource(SearchResource searchResource){
        SearchResourceId id = searchResource.getId();
        saveResource(searchResource);
        if(!searchResourceRepository.existsById(id)){
            searchResourceRepository.saveAndFlush(searchResource);
        }
    }
    
    public void saveSearch(Search search){
        Search toSave;
        SearchId id = search.getId();
        Optional<Search> optional = searchRepository.findById(id);
        if(optional.isPresent()){
            toSave = optional.get();
            toSave.setUpdateDate(search.getOriginalDate());
        }else{
            toSave = search;
        }
        searchRepository.saveAndFlush(toSave);
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
       
    public SearchResource findSearchResourceById(SearchResourceId id){
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

    public void setFileRepositoryPath(String fileRepositoryPath) {
        this.fileRepositoryPath = fileRepositoryPath;
    }
    
    private boolean existsResourceById(String id){
        return resourceRepository.existsById(id);
    }

    public boolean existsSearchResourceById(SearchResourceId id){
        return searchResourceRepository.existsById(id);
    }
    
    public void changeIdResourceOnSearchResource(String resourceId, Resource newResource){
        List<SearchResource> srList;
        if(resourceRepository.existsById(resourceId)){
            srList = searchResourceRepository.findByResourceId(resourceId);
            for(SearchResource sr: srList){
                SearchResource newSr = new SearchResource(sr, newResource);
                searchResourceRepository.delete(sr);
                searchResourceRepository.flush();
                saveSearchResource(newSr);
            }
            resourceRepository.deleteById(resourceId);  
        }
    }
}
