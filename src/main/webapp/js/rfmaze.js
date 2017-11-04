function isBlank(str) {
	if (str == undefined) {
		return true;
	}

    if (!str || /^\s*$/.test(str)) {
        return true;
    }

    return false;
}

function isInteger(str) {
	if (isBlank(str)) {
		return false;
	}
    return /^\+?(0|[1-9]\d*)$/.test(str);
}