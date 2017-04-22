function isBlank(str) {
	if (str == undefined) {
		return true;
	}

    if (!str || /^\s*$/.test(str)) {
        return true;
    }

    return false;
}
