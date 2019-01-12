
var timer=setInterval(getdata, 1800000);

//getdata();
function getdata (){

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
           console.log('ajax');
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

console.log('********************************************');  

  insertsql();

 });

var cont=0;
function insertsql(){ var Connection = require('tedious').Connection;     
 var config = {  
        userName: 'SA',  
        password: 'qM1agSB0FD1v',  
        server: '127.0.0.1',  
        // If you are on Azure SQL Database, you need these next options.  
       // options: {encrypt: true, database: 'AdventureWorks'}  
    };  
  var connection = new Connection(config);  
    
 connection.on('connect', function(err) {  
        // If no error, then good to proceed.  
        console.log("Connected");  
        executeStatement1();
       console.log("ALL FINISH");  
    });
   
   

    var Request = require('tedious').Request  
    var TYPES = require('tedious').TYPES;  

    function executeStatement1() {  
        
      // var tem2=tem.pop();  cont++;
           // console.log(cont);
          // console.log(tem2.SiteName);
            // console.log(tem2.Status); 
            var sqlquery="use weatherDB; INSERT into SiteData values";           
          for(var i=1;i<=tem.length;i++){
if(i>1){sqlquery+=',';}
sqlquery+="(@a"+i+",@b"+i+",getdate()"+",@d"+i+",@e"+i+",@f"+i+",@g"+i+",@h"+i+",@i"+i+")"}
            sqlquery+=';';
	 request = new Request(sqlquery, function(err) {  
         if (err) {  
            console.log(err);}  
        });  
            var temlen=tem.length;
         for(var i=1;i<=temlen;i++){
                  var tem2=tem.pop();
                  var SiteName="a"+i; 
                  var County="b"+i;
                  //var TestTime="c"+i; 
                  var SO2="d"+i;
                  var CO="e"+i; 
                  var O3="f"+i;
                  var PM10="g"+i; 
                  var PM25="h"+i;
                  var NO2="i"+i; 
        request.addParameter(SiteName, TYPES.NVarChar,tem2.SiteName);  
        request.addParameter(County, TYPES.NVarChar , tem2.County);
       // request.addParameter(TestTime, TYPES.DateTime,);  
        request.addParameter(SO2, TYPES.Float , ($.isNumeric(tem2.SO2)==true?parseFloat(tem2.SO2):null)); 
       request.addParameter(CO, TYPES.Float,($.isNumeric(tem2.CO)==true?parseFloat(tem2.CO):null));  
       request.addParameter(O3, TYPES.Float , ($.isNumeric(tem2.O3)==true?parseFloat(tem2.O3):null)); 
       request.addParameter(PM10, TYPES.Float,($.isNumeric(tem2.PM10)==true?parseFloat(tem2.PM10):null));  
       request.addParameter(PM25, TYPES.Float , ($.isNumeric(tem2["PM2.5"])==true?parseFloat(tem2["PM2.5"]):null)); 
       request.addParameter(NO2, TYPES.Float,($.isNumeric(tem2.NO2)==true?parseFloat(tem2.NO2):null));    
        } 
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
        
      return;   
    }
}
}
