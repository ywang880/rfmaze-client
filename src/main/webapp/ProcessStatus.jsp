<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">

<SCRIPT language="javascript">  

function start_process(obj) {
	var row = obj.parentNode.parentNode;
	var config = row.cells[0].innerHTML;
	document.getElementById('process-status_action').value="start " + config;
	document.getElementById('process-status').submit();
}

function stop_process(obj) {
	var row = obj.parentNode.parentNode;
	var pid = row.cells[0].innerHTML;
	document.getElementById('process-status_action').value="stop " + pid;
	if (confirm('Do you want to stop the process?')) {
	   document.getElementById('process-status').submit();
       $("#progressbar").show();
	}
}
</SCRIPT>

<s:form theme="simple" action="process-status.action" method="post">
	<table class="ConfigDataTable" width="90%" align="center" style="border-collapse:collapse;" >
      <thead>  
            <tr>            
                <th width="40%" nowrap>Configuration</th>  
                <th width="35%" nowrap>Status</th>
                <th width="25%" nowrap>Action</th>
            </tr>  
        </thead> 
        <tbody>
        <s:iterator value="processesInfo" status="processesInfoStatus">
           <tr>
              <td width="40%" align="center" style="border: 1px solid #A8A8A8;"><s:property value="%{configFile}"/> </td>
              <td width="35%" align="center" style="border: 1px solid #A8A8A8;"><s:property value="%{status}"/> </td>
              <s:if test="%{status=='running'}">
              <td width="25%" align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" style="background: #DD0000;" value="Stop" onClick="stop_process(this);"/></td>
              </s:if>
              <s:else>
              <td width="20%" align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" value="Start" onClick="start_process(this);"/></td>
              </s:else>      		
  		   </tr>
  		</s:iterator>   
        </tbody>
	</table>
    <table border="0" cellspacing="0" cellpadding="0">
       <thead><tr><th align="left">NOTE:</th></tr></thead>
       <tr><td align="left">When start a server you must wait at least 5 minutes ensuring rfmaze server is stabilized before viewing matrix.</td></tr>
       <tr><td align="left">When stop a server you must wait at least 5 minutes ensuring rfmaze server is shutdown completed before start again.</td></tr>       
    </table>
    <div id="progressbar" style="display: none"><img src="images/progress_bar.gif"></div>
	<s:hidden name="action" value=""/>
</s:form>