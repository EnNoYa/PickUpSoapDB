var express = 'express';


var db = '../config/db';
var sql = 'mssql';

var x;
/*
function sqladd(req, res, next) {

  sql.connect(db, function (err) {
    if (err)
      console.log(err);

    var request = new sql.Request();
    request.input('userid', sql.NVarChar(50), req.body.userid)
      .input('pwd', sql.NVarChar(50), req.body.pwd)
      .input('username', sql.NVarChar(50), req.body.username)
      .input('email', sql.NVarChar(50), req.body.email)
      .query('insert into UserList (userid, pwd, username, email) values (@userid, @pwd, @username, @email)', function (err, result) {

        if (err) {
          console.log(err);
          res.send(err);
        }
        sql.close();
        res.redirect('/');
      });
  });
}*/



 
  
if (!String.prototype.format) {
  String.prototype.format = function() {
	var args = arguments;
	return this.replace(/{(\d+)}/g, function(match, number) { 
		return typeof args[number] != 'undefined'
		? args[number]
		: match
		;
		});
  };
}




	//var query =$('#searbo').val(); 
		var url   = 'https://opendata.epa.gov.tw/webapi/api/rest/datastore/355000000I-000136/?format=json&token=eongM8uzv0eKlLhGrOBQCw';//.format(query);
	
	// ajax prepare in jQuery
		$.ajaxPrefilter(function (options) {
			      console.log("ajax");
				if (options.crossDomain && jQuery.support.cors) {
				// check the protocol which the browser is using
					var http = (window.location.protocol === 'http:' ? 'http:' : 'https:');
				
				// make CORS-available url and setup the option
					options.url = http + '//cors-anywhere.herokuapp.com/' + options.url;
				
				// old version:
				// options.url = "http://cors.corsproxy.io/url=" + options.url;
				}}); 


	(function() {
  
  //加上&callback=?
  $.post( url, function(){
    format: "json"
  }).done(function(data) {
    	console.log(data);
    });
})(); 




	//新北 新莊 eongM8uzv0eKlLhGrOBQCw
			     