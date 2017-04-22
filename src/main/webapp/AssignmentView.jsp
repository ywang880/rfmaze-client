<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<STYLE>
.assign_matrix_th {
    color:white; 
    font-weight:bold; 
    width: 0.5em; 
    height: 0.5em; 
    padding: 0.5em;
}
  
</STYLE>

<SCRIPT language="javascript">
</SCRIPT>
   
<s:form theme="simple" action="rfmaze.action" method="post"> 
  
    <s:if test="%{warningMessage != null}">
        <div class="warn_message"><s:property value="warningMessage" /></div>
    </s:if>
    <s:else>  
        <table class="matrix" id="matrix_assignment" align="center">
            <thead>
                <tr>
                    <th class="assign_matrix_th">&nbsp;</th>
	                <s:iterator value="tableHeader" status="tableHeaderStatus">
	                <th align="center" style="background: #005566;">
	                    <a class="tooltip" href="#" style="cursor:default"><s:property value="%{label}"/>
                            <span class="info"><s:property value="%{description}"/></span>
		                </a>
	                </th>	       
	                </s:iterator>
	            </tr>
	        </thead>
	        <s:iterator value="matrix" status="rowsStatus" var="row">
            <tr>
                <td align="center" class="assign_matrix_th" style="background: #7898BA;">
                    <a class="tooltip" href="#"><s:property value="%{#row[0].label}"/>
                        <span class="info"><s:property value="%{#row[0].description}"/></span>
		            </a>
                </td>
   	            <s:iterator value="#row" status="rowsStatus">
      	        <td align="center"><s:property value="%{name}"/></td>
                </s:iterator>
            </tr>
	        </s:iterator>
        </table>
    </s:else>
    <s:hidden name="action" value=""/>
</s:form>
