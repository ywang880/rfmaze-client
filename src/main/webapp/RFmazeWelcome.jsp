<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<SCRIPT language="javascript">
function viewmatrix() {
    if (document.getElementById('rfmaze_hardware').selectedIndex == 0) {
         alert("Invalid selection!");
         return;
    }    
    document.getElementById('rfmaze').submit();
}
</SCRIPT>  

<s:form theme="simple" action="rfmaze.action" method="post">
   
   <s:if test="%{warningMessage != null}">
       <img src="images/warn.png">&nbsp;<div class="warn_message"><s:property value="warningMessage"/></div>
   </s:if>
   
   <s:else>   
       <table class="ConfigDataTable" align="center" width="60%">
           <thead>
            <tr><th align="center">Hardwares</th></tr>
        </thead>
           <tbody>
            <tr><td align="center"><img src="images/spacer.gif" width="1" height="10"/></td></tr>
            <tr><td align="center"><s:select onchange="viewmatrix();" label="Hardware" headerKey="-1" headerValue="-- select hardware --" list="hardwares" name="hardware"/></td></tr>
            <tr><td align="center"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
            <tr><td align="center"><img src="images/spacer.gif" width="1" height="10"/></td></tr>
        </tbody>
    </table>
    </s:else>
</s:form>