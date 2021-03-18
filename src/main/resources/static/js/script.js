function isNumberKey(evt){
	var charCode = (evt.which) ? evt.which : evt.KeyCode;
	
	if ((charCode >= 48 && charCode <= 57) || charCode <= 31){
		return true;
	}
	
	return false;
}

function searchRest(categoriaId) {
	var searchType = document.getElementById("searchType");
	
	if (categoriaId == null){
		searchType.value = "Texto";
	} else {
		searchType.value = "Categoria";
		document.getElementById("categoriaId").value = categoriaId;
	}
	
	document.getElementById("form").submit();
}

function setCmd(cmd) {
	document.getElementById("cmd").value = cmd;
	document.getElementById("form").submit();
}

function filterCategoria(categoria) {
	document.getElementById("categoria").value = categoria;
	document.getElementById("filterForm").submit();
}