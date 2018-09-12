app.controller('loginController',function($scope,$controller,loginService){
	
	$controller('baseController',{$scope:$scope});
	
	$scope.getLoginUser=function(){
		loginService.loginUser().success(function(response){
			$scope.loginUser=response;
		});
	}
	
});