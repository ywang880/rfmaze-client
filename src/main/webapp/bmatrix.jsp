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
function changeOutputAttn(e) {
    var t = document.getElementById("matrix_view").rows[0].cells[e.cellIndex],
        n = document.getElementById("matrix_view").rows[e.parentNode.rowIndex].cells[0],
        i = t.childNodes[1];

    headerText = i.childNodes[0].nodeValue, labelText = i.childNodes[0].nodeValue;

    var d = e.parentNode.rowIndex,
    a = e.cellIndex;

    $("#slider2").slider("value", e.innerHTML),
    $("#attenuation2").val(e.innerHTML)
    "hidden" == document.getElementById("bkg_atten").style.visibility && (document.getElementById("bkg_atten").style.visibility = "",
    $("#bkg_atten").hide()),
    "hidden" == document.getElementById("dlg_atten").style.visibility && (document.getElementById("dlg_atten").style.visibility = "",
    $("#dlg_atten").hide()),
    $("#bkg_atten").fadeIn(500, "linear", function() {
         isSetAttenuationActive = !0,
         $("#dlg_atten").show(500, "swing"),
         $("#dlg_atten").draggable()
    }),

    document.getElementById("id_outputs2").value = a,
    document.getElementById("id_inputs2").disabled = !0,
    document.getElementById("id_outputs2").disabled = !0,
    document.getElementById("inputs_label2").innerHTML = 'Input',
    document.getElementById("outputs_label2").innerHTML = headerText
}

function changeAttn(e) {
    var currentValue = e.innerHTML;
    if (currentValue == 'ON') {
        document.getElementById('switch_on').style.display = "none"
        document.getElementById('switch_off').style.display = ""
    } else {
        document.getElementById('switch_on').style.display = ""
        document.getElementById('switch_off').style.display = "none"
    }

    var t = document.getElementById("matrix_view").rows[0].cells[e.cellIndex],
        n = document.getElementById("matrix_view").rows[e.parentNode.rowIndex].cells[0],
        i = t.childNodes[1];
    headerText = i.childNodes[0].nodeValue, i = n.childNodes[1], labelText = i.childNodes[0].nodeValue;
    var d = e.parentNode.rowIndex,
        a = e.cellIndex;
        document.getElementById("attenuation").value = e.innerHTML,
        $("#slider").slider("value", e.innerHTML),
        "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "",
        $("#bkg").hide()),
        "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "",
        $("#dlg").hide()), $("#bkg").fadeIn(500, "linear", function() {
            isSetAttenuationActive = !0,
            $("#dlg").show(500, "swing"),
            $("#dlg").draggable()
        }),

        document.getElementById("id_inputs").value = d - 1, document.getElementById("id_outputs").value = a,
        document.getElementById("id_inputs").disabled = !0,
        document.getElementById("id_outputs").disabled = !0,
        document.getElementById("inputs_label").innerHTML = labelText,
        document.getElementById("outputs_label").innerHTML = headerText
}

function changeAttn2(e) {
    var t = e.parentNode.rowIndex,
        n = e.cellIndex - 1;
    0 != n && (document.getElementById("attenuation1").value = e.innerHTML, $("#slider1").slider("value", e.innerHTML), "hidden" == document.getElementById("bkg1").style.visibility && (document.getElementById("bkg1").style.visibility = "", $("#bkg1").hide()), "hidden" == document.getElementById("dlg1").style.visibility && (document.getElementById("dlg1").style.visibility = "", $("#dlg1").hide()), $("#bkg1").fadeIn(500, "linear", function() {
        $("#dlg1").show(500, "swing"), $("#dlg1").draggable()
    }), document.getElementById("id_inputs1").value = t, document.getElementById("id_outputs1").value = n, document.getElementById("id_inputs1").disabled = !0, document.getElementById("id_outputs1").disabled = !0, document.getElementById("inputs_label1").innerHTML = labelText, document.getElementById("outputs_label1").innerHTML = headerText)
}

function set_attenuation_btn(e) {
    if (1 == e) {
        var t = document.getElementById("attenuation").value,
            n = document.getElementById("id_inputs").value,
            i = document.getElementById("id_outputs").value;
        $("#slider").slider("value", t), set_attenuation(n, i, t)
    } else if (2 == e) {
        var t = document.getElementById("attenuation1").value,
            n = document.getElementById("id_inputs1").value,
            i = document.getElementById("id_outputs1").value;
        $("#slider1").slider("value", t), set_attenuation(n, i, t)
    }
}

