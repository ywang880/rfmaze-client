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

var x;
var y;
var previous;

function changeAttn(obj) {
	x = obj.parentNode.rowIndex;
	y = obj.cellIndex-1; // skip the first column that is the offset
	if (previous!=undefined) {
		previous.style.background="#688DB2";			
    }
    obj.style.background="#A3CCA3";
    previous=obj;

    document.getElementById('id_inputs').value=x;
    document.getElementById('id_outputs').value=y;
}

function set_atten() {
	var selector = document.getElementById('id_attenuation_select');
	document.getElementById('id_attenuation').value=selector.options[selector.selectedIndex].value;
}

function set_attenuation() {
	if (x == undefined || y == undefined) {
		alert("Cell not selected. Click to select the cell first!");
		return;
	}

	document.getElementById('rfmaze_action').value = 'set_attenuation ' + x + ','  + y + ',' + document.getElementById('id_attenuation').value;
	document.getElementById('rfmaze').submit();
}
</SCRIPT>
   
<s:form theme="simple" action="rfmaze.action" method="post"> 
  
   <s:if test="%{warningMessage != null}">
      <div class="warn_message"><s:property value="warningMessage" /></div>
   </s:if>
   <s:else>
       <table class="matrix" id="matrix_view" align="center">
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
               <td align="center" class="assign_matrix_th" style="background: #005566;">
                   <a class="tooltip" href="#" style="cursor:default"><s:property value="%{#row[0].label}"/>
                       <span class="info"><s:property value="%{#row[0].description}"/></span>
		           </a>
               </td>
          
   	           <s:iterator value="#row" status="rowsStatus">
      	           <td align="center" onclick="changeAttn(this);"><s:property value="%{name}"/></td>
               </s:iterator>
           </tr>
	       </s:iterator>
        </table>

       	<s:div class="center1">
       		<s:label>Inputs: </s:label><s:textfield id="id_inputs"></s:textfield>
       		<s:label>Outputs: </s:label><s:textfield id="id_outputs"></s:textfield>
       		<s:label>Attenuation: </s:label><s:textfield id="id_attenuation"></s:textfield>
       		<s:select list="attenuation" id="id_attenuation_select" onchange="set_atten();"></s:select>
       		<input type="button" name="setatten" value="Set Attenuation" onclick="set_attenuation();"/>       		
       	</s:div>
       	
    </s:else>
    <s:hidden name="action" value=""/>
</s:form>
