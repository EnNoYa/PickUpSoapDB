
var jsdom = require('jsdom');
const { JSDOM } = jsdom;
const { window } = new JSDOM();
const { document } = (new JSDOM('')).window;
global.document = document;

var $ = jQuery = require('jquery')(window);



var tem;

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

     


 
    var url   = 'http://opendata.epa.gov.tw/webapi/Data/REWIQA/?$orderby=SiteName&$skip=0&$top=1000&format=json&token=eongM8uzv0eKlLhGrOBQCw';//.format(query);
  
  // ajax prepare in jQuery
    $.ajaxPrefilter(function (options) {
           
        if (options.crossDomain && jQuery.support.cors) {
        // check the protocol which the browser is using
          var http = (window.location.protocol === 'http:' ? 'http:' : 'https:');
        
        // make CORS-available url and setup the option
          options.url = http + '//cors-anywhere.herokuapp.com/' + options.url;
        
        // old version:
        // options.url = "http://cors.corsproxy.io/url=" + options.url;
        }}); 


$.get( url, function(response){
      var res=response;
     tem=res;

    console.log(tem);
    insertsql();

 });


var Connection = require('tedious').Connection;  
    var config = {  
        userName: 'SA',  
        password: '1qaz@WSX',  
        server: '127.0.0.1',  
        // If you are on Azure SQL Database, you need these next options.  
       // options: {encrypt: true, database: 'AdventureWorks'}  
    };  
    var connection = new Connection(config);  
    
    function insertsql() {connection.on('connect', function(err) {  
        // If no error, then good to proceed.  
        console.log("Connected");  
        executeStatement1();  
    });  }

    var Request = require('tedious').Request  
    var TYPES = require('tedious').TYPES;  

    function executeStatement1() {  
        var cont=0;
        while(tem.length>0){var tem2=tem.pop();  cont++; console.log("N "+cont);
            request = new Request("use TestDB; INSERT into weather values(@site,@status) ;", function(err) {  
         if (err) {  
            console.log(err);}  
        });  
        request.addParameter('site', TYPES.NvarChar(10),tem2.SiteName);  
        request.addParameter('status', TYPES.NVarChar(10) , tem2.Status);  
        request.on('row', function(columns) {  
            columns.forEach(function(column) {  
              if (column.value === null) {  
                console.log('NULL');  
              } else {  
                console.log("Product id of inserted item is " + column.value);  
              }  
            });  
        });       
        connection.execSql(request); 
        } 
        console.log("ALL FIN");
    }

