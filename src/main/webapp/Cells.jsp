<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

		
<SCRIPT language="javascript">  

var array_rows = new Array(0);
var array_cols = new Array(0);
var rowcount = 0;
var colcount = 0;

function showselectedcol(obj) {
   var cindex = obj.cellIndex;

   if (colcount > 0) {
      for (var i = 0; i < colcount; i++) {		    
         if (array_cols[i] == cindex) {
            var table = document.getElementById('matrix_assignment');
            for (var r = 1; r < table.rows.length; r++) {
               table.rows[r].cells[cindex].style.background=regular_bgcolor;
            }
            array_cols.splice(i, 1);
            colcount--;
            document.getElementById('cells_assignedcolumns').value=array_cols.join(",");
            return;
         }
      }
   }

   array_cols.push(cindex);
   colcount++;
   var table = document.getElementById('matrix_assignment');
   for (var r = 1; r < table.rows.length; r++) {
      table.rows[r].cells[cindex].style.background="yellow";
   }
   document.getElementById('cells_assignedcolumns').value=array_cols.join(",");	   
}

var regular_bgcolor;
function showselectedrow(obj) {

   if (rowcount > 0) {
      for (var i = 0; i < rowcount; i++) {		    
         if (array_rows[i] == obj.rowIndex) {
            for (var i = 1; i < obj.cells.length; i++) {
               obj.cells[i].style.background=regular_bgcolor;
            }
            array_rows.splice(i, 1);
            rowcount--;
            document.getElementById('cells_assignedrows').value=array_rows.join(",");
            return;
         }
      }
   }
	
	rowcount++;
	array_rows.push(obj.rowIndex);
	for (var i = 1; i < obj.cells.length; i++) {
       regular_bgcolor = obj.cells[i].style.background;
	   obj.cells[i].style.background="yellow";
	}
	document.getElementById('cells_assignedrows').value=array_rows.join(",");
}

function buildmatrix() {
   var s1 = document.getElementById('cells_assigntouser');
   if (s1.selectedIndex == 0) {
      alert("User must be selected!");
      return;
   }
   document.getElementById('cells').submit();	
}

function resetselect() {
   if (typeof regular_bgcolor != 'undefined') {
      var table = document.getElementById('matrix_assignment');	   
      for (var r = 1; r < table.rows.length; r++) {
         for (var c = 1; c < table.rows[r].cells.length; c++) {	  
            table.rows[r].cells[c].style.background=regular_bgcolor;
         }
      }
      array_rows = [];
      array_cols = [];
      rowcount = 0;
      colcount = 0;
   }
   document.getElementById('cells_assignedrows').value="";
   document.getElementById('cells_assignedcolumns').value="";
}

function commitselection() {
	var s1 = document.getElementById('cells_hardware');
	var s2 = document.getElementById('cells_assigntouser');
	
	if (s1.selectedIndex == 0) {
		alert("Hardware not selected!");
		return;
	}
	
	if (s2.selectedIndex == 0) {
		alert("User not selected!");
		return;
	}
	if ((rowcount == 0) || (colcount == 0)) {
	   alert("You must select inputs and outputs before commit!");
	   return;
	}
	
	document.getElementById('cells_action').value='assignment ' + array_rows.join(",")+" "+array_cols.join(",")
    document.getElementById('cells').submit();
}

</SCRIPT>

<s:form theme="simple" action="cells.action" method="post">
   <table border="0" width="100%">
     <tr>
        <td align="right">Assigned User:</td>
		<td align="left">
		  <s:select label="assigned user" headerKey="-1" headerValue="-- select user --" list="assignedusers" name="assigntouser"/>
	    </td>
     </tr>
      <tr>
         <td align="right">Select hardware:</td>
         <td align="left">
            <s:select label="Hardware" headerKey="-1" headerValue="-- select hardware --" list="hardwarelist" name="hardware"/>
         </td>
      </tr>
      <tr>
         <td align="right">Inputs:</td>
         <td align="left">
            <s:textfield name="assignedrows" size="40"/>
         </td>
      </tr>
      <tr>
         <td align="right">Outputs:</td>
         <td align="left">
            <s:textfield name="assignedcolumns" size="40"/>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <img src="images/spacer.gif" width="1" height="10"/>
         </td>
      </tr>
  </table>
  <div class="menubar" align="left">
    <input type="button" value="Preview" onclick="buildmatrix();"/>
    <input type="button" value="Reset" onclick="resetselect();"/>
    <input type="button" value="Commit" onclick="commitselection();"/>    
  </div>
  <s:if test="%{warningMessage != null}">
     <div class="warn_message"><s:property value="warningMessage" /></div>
  </s:if>             
  <s:else>
  <table class="matrix" id="matrix_assignment" align="center">
    <thead>
    <tr>
    <th align="center" style="background: #005566; color:white; font-weight:bold; width: 0.5em; height: 0.5em; padding: 0.5em;">&nbsp;</th>
	<s:iterator value="tableHeader" status="tableHeaderStatus">
	  <th align="center" style="background: #005566; color:white; font-weight:bold; width: 0.5em; height: 0.5em; padding: 0.5em;" onClick="showselectedcol(this);"><s:property/></th>
	</s:iterator>
	</tr>
	</thead>
	<s:iterator value="matrix" status="rowsStatus" var="row">
      <tr onClick="showselectedrow(this);">
        <td align="center" style="background: #005566; color:white; font-weight:bold; width: 0.5em; height: 0.5em; padding: 0.5em;"><s:property value="%{#row[0].label}"/></td>
   	    <s:iterator value="#row" status="rowsStatus">
   	        <s:if test="%{name == 'N/A'}">   	        
      	       <td align="center" style="width: 0.5em; height: 0.5em; padding: 0.5em; background:green;"><s:property value="%{name}"/></td>
      	    </s:if>
   	        <s:else>
  	           <td align="center" style="width: 0.5em; height: 0.5em; padding: 0.5em;"><s:property value="%{name}"/></td>
      	    </s:else>      	    
        </s:iterator>
      </tr>
	</s:iterator>
  </table>
  <div class="menubar" align="left">
    <input type="button" value="Preview" onclick="buildmatrix();"/>
    <input type="button" value="Reset" onclick="resetselect();"/>
    <input type="button" value="Commit" onclick="commitselection();"/>
  </div>
  </s:else>
  <s:hidden name="action" value=""/>
</s:form>
