package com.cdweb.Treestore.services.implement;

import com.cdweb.Treestore.config.SystemConstant;
import com.cdweb.Treestore.repository.RoleRepository;
import com.cdweb.Treestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.cdweb.Treestore.domain.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = this.userRepository.findByEmailIgnoreCaseAndIsEnabled(email, SystemConstant.ACTIVE_USER);
		if (user == null) {
			System.out.println("User not found! " + email);
			throw new UsernameNotFoundException("email " +email + " was not found in the database or not active");
		}


		List<String> roleNames = this.roleRepository.getRoleNames(user.getId());

		List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
		if (roleNames != null) {
			for (String role : roleNames) {
				GrantedAuthority authority = new SimpleGrantedAuthority(role);
				grantList.add(authority);
			}
		}
		UserDetails userDetails = (UserDetails)new org.springframework.security.core.userdetails.User(user.getEmail(), //
				user.getPassword(), grantList);

		return userDetails;
	}

}