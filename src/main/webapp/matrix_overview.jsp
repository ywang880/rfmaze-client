<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">
<link href="css/960.css" rel="stylesheet" media="screen" />
<link href="css/defaultTheme.css" rel="stylesheet" media="screen" />

<style>
.divider {
    margin-top: 20px
}
.height380 {
    height: 380px;
    overflow-x: auto;
    overflow-y: auto
}
.matrixscrollabe {
    font-size: 12px;
    color: #000;
    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif
}
.matrixscrollabe td,
.matrixscrollabe th {
    border: 1px solid #789;
    padding: 5px
}
.matrixscrollabe tbody tr td {
    background-color: #eef2f9;
    background-image: -moz-linear-gradient(top, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.0) 100%);
    background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, rgba(255, 255, 255, 0.4)), color-stop(100%, rgba(255, 255, 255, 0.0)))
}
.matrixscrollabe tbody tr.odd td {
    background-color: #d6e0ef;
    background-image: -moz-linear-gradient(top, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.0) 100%);
    background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, rgba(255, 255, 255, 0.4)), color-stop(100%, rgba(255, 255, 255, 0.0)))
}
.matrixscrollabe thead tr th,
.matrixscrollabe thead tr td,
.matrixscrollabe tfoot tr th,
.matrixscrollabe tfoot tr td {
    background-color: #8ca9cf;
    background-image: -moz-linear-gradient(top, rgba(255, 255, 255, 0.4) 0%, rgba(255, 255, 255, 0.0) 100%);
    background-image: -webkit-gradient(linear, left top, left bottom, color-stop(0%, rgba(255, 255, 255, 0.4)), color-stop(100%, rgba(255, 255, 255, 0.0)));
    font-weight: 700
}
</style>

<script type="text/javascript" language="javascript" src="js/rfmaze.js"></script>

