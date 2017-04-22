<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>


<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<script type="text/javascript" language="javascript" src="js/rfmaze.js"></script>

<SCRIPT language="javascript">  
function hardwareassignment(obj) {
	if (obj.selectedIndex == 0) {
		alert("Selection is invalid. Please try again!");
		return;
	}
    document.getElementById('monitor').submit();	
}

</SCRIPT>
   
<s:if test="%{errorMessage != null}">
    <div class="error_message"><font size="+1"> </font><s:property value="errorMessage"/></font></div>
	    <img src="images/spacer.gif" width="1" height="20"/>
</s:if>
	   
<s:form theme="simple" action="monitor.action" method="post">
    <table class="ConfigDataTable" width="90%" align="center">
        <thead><tr><th colspan="2">Matrix Assignment Monitor</th></tr></thead>
        <tbody>
      	    <tr><td colspan="2"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
            <tr>
                <td align="right" width="40%">Select hardware:</td>
                <td align="left" width="60%">
                    <s:select cssStyle="width:260px;" label="Hardware" headerKey="-1" headerValue="-- select hardware --" list="hardwarelist" name="hardware" onchange="hardwareassignment(this);"/>
                </td>
            </tr>       
            <tr><td colspan="2"><img src="images/spacer.gif" width="1" height="10"/></td></tr>
        </tbody>
    </table>

    <s:if test="%{warningMessage != null}">
        <div class="warn_message"><s:property value="warningMessage" /></div>
    </s:if>
    <s:else>
	<table class="matrix" id="matrix_monitor" align="center">
	    <thead>
	    <tr>
	        <th>&nbsp;</th>
		    <s:iterator value="tableHeader" status="tableHeaderStatus">
		    <th>
		        <a class="tooltip" href="#" style="cursor:default"><s:property value="%{label}"/>
	                <span class="info"><s:property value="%{description}"/></span>
			    </a>
		    </th>
		    </s:iterator>
		</tr>
		</thead>
		<s:iterator value="matrix" status="rowsStatus" var="row">
	    <tr>
	        <td align="center" style="background: #005566; color:white; white-space:nowrap; width: 120px; max-width:120px; font-weight:bold;">
	            <a class="tooltip" href="#"><s:property value="%{#row[0].label}"/>
	                <span class="info"><s:property value="%{#row[0].description}"/></span>
		        </a>
	        </td>
	   	    <s:iterator value="#row" status="rowsStatus">
	   	    <s:if test='%{name=="-"}'>	   	    
	      	<td align="center" style="background:green;"><s:property value="%{name}"/></td>
	      	</s:if>
	   	    <s:else>
	  	    <td align="center" style="background: orange;"><s:property value="%{name}"/></td>
	      	</s:else>
	        </s:iterator>
	    </tr>
	    </s:iterator>
	</table>
	</s:else>
	<s:hidden name="action" value=""/>
</s:form>