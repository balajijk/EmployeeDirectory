package com.headspring.employee.directory.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.headspring.employee.directory.dao.Employee;

@RepositoryRestResource
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

	List<Employee> findById(@Param("id") long id);
	List<Employee> findByEmail(@Param("email") String firstName);
	List<Employee> findByFirstName(@Param("firstName") String firstName);
	List<Employee> findByLastName(@Param("lastName") String lastName);

}
