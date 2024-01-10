<SCRIPT LANGUAGE="JavaScript">
	var w,v;
	w=window.open("","csmcGuiMainFrame");
	w.close();
	w=window.open("","csmcGuiLogin");
	if (w!=self) {
		window.close();
	}
	w=window.open("/pms/control/checkLogin","csmcGuiLogin","width="+(screen.availWidth)+",height="+(screen.availHeight)+",toolbar=0,location=0,directories=0,status=2,menubar=0,scrollbars=2,resizable=2,copyhistory=0");
	w.moveTo(-4,-4);
	w.resizeTo(screen.availWidth+8, screen.availHeight+8);
</SCRIPT>