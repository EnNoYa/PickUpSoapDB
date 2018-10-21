
var timer=setInterval(getdata, 60000);


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
        password: '1qaz@WSX',  
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
            var sqlquery="use TestDB; INSERT into weather values";           
          for(var i=1;i<=tem.length;i++){
if(i>1){sqlquery+=',';}
sqlquery+="(@a"+i+",@b"+i+",getdate()"+",@d"+i+",@e"+i+",@f"+i+",@g"+i+",@h"+i+",@i"+i+",@j"+i+",@k"+i+",@l"+i+",@m"+i+",@n"+i+",@o"+i+")"}
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
                  var SO2Ans="j"+i;
                  var COAns="k"+i; 
                  var O3Ans="l"+i;
                  var PM10Ans="m"+i; 
                  var PM25Ans="n"+i;
                  var NO2Ans="o"+i;
        request.addParameter(SiteName, TYPES.NVarChar,tem2.SiteName);  
        request.addParameter(County, TYPES.NVarChar , tem2.County);
       // request.addParameter(TestTime, TYPES.DateTime,);  
        request.addParameter(SO2, TYPES.Decimal , tem2.SO2); 
        request.addParameter(CO, TYPES.Decimal,tem2.CO);  
        request.addParameter(O3, TYPES.Decimal , tem2.O3); 
        request.addParameter(PM10, TYPES.Decimalr,tem2.PM10);  
        request.addParameter(PM25, TYPES.Decimal , tem2["PM2.5"]); 
        request.addParameter(NO2, TYPES.Decimal,tem2.NO2);  
        request.addParameter(SO2Ans, TYPES.Integer , null); 
        request.addParameter(COAns, TYPES.Integer,null);  
        request.addParameter(O3Ans, TYPES.Integer , null); 
        request.addParameter(PM10Ans, TYPES.Integer,tem2.PM10_AVG);  
        request.addParameter(PM25Ans, TYPES.Integer , tem2["PM2.5_AVG"]); 
        request.addParameter(NO2Ans, TYPES.Integer,null);   
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