<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<STYLE>
.button{height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}
</STYLE>

<SCRIPT language="javascript">
function addRow(e){var t=document.getElementById(e);var n=t.rows.length;var r=t.insertRow(n);var i=t.rows[0].cells.length;for(var s=0;s<i;s++){var o=r.insertCell(s);o.innerHTML=t.rows[0].cells[s].innerHTML;switch(o.childNodes[0].type){case"text":o.childNodes[0].value="";break;case"checkbox":o.childNodes[0].checked=false;break;case"select-one":o.childNodes[0].selectedIndex=0;break}}}function deleteRow(e){try{var t=document.getElementById(e);var n=t.rows.length;for(var r=0;r<n;r++){var i=t.rows[r];var s=i.cells[0].childNodes[0];if(null!=s&&true==s.checked){if(n<=1){alert("Cannot delete all the rows.");break}t.deleteRow(r);n--;r--}}}catch(o){alert(o)}}function enableInput(e){var t=document.getElementById(e).style.display;if(t=="none"){document.getElementById(e).style.display=""}else{document.getElementById(e).style.display="none"}}function disableInput(e){document.getElementById(e).style.display="none"}function commit(e){document.getElementById("_action").value=e;document.getElementById("users").submit()}function deleteUser(e){if(!confirm("Do you want to delete selected user?")){return}document.getElementById("_action").value="delete "+e;document.getElementById("users").submit()}function editUser(e,t,n){var r=e.parentNode.parentNode.cells[0].innerHTML;var i=e.parentNode.parentNode.cells[1].innerHTML;enableInput(t);document.getElementById("users_id").value=r;document.getElementById("users_password").value=i}function show_hide_password(){var attr=$("#user_passwd").attr('type');if (attr=="text"){$("#user_passwd").attr('type','password');}else{$("#user_passwd").attr('type','text');}}
$(document).ready(function() {
    $("#addselected").click(function() {
alert("aaa");
        }),
    $("#removeselected").click(function() {
        alert("bbb");
});
});
</SCRIPT>

<s:form theme="simple" action="users.action" method="post">

<s:if test="%{warningMessage != null}">
    <img src="images/warn.png">&nbsp;<div class="warn_message"><s:property value="warningMessage"/></div>
</s:if>

<table class="ConfigDataTable" width="80%" align="center" style="border-collapse:collapse;">
   <thead>
      <tr>
         <th nowrap>User ID</th>
         <th nowrap>Password</th>
         <th nowrap colspan="2"><input type="button" class="button" value="Add New" align="center" onClick="enableInput('_user_data');"/></th>
      </tr>
   </thead>
   <tbody>
      <s:iterator value="users" status="usersStatus">
  	  <tr>
		 <td align="center" style="border: 1px solid #A8A8A8;"><s:property value="%{id}"/></td>
		 <td align="center" style="border: 1px solid #A8A8A8;">********</td>
		 <td align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" value="Edit" onclick="editUser(this, '_user_data', '<s:property value="#usersStatus.count"/>');"/></td>
		 <td align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" value="Delete" onclick="deleteUser('<s:property value="%{id}"/>');"/></td>
      </tr>
	  </s:iterator>
   </table>
   <img src="images/spacer.gif" width="1" height="20"/>

   <table align="center" id="_user_data" style="display: none;">
    <tr>
      <td align="center">
        <table>
	      <tr>
    	    <td align="right" nowrap>Name:&nbsp;</td>
            <td align="left"><s:textfield name="id" key="label.username" size="20"/></td>
    	    <td align="right" nowrap>Password:&nbsp;</td>
            <td align="left"><s:password id="user_passwd" name="password" key="label.password" size="20"/>
               <img id="show_password" src="images/eye.png" onClick="show_hide_password();"/>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td align="center">
        <table align="center">
          <tr>
            <td align="right" nowrap>
              <s:select cssStyle="width:260px;" label="AvailableHardware" multiple="true" size="5" headerKey="-1" list="hardwarelist" name="hardware"/></td>
            <td>
              <table>
                <tr>
                  <td>
                    <input type="button" value=" >> " class="button" id="addselected">
                  </td>
                </tr>
                <tr>
                  <td>
                    <input type="button" value=" << " class="button" id="removeselected">
                  </td>
                </tr>
              </table>
            </td>
       	    <td align="right" nowrap>
              <s:select cssStyle="width:260px;" label="Assigned" multiple="true" size="5" list="assignedHardwares" name="assigntouser"/>
            </td>
	      </tr>
        </table>
      </td>
    </tr>
    <tr><td><img src="images/spacer.gif" width="1" height="10"/></td></tr>
    <tr>
      <td colspan="2" align="center">
    	<input type="button" class="button" value="Commit" align="center" onclick="commit('commit');"/>
    	<input type="button" class="button" value="Cancel" align="center" onClick="disableInput('_user_data');"/>
      </td>
    </tr>
    <tr><td><img src="images/spacer.gif" width="1" height="20"/></td><tr>
   </table>
   <s:hidden name="action" id="_action" value="commit"/>
</s:form>
