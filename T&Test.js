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
        request = new Request("use TestDB; SELECT *  FROM weather;", function(err) {  
        if (err) {  
            console.log(err);}  
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
            res.send(result);  
            result ="";  
        });  

        request.on('done', function(rowCount, more) {  
        console.log(rowCount + ' rows returned');  
        });  
        connection.execSql(request);  
    }  




  res.send('Hello World!');
});

app.listen(3000, function () {
  console.log('Example app listening on port 3000!');
});