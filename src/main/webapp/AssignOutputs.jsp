<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<STYLE>
.pform {display:none;border:1px solid #D0D0D0;border-radius:3px;padding:8px;}.button{height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}.assignment_table{border:1px solid green;border-radius:5px;display:block;width:800px;height:100px;text-align:center;margin-left:auto;margin-right:auto;background:#B0C0D0;overflow:auto}.assignment_table th{background:#056;color:#fff;white-space:nowrap;width:100px;max-width:100px;font-weight:700}.assignment_table td{border:1px solid green;border-radius:5px;background:#688DB2;width:100px;max-width:100px;white-space:nowrap;height:30px;padding:2px;text-align:center}.assignment_table td:hover{background-color:#A3CCA3;cursor:pointer}label{display:inline-block;width:5em}
</STYLE>

<SCRIPT language="javascript">  
function getPdata(e){var t=document.getElementById("pForm");var n;var r=document.getElementById("outputs_user");var i=r.options[r.selectedIndex].value;if("Cancel"==e){}else if("Reassign"==e){n="reassignment "+currentdata+" from "+from+" to "+i}else if("Free"==e){n="free "+currentdata+" from "+from}document.getElementById("outputs_action").value=n;document.getElementById("outputs").submit();t.style.display="none"}function showselectedcol(e){var t=e.cellIndex+1;if(colcount>0){for(var n=0;n<colcount;n++){if(array_cols[n]==t){var r=document.getElementById("matrix_assignment");for(var i=1;i<r.rows.length;i++){r.rows[i].cells[t-1].style.background=regular_bgcolor}array_cols.splice(n,1);colcount--;array_cols.sort(function(e,t){return e-t});for(var j=1;j<array_cols.length;){if( array_cols[j-1] == array_cols[j]){array_cols.splice(j,1);}else{j++;}}document.getElementById("outputs_assignedcolumns").value=array_cols.join(",");return}}}array_cols.push(t);colcount++;var r=document.getElementById("matrix_assignment");for(var i=1;i<r.rows.length;i++){r.rows[i].cells[t-1].style.background="yellow"}document.getElementById("outputs_assignedcolumns").value=array_cols.sort(function(e,t){return e-t}).join(",")}function buildmatrix(){var e=document.getElementById("outputs_assigntouser");if(e.selectedIndex==0){alert("User and hardware must be selected!");return}document.getElementById("outputs").submit()}function resetselect(){if(typeof regular_bgcolor!="undefined"){var e=document.getElementById("matrix_assignment");for(var t=1;t<e.rows.length;t++){for(var n=1;n<e.rows[t].cells.length;n++){e.rows[t].cells[n].style.background=regular_bgcolor}}array_cols=[];colcount=0}document.getElementById("outputs_assignedcolumns").value=""}function commitselection(){var e=document.getElementById("outputs_hardware");var t=document.getElementById("outputs_assigntouser");if(e.selectedIndex==0){alert("Hardware not selected!");return}if(t.selectedIndex==0){alert("User not selected!");return}if(colcount==0){alert("You must assign outputs before commit!");return}var n="assignment "+array_cols.join(",");document.getElementById("outputs_action").value=n;document.getElementById("outputs").submit()}function hardwareassignment(e){if(e.selectedIndex>0&&document.getElementById("outputs_assigntouser").selectedIndex>0){buildmatrix()}}function usertoassign(e){if(e.selectedIndex>0&&document.getElementById("outputs_hardware").selectedIndex>0){buildmatrix()}}function freeselectedcol(e){currentdata=e.cellIndex+1;from=document.getElementById("matrix_assignment").rows[1].cells[currentdata-1].innerHTML;document.getElementById("_id_column").value=currentdata;document.getElementById("_id_from_user").value=from;document.getElementById("pForm").style.display="block";return}var currentdata;var from;var regular_bgcolor="green";var array_cols=new Array(0);var colcount=0;$(function(){$(document).tooltip()});
</SCRIPT>

<s:form theme="simple" action="outputs.action" method="post">
   <table class="ConfigDataTable" width="60%" align="center">
      <thead><tr><th colspan="2">Matrix Output Port Assignment</th></tr></thead>
      <tbody>
         <tr>
            <td colspan="2"><img src="images/spacer.gif" width="1" height="5"/></td>
         </tr>
         <tr>
            <td align="right" width="40%">Assign to User: </td>
		    <td align="left" width="60%">
		       <s:select cssStyle="width:260px;" label="assigned user" headerKey="-1" headerValue="-- select user --" list="assignedusers" name="assigntouser" onchange="usertoassign(this);"/>
	        </td>
         </tr>
         <tr>
            <td align="right" width="40%">Select Hardware: </td>
            <td align="left" width="60%">
               <s:select cssStyle="width:260px;" label="Hardware" headerKey="-1" headerValue="-- select hardware --" list="hardwarelist" name="hardware" onchange="hardwareassignment(this);"/>
            </td>
         </tr>
         <tr>
            <td align="right" width="40%">Selected Outputs: </td>
            <td align="left" width="60%"><s:textfield name="assignedcolumns" size="40"/></td>
         </tr>
         <tr>
            <td colspan="2" align="center"><img src="images/spacer.gif" width="1" height="10"></td>
         </tr>
         <tr>
            <td colspan="2" align="center">Assign entire matrix to a user go to user management page. </td>
         </tr>
         <tr>
            <td colspan="2"><img src="images/spacer.gif" width="1" height="10"/></td>
         </tr>
      </tbody>
   </table>

   <div id="pForm" class="pform">
      <table class="ConfigDataTable" width="90%" align="center">
          <thead><tr><th>Edit Output Assignment</th></tr><thead>
          <tr>
              <td align="center">
			      <table>
				      <tr>
			              <td>Output: <input style="border:none;" id="_id_column" type="text" size="20" value=""></td>
			              <td>From: <input style="border:none;" id="_id_from_user" type="text" size="20" value=""></td>
						  <td>To: <s:select cssStyle="width:100px;" list="users" name="user"/></td>
			          </tr>
			          <tr>
			              <td colspan="3" align="center">
			                  <input type="button" class="button" value="Reassign" onclick="getPdata(this.value)"/> 
			                  <input type="button" class="button" value="Free" onclick="getPdata(this.value)"/> 
			                  <input type="button" class="button" value="Cancel" onclick="getPdata(this.value)"/>
			              </td>
			          </tr>
			      </table>
			  </td>
          <tr>
      </table>
   </div>

   <s:if test="%{warningMessage != null}">
     <div class="warn_message"><s:property value="warningMessage" /></div>
   </s:if>             
   <s:else>

   <table class="assignment_table" id="matrix_assignment" align="center">
     <thead>
     <tr>
	    <s:iterator value="tableHeader" status="tableHeaderStatus">
	    <s:if test="%{user!=null}">
	    <th align="center" style="background: #808080;" onclick="freeselectedcol(this);">
		   <div title='<s:property value="%{description}"/>'><s:property value="%{label}"/></div>
	    </th>
	    </s:if>
	    <s:else>
	       <th align="center" style="background: #005566; cursor: pointer;" onClick="showselectedcol(this);">
		      <div title='<s:property value="%{description}"/>'><s:property value="%{label}"/></div>
	       </th>	           
	    </s:else>
	   </s:iterator>
	</tr>
	</thead>	
    <tr>
   	    <s:iterator value="matrix" status="rowsStatus">
   	        <s:if test='%{name=="-"}'>
      	       <td align="center" style="background:green;" onClick="showselectedcol(this);"><s:property value="%{name}"/></td>
      	    </s:if>
   	        <s:else>
  	           <td align="center" onClick="freeselectedcol(this);"><s:property value="%{name}"/></td>
      	    </s:else>
        </s:iterator>
    </tr>
  </table>
  <div id="labeleditor"><img src="images/spacer.gif" width="1" height="5"></div>
  <div align="center">
    <input type="button" class="button" value="Commit" onclick="commitselection();"/>
    <input type="button" class="button" value="Cancel" onclick="resetselect();"/>
  </div>
  </s:else>
  <s:hidden name="action" value=""/>
</s:form>
