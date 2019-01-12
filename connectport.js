var express = require('express'); var app = express();

app.get('/recentAQI', function (req, res) {

   var Connection = require('tedious').Connection;  
    var config = {  
        userName: 'SA',  
        password: 'qM1agSB0FD1v', 
        server: '127.0.0.1',  
       
    };  
    var connection = new Connection(config);  
    connection.on('connect', function(err) {  
        // If no error, then good to proceed.  
        console.log("Connected");  
        executeStatement();  
    });  

    var Request = require('tedious').Request;  
    var TYPES = require('tedious').TYPES;  
   
    function executeStatement() {
  	var bakres=[];
        var check=0;
        request = new Request("use weatherDB; SELECT *  FROM SiteAnswer where TestTime=(select max(TestTime) from SiteAnswer) FOR JSON AUTO;", function(err) {  
        if (err) {  
            console.log(err);}  
       	    connection.close();
	 });  
        var result = "";
	request.on('row', function(columns) {  
            columns.forEach(function(column) {  
              if (column.value === null) {  
                console.log('NULL');  
              } else {  
                result+= column.value ;  
              }  
            });  
          // res.send(result)
         // console.log(check); 
         // bakres.push(result);
	// console.log(bakres); 
check++;
	  //  result ="";  
        });
 bakres.push(result);
      request.on("error",function(rowCount, more) {  
        res.send(bakres);
        console.log(rowCount + ' rows returned');  
        }); 

      request.on('doneProc', function(rowCount, more) {  
        res.send(result);
        console.log(rowCount + ' rows returned');  
        });  
       // console.log("************"+bakres);
	//res.send(bakres);  
     	 connection.execSql(request);  
  	 }   
});


app.get('/recentAIR', function (req, res) {

   var Connection = require('tedious').Connection;
    var config = {
        userName: 'SA',
        password: 'qM1agSB0FD1v',
        server: '127.0.0.1',

    };
    var connection = new Connection(config);
    connection.on('connect', function(err) {
        // If no error, then good to proceed.
        console.log("Connected");
        executeStatement();
    });
 var Request = require('tedious').Request;
    var TYPES = require('tedious').TYPES;

    function executeStatement() {
        var bakres=[];
        var check=0;
         request = new Request("use weatherDB; SELECT *  FROM SiteData where TestTime=(select max(TestTime) from SiteData) FOR JSON AUTO;", function(err) {
        if (err) {
            console.log(err);}
            connection.close();
         });
  var result = "";
        request.on('row', function(columns) {
            columns.forEach(function(column) {
              if (column.value === null) {
                console.log('NULL');
              } else {
                result+= column.value ;
              }
            });
check++;
          //  result ="";
        });
 bakres.push(result);
      request.on("error",function(rowCount, more) {
        res.send(bakres);
        console.log(rowCount + ' rows returned');
        });

      request.on('doneProc', function(rowCount, more) {
        res.send(result);
        console.log(rowCount + ' rows returned');
        });
       // console.log("************"+bakres);
        //res.send(bakres);
         connection.execSql(request);
         }
});



app.listen(9487, function () {
  console.log('Llistening on port 9487!');
});
