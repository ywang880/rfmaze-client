<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<style>
#block_container{text-align:center}#connection_state,#hardware_name{display:inline}.spinner{position:fixed;top:50%;left:50%;margin-left:-50px;margin-top:-50px;text-align:center;z-index:1234;overflow:auto;width:100px;height:102px}
</style>

<SCRIPT type="text/javascript" language="javascript" src="js/rfmaze.js"></SCRIPT>
<SCRIPT type="text/javascript" src="js/jscolor.js"></SCRIPT>

<SCRIPT language="javascript">
function viewmatrix(){if(document.getElementById("rfmaze_hardware").selectedIndex==0){alert("Invalid selection!");return}stopRefresh();document.getElementById("rfmaze").submit()}function onInit(){$("#spinner").bind("ajaxSend",function(){$(this).show()}).bind("ajaxStop",function(){$(this).hide()}).bind("ajaxError",function(){$(this).hide()});ping()}function ping(){pingConnection();setTimeout(function(){ping()},5e3)}function pingConnection(){var e=$.get("/rfmaze/mazeServlet?command=hello",function(e){document.getElementById("connection_state").innerHTML="<img src='images/connected.png'>";$("#spinner").hide();if(processid=="PID"){processid=e}else if(processid!=e){$("#dialog_alert").dialog()}}).fail(function(){document.getElementById("connection_state").innerHTML="<img src='images/disconnected.png'>";$("#spinner").show()})}var processid="PID"
</SCRIPT>

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
    headerText = i.childNodes[0].nodeValue, i = n.childNodes[1], labelText = i.childNodes[0].nodeValue;
    var d = e.parentNode.rowIndex,
    a = e.cellIndex;
    document.getElementById("attenuation").value = e.innerHTML,
    "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "",
    $("#bkg").hide()),
      "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "",
      $("#dlg").hide()), $("#bkg").fadeIn(500, "linear", function() {
          $("#dlg").show(500, "swing"),
          $("#dlg").draggable();
          $("#dlg").zIndex(1)
      }),

    document.getElementById("id_inputs").value = d, document.getElementById("id_outputs").value = a,
    document.getElementById("id_inputs").disabled = !0,
    document.getElementById("id_outputs").disabled = !0,
    document.getElementById("inputs_label").innerHTML = labelText,
    document.getElementById("outputs_label").innerHTML = headerText
}

function set_attenuation_btn(e) {
    if (1 == e) {
        var t = document.getElementById("attenuation").value,
            n = document.getElementById("id_inputs").value,
            i = document.getElementById("id_outputs").value;
        set_attenuation(n, i, t)
    } else if (2 == e) {
        var t = document.getElementById("attenuation1").value,
            n = document.getElementById("id_inputs1").value,
            i = document.getElementById("id_outputs1").value;
        set_attenuation(n, i, t)
    }
}

function on_switching(e, t) {
    if (1 == e) {
        document.getElementById("attenuation").value = t;
        var n = document.getElementById("id_inputs").value,
            i = document.getElementById("id_outputs").value;
        set_attenuation(n, i, t)
    } else if (2 == e) {
        document.getElementById("attenuation1").value = t;
        var n = document.getElementById("id_inputs1").value,
            i = document.getElementById("id_outputs1").value;
        set_attenuation(n, i, t)
    }
}

function set_attenuation(e, t, n) {
    if (isBlank(e) || isBlank(t)) return void alert("Input is invalid. Row and column cannot not empty!");
    var i = /^[0-9,]*$/.test(e);
    if (!i) return void alert("Input is invalid. The inputs must be digits separated by comma.");
    if (i = /^[0-9,]*$/.test(t), !i) return void alert("Output is invalid. The outputs must be digits separated by comma.");
    for (var d = document.getElementById("matrix_view").rows.length - 1, a = document.getElementById("matrix_view").rows[0].cells.length - 1, l = e.split(","), o = t.split(","), u = 0; u < l.length; u++)
        if (l[u] > d) return void alert("Input is invalid. Maximum row number is " + d);
    for (var u = 0; u < o.length; u++)
        if (o[u] > a) return void alert("Output is invalid. Maximum column number is " + a);
    var s = 0,
        r = "set_attenuation";
    s && (r = "set_mimo"), 0 > n ? n = 0 : n > 63 && (n = 63);
    var m;
    m = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"), m.onreadystatechange = function() {
        if (4 == m.readyState && 200 == m.status) {
            var e = m.responseXML.documentElement.getElementsByTagName("tr");
        }
    }, m.open("GET", "/rfmaze/mazeServlet?command=" + r + "&outputs=" + t + "&inputs=" + e + "&value=" + n, !0), m.send(null)
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
    }, e.open("GET", "/rfmaze/mazeServlet?command=refresh", !0), e.send()
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
            var t = e.responseXML.documentElement.getElementsByTagName("server_state"),
                n = t[0].firstChild.nodeValue;
            if ("reload" == n) return void(window.location = "rfmaze.action");
            var i = e.responseXML.documentElement.getElementsByTagName("state"),
                d = i[0].firstChild.nodeValue;
            document.getElementById("connection_state").innerHTML = '<img src="images/' + d + '">', -1 == d.indexOf("disconnected") && $("#progressbar").hide();
            var a = e.responseXML.documentElement.getElementsByTagName("tr");
        }
    }, e.open("POST", "/rfmaze/mazeServlet?command=isconnected", !0), e.send()
}
var isSetAttenuationActive = !1,
    headerText = "",
    labelText = "",
    in_progress1 = !1,
    in_progress2 = !1;

