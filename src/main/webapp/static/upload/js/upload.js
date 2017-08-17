var Suze = {
    /**
	 * 全局变量
	 */
	Cts:{
		//urlPrefix:"/auditReason/",
		//act: $('#act').val()
	},
	/**
	 * 初始化入口
	 */
	init:function(){
		//this.initBtns();
		this.initUpload();
	},
	/**
	 * 初始化事件
	 */
	initEvts:function(){
	},
	/**
	 * 初始化按钮状态,让按钮不可用
	 */
	initBtns:function(){
		var arrays = ['btnModify','btnDel'];
		Util.setBtnEnable(arrays, false);//初始化导航功能按钮是否可用
	},
	/**
	 * 初始化上传插件
	 */
	initUpload:function(){
		var uploader = new plupload.Uploader({
			runtimes : 'html5,flash,silverlight,html4',
			browse_button : 'pickfiles', // you can pass an id...
			container: document.getElementById('container'), // ... or DOM Element itself
			url : '/workshop/uploadController/upload.do',
			flash_swf_url : '../../static/plugins/plupload-2.1.9/Moxie.swf',
			silverlight_xap_url : '../../static/plugins/plupload-2.1.9/Moxie.xap',
			chunk_size : '1mb',//分块大小，小于这个大小的不分块

			filters : {
				max_file_size : '10mb',
				mime_types: [
					{title : "Image files", extensions : "jpg,gif,png"},
					{title : "Zip files", extensions : "zip"}
				]
			},

			init: {
				PostInit: function() {
					document.getElementById('filelist').innerHTML = '';

					document.getElementById('uploadfiles').onclick = function() {
						uploader.start();
						return false;
					};
					document.getElementById('stopUpload').onclick = function() {
						uploader.stop();
					};
				},

				FilesAdded: function(up, files) {
					plupload.each(files, function(file) {
						document.getElementById('filelist').innerHTML += '<div id="' + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ') <b></b></div>';
					});
				},

				UploadProgress: function(up, file) {
					document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
				},

				Error: function(up, err) {
					document.getElementById('console').appendChild(document.createTextNode("\nError #" + err.code + ": " + err.message));
				}
			}
		});

		uploader.init();
	}
};
Suze.init();