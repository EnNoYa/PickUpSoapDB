
var jsdom = require('jsdom');
const { JSDOM } = jsdom;
const { window } = new JSDOM();
const { document } = (new JSDOM('')).window;
global.document = document;

var $ = jQuery = require('jquery')(window);

var x;

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
    var url   = 'http://opendata.epa.gov.tw/webapi/Data/REWIQA/?$orderby=SiteName&$skip=0&$top=1000&format=json&token=eongM8uzv0eKlLhGrOBQCw';//.format(query);
  
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


/*  (function() {
  
  //加上&callback=?
  $.get( url, function(){
    format: "json"
  }).done(function(data) {
      console.log(data);
    });
})(); */
$.get( url, function(response){
      var res=response;
     x=res;
    console.log('fin');
    console.log(x);

 });