<SCRIPT language="javascript">
function changeAttn(e){var t=document.getElementById("matrix_view").rows[0].cells[e.cellIndex],n=document.getElementById("matrix_view").rows[e.parentNode.rowIndex].cells[0],i=t.childNodes[1];headerText=i.childNodes[0].nodeValue,i=n.childNodes[1],labelText=i.childNodes[0].nodeValue;var d=e.parentNode.rowIndex,l=e.cellIndex-1;if(0!=l){if(isSetAttenuationActive)return void changeAttn2(e);document.getElementById("attenuation").value=e.innerHTML,$("#slider").slider("value",e.innerHTML),"hidden"==document.getElementById("bkg").style.visibility&&(document.getElementById("bkg").style.visibility="",$("#bkg").hide()),"hidden"==document.getElementById("dlg").style.visibility&&(document.getElementById("dlg").style.visibility="",$("#dlg").hide()),$("#bkg").fadeIn(500,"linear",function(){isSetAttenuationActive=!0,$("#dlg").show(500,"swing"),$("#dlg").draggable();$("#dlg").zIndex(1)}),document.getElementById("id_inputs").value=d-1,document.getElementById("id_outputs").value=l,document.getElementById("id_inputs").disabled=!0,document.getElementById("id_outputs").disabled=!0,document.getElementById("mimo").disabled=!0,document.getElementById("inputs_label").innerHTML=labelText,document.getElementById("outputs_label").innerHTML=headerText,$("#mimo").attr("checked",!1),$("#mimo1").attr("checked",!1)}}function changeAttn2(e){document.getElementById("attenuation1").value=e.innerHTML,$("#slider1").slider("value",e.innerHTML);var t=e.parentNode.rowIndex,n=e.cellIndex-1;0!=n&&("hidden"==document.getElementById("bkg1").style.visibility&&(document.getElementById("bkg1").style.visibility="",$("#bkg1").hide()),"hidden"==document.getElementById("dlg1").style.visibility&&(document.getElementById("dlg1").style.visibility="",$("#dlg1").hide()),$("#bkg1").fadeIn(500,"linear",function(){$("#dlg1").show(500,"swing"),$("#dlg1").draggable();$("#dlg1").zIndex(1)}),document.getElementById("id_inputs1").value=t-1,document.getElementById("id_outputs1").value=n,document.getElementById("id_inputs1").disabled=!0,document.getElementById("id_outputs1").disabled=!0,document.getElementById("mimo1").disabled=!0,document.getElementById("inputs_label1").innerHTML=labelText,document.getElementById("outputs_label1").innerHTML=headerText,$("#mimo").attr("checked",!1),$("#mimo1").attr("checked",!1))}function set_attenuation_btn(e){if(1==e){var t=document.getElementById("attenuation").value,n=document.getElementById("id_inputs").value,i=document.getElementById("id_outputs").value;$("#slider").slider("value",t),set_attenuation(n,i,t)}else if(2==e){var t=document.getElementById("attenuation1").value,n=document.getElementById("id_inputs1").value,i=document.getElementById("id_outputs1").value;$("#slider1").slider("value",t),set_attenuation(n,i,t)}}function set_atten(e,t){if(1==e){document.getElementById("attenuation").value=t;var n=document.getElementById("id_inputs").value,i=document.getElementById("id_outputs").value;$("#slider").slider("value",t),set_attenuation(n,i,t)}else if(2==e){document.getElementById("attenuation1").value=t;var n=document.getElementById("id_inputs1").value,i=document.getElementById("id_outputs1").value;$("#slider1").slider("value",t),set_attenuation(n,i,t)}}function increment_and_send(e){if(!in_progress1)if(in_progress1=!0,setTimeout(function(){in_progress1=!1},300),1==e){var t=document.getElementById("id_inputs").value,n=document.getElementById("id_outputs").value,i=document.getElementById("attenuation").value;if(63==i)return void alert("The new value exceeds the limit. Maximum value is 63");var d=++i;document.getElementById("attenuation").value=d,$("#slider").slider("value",d),set_attenuation(t,n,d)}else if(2==e){var t=document.getElementById("id_inputs1").value,n=document.getElementById("id_outputs1").value,i=document.getElementById("attenuation1").value;if(63==i)return void alert("The new value exceeds the limit. Maximum value is 63");var d=++i;document.getElementById("attenuation1").value=d,$("#slider1").slider("value",d),set_attenuation(t,n,d)}}function decrment_and_send(e){if(!in_progress2)if(in_progress2=!0,setTimeout(function(){in_progress2=!1},300),1==e){var t=document.getElementById("id_inputs").value,n=document.getElementById("id_outputs").value,i=document.getElementById("attenuation").value;if(0==i)return void alert("The value cannot be decremented as the current value already reached the low boundary!");var d=--i;document.getElementById("attenuation").value=d,$("#slider").slider("value",d),set_attenuation(t,n,d)}else if(2==e){var t=document.getElementById("id_inputs1").value,n=document.getElementById("id_outputs1").value,i=document.getElementById("attenuation1").value;if(0==i)return void alert("The value cannot be decremented as the current value already reached the low boundary!");var d=--i;document.getElementById("attenuation1").value=d,$("#slider1").slider("value",d),set_attenuation(t,n,d)}}function set_attenuation(e,t,n){if(isBlank(e)||isBlank(t))return void alert("Input is invalid. Row and column cannot not empty!");var i=/^[0-9,]*$/.test(e);if(!i)return void alert("Input is invalid. The inputs must be digits separated by comma.");if(i=/^[0-9,]*$/.test(t),!i)return void alert("Output is invalid. The outputs must be digits separated by comma.");for(var d=document.getElementById("matrix_view").rows.length-1,l=document.getElementById("matrix_view").rows[0].cells.length-2,a=e.split(","),u=t.split(","),o=0;o<a.length;o++)if(a[o]>d)return void alert("Input is invalid. Maximum row number is "+d);for(var o=0;o<u.length;o++)if(u[o]>l)return void alert("Output is invalid. Maximum column number is "+l);var s=document.getElementById("mimo").checked,m="set_attenuation";s&&(m="set_mimo"),0>n?n=0:n>63&&(n=63);var r;r=window.XMLHttpRequest?new XMLHttpRequest:new ActiveXObject("Microsoft.XMLHTTP"),r.onreadystatechange=function(){if(4==r.readyState&&200==r.status){var e=r.responseXML.documentElement.getElementsByTagName("tr");updateMatrix(e,!1)}};var c=document.getElementById("hardware_name").innerHTML;r.open("GET","/rfmaze/mazeAdminServlet?command="+m+"&outputs="+t+"&inputs="+e+"&value="+n+"&hardware="+c,!0),r.send(null)}function updateMatrix(e,t){var e,n,i,d,l,a=document.getElementById("matrix_view"),u=a.rows.length;for(n=0;n<e.length;n++)try{for(tds=e[n].getElementsByTagName("td"),i=0;i<tds.length;i++)(0!=i||t)&&(d=tds[i].getElementsByTagName("v"),l=tds[i].getElementsByTagName("c"),u-2>n&&(a.rows[n+2].cells[i+1].innerHTML=d[0].firstChild.nodeValue,a.rows[n+2].cells[i+1].style.backgroundColor=l[0].firstChild.nodeValue))}catch(o){alert(o)}}function updateMatrixOffset(e){for(var t,n,i=document.getElementById("matrix_view"),d=i.rows.length,l=0;l<e.length;l++)try{t=e[l].getElementsByTagName("td"),n=t[0].getElementsByTagName("v"),d-2>l&&(i.rows[l+2].cells[1].innerHTML=n[0].firstChild.nodeValue)}catch(a){alert(a)}}function timedRefresh(){sendRefreshCommand(),timerId=setTimeout(function(){timedRefresh()},refreshInterval)}function startRefresh(){timer_is_on||(timer_is_on=1,timedRefresh())}function stopRefresh(){clearTimeout(timerId),timer_is_on=0}function sendRefreshCommand(){var e;e=window.XMLHttpRequest?new XMLHttpRequest:new ActiveXObject("Microsoft.XMLHTTP");{var t=document.getElementById("matrix_view"),n=t.rows;n.length}e.onreadystatechange=function(){if(4==e.readyState&&200==e.status){var t=e.responseXML.documentElement.getElementsByTagName("tr");updateMatrix(t,!0)}};var i=document.getElementById("hardware_name").innerHTML;e.open("POST","/rfmaze/mazeAdminServlet?command=refresh&hardware="+i,!0),e.send()}function heartbeat(){checkConnection(),setTimeout(function(){heartbeat()},5e3)}function checkConnection(){var e;e=window.XMLHttpRequest?new XMLHttpRequest:new ActiveXObject("Microsoft.XMLHTTP"),e.onreadystatechange=function(){if(4==e.readyState&&200==e.status){var t=e.responseXML.documentElement.getElementsByTagName("state"),n=t[0].firstChild.nodeValue;document.getElementById("connection_state").innerHTML='<img src="images/'+n+'">',-1==n.indexOf("disconnected")&&$("#progressbar").hide();var i=e.responseXML.documentElement.getElementsByTagName("tr");updateMatrixOffset(i)}};var t=document.getElementById("hardware_name").innerHTML;e.open("POST","/rfmaze/mazeAdminServlet?command=isconnected&hardware="+t,!0),e.send()}var isSetAttenuationActive=!1,headerText="",labelText="",in_progress1=!1,in_progress2=!1;$(document).ready(function(){$("#closebtn").click(function(){isSetAttenuationActive=!1,$("#dlg").hide("500","swing",function(){$("#bkg").fadeOut("300")})}),$("#closebtn1").click(function(){$("#dlg1").hide("500","swing",function(){$("#bkg1").fadeOut("300")})}),$("#setattenuation").click(function(){manual_input=!0,"hidden"==document.getElementById("bkg").style.visibility&&(document.getElementById("bkg").style.visibility="",$("#bkg").hide()),"hidden"==document.getElementById("dlg").style.visibility&&(document.getElementById("dlg").style.visibility="",$("#dlg").hide()),$("#bkg").fadeIn(300,"linear",function(){$("#dlg").show(500,"swing")}),document.getElementById("inputs_label").innerHTML="Inputs",document.getElementById("outputs_label").innerHTML="Outputs",document.getElementById("id_inputs").value="",document.getElementById("id_outputs").value="",document.getElementById("id_inputs").disabled=!1,document.getElementById("id_outputs").disabled=!1,document.getElementById("mimo").disabled=!1}),$("#dlg").draggable(),$("#closebtn2").click(function(){$("#dlg2").hide("500","swing",function(){$("#bkg2").fadeOut("300")})}),$("#handoff").click(function(){"hidden"==document.getElementById("bkg2").style.visibility&&(document.getElementById("bkg2").style.visibility="",$("#bkg2").hide()),"hidden"==document.getElementById("dlg2").style.visibility&&(document.getElementById("dlg2").style.visibility="",$("#dlg2").hide()),$("#bkg2").fadeIn(300,"linear",function(){$("#dlg2").show(500,"swing"),$("#dlg2").draggable()})}),$("#closebtn_cp").click(function(){$("#dlg_cp").hide("500","swing",function(){$("#bkg_cp").fadeOut("300")})}),heartbeat(),refreshInterval=5e3,startRefresh()}),$(function(){$("#slider").slider({value:32,min:0,max:63,step:1,slide:function(e,t){$("#attenuation").val(t.value)}}),$("#attenuation").val($("#slider").slider("value")),$("#slider").mouseup(function(){$(this).after(function(){var e=document.getElementById("attenuation").value,t=document.getElementById("id_inputs").value,n=document.getElementById("id_outputs").value;set_attenuation(t,n,e)})}),$("#slider1").slider({value:32,min:0,max:63,step:1,slide:function(e,t){$("#attenuation1").val(t.value)}}),$("#attenuation1").val($("#slider1").slider("value")),$("#slider1").mouseup(function(){$(this).after(function(){var e=document.getElementById("attenuation1").value,t=document.getElementById("id_inputs1").value,n=document.getElementById("id_outputs1").value;set_attenuation(t,n,e)})})});var timerId,timer_is_on=0,refreshInterval=1e3,connection_mon_timer;$(function(){$(document).tooltip()});
</SCRIPT>
<SCRIPT type="text/javascript" language="javascript" src="js/fix_table.js"></SCRIPT>
<SCRIPT language="javascript">
function viewmatrix() {
    if (document.getElementById('matrix_overview_hardware').selectedIndex == 0) {
        alert("Invalid selection!");
        return;
    }
    document.getElementById('matrix_overview_action').value="list_hardware";
    document.getElementById('matrix_overview').submit();
}
</SCRIPT>

