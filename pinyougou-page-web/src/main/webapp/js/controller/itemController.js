app.controller("itemController",function($scope,$http){
	
	//操作购买数量
	$scope.num=1;
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	//选择规格
	$scope.specificationItems={};
	$scope.selectSpecification=function(key,value){
		$scope.specificationItems[key]=value;
		searchSKU();
	}
	
	//判断规格是否被选中
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}
		return false;
	}
	
	$scope.loadSKU=function(){
		$scope.sku=skuList[0];
		angular.copy($scope.sku.spec,$scope.specificationItems);
	}
	
	//两对象是否相等
	var matchObject=function(obj1,obj2){
		for(var attr in obj1){
			if(obj1[attr]!=obj2[attr])return false;
		}
		for(var attr in obj2){
			if(obj1[attr]!=obj2[attr])return false;
		}
		return true;
	}
	
	//查找SKU
	var searchSKU=function(){
		for(var i in skuList){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的
	}
	
	//添加商品到购物车
	$scope.addToCart=function(){
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(function(response){
			if(response.success){
				 location.href='http://localhost:9107/cart.html';//跳转到购物车页面
			 }else{
				 alert(response.message);
			 }	
		});	
	}
	
	
});