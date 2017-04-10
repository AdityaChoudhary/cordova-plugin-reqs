var exec = require('cordova/exec');

module.exports = function (data, done, fail) {
	data = data || {};

	fail = fail || function () {
			return {};
		};

	done = done || function () {
			return {};
		};

	exec(function (resp) {
		window.test = resp;

		if (resp.code < 100 || resp.code > 599) {
			fail({data: new TypeError('Network request failed'), msgs: resp});
		} else {
			done(resp);
		}
	}, function (resp) {
		fail({data: new TypeError('Network request failed'), msgs: resp});
	}, 'ReqsPlugin', 'make', [data.type || 'GET', data.path || '', data.body || '', data.head || {}]);
};