<s:form theme="simple" action="matrix_overview.action" method="post">
   <s:if test="%{errorMessage != null}">
       <img src="images/warn.png">&nbsp;<div class="error_message"><s:property value="errorMessage"/></div>
   </s:if>
   <s:elseif test="%{warningMessage != null}">
       <img src="images/warn.png">&nbsp;<div class="warn_message"><s:property value="warningMessage"/></div>
   </s:elseif>
   <s:else>
   <div id="server_response"></div>
   <table class="ConfigDataTable" align="center" width="30%">
      <thead>
         <tr><th align="center">Select Matrix</th></tr>
      </thead>
      <tbody>
         <tr>
            <td>
                <img src="images/spacer.gif" width="1" height="5"/>
                <div id="hardware_name" style="display: none;"><s:property value="%{hardware}"/></div>
            </td>
         </tr>
         <tr>
            <td align="center">
               <s:select onchange="viewmatrix();" label="Matrix Hardware" headerKey="-1" headerValue="-- select matrix --" list="hardwares" name="hardware"/>
               <img src="images/spacer.gif" width="2" height="1"/>
               <span id="connection_state"></span>
            </td>
         </tr>
         <tr><td align="center"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
      </tbody>
   </table>

   <div class="container_12 divider">
     <div class="grid_12 height380">
        <table class="matrixscrollabe" id="matrix_view" cellpadding="0" cellspacing="0">
           <thead>
              <tr>
                 <th style="width:120px; max-width:120px;"><img src="images/label_admin.png"/></th>
                 <s:iterator value="tableHeader" status="tableHeaderStatus">
                 <th align="center">
                    <div title='<s:property value="%{description}"/>'><s:property value="%{label}"/></div>
                 </th>
                 </s:iterator>
              </tr>
           </thead>
           <tr>
              <td style="cursor: default; background:#688DB2; width:120px; max-width:120px;" align="center">User</td>
              <s:iterator value="tableHeader" status="tableHeaderUserStatus">
                 <td align="center" style="cursor: default; background:#688DB2;">
                   <s:property value="%{user}"/>
              </td>
              </s:iterator>
           </tr>
           <s:iterator value="matrix" status="rowsStatus" var="row">
           <tr>
              <td align="center" style="background: #8ca9cf; white-space:nowrap; width: 120px; max-width:120px; overflow: hidden; text-overflow: ellipsis;">
                 <div title='<s:property value="%{#row[0].description}"/>'><s:property value="%{#row[0].label}"/></div>
              </td>

              <s:iterator value="#row" status="colStatus">
              <s:if test="%{#colStatus.index==0}">
              <td align="center" style="cursor: default; background:#688DB2;"><s:property value="%{name}"/></td>
              </s:if>
              <s:else>
              <td align="center" style='background: <s:property value="%{bgcolor}"/>' onclick="changeAttn(this);"><s:property value="%{name}"/></td>
              </s:else>
              </s:iterator>
           </tr>
           </s:iterator>
        </table>
      </div>
   </div>
   </s:else>

   <s:hidden name="action" value=""/>
   <s:textfield style="visibility: hidden;" id="connected_server_name" name="server"></s:textfield>

   <div class="blockbkg" id="bkg" style="visibility: hidden;">
   <div class="cont" id="dlg" style="visibility: hidden;">
       <table >
           <tr>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right"><strong>Set Attenuation</strong></td>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
           </tr>
       </table>
       <table>
            <thead>
            <tr>
              <th><div id="outputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th><th><div id="inputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th><th nowrap>Attenuation(db)</th><th nowrap>MIMO</th></tr>
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs" name="input" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="attenuation" name="value" size="6"></s:textfield></td>
                <td align="center"><input type="checkbox" id="mimo"></td>
            </tr>
            <tr>
                <td colspan="4" align="center">
                    <fieldset>
                        <legend>Quick Pick</legend>
                        <table>
                        <tr>
                            <td><input type="button" class="button" value="0" onclick="set_atten(1, '0');"></td>
                            <td><input type="button" class="button" value="5" onclick="set_atten(1, '5');"></td>
                            <td><input type="button" class="button" value="10" onclick="set_atten(1, '10');"></td>
                            <td><input type="button" class="button" value="15" onclick="set_atten(1, '15');"></td>
                            <td><input type="button" class="button" value="20" onclick="set_atten(1, '20');"></td>
                        </tr>
                        <tr>
                            <td><input type="button" class="button" value="25" onclick="set_atten(1, '25');"></td>
                            <td><input type="button" class="button" value="30" onclick="set_atten(1, '30');"></td>
                            <td><input type="button" class="button" value="35" onclick="set_atten(1, '35');"></td>
                            <td><input type="button" class="button" value="40" onclick="set_atten(1, '40');"></td>
                            <td><input type="button" class="button" value="63" onclick="set_atten(1, '63');"></td>
                        </tr>
                        </table>
                    </fieldset>
                </td>
            </tr>
            <tr><td colspan="4"><div id="slider"></div></td></tr>
            <tr>
                <td align="left" valign="top">0</td>
                <td colspan="2" align="center">
                    <input type="button" class="button" onclick="decrment_and_send(1);" value="1 <<"><img src="images/spacer.gif" height="1" width="3"><input type="button" class="button" onclick="increment_and_send(1);" value=">> 1">
                </td>
                <td align="right" valign="top">63</td>
            </tr>
            <tr><td colspan="4"><img src="images/spacer.gif" width="1" height="5"></td></tr>
            <tr style="visibility:hidden" id="attenuation_field"><td colspan="4" align="center"><input type="button" name="setatten" value="Set Attenuation" onclick="set_attenuation_btn(1);"/></td></tr>
        </table>
    </div>
    </div>

    <div class="blockbkg" id="bkg1" style="visibility: hidden;">
    <div class="cont" id="dlg1" style="visibility: hidden;">
        <table >
            <tr>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><strong>Set Attenuation</strong></td>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><div class="closebtn" title="Close" id="closebtn1"></div></td>
            </tr>
          </table>

        <table>
            <thead>
            <tr><th><div id="outputs_label1"></div><img src="images/spacer.gif" width="5" height="1"></th><th><div id="inputs_label1"></div><img src="images/spacer.gif" width="5" height="1"></th><th nowrap>Attenuation(db)</th><th nowrap>MIMO</th></tr>
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs1" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs1" name="input" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="attenuation1" name="value" size="6" ></s:textfield></td>
                <td align="center"><input type="checkbox" id="mimo1"></td>
            </tr>
            <tr>
                <td colspan="4" align="center">
                    <fieldset>
                        <legend>Quick Pick</legend>
                        <table>
                        <tr>
                            <td><input type="button" class="button" value="0" onclick="set_atten(2, '0');"></td>
                            <td><input type="button" class="button" value="5" onclick="set_atten(2, '5');"></td>
                            <td><input type="button" class="button" value="10" onclick="set_atten(2, '10');"></td>
                            <td><input type="button" class="button" value="15" onclick="set_atten(2, '15');"></td>
                            <td><input type="button" class="button" value="20" onclick="set_atten(2, '20');"></td>
                        </tr>
                        <tr>
                            <td><input type="button" class="button" value="25" onclick="set_atten(2, '25');"></td>
                            <td><input type="button" class="button" value="30" onclick="set_atten(2, '30');"></td>
                            <td><input type="button" class="button" value="35" onclick="set_atten(2, '35');"></td>
                            <td><input type="button" class="button" value="40" onclick="set_atten(2, '40');"></td>
                            <td><input type="button" class="button" value="63" onclick="set_atten(2, '63');"></td>
                        </tr>
                        </table>
                    </fieldset>
                </td>
            </tr>
            <tr><td colspan="4"><div id="slider1"></div></td></tr>
            <tr>
                <td align="left" valign="top">0</td>
                <td colspan="2" align="center">
                    <input type="button" class="button" onclick="decrment_and_send(2);" value="1 <<"><img src="images/spacer.gif" height="1" width="3"><input type="button" class="button" onclick="increment_and_send(2);" value=">> 1">
                </td>
                <td align="right" valign="top">63</td>
            </tr>
            <tr style="visibility:hidden" id="attenuation_field1"><td colspan="4" align="center"><input type="button" name="setatten" value="Set Attenuation" onclick="set_attenuation_btn(2);"/></td></tr>
        </table>
    </div>
    </div>
    <span id="progressbar"><img src="images/progressbar.gif"></span>
</s:form>
