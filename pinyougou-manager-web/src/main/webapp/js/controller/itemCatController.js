 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				if($scope.paginationConf.totalItems != response.total) $scope.flag = true;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.grade[$scope.grade.length-1].id);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.findByParentId($scope.grade[$scope.grade.length-1].id);//刷新列表
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	$scope.tempSearchEntity = {};//暂存搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				if($scope.paginationConf.totalItems != response.total) $scope.flag = true;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	$scope.findByParentId=function(parentId){
		$scope.selectIds=[];
		itemCatService.findByParentId(parentId).success(function(response){
			$scope.list=response;
		});
	}
	
	//面包屑导航
	$scope.grade=[{id:0,name:'顶级分类列表'}];
	$scope.setGrade=function(value){
		while($scope.grade.length>value+1&&$scope.grade.length>0){
			$scope.grade.pop();
		}
		$scope.findByParentId($scope.grade[value].id);
	}
	$scope.selectList=function(p_entity){
		$scope.grade.push(p_entity);
		$scope.findByParentId(p_entity.id);
	}
	
	//模板下拉列表
	$scope.typeList={data:[]};
	$scope.findTypeList=function(){
		typeTemplateService.selectOptionList().success(
			function(response){
				$scope.typeList={data:response};	
			}
		);		
	}
});	
