 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService,brandService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				if($scope.paginationConf.totalItems != response.total) $scope.flag = true;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片列表
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);	
				//规格
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				
				//SKU列表规格列转换				
				for( var i in $scope.entity.itemList){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);		
				}
				
				if($scope.entity.goods.typeTemplateId!=null){
					typeTemplateService.findSpecList($scope.entity.goods.typeTemplateId).success(function(response){
						$scope.specList=response;
					});
				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	$scope.tempSearchEntity = {};//暂存搜索对象
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				if($scope.paginationConf.totalItems != response.total) $scope.flag = true;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	$scope.status=['未审核','已审核','审核未通过','关闭'];
	
	$scope.itemCatList=[];//商品分类列表
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(function(response){
			for(var i in response){
				$scope.itemCatList[response[i].id]=response[i].name;
			}
		});
	}
	
	$scope.brandList=[];//品牌列表
	$scope.findBrandList=function(){
		brandService.findAll().success(function(response){
			for(var i in response){
				$scope.brandList[response[i].id]=response[i].name;
			}
		});
	}
    
	$scope.checkAttributeValue=function(specName,optionName){
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',specName);
		if(object==null)return false;
		if(object.attributeValue.indexOf(optionName)<0)return false;
		return true;
	}
	
	$scope.updateStatus=function(status){
		goodsService.updateStatus($scope.selectIds,status).success(function(response){
			if(response.success){//成功
				$scope.reloadList();//刷新列表
			}else{
				alert(response.message);
			}
		});
	}
});	
