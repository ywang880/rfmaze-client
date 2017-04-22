<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">
<link href="css/960.css" rel="stylesheet" media="screen" />
<link href="css/defaultTheme.css" rel="stylesheet" media="screen" />

<style>
.divider{margin-top:20px}.height380{height:480px;overflow-x:auto;overflow-y:auto}.matrixscrollabe{font-size:12px;color:#000;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif}.matrixscrollabe td,.matrixscrollabe th{border:1px solid #789;padding:5px}.matrixscrollabe tbody tr td{background-color:#eef2f9;background-image:-moz-linear-gradient(top,rgba(255,255,255,0.4) 0%,rgba(255,255,255,0.0) 100%);background-image:-webkit-gradient(linear,left top,left bottom,color-stop(0%,rgba(255,255,255,0.4)),color-stop(100%,rgba(255,255,255,0.0)))}.matrixscrollabe tbody tr.odd td{background-color:#d6e0ef;background-image:-moz-linear-gradient(top,rgba(255,255,255,0.4) 0%,rgba(255,255,255,0.0) 100%);background-image:-webkit-gradient(linear,left top,left bottom,color-stop(0%,rgba(255,255,255,0.4)),color-stop(100%,rgba(255,255,255,0.0)))}.matrixscrollabe thead tr th,.matrixscrollabe thead tr td,.matrixscrollabe tfoot tr th,.matrixscrollabe tfoot tr td{background-color:#8ca9cf;background-image:-moz-linear-gradient(top,rgba(255,255,255,0.4) 0%,rgba(255,255,255,0.0) 100%);background-image:-webkit-gradient(linear,left top,left bottom,color-stop(0%,rgba(255,255,255,0.4)),color-stop(100%,rgba(255,255,255,0.0)));font-weight:700}
</style>

<script type="text/javascript" language="javascript" src="js/rfmaze.js"></script>

<SCRIPT language="javascript">
function changeAttn(e) {
    var currentValue = e.innerHTML;
    if (currentValue == 'ON') {
        alert("You can only turn on the switch for a selected output!");
        return;
    }
    var t = document.getElementById("matrix_view").rows[0].cells[e.cellIndex],
        n = document.getElementById("matrix_view").rows[e.parentNode.rowIndex].cells[0],
        i = t.childNodes[1];

    headerText = i.childNodes[0].nodeValue,
    i = n.childNodes[1],
    labelText = i.childNodes[0].nodeValue;

    var d = e.parentNode.rowIndex,
        l = e.cellIndex;

    if (0 != l) {
        document.getElementById("attenuation").value = currentValue,
        "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "",
        $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "",
        $("#dlg").hide()), $("#bkg").fadeIn(500, "linear", function() {
            $("#dlg").show(500, "swing"), $("#dlg").draggable();
            $("#dlg").zIndex(1)
        }),

        document.getElementById("id_inputs").value = d,
        document.getElementById("id_outputs").value = l,
        document.getElementById("id_inputs").disabled = !0,
        document.getElementById("id_outputs").disabled = !0,
        document.getElementById("inputs_label").innerHTML = labelText,
        document.getElementById("outputs_label").innerHTML = headerText
    }
}

function turn_on_switch () {
    var t = document.getElementById("attenuation").value,
        n = document.getElementById("id_inputs").value,
        i = document.getElementById("id_outputs").value;
    set_attenuation(n, i, 1)
}

function set_attenuation(e, t, n) {
    if (isBlank(e) || isBlank(t)) return void alert("Input is invalid. Row and column cannot not empty!");
    var i = /^[0-9,]*$/.test(e);
    if (!i) return void alert("Input is invalid. The inputs must be digits separated by comma.");
    if (i = /^[0-9,]*$/.test(t), !i) return void alert("Output is invalid. The outputs must be digits separated by comma.");
    for (var d = document.getElementById("matrix_view").rows.length - 1, l = document.getElementById("matrix_view").rows[0].cells.length - 2, a = e.split(","), u = t.split(","), o = 0; o < a.length; o++)
        if (a[o] > d) return void alert("Input is invalid. Maximum row number is " + d);
    for (var o = 0; o < u.length; o++)
        if (u[o] > (1+l)) return void alert("Output is invalid. Maximum column number is " + l);
    var m = "set_attenuation";
    0 > n ? n = 0 : n > 31 && (n = 31);
    var r;
    r = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"), r.onreadystatechange = function() {
        if (4 == r.readyState && 200 == r.status) {
            turnOffCurrent(e, t);
            turnOnSelectedNode(e, t);
        }
    };
    var c = document.getElementById("hardware_name").innerHTML;
    r.open("POST", "/rfmaze/mazeAdminServlet?command=" + m + "&outputs=" + t + "&inputs=" + e + "&value=" + n + "&hardware=" + c, !0), r.send(null)
    document.getElementById('attenuation').value="ON";
}

function turnOnSelectedNode(e, t) {
    var vt = document.getElementById("matrix_view");
    vt.rows[e].cells[t].childNodes[0].nodeValue = "ON";
    vt.rows[e].cells[t].style.backgroundColor = 'green';
}

function turnOffCurrent(e, t) {
    var vt = document.getElementById("matrix_view");
    for (ri = 0; ri < vt.rows.length; ri++) {
        var cellVal = vt.rows[ri].cells[t].childNodes[0].nodeValue;
        if (cellVal == "ON") {
            vt.rows[ri].cells[t].childNodes[0].nodeValue = "OFF";
            vt.rows[ri].cells[t].style.backgroundColor = '#C0C0C0';
        }
    }
    for (ri = 0; ri < vt.rows[0].cells.length; ri++) {
        var cellVal = vt.rows[e].cells[ri].childNodes[0].nodeValue;
        if (cellVal == "ON") {
            vt.rows[e].cells[ri].childNodes[0].nodeValue = "OFF";
            vt.rows[e].cells[ri].style.backgroundColor = '#C0C0C0';
        }
    }
}

