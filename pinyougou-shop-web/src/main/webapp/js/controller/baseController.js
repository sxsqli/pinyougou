 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
		$scope.selectIds=[];
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: -1,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
			 if ($scope.flag) {
					$scope.flag = false;
					return;
				}
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	
	//模糊查询
	$scope.doSearch = function() {
		angular.copy($scope.tempSearchEntity,$scope.searchEntity)
		if ($scope.paginationConf.currentPage == 1) {
			$scope.reloadList();
		} else {
			$scope.paginationConf.currentPage = 1;
		}
	}
	
	//解析json
	$scope.jsonToString = function(jsonString,key) {
		var json = JSON.parse(jsonString);
		var value = "";
		for(var i in json){
			if(i>0)value+="，";
			value +=json[i][key];
		}
		return value;
	}
	
	//从json对象数组中找到指定对象(此对象中key对应的值为keyValue)
	$scope.searchObjectByKey = function(list,key,keyValue){
		for(var i in list){
			if(list[i][key]==keyValue){
				return list[i];//找到
			}
		}
		return null;//没找到
	}
	
	//全选
	$scope.checkAll=function($event,keys){
		$scope.selectIds=[];
		if($event.target.checked){
			for(var i in $scope.list){
				var pojo = $scope.list[i];
				for(var j in keys){
					pojo = pojo[keys[j]];
				}
				$scope.selectIds.push(pojo.id);
			}
		}else{
			$scope.selectIds=[];
		}
	}
});	