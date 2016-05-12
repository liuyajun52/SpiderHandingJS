//Lets require/import the HTTP module
var http = require('http');
var cluser = require('cluster');
var qs = require('querystring');
var phantom = require('phantom');
var url = require('url');

const DEFAULT_WORK_AMOUNT = 3;
const DEFAULT_PORT = 10080;
const DEFAULT_HOST = '0.0.0.0';

var workAmount = (process.argv[2] == undefined ? DEFAULT_WORK_AMOUNT : process.argv[2]);
var host = (process.argv[3] == undefined ? DEFAULT_HOST : process.argv[3]);
var port = (process.argv[4] == undefined ? DEFAULT_PORT : process.argv[4]);
var phInstance=null;
if(cluser.isMaster){
    console.log('JS Page Crawler run on '+host+':'+port);
    for(var i =0;i<workAmount;i++){
        cluser.fork();
    }
    cluser.on('exit',function (worker,code,signal) {
        cluser.fork();
    });
}else {
    phInstance = phantom.create(['--ignore-ssl-errors=yes', '--load-images=no']);
    http.createServer(function (req, res) {
            if (req.method == 'POST') {
                var body = '';
                req.on('data', function (data) {
                    body += data;
                    // Too much POST data, kill the connection!
                    // 1e6 === 1 * Math.pow(10, 6) === 1 * 1000000 ~~~ 1MB
                    if (body.length > 1e6) {
                        res.writeHead(413, {'Content-Type': 'application/json'});
                        res.end(
                            JSON.stringify({errno: 1, msg: 'post data too long'})
                        );
                        req.connection.destroy();
                        return;
                    }
                });

                req.on('end', function () {
                    var post = qs.parse(body);
                    var url = post['url'];
                    if (url == undefined) {
                        res.writeHead(400)
                        res.end(
                            JSON.stringify({errno: 2, msg: 'need param "url"'})
                        );
                        return;
                    }
                    getHTMLFromPhantom(url, res);
                });
            } else {
                var queryData = url.parse(req.url, true).query;
                var u = queryData.url;
                if (u == undefined) {
                    res.writeHead(400, {'Content-Type': 'application/json'});
                    res.end(
                        JSON.stringify({errno: 2, msg: 'need param "url"'})
                    );
                    return;
                }
                getHTMLFromPhantom(u, res);
            }
        }
    ).listen(port, host);
}

function getHTMLFromPhantom(url, res) {
    var sitepage=null;
    phInstance.then(function (ph) {
        return ph.createPage();
    }).then(function (page) {
        sitepage=page;
        return page.open(url);
    }).then(function(status){
            return sitepage.property('content');
        })
        .then(function (content) {
            // console.log(sitepage.property('Content-Type'));
            sitepage.close();
            res.writeHead(200);
            res.end(content);
        })
        .catch(function (error) {
            res.writeHead(500);
            res.end();
        })
    ;
}
