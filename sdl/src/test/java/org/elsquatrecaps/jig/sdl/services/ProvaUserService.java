package org.elsquatrecaps.jig.sdl.services;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.elsquatrecaps.jig.sdl.model.ProvaUser;

@Service
public class ProvaUserService {

    private List<ProvaUser> users;

    public List<ProvaUser> findByUserName(String username) {

        List<ProvaUser> result = users.stream().filter(x -> x.getUsername().equalsIgnoreCase(username)).collect(Collectors.toList());

        return result;

    }

    public List<String> findUserNames() {
         List<String> result = users.stream().map(x -> x.getUsername()).collect(Collectors.toList());
        return result;
    }
    
// Init some users for testing
    @PostConstruct
    private void iniDataForTesting() {

        users = new ArrayList<ProvaUser>();

        ProvaUser user1 = new ProvaUser("mkyong", "password111", "mkyong@yahoo.com");
        ProvaUser user2 = new ProvaUser("yflow", "password222", "yflow@yahoo.com");
        ProvaUser user3 = new ProvaUser("laplap", "password333", "mkyong@yahoo.com");

        users.add(user1);
        users.add(user2);
        users.add(user3);

    }

}