$(document).ready(function() {
    $("#closebtn_atten").click(function() {
        $("#dlg_atten").hide("500", "swing", function() {
            $("#bkg_atten").fadeOut("300")
        })
    }),
    $("#closebtn").click(function() {
        isSetAttenuationActive = !1, $("#dlg").hide("500", "swing", function() {
            $("#bkg").fadeOut("300")
        })
    }), $("#closebtn1").click(function() {
        $("#dlg1").hide("500", "swing", function() {
            $("#bkg1").fadeOut("300")
        })
    }), $("#setattenuation").click(function() {
        manual_input = !0,
        "hidden" == document.getElementById("bkg_atten").style.visibility && (document.getElementById("bkg_atten").style.visibility = "",
        $("#bkg_atten").hide()),
        "hidden" == document.getElementById("dlg_atten").style.visibility && (document.getElementById("dlg_atten").style.visibility = "",
        $("#dlg_atten").hide()),
        $("#bkg_atten").fadeIn(300, "linear", function() {
            $("#dlg_atten").show(500, "swing")
        }),
        document.getElementById("label_inputs").innerHTML = "Input",
        document.getElementById("label_outputs").innerHTML = "Outputs",
        document.getElementById("id_inputs").value = "",
        document.getElementById("id_outputs").value = "",
        document.getElementById("id_inputs").disabled = !1,
        document.getElementById("id_outputs").disabled = !1
    }), $("#dlg").draggable(), $("#closebtn2").click(function() {
        $("#dlg2").hide("500", "swing", function() {
            $("#bkg2").fadeOut("300")
        })
    }), $("#closebtn_cp").click(function() {
        $("#dlg_cp").hide("500", "swing", function() {
            $("#bkg_cp").fadeOut("300")
        })
    }), $("#customer_color_picker").click(function() {
        "hidden" == document.getElementById("bkg_cp").style.visibility && (document.getElementById("bkg_cp").style.visibility = "", $("#bkg_cp").hide()), "hidden" == document.getElementById("dlg_cp").style.visibility && (document.getElementById("dlg_cp").style.visibility = "", $("#dlg_cp").hide()), $("#bkg_cp").fadeIn(300, "linear", function() {
            $("#dlg_cp").show(500, "swing"), $("#dlg_cp").draggable()
        })
    }), $("#attenuation").keypress(function() {
        document.getElementById("attenuation_field").style.visibility = ""
    }), $("#attenuation1").keypress(function() {
        document.getElementById("attenuation_field1").style.visibility = ""
    }), onInit(), heartbeat(), startRefresh()
});

var timerId, timer_is_on = 0,
    refreshInterval = 1000,
    connection_mon_timer;

</SCRIPT>

