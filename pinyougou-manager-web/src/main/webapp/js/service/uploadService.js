app.service('uploadService',function($http){
	//上传
	this.uploadFile=function(){
		var formData=new FormData();
		formData.append('file',file.files[0]);
		return $http({
			method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
		});
	}
	
	//删除
	this.deleFile=function(url){
		return $http.get('../delete.do?url='+url);
	} 
});