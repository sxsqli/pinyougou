package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService {
	@Reference
	private SellerService sellerService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		TbSeller seller = sellerService.findOne(username);
		if (seller == null) {
			throw new UsernameNotFoundException("username not found");
		}
		
		if (!"1".equals(seller.getStatus())) {
			throw new UsernameNotFoundException("seller has not been checked");
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		return new User(username, seller.getPassword(), authorities);

	}

}