function set_atten(e, t) {
    if (1 == e) {
        document.getElementById("attenuation2").value = t;
        var n = document.getElementById("id_inputs2").value,
            i = document.getElementById("id_outputs2").value;
        $("#slider").slider("value", t), set_attenuation(n, i, t)
    } else if (2 == e) {
        document.getElementById("attenuation2").value = t;
        var n = document.getElementById("id_inputs2").value,
            i = document.getElementById("id_outputs2").value;
        $("#slider1").slider("value", t), set_attenuation(n, i, t)
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

function updateMatrix(e, t) {
    var e, n, i, d, l = document.getElementById("matrix_view");
    for (n = 0; n < e.length; n++) try {
        for (tds = e[n].getElementsByTagName("td"), i = 1; i < tds.length; i++)(0 != i || t) && (d = tds[i].getElementsByTagName("v")),
        new_val= (d[0].firstChild.nodeValue=='1')? 'ON' :'OFF',
        new_color = (d[0].firstChild.nodeValue=='1')? 'green' :'#C0C0C0',
        l.rows[n + 2].cells[i].innerHTML = new_val,
        l.rows[n + 2].cells[i].style.backgroundColor = new_color
    } catch (o) {
        alert(count+ "  " + n + "  " + i + "  " + o);
    }
}

function updateMatrixAttenuation(e, t) {
    var e, i, d, l = document.getElementById("matrix_view");
    try {
        for (ths = e[0].getElementsByTagName("th"), i = 0; i < ths.length; i++) {
            d = ths[i].getElementsByTagName("v");
            c = ths[i].getElementsByTagName("c");
            l.rows[1].cells[i+1].innerHTML = d[0].firstChild.nodeValue;
            l.rows[1].cells[i+1].style.backgroundColor = c[0].firstChild.nodeValue;
        }
    } catch (o) {
        alert(count+ "  " + i + "  " + o);
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

function change_scheme() {
    document.getElementById("rfmaze_action").value = "color_scheme", document.getElementById("rfmaze").submit()
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
            var h = e.responseXML.documentElement.getElementsByTagName("thead");
            updateMatrixAttenuation(h)
            updateMatrix(t, !0)
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
}), $(function() {
    $("#slider2").slider({
        value: 15,
        min: 0,
        max: 31,
        step: 1,
        slide: function(e, t) {
            $("#attenuation2").val(t.value)
        }
    }),
    $("#attenuation2").val($("#slider2").slider("value")),
    $("#slider2").mouseup(function() {
        $(this).after(function() {
            var e = document.getElementById("attenuation2").value,
                t = document.getElementById("id_inputs2").value,
                n = document.getElementById("id_outputs2").value;
            set_attenuation(t, n, e)
        })
    })
});
var timerId, timer_is_on = 0,
    refreshInterval = 1e3,
    connection_mon_timer;

function increment_and_send(a) {
    if (!in_progress1) if (in_progress1 = !0, setTimeout(function() {
        in_progress1 = !1;
    }, 300), 1 == a) {
        var b = document.getElementById("id_inputs2").value,
            c = document.getElementById("id_outputs2").value,
            d = document.getElementById("attenuation2").value;
        if (31 == d) return void alert("The new value exceeds the limit. Maximum value is 31");
            var e = ++d;
            document.getElementById("attenuation2").value = e,
            $("#slider2").slider("value", e),
        set_attenuation(b, c, e);
    } else if (2 == a) {
        var b = document.getElementById("id_inputs1").value,
            c = document.getElementById("id_outputs1").value,
            d = document.getElementById("attenuation1").value;
        if (31 == d) return void alert("The new value exceeds the limit. Maximum value is 31");
            var e = ++d;
            document.getElementById("attenuation1").value = e,
            $("#slider1").slider("value", e),
        set_attenuation(b, c, e);
    }
}

function decrment_and_send(a) {
    if (!in_progress2) if (in_progress2 = !0, setTimeout(function() {
        in_progress2 = !1;
    }, 300), 1 == a) {
        var b = document.getElementById("id_inputs2").value,
            c = document.getElementById("id_outputs2").value,
            d = document.getElementById("attenuation2").value;
        if (0 == d) return void alert("The value cannot be decremented as the current value already reached the low boundary!");
        var e = --d;
        document.getElementById("attenuation2").value = e, $("#slider2").slider("value", e),
        set_attenuation(b, c, e);
    } else if (2 == a) {
        var b = document.getElementById("id_inputs1").value,
            c = document.getElementById("id_outputs1").value,
            d = document.getElementById("attenuation1").value;
        if (0 == d) return void alert("The value cannot be decremented as the current value already reached the low boundary!");
        var e = --d;
        document.getElementById("attenuation1").value = e, $("#slider1").slider("value", e),
        set_attenuation(b, c, e);
    }
}
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
   <div><img src="images/spacer.gif" width="1" height="10"/></div>
   <div id="block_container" style="color:#EEEEEE;font-weight:bold;height:30;display:table-cell;vertical-align:bottom;text-align:center;border:0px">
       <div id="hardware_name"><s:property value="%{hardware}"/>&nbsp;</div>
       <div id="connection_state"></div>
   </div>

    <table class="ConfigDataTable" align="center" width="30%">
        <thead>
            <tr><th align="center" colspan="2">Select Matrix</th></tr>
        </thead>
        <tbody>
            <tr><td align="center" colspan="2"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
            <tr>
                <td align="center"><s:select onchange="viewmatrix();" label="Matrix Hardware" headerKey="-1" headerValue="-- select matrix --" list="hardwares" name="hardware"/></td>
                <td><div id="datachanged"></div></td>
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

       <tr>
           <td align="center" style="background: #005566; white-space:nowrap"></td>
           <s:iterator value="outputAttenuation" status="attenrow">
           <td align="center" style="background: <s:property value="%{bgcolor}"/>" onclick="changeOutputAttn(this);"><s:property value="%{name}"/></td>
           </s:iterator>
       </tr>

       <s:iterator value="matrix" status="rowsStatus" var="row">
       <tr>
           <td align="center" style="background: #005566; white-space:nowrap">
               <a class="tooltip" href="#" style="cursor:default"><s:property value="%{#row[0].label}"/><span class="info"><s:property value="%{#row[0].description}"/></span></a>
           </td>

           <s:iterator value="#row" status="colStatus">
           <s:if test="%{#colStatus.index==0}">
           </s:if>
           <s:else>
                <s:if test="%{name==1}">
                     <td align="center" style='background: green; cursor: default;' onclick="changeAttn(this);">ON</td>
                </s:if>
                <s:else>
                     <td align="center" style='background: #C0C0C0; cursor: pointer;' onclick="changeAttn(this);">OFF</td>
                </s:else>
           </s:else>
           </s:iterator>
       </tr>
       </s:iterator>
   </table>
   <div id="progressbar"><img src="images/progress_bar.gif"></div>
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
                   <input id="switch_on" type="button" class="button" value="ON" onclick="on_switching(1, '1');">
                   <input id="switch_off" type="button" class="button" style="display: none" value="OFF" onclick="on_switching(1, '0');">
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
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs1" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs1" name="input" size="6"></s:textfield></td>
                <td align="center"><s:textfield readonly="true" id="attenuation1" name="value" size="6" ></s:textfield></td>
            </tr>
            <tr><td><img src="images/spacer.gif" width="1" height="10"/></td></tr>
            <tr>
                <td colspan="3" align="right">
                    <input id="switch_on" type="button" class="button" value="ON" onclick="on_switching(2, '1');">
                </td>
            </tr>
        </table>
    </div>
    </div>

    <div class="blockbkg" id="bkg_atten" style="visibility: hidden;">
    <div class="cont" id="dlg_atten" style="visibility: hidden;">
        <table >
            <tr>
                <td width="20%"></td>
                <td width="60%" align="center" nowrap><strong>Set Outputs Attenuation</strong></td>
                <td width="20%" align="right"><div class="closebtn" title="Close" id="closebtn_atten"></div></td>
            </tr>
        </table>

        <table>
            <thead>
            <tr>
               <th><div id="outputs_label2"></div><img src="images/spacer.gif" width="5" height="1"></th>
               <th><div id="inputs_label2"></div><img src="images/spacer.gif" width="5" height="1"></th>
               <th nowrap>Attenuation(db)</th></tr>
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs2" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs2" name="input" size="6" readonly="true" value="0"></s:textfield></td>
                <td align="center"><s:textfield id="attenuation2" name="value" size="6" ></s:textfield></td>
            </tr>
            <tr>
                <td colspan="3" align="center">
                    <fieldset>
                        <legend>Quick Pick</legend>
                        <table>
                        <tr>
                            <td><input type="button" class="button" value="0" onclick="set_atten(2, '0');"></td>
                            <td><input type="button" class="button" value="3" onclick="set_atten(2, '3');"></td>
                            <td><input type="button" class="button" value="6" onclick="set_atten(2, '6');"></td>
                            <td><input type="button" class="button" value="9" onclick="set_atten(2, '9');"></td>
                            <td><input type="button" class="button" value="12" onclick="set_atten(2, '12');"></td>
                        </tr>
                        <tr>
                            <td><input type="button" class="button" value="15" onclick="set_atten(2, '15');"></td>
                            <td><input type="button" class="button" value="18" onclick="set_atten(2, '18');"></td>
                            <td><input type="button" class="button" value="21" onclick="set_atten(2, '21');"></td>
                            <td><input type="button" class="button" value="24" onclick="set_atten(2, '24');"></td>
                            <td><input type="button" class="button" value="31" onclick="set_atten(2, '31');"></td>
                        </tr>
                        </table>
                    </fieldset>
                </td>
            </tr>
            <tr><td colspan="3"><div id="slider2"></div></td></tr>
            <tr>
                <td align="left" valign="top">0</td>
                <td colspan="2" align="center">
                    <input type="button" class="button" onclick="decrment_and_send(1);" value="1 <<">
                    <img src="images/spacer.gif" height="1" width="3">
                    <input type="button" class="button" onclick="increment_and_send(1);" value=">> 1">
                </td>
                <td align="right" valign="top">31</td>
            </tr>
            <tr style="visibility:hidden" id="attenuation_field1"><td colspan="3" align="center"><input type="button" name="setatten" value="Set Attenuation" onclick="set_attenuation_btn(2);"/></td></tr>
        </table>
    </div>
    </div>

    <div class="block_tr_bkg" id="tr_bkg" style="visibility: hidden;">
    <div class="cont" id="tr_dlg" style="visibility: hidden;">
    <table>
        <tr>
            <td><img src="spacer.gif" width="1" height="1"/></td>
            <td align="center"><strong>Tier Roam for Output Attenuator</strong></td>
            <td align="right"><div class="closebtn" title="Close" id="tier_roam_closebtn"></div></td></tr>
        <tr>
            <td colspan="3">
                <table width="100%">
                    <thead><tr><th>Output</th><th>Input</th></tr><thead>
                    <tr>
                       <td align="center"><input type="text" size="12" id="tierroam_out" value=""></td>
                       <td align="center"><input type="text" size="12" id="tierroam_in" readonly="true" value="0"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <table>
                    <tr>
                        <td align="right" nowrap>Step (dB)</td>
                        <td align="left"><input type="text" id="tierroam_step" value="" size="6"></td>
                        <td align="right" nowrap>Start ATTN (dB)</td>
                        <td align="left"><input type="text" id="tierroam_start" value="" size="6"></td>
                    </tr>
                     <tr>
                        <td align="right" nowrap>Speed (sec.)</td>
                        <td align="left"><input type="text" id="tierroam_speed" value="" size="6"></td>
                        <td align="right" nowrap>End ATTN (dB)</td>
                        <td align="left"><input type="text" id="tierroam_end" value="" size="6"></td>
                    </tr>
                    <tr>
                        <td align="right" colspan="2" nowrap>Min/Max Pause(Sec.)</td>
                        <td align="left" colspan="2" valign="top"><input type="text" id="mm_pause" value="0" size="6"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td colspan="3"><img src="images/spacer.gif" width="1" height="10"></td></tr>
        <tr>
            <td align="center" colspan="3">
               <input type="button" value="Start" onClick="tier_roam('start');">
               <img src="images/spacer.gif" width="10" height="1">
               <input type="button" value="Stop" onClick="tier_roam('stop');">
            </td>
        </tr>
    </table>
    </div>
    </div>

    <div id="dialog_alert" title="Connection Recovered" style="display:none;">
        <p>Application Server Connection is Recovered. Please Login Again!</p>
    </div>
    <div id="debug"></div>
</s:form>
