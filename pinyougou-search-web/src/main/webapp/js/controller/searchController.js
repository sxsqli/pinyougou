app.controller("searchController",function($scope,$location,searchService){
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40};
	$scope.tempSearchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40};
	
	$scope.doSearch=function(){
		angular.copy($scope.tempSearchMap,$scope.searchMap);
		$scope.search();
	}
	
	$scope.search=function(){
		searchService.search($scope.searchMap).success(function(response){
			$scope.resultMap=response;//搜索返回的结果
			buildPageLabel();
		});
	}
	
	$scope.addSearchItem=function(key,value){
		if(key=='category'||key=='brand'||key=='price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		$scope.searchMap.pageNo=1;
		$scope.search();
	}
	
	$scope.removeSearchItem=function(key,value){
		if(key=='category'||key=='brand'||key=='price'){
			$scope.searchMap[key]='';
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.searchMap.pageNo=1;
		$scope.search();
	}
	
	var buildPageLabel=function(){
		$scope.pageLabel=[];//新增分页栏属性
		var firstPage = 1;
		var lastPage = $scope.resultMap.totalPages;
		if($scope.resultMap.totalPages>5){
			if($scope.searchMap.pageNo<=3){
				lastPage = 5;
			}else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
				firstPage = $scope.resultMap.totalPages-4;
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}
		
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
		$scope.tempPageNo=$scope.searchMap.pageNo;
	}
	
	$scope.queryByPage=function(pageNo){
		if(pageNo<1||pageNo>$scope.resultMap.totalPages){
			return;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	$scope.sortSearch=function(sortField,sortValue){
		$scope.searchMap.sortField=sortField;	
		$scope.searchMap.sortValue=sortValue;	
		$scope.search();
	}
	
	$scope.keywordsIsBrand=function(){
		for(var i in $scope.resultMap.brandList){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	$scope.loadkeywords=function(){
		if($location.search()['keywords']){
			$scope.tempSearchMap.keywords=$location.search()['keywords'];
			$scope.doSearch();
		}
	}
});