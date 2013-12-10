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

    $scope.query = function() {
        url = "/db/" + ($scope.obj ? $scope.obj : "-")
            + "/" + ($scope.pre ? $scope.pre : "-")
            + "/" + ($scope.sub ? $scope.sub : "-")

        if (url.endsWith("/-/-")) url = url.replace("/-/-", "")
        if (url.endsWith("/-")) url = url.replace("/-", "")

        $scope.url = url
        console.log(url)
        $http.get(url).success(function(data) {
            console.log(data)
            $scope.result = JSON.stringify(data, undefined, 4)
            $scope.err = undefined
        });
    }
}]);