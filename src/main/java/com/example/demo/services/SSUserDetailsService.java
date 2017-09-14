package com.example.demo.services;



import com.example.demo.models.Authority;
import com.example.demo.models.Person;
import com.example.demo.repositories.PersonRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
public class SSUserDetailsService implements UserDetailsService {

  private PersonRepo personRepo;

  public SSUserDetailsService(PersonRepo personRep){
      this.personRepo=personRep;
  }
    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
      try {
          Person person = personRepo.findByUsername(username);
          if (person == null) {
//              System.out.println("User not Found with the provided username" + person.toString());
              return null;
          }
//          System.out.println("user from username" + person.toString());
          return new org.springframework.security.core.userdetails.User(person.getUsername(), person.getPassword(),
                  getAuthorities(person));
      }catch(Exception e){
          throw new UsernameNotFoundException("User not found");
      }
  }
      private Set<GrantedAuthority> getAuthorities(Person person) {
          Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
          for (Authority role : person.getAuthorities()) {
              GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getRole());
              authorities.add(grantedAuthority);

          }
//          System.out.println("User Authorities are" + authorities.toString());
          return authorities;
      }
}
