 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){	
	
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
	
	$scope.searchId=$location.search()['id'];//获取参数值
	$scope.searchCount=[];
	//查询实体 
	$scope.findOne=function(){
		if($scope.searchId==null){
			return ;
		}
		goodsService.findOne($scope.searchId).success(
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
			}
		);				
	}
	
	//保存 
	$scope.save=function(){		
		//提取文本编辑器的值
		$scope.entity.goodsDesc.introduction=editor.html();

		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
		        	editor.html('');//清空富文本编辑器
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
	
	//添加商品
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.add=function(){				
		$scope.entity.goodsDesc.introduction=editor.html();
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					alert(response.message);
		        	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
		        	editor.html('');//清空富文本编辑器
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	//添加图片列表
    $scope.add_image_entity=function(){    	
    	if($scope.image_entity.url=='../img/linker.png')return;
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //移除图片列表
    $scope.remove_image_entity=function(index){
	    $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

	//上传文件
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response) {
			if(response.success){
				$scope.image_entity.url=response.message;//设置文件地址
			}else{
				alert(response.message);
			}
		}).error(function() {           
    	    alert("上传发生错误");
        }); 
	}
	
	//删除文件
	$scope.deleFile=function(url){
		if(url=='../img/linker.png')return;
		uploadService.deleFile(url).success(function(response) {
			alert(response.message);
		}).error(function() {           
    	    alert("删除发生错误");
        }); 
	}
	
	//一级分类列表
	$scope.selectItemCat1List=function(){
		itemCatService.findByParentId(0).success(function(response){
			$scope.itemCat1List=response;
		});
	}
	//二级分类列表
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		if($scope.searchId==null||$scope.searchCount[0])$scope.entity.goods.category2Id='';
		if(newValue){
			itemCatService.findByParentId(newValue).success(function(response){
				$scope.itemCat2List=response;
			});
			$scope.searchCount[0]=true;
		}else{
			$scope.itemCat2List=[];
		}
	});
	//三级分类列表
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		if($scope.searchId==null||$scope.searchCount[1])$scope.entity.goods.category3Id='';
		if(newValue){
			itemCatService.findByParentId(newValue).success(function(response){
				$scope.itemCat3List=response;
			});
			$scope.searchCount[1]=true;
		}else{
			$scope.itemCat3List=[];
		}
	});
	//模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		if(newValue){
			itemCatService.findOne(newValue).success(function(response){
				$scope.entity.goods.typeTemplateId=response.typeId;
			});
		}else{
			$scope.entity.goods.typeTemplateId='';
		}
	});
	//监听模板ID
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		if($scope.searchId==null||$scope.searchCount[2])$scope.entity.goods.brandId='';
		if(newValue){
			typeTemplateService.findOne(newValue).success(function(response){
				$scope.typeTemplate=response;//模板
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌列表
				if($scope.searchId==null||!$scope.entity.goodsDesc.customAttributeItems.length){
					$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
				}
			});
			typeTemplateService.findSpecList(newValue).success(function(response){
				$scope.specList=response;
			});
			$scope.searchCount[2]=true;
		}else{
			$scope.typeTemplate={};
			$scope.typeTemplate.brandIds=[];
			$scope.entity.goodsDesc.customAttributeItems=[];
			$scope.specList=[];
		}
	});
	
	//根据复选框更新entity的规格选项
	$scope.updateSpecAttribute=function($event,name,value){
		//从规格数组中找到对应规格
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		//如果有此条规格
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{
				object.attributeValue.splice( object.attributeValue.indexOf(value),1);
				
				//如果此条规格不包含选项，将其移除
				if(object.attributeValue.length<=0){
					$scope.entity.goodsDesc.specificationItems.splice(
					$scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				};
			}
		}else{//没有这条规格，则创建个新的
			$scope.entity.goodsDesc.specificationItems.push({attributeName:name,attributeValue:[value]});
		}
	}
	
	//根据已选的规格选项，生成规格表格
	$scope.createItemList=function(){
		var sourceList = [{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];//初始值
		var targetList = [];//初始值
		
		for(var i in $scope.entity.goodsDesc.specificationItems){
			var item = $scope.entity.goodsDesc.specificationItems[i];
			
			targetList=[];
			for(var j in sourceList){
				var oldRow = sourceList[j]
				
				for(var k in item.attributeValue){
					var newRow = angular.copy(oldRow);
					
					newRow.spec[item.attributeName]=item.attributeValue[k];
					targetList.push(newRow);
				}
			}
			
			sourceList=targetList;
		}
		
		$scope.entity.itemList=targetList;
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
	
	$scope.checkAttributeValue=function(specName,optionName){
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',specName);
		if(object==null)return false;
		if(object.attributeValue.indexOf(optionName)<0)return false;
		return true;
	}
	
	$scope.marketable=['已下架','已上架'];
	$scope.updateIsMarketable=function(isMarketable){
		goodsService.updateIsMarketable($scope.selectIds,isMarketable).success(function(response){
			if(response.success){//成功
				$scope.reloadList();//刷新列表
			}else{
				alert(response.message);
			}
		});
	}
});	