function updateMatrix(data) {
    var data_table = document.getElementById("matrix_view");
    for (var i = 0; i < data.length; i++) {
        var tds = data[i].getElementsByTagName("td");
        for (var n = 1; n < tds.length; n++) {
            var value = tds[n].getElementsByTagName("v");
            var new_val= (value[0].firstChild.nodeValue=='1')? 'ON' :'OFF';
            var new_color = (value[0].firstChild.nodeValue=='1')? 'green' :'#C0C0C0';
            data_table.rows[i + 1].cells[n].innerHTML = new_val;
            data_table.rows[i + 1].cells[n].style.backgroundColor = new_color;
            data_table.rows[i + 1].cells[n].style.cursor = (value[0].firstChild.nodeValue=='1')? 'default' : 'pointer';
        }
    }
}

function timedRefresh() {
    sendRefreshCommand(), timerId = setTimeout(function() {
        timedRefresh()
    }, refreshInterval)
}

function startRefresh() {
    timer_is_on || (timer_is_on = 1, timedRefresh())
}

function stopRefresh() {
    clearTimeout(timerId), timer_is_on = 0
}

function sendRefreshCommand() {
    var e;
    e = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"); {
        var t = document.getElementById("matrix_view"),
            n = t.rows;
        n.length
    }
    e.onreadystatechange = function() {
        if (4 == e.readyState && 200 == e.status) {
            var t = e.responseXML.documentElement.getElementsByTagName("tr");
            updateMatrix(t)
        }
    };
    var i = document.getElementById("hardware_name").innerHTML;
    e.open("POST", "/rfmaze/mazeAdminServlet?command=refresh&hardware=" + i, !0), e.send()
}

function heartbeat() {
    checkConnection(), setTimeout(function() {
        heartbeat()
    }, 5e3)
}

function checkConnection() {
    var e;
    e = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"), e.onreadystatechange = function() {
        if (4 == e.readyState && 200 == e.status) {
            var t = e.responseXML.documentElement.getElementsByTagName("state"),
                n = t[0].firstChild.nodeValue;
            document.getElementById("connection_state").innerHTML = '<img src="images/' + n + '">', -1 == n.indexOf("disconnected") && $("#progressbar").hide();
        }
    };
    var t = document.getElementById("hardware_name").innerHTML;
    e.open("POST", "/rfmaze/mazeAdminServlet?command=isconnected&hardware=" + t, !0), e.send()
}
var headerText = "",
    labelText = "",
    in_progress1 = !1,
    in_progress2 = !1;
$(document).ready(function() {
    $("#closebtn").click(function() {
        $("#dlg").hide("500", "swing", function() {
            $("#bkg").fadeOut("300")
        })
    }), $("#setattenuation").click(function() {
        manual_input = !0, "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "", $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "", $("#dlg").hide()), $("#bkg").fadeIn(300, "linear", function() {
            $("#dlg").show(500, "swing")
        }), document.getElementById("inputs_label").innerHTML = "Inputs", document.getElementById("outputs_label").innerHTML = "Outputs", document.getElementById("id_inputs").value = "", document.getElementById("id_outputs").value = "", document.getElementById("id_inputs").disabled = !1, document.getElementById("id_outputs").disabled = !1, document.getElementById("mimo").disabled = !1
    }), $("#dlg").draggable(), $("#closebtn_cp").click(function() {
        $("#dlg_cp").hide("500", "swing", function() {
            $("#bkg_cp").fadeOut("300")
        })
    }), heartbeat(), refreshInterval = 5e3, startRefresh()
});

var timerId, timer_is_on = 0,
    refreshInterval = 1e3,
    connection_mon_timer;
$(function() {
    $(document).tooltip()
});
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
         <tr><td><img src="images/spacer.gif" width="1" height="5"/></td></tr>
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

           <s:iterator value="matrix" status="rowsStatus" var="row">
           <tr>
              <td align="center" style="width:120px; max-width:120px; background: #8ca9cf; white-space:nowrap; overflow: hidden; text-overflow: ellipsis;">
                 <div title='<s:property value="%{#row[0].description}"/>'><s:property value="%{#row[0].label}"/></div>
              </td>
              <s:iterator value="#row" status="colStatus">
                <s:if test="%{name==1}">
                     <td align="center" style='background: green; cursor: default;' onclick="changeAttn(this);">ON</td>
                </s:if>
                <s:else>
                     <td align="center" style='background: #C0C0C0; cursor: pointer;' onclick="changeAttn(this);">OFF</td>
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
               <td align="right"><strong>Turn ON Switch</strong></td>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
           </tr>
       </table>
       <table>
            <thead>
            <tr>
              <th><div id="outputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th>
              <th><div id="inputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th>
              <th nowrap>Current State</th>
            </tr>  
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs" name="input" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="attenuation" name="value" size="6"></s:textfield></td>
            </tr>
            <tr>
                <td colspan="3" align="center"><img src="images/spacer.gif" width="1" height="5"></td>
            </tr>
            <tr><td colspan="3"><img src="images/spacer.gif" width="1" height="5"></td></tr>
            <tr>
                <td colspan="3" align="center">
                    <input type="button" name="setatten" value="Turn ON" onclick="turn_on_switch ();"/>
                </td>
            </tr>
        </table>
    </div>
    </div>
    <span id="progressbar"><img src="images/progressbar.gif"></span>
</s:form>