<s:form theme="simple" action="rfmaze.action" method="post">
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
            <tr><th align="center" colspan="2">Select Matrix</th></tr>
        </thead>
        <tbody>
            <tr>
               <td align="center" colspan="2">
                  <img src="images/spacer.gif" width="1" height="5"/>
                  <div id="hardware_name" style="display: none"><s:property value="%{hardware}"/></div>
               </td>
            </tr>
            <tr>
                <td align="center"><s:select onchange="viewmatrix();" label="Matrix Hardware" headerKey="-1" headerValue="-- select matrix --" list="hardwares" name="hardware"/></td>
                <td><div id="connection_state"></div></td>
            </tr>
            <tr><td align="center" colspan="2"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
        </tbody>
    </table>

    <table class="matrix" id="matrix_view" align="center">
        <thead>
            <tr>
                <th><img src="images/label.png"/></th>
                <s:iterator value="tableHeader" status="tableHeaderStatus">
                <s:if test="%{label=='Power'}">
                </s:if>
                <s:else>
                <th align="center">
                    <a class="tooltip" href="#" style="cursor:default"><s:property value="%{label}"/><span class="info"><s:property value="%{description}"/></span></a>
                </th>
                </s:else>
                </s:iterator>
            </tr>
        </thead>

        <s:iterator value="matrix" status="rowsStatus" var="row">
        <tr>
            <td align="center" style="style="width:120px; max-width:120px; background: #8ca9cf; white-space:nowrap; overflow: hidden; text-overflow: ellipsis;">
                <div title='<s:property value="%{#row[0].description}"/>'><s:property value="%{#row[0].label}"/></div>
            </td>
            <s:iterator value="#row" status="colStatus">
            <s:if test="%{#colStatus.index>0}">
                <s:if test="%{name==1}">
                <td align="center" style='background: green; cursor: default;' onclick="changeAttn(this);">ON</td>
                </s:if>
            <s:else>
                <td align="center" style='background: #C0C0C0; cursor: pointer;' onclick="changeAttn(this);">OFF</td>
            </s:else>
            </s:if>
        </s:iterator>
        </tr>
        </s:iterator>
    </table>

    </s:else>
   <s:hidden name="action" value=""/>
   <s:textfield style="visibility: hidden;" id="connected_server_name" name="server"></s:textfield>
   <div id="spinner" class="spinner" style="display:none;">
       <img id="img-spinner" src="images/spinner.gif" alt="Connecting"/>
   </div>

   <div class="blockbkg" id="bkg" style="visibility: hidden;">
   <div class="cont" id="dlg" style="visibility: hidden;">
       <table >
           <tr>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right"><strong>Toggle ON and OFF</strong></td>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
           </tr>
           <tr><td><img src="images/spacer.gif" width="1" height="10"/></td></tr>
       </table>
       <table>
           <thead>
           <tr>
               <th><div id="outputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th>
               <th><img src="images/spacer.gif" width="10" height="1"></th>
               <th><div id="inputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th>
               </tr>
           </thead>
           <tr>
               <td align="center"><s:textfield id="id_outputs" name="output" size="6"></s:textfield></td>
               <td align="center"><s:textfield id="id_inputs" name="input" size="6"></s:textfield></td>
               <td align="center"><s:textfield readonly="true" id="attenuation" name="value" size="6"></s:textfield></td>
           </tr>
           <tr><td><img src="images/spacer.gif" width="1" height="10"/></td></tr>
             <tr>
               <td colspan="3" align="center">
                   <input type="button" class="button" value="ON" onclick="on_switching(1, '1');">
               </td>
           </tr>
       </table>
    </div>
    </div>

    <div class="blockbkg" id="bkg1" style="visibility: hidden;">
    <div class="cont" id="dlg1" style="visibility: hidden;">
        <table >
            <tr>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><strong>Toggle ON and OFF</strong></td>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><div class="closebtn" title="Close" id="closebtn1"></div></td>
            </tr>
            <tr><td><img src="images/spacer.gif" width="1" height="10"/></td></tr>
          </table>

        <table>
            <thead>
            <tr><th><div id="outputs_label1"></div><img src="images/spacer.gif" width="5" height="1"></th>
            <th><img src="images/spacer.gif" width="10" height="1"></th>
            <th><div id="inputs_label1"></div><img src="images/spacer.gif" width="5" height="1"></th>
            </tr>
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs1" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs1" name="input" size="6"></s:textfield></td>
                <td align="center"><s:textfield readonly="true" id="attenuation1" name="value" size="6" ></s:textfield></td>
            </tr>
            <tr><td><img src="images/spacer.gif" width="1" height="10"/></td></tr>
            <tr>
                <td colspan="3" align="right">
                    <input type="button" class="button" value="ON" onclick="on_switching(2, '1');">
                </td>
            </tr>
        </table>
    </div>
    </div>
    <span id="progressbar"><img src="images/progress_bar.gif"></span>
    <div id="dialog_alert" title="Connection Recovered" style="display:none;">
        <p>Application Server Connection is Recovered. Please Login Again!</p>
    </div>
    <div id="debug"></div>
</s:form>
