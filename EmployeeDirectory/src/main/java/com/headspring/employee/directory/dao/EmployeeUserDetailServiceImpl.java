package com.headspring.employee.directory.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import com.headspring.employee.directory.service.EmployeeRepository;

public class EmployeeUserDetailServiceImpl implements UserDetailsService {
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	public UserDetails loadUserByUsername(String email)  throws UsernameNotFoundException {
		
		if(StringUtils.isEmpty(email)) {
			throw new RuntimeException("Email id is empty");
		}
		
		List<Employee> employees = employeeRepository.findByEmail(email);
		
		if ( employees.size() != 0) {
			throw new RuntimeException("Error ");
		}
		
		Employee employee = employees.get(0);
		User user = new User();
		employee.getEmail();
		
		return null;
	}

	
}

