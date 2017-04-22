<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<style>
.container{width:90%;margin-left:0;margin-right:0;padding:5px}.container div{padding:5px;width:100%}.container .header{background-color:silver;border:1px solid silver;border-radius:5px;padding:2px;cursor:pointer;font-weight:700}.container .content{display:none;padding:5px}table.altrowstable{font-family:verdana,arial,sans-serif;font-size:12px;color:#333;text-align:center;border-collapse:collapse;border-color:#506080;border-width:1px}.button{height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}
</style>

<SCRIPT language="javascript">
function browseConfigureFile(e){if(e.selectedIndex==0){document.getElementById("id_modify").style.display="none";document.getElementById("id_delete").style.display="none"}else{var t=e.options[e.selectedIndex].value;document.getElementById("server_action").value="browse "+t;document.getElementById("server").submit()}}function addServer(){var matrix_name=document.getElementById("server_matrixName").value;if(/^\s*$/.test(matrix_name)||/\s/g.test(matrix_name)){alert("matrix name ["+matrix_name+"] cannot be empty or contain whitespaces!");return;}var e=document.getElementById("server_hwPort").value;var t=Number(e);if(Math.floor(t)!=t){alert("hardware port must be an integer!");return}e=document.getElementById("server_maxAttn").value;t=Number(e);if(Math.floor(t)!=t){alert("maximum attenuation must be an integer!");return}e=document.getElementById("server_minAttn").value;t=Number(e);if(Math.floor(t)!=t){alert("minimum attenuation must be an integer!");return}e=document.getElementById("server_numberOfInputs").value;t=Number(e);if(Math.floor(t)!=t){alert("number of inputs must be an integer!");return}e=document.getElementById("server_numberOfOutputs").value;t=Number(e);if(Math.floor(t)!=t){alert("number of outputs must be an integer!");return}e=document.getElementById("server_stepAttn").value;t=Number(e);if(Math.floor(t)!=t){alert("step of attenuation must be an integer!");return}document.getElementById("server_action").value="create_server";document.getElementById("server").submit()}function modifyServer(){if(document.getElementById("server_mazeServer").selectedIndex==0){alert("Invalid server selection!");return}document.getElementById("server_action").value="modify_server";document.getElementById("server").submit()}function removeFromSelected(e){if(document.getElementById("server_mazeServer").selectedIndex==0){alert("Invalid server selection!");return}if(confirm("Do you want to permanently delete configuration?")){document.getElementById("server_action").value="delete_server_and_config"}else{document.getElementById("server_action").value="delete_server"}document.getElementById("server").submit()}function changeBroadcastPort(){var e=/^\d{3,5}$/;var t=document.getElementById("server_bsPort").value;if(!e.test(t)){alert("Not a valid server port!");return}document.getElementById("server_action").value="change_port";document.getElementById("server").submit()}$(document).ready(function(){if($ ("#server_matrixType").val()=="R"){$("#row_max_atten").hide();$("#row_min_atten").hide();$("#row_step_atten").hide();}$("#server_matrixType" ).change(function(){if($("#server_matrixType").val()=="R"){$("#row_max_atten").hide();$("#row_min_atten").hide();$("#row_step_atten").hide();}else{$("#row_max_atten").show();$("#row_min_atten").show();$("#row_step_atten").show();}});});
</SCRIPT>
<img src="images/spacer.gif" width="20" height="10"/>
<s:form theme="simple" action="server.action" method="post">

<s:set name="numServers" value="numServers"/>  
<table class="ConfigDataTable" width="80%" align="center">
   <thead><tr><th>Matrix Configuration </th></tr></thead>
   <tbody>
      <tr>
         <td align="center">
            <table border="0" cellspacing="10" cellpadding="10" align="center" width="100%">
               <s:if test="%{successMessage != null}">
               <tr><td class="success_message" colspan="2" align="center"><s:property value="successMessage" /></td></tr>
               </s:if>
               <s:if test="%{errorMessage != null}">
               <tr><td class="error_message" colspan="2" align="center"><s:property value="errorMessage" /></td></tr>
               </s:if>
               <s:if test="%{warningMessage != null}">
               <div class="warn_message"><img src="images/warn.png"/><s:property value="warningMessage" /></div>
               </s:if>
		       <tr>
		          <td valign="top" align="center">
			         <table>
			            <tr>
			               <td align="right">Matrix type: </td>
			               <td align="left">                           
                           <s:select id="server_matrixType" name="matrixType" value="defaultType" list="listType" listKey="key" listValue="value"/>
                           </td>
			            </tr>
			            <tr><td align="right">Matrix Name: </td><td align="left"><s:textfield name="matrixName" size="20" /></td></tr>
			            <tr style="display: none;"><td colspan="2"><s:textfield name="currentMatrixName" size="20" /><td></tr>			            
			            <tr><td align="right">Matrix Hardware Address: </td><td align="left"><s:textfield name="hwIp"  size="20" /></td></tr>
			            <tr><td align="right">Matrix Hardware Port: </td><td align="left"><s:textfield name="hwPort"  size="20" /></td></tr>			
			            <tr id="row_max_atten"><td align="right"><span id="max_atten">Maximum Attenuation: </span></td><td align="left"><s:textfield name="maxAttn" size="20" />dB</td></tr>
			            <tr id="row_min_atten"><td align="right"><span id="min_atten">Minimum Attenuation: </span></td><td align="left"><s:textfield name="minAttn" size="20" />dB</td></tr>
			         </table>
  		          </td>
		          <td valign="top" align="left">
		   		     <table>
		   		        <s:if test="%{#numServers > 0}">
		   		        <tr>
			               <td align="right">RF Matrix list: </td>
			               <td align="left">
			                  <s:select label="Matrix Server" headerKey="-1" headerValue="-- select server --" list="mazeServers" name="mazeServer" onchange="browseConfigureFile(this)"/>			         
			               </td>			       
                        </tr>
                         </s:if>		   		     
		 		        <tr><td align="right">Number of Inputs:</td><td align="left"><s:textfield name="numberOfInputs" size="20" /></td></tr>
			   	        <tr><td align="right">Number of Outputs:</td><td align="left"><s:textfield name="numberOfOutputs" size="20" /></td></tr>			
			   	        <tr id="row_step_atten"><td align="right"><span id="step_atten">Step Attenuation:</span></td><td align="left"><s:textfield name="stepAttn" size="20" />dB</td></tr>
			   	        <tr><td align="right">Invert Input Output:</td><td align="left"><s:checkbox name="invertInputOutput"/></td></tr>
			         </table>
		          </td>
		       </tr>
		       <tr>
		          <td colspan="2" align="center">
		             <input theme="simple" type="submit" value=" Add " class="button" onClick="addServer();">
		 	         <img src="images/spacer.gif" width="10" height="1"/>
		 	         <input id="id_modify" style="display:none" theme="simple" type="submit" value=" Modify " class="button" onClick="modifyServer();">
					 <img src="images/spacer.gif" width="10" height="1"/>
		 	         <input id="id_delete" style="display:none" theme="simple" type="submit" value=" Delete " class="button" onClick="removeFromSelected();">
		          </td>
		       </tr>
	        </table>
         </td>
      </tr>
   </tbody>
</table>

<script>
    if (document.getElementById('server_mazeServer').selectedIndex > 0) {
 	    document.getElementById('id_modify').style.display='';
	    document.getElementById('id_delete').style.display='';
    }
</script>

<s:hidden name="action" value=""/>
</s:form>
			
