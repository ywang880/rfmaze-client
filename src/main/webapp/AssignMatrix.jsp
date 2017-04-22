<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<STYLE>.button{height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}</STYLE>

<SCRIPT language="javascript">
    function assignalltouser() {
        document.getElementById("assignall_action").value = "assign";
        document.getElementById("assignall").submit();
    }
    function freeallallfromuser() {
        document.getElementById("assignall_action").value = "free";
        document.getElementById("assignall").submit();
    }
</SCRIPT>

<s:form theme="simple" action="assignall.action" method="post">
   <table class="ConfigDataTable" width="60%" align="center">
      <thead><tr><th colspan="2">Matrix Assignment</th></tr></thead>
      <tbody>
         <tr>
            <td colspan="2"><img src="images/spacer.gif" width="1" height="5"/></td>
         </tr>
         <tr>
            <td align="right" width="40%">Assign to User:</td>
		    <td align="left" width="60%">
		       <s:select cssStyle="width:260px;" label="assigned user" headerKey="-1" headerValue="-- select user --" list="assignedusers" name="assigntouser"/>
	        </td>
         </tr>
         <tr>
            <td align="right" width="40%">Select Hardware:</td>
            <td align="left" width="60%">
               <s:select cssStyle="width:260px;" label="Hardware" headerKey="-1" headerValue="-- select hardware --" list="hardwarelist" name="hardware"/>
            </td>
         </tr>
         <tr>
            <td colspan="2"><img src="images/spacer.gif" width="1" height="10"/></td>
         </tr>
      </tbody>
   </table>

  <div align="center">
    <input type="button" class="button" value="Commit" onclick="assignalltouser();"/>
    <input type="button" class="button" value="Free" onclick="freeallallfromuser();"/>
  </div>
  <div><img src="images/spacer.gif" width="1" height="10"></div>
  <div style="font-family: Helvetica; font-size:16px;"><s:property value="returnMessage"/></div>
    
  <s:hidden name="action" value=""/>
</s:form>
