var express = require('express');
var app = express();

app.get('/', function (req, res) {

   var Connection = require('tedious').Connection;  
    var config = {  
        userName: 'SA',  
        password: '1qaz@WSX',  
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
  	var bakres=["sitename"];
        var check=0;
        request = new Request("use TestDB; SELECT site  FROM weather ;", function(err) {  
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
                result+= column.value + " ";  
              }  
            });  
          // res.send(result)
          console.log(check); 
         bakres.push(result)
	// console.log(bakres); 
check++;
	    result ="";  
        });
      request.on("error",function(rowCount, more) {  
        res.send(bakres);
        console.log(rowCount + ' rows returned');  
        }); 

      request.on('doneProc', function(rowCount, more) {  
        res.send(bakres);
        console.log(rowCount + ' rows returned');  
        });  
       // console.log("************"+bakres);
	//res.send(bakres);  
     	 connection.execSql(request);  
  	 }  




 
});

app.listen(9487, function () {
  console.log('Example app listening on port 9487!');
});
