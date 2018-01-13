<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">

<script>
(function($){$(document).ready(function(){$("#show_version").click(function(){$("#version_dialog").dialog();});$("#nav").hover(function() {$("#menu_img").attr("src", "images/arrow_e.png")});$("#nav").mouseleave(function(){$("#menu_img").attr("src", "images/arrow_s.png")});});})(jQuery);function restartServer(){if (confirm("Restart application server will cause lost connection temporarily. Do you want to proceed anyway?")){var xmlhttp;if(window.XMLHttpRequest){xmlhttp=new XMLHttpRequest();}else{xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");}xmlhttp.open("POST","/rfmaze/mazeServlet?command=restart");xmlhttp.send();}}
</script>

<style>#nav{width:110px;font-family:"Helvetica Neue",Helvetica,Arial,Sans-Serif;font-size:12px;color:#203080;line-height:15px;text-align:left}#nav ul{margin:0;padding:0}#nav li{list-style:none}ul.top-level li{border:none}#nav ul.sub-level{border:1px solid #e0e0e0}#nav a{color:#203080;cursor:pointer;display:block;height:25px;line-height:25px;text-indent:10px;text-decoration:none;width:100%}#nav a:hover{text-decoration:underline}#nav li:hover{background:#85A7BF;position:relative}ul.sub-level{display:none;z-index:1}li:hover .sub-level{background:#85A7BF;border:#fff solid;border-width:1px;display:block;position:absolute;left:30px;top:20px}ul.sub-level li{font-weight:700;color:#203080;float:left;background:#cedeee;border-width:1px;border:1px solid #e0e0e0;border-bottom:2px solid #a0a0a0;width:220px;z-index:1}#nav .sub-level{background:#FFF}</style>

<div class="greynavbar" align="right">
    <table style="color: #203080;">
        <tr>
            <td>
                <div id="nav">
                <ul class="top-level">
                    <li><a href="#" id="menu_admin_action">Admin Actions <img id="menu_img" src="images/arrow_s.png"></a>
                        <ul class="sub-level">
                            <s:if test="username=='admin'">
                            <li><a href="server.action">RF Matrix Configuration</a></li>
                            <li><a href="users.action">User Management</a></li>
                            <li><a href="labels.action">Assign Matrix Ports Labels</a></li>
                            <li><a href="outputs.action">Assign Matrix Outputs</a></li>
                            <li><a href="inputs.action">Assign Matrix Inputs</a></li>
                            <li><a href="process-status.action">Start/Stop Matrix Control</a></li>
                            <li><a href="matrix_overview.action">Matrix Monitor and Switch Setting</a></li>
                            <li><a href="download.action">Show and Download Log Files</a></li>
                            <li><a href="javascript:restartServer();">Restart Server</a></li>
                            </s:if>
                            <s:else>
                            <li><a href="matrix_overview.action">Switch Setting</a></li>
                            </s:else>
                        </ul>
                    </li>
                </ul>
                </div>
            </td>
            <td class="tdLabel" align="right">
                <img src="images/spacer.gif" width="10" height="1"/><s:property value="username"/>&nbsp;
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
                <img src="images/spacer.gif" width="10" height="1"/>
            </td>
        </tr>
    </table>
 </div>