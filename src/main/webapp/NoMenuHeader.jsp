<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">

<script>
(function($){$(document).ready(function(){$("#show_version").click(function(){$("#version_dialog").dialog();});});})(jQuery);function restartServer(){if (confirm("Restart application server will cause lost connection temporarily. Do you want to proceed anyway?")){var xmlhttp;if(window.XMLHttpRequest){xmlhttp=new XMLHttpRequest();}else{xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");}xmlhttp.open("POST","/rfmaze/mazeServlet?command=restart");xmlhttp.send();}}
</script>  
   
<table border="0" cellspacing="0" cellpadding="0" height="100px" width="100%" background="images/banner_bg.png">
    <tr>
        <td align="left" width="10%">
            <img src="images/acentury_logo.png" width="120" height="73"/>
        </td>
        <td align="center" width="90%">
            <img src="images/logo.png"/>
        </td>
    </tr>
    <tr>    
        <td align="right" colspan="2">    
          <div class="greynavbar">
            <table style="color: #203080;">
                <tr>
                    <td class="tdLabel" align="right">
                        <img src="images/spacer.gif" width="10" height="1"/><s:property value="username" />&nbsp;
                    </td>
                    <td align="left"><img src="images/logout_icon.png"/></td>
                    <td>
                       <img src="images/separator.png"/>
                       <s:a errorText="Sorry your request had an error." href="logout.action" cssStyle="color: #203080;" >Sign Out</s:a>
                       <img src="images/separator.png"/>
                       <a href="help/index.html?version=1.4.1" target="_blank" style="color: #203080;">Help</a>
                    </td>                    
                    <td>
                        <img src="images/separator.png"/>
                        <div id="show_version" style="display: inline; cursor: pointer; color: #203080;">Version</div>
                    </td>
                </tr>
            </table>
            </div>
        </td>
    </tr>    
</table>
<%@include file="copyright.jsp"%>
