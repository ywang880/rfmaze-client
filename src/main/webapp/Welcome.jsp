<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
		
<table width="80%" align="center">
   <tr>
      <s:if test="username=='admin'">
      <td align="center">
        <p style="font-family: Helvetica; color:#002233; font-size:16px;">
           <B>Click "Admin Actions" to select administration action menu</B></p>
           <table style="font-size: 14px;">
           <tr><td><b>RF Matrix Configuration</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Add a new RF matrix hardware, modify existing matrix configuration. </td></tr>
           <tr><td><b>User Management</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Create and modify user profiles </td></tr>
           <tr><td><b>Assign Matrix Ports Labels</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Add label and description for each matrix port.</td></tr>
           <tr><td><b>Assign Matrix Outputs</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Assign outputs resources to different users </td></tr>
           <tr><td><b>Assign Matrix Inputs</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Assign inputs resources to different users </td></tr>
           <tr><td><b>Start/Stop Matrix Control</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; View, start or stop RF Matrix control process</td></tr>
           <tr><td><b>Matrix Monitor and Switch Setting</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Monitor RF matrices user assignments and change switch settings</td></tr>
           <tr><td><b>Show and Download Log Files</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Show and download log files</td></tr>
		   <tr><td><b>Restart Server</b></td><td><img src="images/spacer.gif" width="10" height="1"></td><td>&#187; Restart application server</td></tr>
		   </table>
      </td>
      </s:if>
      <s:else>
      <td align="center">
        <p style="font-family: Helvetica; color:#002233; font-size:16px;">
           <B>Click "Admin Actions" to select action menu</B></p>
           <table style="font-size: 14px;">
           <tr><td><td>&nbsp;</td></tr>
           <tr><td><b>NOTE:</b> This user can only change switch settings. Please consult your system administrator to grant access privileges.</td></tr>
		   </table>
      </td>
      </s:else>
   </tr>
</table>

