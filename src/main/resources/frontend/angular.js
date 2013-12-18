// http://stackoverflow.com/a/2548133
String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

var gdb4s = angular.module('gdb4s', ['hljs']);

gdb4s.controller('PageCtrl', function ($scope) {
    $scope.pages = {
        query : true,
        api : false,
        about : false
    }

    $scope.goTo = function(page) {
        for (var key in $scope.pages) {
            $scope.pages[key] = (key == page)
        }
    }
});

gdb4s.controller('QueryCtrl', ['$scope', '$http', function ($scope, $http) {

    request = function(method, url) {
        $http({"method": method, "url": url})
            .success(function(data) {
                $scope.apicall = method + " " + url
                if (data === "[]") {
                    $scope.err = "Not Found"
                    $scope.result = undefined
                }
                else{
                    $scope.result = JSON.stringify(data, undefined, 4)
                    $scope.err = undefined
                }
            })
            .error(function(){
                $scope.apicall = method + " " + url
                $scope.err = "Not Found"
                $scope.result = undefined
            })
    }

    $scope.query = function() {
        url = "/db/" + ($scope.obj ? $scope.obj : "-")
            + "/" + ($scope.pre ? $scope.pre : "-")
            + "/" + ($scope.sub ? $scope.sub : "-")

        if (url.endsWith("/-/-")) url = url.replace("/-/-", "")
        if (url.endsWith("/-")) url = url.replace("/-", "")

        request("GET", url)
    }

    $scope.add = function() {
        url = "/db/" + $scope.obj
            + "/" + $scope.pre
            + "/" + $scope.sub

        request("POST", url)
    }

    $scope.remove = function() {
        url = "/db/" + $scope.obj
            + "/" + $scope.pre
            + "/" + $scope.sub

        request("DELETE", url)
    }

}]);