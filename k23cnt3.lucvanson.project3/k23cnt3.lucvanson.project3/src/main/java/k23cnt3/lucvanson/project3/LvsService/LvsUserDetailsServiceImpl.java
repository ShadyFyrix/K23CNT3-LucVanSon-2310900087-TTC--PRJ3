package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LvsUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LvsUser user = lvsUserRepository.findByLvsUsernameOrLvsEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getLvsUsername(),
                user.getLvsPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getLvsRole().name())));
    }
}
