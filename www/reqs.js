(function () {
	var exec = require('cordova/exec');

	module.exports = function (data) {
		return new Promise(function (done, fail) {
			exec(function (resp) {
				if (resp.status < 100 || resp.status > 599) {
					fail(new TypeError('Network request failed'));
					return;
				}

				done({
					url: resp.url,
					status: resp.status,
					responseText: resp.responseText,
					getAllResponseHeaders: function () {
						return resp.responseHeaders;
					}
				});
			}, function (resp) {
				fail(new TypeError('Network request failed'))

			}, 'ReqsPlugin', 'gets', [data.method, data.url, '', data.headers]);
		})
	};
})();