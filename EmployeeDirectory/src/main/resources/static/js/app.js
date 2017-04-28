'use strict';


var employeeDirectoryApp = angular.module('employeeDirectoryApp', ['ngRoute', 'ngLocale']).config(function($routeProvider, $httpProvider, $locationProvider) {
	
	$locationProvider.html5Mode(true);
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    
    

	$routeProvider.when('/home', {
		controller : 'EmployeeListController'
	}).when('/login', {
		templateUrl : '/login.html',
		controller : 'EmployeeListController'
	}).otherwise('/home');



}).controller('EmployeeListController', function ($rootScope, $scope,$http, $filter, $location, $window) {

	//Initialize variables
	$scope.toggle=true;	
	$scope.messageText="";
	$scope.message=false;
	
		  var authenticate = function(credentials, callback) {

		    var headers = credentials ? {authorization : "Basic "
		        + btoa(credentials.username + ":" + credentials.password)
		    } : {};

		    $http.get('user', {headers : headers}).success(function(data) {
		      if (data.name) {
		        $rootScope.authenticated = true;
		        $rootScope.principal = data;
		        if (data !==null && data.authorities != null && data.authorities.length > 0) {
		        	$rootScope.isAdmin = (data.authorities[0].authority == "ROLE_ADMIN");
		        }
		      } else {
		        $rootScope.authenticated = false;
		      }
		      callback && callback();
		    }).error(function() {
		      $rootScope.authenticated = false;
		      callback && callback();
		    });

		  }

		  authenticate();
		  $scope.credentials = {};
		  $scope.login = function() {
		      authenticate($scope.credentials, function() {
		        if ($rootScope.authenticated) {
		        	$scope.error = false;
		        	$location.path('/home');
		        	$window.location.href = '/home';
		        } else {
		          $location.path("/login");
		          $scope.error = true;
		        }
		      });
		  };
	
		  $scope.logout = function() {
				$http.post('logout', {}).success(function() {
					$rootScope.authenticated = false;
					$rootScope.principal=null;
					$location.path("/login");
					 $window.location.href = '/login';
				}).error(function(data) {
					console.log("Logout failed")
					$rootScope.authenticated = false;
				});
			}

	$scope.editEmployee = function editEmployee(employee) {
	    	 console.log(employee);
	    	 $scope.employee = employee;
    	 
	    	 $scope.firstName= employee.firstName; 
	         $scope.lastName = employee.lastName;
	         $scope.email = employee.email;
	         $scope.phone = employee.phone;  
	         $scope.location= employee.location;
			 $scope.jobTitle= employee.jobTitle;
			 $scope.startDate= employee.startDate;				 
			 $scope.edit="true";
	    	 $scope.toggle = ! $scope.toggle;
	}
	
	$scope.addUpdateEmployee = function addUpdateEmployee(employee) {
		$scope.messageText="";
		$scope.message=false;
		
		if($scope.edit) {
			updateEmployee(employee);
		}
		else {
			addEmployee();
		}
		list();
        $scope.toggle = ! $scope.toggle;
        $scope.firstName=""; 
        $scope.lastName ="";
        $scope.email = "";
        $scope.phone = "";  
        $scope.location="";
		 $scope.jobTitle="";
		 $scope.startDate="";
	}
	
	// Update DB and refresh list
	function updateEmployee(employee) {
		$http.patch(employee._links.self.href, {
			 firstName: $scope.firstName,
			 lastName: $scope.lastName,
			 email: $scope.email,
			 phone: $scope.phone,
			 location: $scope.location,
			 jobTitle: $scope.jobTitle,
			 startDate: $scope.startDate
			 
        }).
		  success(function(data, status, headers) {
		    $scope.messageText="Employee " + $scope.firstName + " Updated ";
			$scope.message=true;
		   });
	}
	
	
	
	$scope.deleteEmployee = function deleteEmployee(href) {
		
		 $http.delete(href).
		  success(function(data, status, headers) {
			  $scope.messageText="Employee Deleted. href=" + href;
			  $scope.message=true;
           
           list();
           
		    });
	}
	
		
	//add a new Employee
		function addEmployee() {
			if($scope.firstName=="" || $scope.lastName =="" || $scope.email == "" || $scope.phone == "" 
				|| $scope.firstName== null || $scope.lastName == null || $scope.email == null || $scope.phone == null	
			){
				alert("Please enter all details");
			}
			else{
			 $http.post('/employees', {
				 firstName: $scope.firstName,
				 lastName: $scope.lastName,
				 email: $scope.email,
				 phone: $scope.phone,
				 location: $scope.location,
				 jobTitle: $scope.jobTitle,
				 startDate: $scope.startDate				 
	         }).
			  success(function(data, status, headers) {
				  $scope.messageText="Employee " + $scope.firstName+ " added";
				  $scope.message=true;
	             var loc = headers()["location"];
	             console.log("Might be good to GET " + loc + " and append the task.");
	             list();
			    });
			}
		}
		
		function list() {
			 $http.get('/employees').
		     success(function (data) {
		    	 $scope.employees = data._embedded.employees;
		     });
		}		
		list();
		
		
		$scope.toggleAddEditEmp = function(isAdd) {
			$scope.messageText="";
			$scope.message=false;
			$scope.toggle = !$scope.toggle;
			$scope.edit= !isAdd;
		}
});
