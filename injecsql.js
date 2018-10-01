var Connection = require('tedious').Connection;  
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
    });  

    var Request = require('tedious').Request  
    var TYPES = require('tedious').TYPES;  

    function executeStatement1() {  
        request = new Request("use TestDB; INSERT into ttest values(@id,@name) ;", function(err) {  
         if (err) {  
            console.log(err);}  
        });  
        request.addParameter('id', TYPES.Int,9);  
        request.addParameter('name', TYPES.NVarChar , 'X');  
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
