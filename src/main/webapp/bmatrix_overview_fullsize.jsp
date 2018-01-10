<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%@ include file="matrix_fullview_style.jsp" %>

<SCRIPT language="javascript">
function changeAttn(e) {
    var t = document.getElementById("matrix_view").rows[0].cells[e.cellIndex],
        n = document.getElementById("matrix_view").rows[e.parentNode.rowIndex].cells[0],
        i = t.childNodes[1];

    var currentVal = e.childNodes[0].nodeValue;
    if ( "ON" == currentVal ) {
        document.getElementById("id_commit").value = " Turn OFF Switch ";
    } else {
        document.getElementById("id_commit").value = " Turn ON Switch ";
    }
    headerText = i.childNodes[0].nodeValue,
    i = n.childNodes[1],
    labelText = i.childNodes[0].nodeValue;

    var d = e.parentNode.rowIndex,
        l = e.cellIndex;

    var changedValue;
    if ( "ON" == currentVal ) {
        changedValue = "OFF";
    } else {
        changedValue = "ON";
    };

    if (0 != l) {
        document.getElementById("attenuation").value = changedValue,
        "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "",
        $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "",
        $("#dlg").hide()), $("#bkg").fadeIn(500, "linear", function() {
            isSetAttenuationActive = !0, $("#dlg").show(500, "swing"), $("#dlg").draggable();
            $("#dlg").zIndex(1)
        }),

        document.getElementById("id_inputs").value = d - 1,
        document.getElementById("id_outputs").value = l,
        document.getElementById("id_inputs").disabled = !0,
        document.getElementById("id_outputs").disabled = !0,
        document.getElementById("inputs_label").innerHTML = labelText,
        document.getElementById("outputs_label").innerHTML = headerText
    }
}

function changeOutputAttn(e) {
    var t = document.getElementById("matrix_view").rows[0].cells[e.cellIndex],
        n = document.getElementById("matrix_view").rows[e.parentNode.rowIndex].cells[0],
        i = t.childNodes[1];

    headerText = i.childNodes[0].nodeValue, labelText = i.childNodes[0].nodeValue;

    var d = e.parentNode.rowIndex,
    a = e.cellIndex;

    $("#slider1").slider("value", e.innerHTML),
    $("#attenuation1").val(e.innerHTML.replace(/dB/i, ""))
    "hidden" == document.getElementById("bkg1").style.visibility && (document.getElementById("bkg1").style.visibility = "",
    $("#bkg1").hide()),
    "hidden" == document.getElementById("dlg1").style.visibility && (document.getElementById("dlg1").style.visibility = "",
    $("#dlg1").hide()),
    $("#bkg1").fadeIn(500, "linear", function() {
         isSetAttenuationActive = !0,
         $("#dlg1").show(500, "swing"),
         $("#dlg1").draggable();
         $("#dlg1").zIndex(1)
    }),

    document.getElementById("id_inputs1").value = d - 1,
    document.getElementById("id_outputs1").value = a,
    document.getElementById("id_inputs1").disabled = !0,
    document.getElementById("id_outputs1").disabled = !0,
    document.getElementById("inputs_label1").innerHTML = 'Input',
    document.getElementById("outputs_label1").innerHTML = headerText
}

function turn_on_switch (e) {
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

function set_atten(e, t) {
    if (1 == e) {
        document.getElementById("attenuation").value = t;
        var n = document.getElementById("id_inputs").value,
            i = document.getElementById("id_outputs").value;
        $("#slider").slider("value", t), set_attenuation(n, i, t)
    } else if (2 == e) {
        document.getElementById("attenuation1").value = t;
        var n = document.getElementById("id_inputs1").value,
            i = document.getElementById("id_outputs1").value;
        $("#slider1").slider("value", t), set_attenuation(n, i, t)
    }
}

function increment_and_send(e) {
    if (!in_progress1)
        if (in_progress1 = !0, setTimeout(function() {
                in_progress1 = !1
            }, 300), 1 == e) {
            var t = document.getElementById("id_inputs").value,
                n = document.getElementById("id_outputs").value,
                i = document.getElementById("attenuation1").value;
            if (13 == i) return void alert("The new value exceeds the limit. Maximum value is 63");
            var d = ++i;
            document.getElementById("attenuation").value = d, $("#slider").slider("value", d), set_attenuation(t, n, d)
        } else if (2 == e) {
        var t = document.getElementById("id_inputs1").value,
            n = document.getElementById("id_outputs1").value,
            i = document.getElementById("attenuation1").value;
        if (31 == i) return void alert("The new value exceeds the limit. Maximum value is 63");
        var d = ++i;
        document.getElementById("attenuation1").value = d, $("#slider1").slider("value", d), set_attenuation(t, n, d)
    }
}

function decrment_and_send(e) {
    if (!in_progress2)
        if (in_progress2 = !0, setTimeout(function() {
                in_progress2 = !1
            }, 300), 1 == e) {
            var t = document.getElementById("id_inputs").value,
                n = document.getElementById("id_outputs").value,
                i = document.getElementById("attenuation").value;
            if (0 == i) return void alert("The value cannot be decremented as the current value already reached the low boundary!");
            var d = --i;
            document.getElementById("attenuation").value = d, $("#slider").slider("value", d), set_attenuation(t, n, d)
        } else if (2 == e) {
        var t = document.getElementById("id_inputs1").value,
            n = document.getElementById("id_outputs1").value,
            i = document.getElementById("attenuation1").value;
        if (0 == i) return void alert("The value cannot be decremented as the current value already reached the low boundary!");
        var d = --i;
        document.getElementById("attenuation1").value = d, $("#slider1").slider("value", d), set_attenuation(t, n, d)
    }
}

function set_attenuation(e, t, n) {
    if (isBlank(e) || isBlank(t)) return void alert("Input is invalid. Row and column cannot not empty!");
    var i = /^[0-9,]*$/.test(e);
    if (!i) return void alert("Input is invalid. The inputs must be digits separated by comma.");
    if (i = /^[0-9,]*$/.test(t), !i) return void alert("Output is invalid. The outputs must be digits separated by comma.");
    for (var d = document.getElementById("matrix_view").rows.length - 1, l = document.getElementById("matrix_view").rows[0].cells.length - 1, a = e.split(","), u = t.split(","), o = 0; o < a.length; o++)
        if (a[o] > d) return void alert("Input is invalid. Maximum row number is " + d);
    for (var o = 0; o < u.length; o++)
        if (u[o] > l) return void alert("Output is invalid. Maximum column number is " + l);
    var m = "set_attenuation";
    0 > n ? n = 0 : n > 31 && (n = 31);
    var r;
    r = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"), r.onreadystatechange = function() {
        if (4 == r.readyState && 200 == r.status) {
            var e = r.responseXML.documentElement.getElementsByTagName("tr");
            updateMatrix(e, !1)
        }
    };
    var c = document.getElementById("hardware_name").innerHTML;
    r.open("POST", "/rfmaze/mazeAdminServlet?command=" + m + "&outputs=" + t + "&inputs=" + e + "&value=" + n + "&hardware=" + c, !0),
    r.send(null)
}

function updateMatrix(e, t) {
    var e, n, i, d, a, l = document.getElementById("matrix_view");
    var tds = e[0].getElementsByTagName("td");
    for (i = 0; i < tds.length; i++) {
        d = tds[i].getElementsByTagName("v");
        c = tds[i].getElementsByTagName("c");
        l.rows[1].cells[i+1].innerHTML = d[0].firstChild.nodeValue + " dB";
        l.rows[1].cells[i+1].style.backgroundColor = c[0].firstChild.nodeValue;
    }

    for (n = 1; n < e.length; n++) {
        tds = e[n].getElementsByTagName("td");
        for (i = 1; i < tds.length; i++) {
            d = tds[i].getElementsByTagName("v");
            var new_val= (d[0].firstChild.nodeValue=='1')? 'ON' :'OFF';
            new_color = (d[0].firstChild.nodeValue=='1')? 'green' :'#C0C0C0';
            l.rows[n + 1].cells[i].innerHTML = new_val;
            l.rows[n + 1].cells[i].style.backgroundColor = new_color;
        }
    }
}

function updateMatrixOffset(e) {
    for (var t, n, i = document.getElementById("matrix_view"), d = i.rows.length, l = 0; l < e.length; l++) try {
        t = e[l].getElementsByTagName("td"), n = t[0].getElementsByTagName("v"), d - 2 > l && (i.rows[l + 2].cells[1].innerHTML = n[0].firstChild.nodeValue)
    } catch (a) {
        alert(a)
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
            updateMatrix(t, !0)
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
            var i = e.responseXML.documentElement.getElementsByTagName("tr");
            updateMatrixOffset(i)
        }
    };
    var t = document.getElementById("hardware_name").innerHTML;
    e.open("POST", "/rfmaze/mazeAdminServlet?command=isconnected&hardware=" + t, !0), e.send()
}
var isSetAttenuationActive = !1,
    headerText = "",
    labelText = "",
    in_progress1 = !1,
    in_progress2 = !1;

$(document).ready(function() {
    $("#closebtn").click(function() {
        isSetAttenuationActive = !1, $("#dlg").hide("500", "swing", function() {
            $("#bkg").fadeOut("300")
        })
    }), $("#closebtn1").click(function() {
        $("#dlg1").hide("500", "swing", function() {
            $("#bkg1").fadeOut("300")
        })
    }), $("#setattenuation").click(function() {
        manual_input = !0, "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "", $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "", $("#dlg").hide()), $("#bkg").fadeIn(300, "linear", function() {
            $("#dlg").show(500, "swing")
        }), document.getElementById("inputs_label").innerHTML = "Inputs", document.getElementById("outputs_label").innerHTML = "Outputs", document.getElementById("id_inputs").value = "", document.getElementById("id_outputs").value = "", document.getElementById("id_inputs").disabled = !1, document.getElementById("id_outputs").disabled = !1, document.getElementById("mimo").disabled = !1
    }), $("#dlg").draggable(), $("#closebtn2").click(function() {
        $("#dlg2").hide("500", "swing", function() {
            $("#bkg2").fadeOut("300")
        })
    }), $("#handoff").click(function() {
        "hidden" == document.getElementById("bkg2").style.visibility && (document.getElementById("bkg2").style.visibility = "", $("#bkg2").hide()), "hidden" == document.getElementById("dlg2").style.visibility && (document.getElementById("dlg2").style.visibility = "", $("#dlg2").hide()), $("#bkg2").fadeIn(300, "linear", function() {
            $("#dlg2").show(500, "swing"), $("#dlg2").draggable()
        })
    }), $("#closebtn_cp").click(function() {
        $("#dlg_cp").hide("500", "swing", function() {
            $("#bkg_cp").fadeOut("300")
        })
    }), heartbeat(), refreshInterval = 5e3, startRefresh()
}), $(function() {
    $("#slider").slider({
        value: 15,
        min: 0,
        max: 31,
        step: 1,
        slide: function(e, t) {
            $("#attenuation").val(t.value)
        }
    }), $("#attenuation").val($("#slider").slider("value")), $("#slider").mouseup(function() {
        $(this).after(function() {
            var e = document.getElementById("attenuation").value,
                t = document.getElementById("id_inputs").value,
                n = document.getElementById("id_outputs").value;
            set_attenuation(t, n, e)
        })
    }), $("#slider1").slider({
        value: 32,
        min: 0,
        max: 63,
        step: 1,
        slide: function(e, t) {
            $("#attenuation1").val(t.value)
        }
    }), $("#attenuation1").val($("#slider1").slider("value")), $("#slider1").mouseup(function() {
        $(this).after(function() {
            var e = document.getElementById("attenuation1").value,
                t = document.getElementById("id_inputs1").value,
                n = document.getElementById("id_outputs1").value;
            set_attenuation(t, n, e)
        })
    })
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
   <div><img src="images/spacer.gif" width="1" height="10"/></div>
   <div id="block_container" style="color:#EEEEEE;font-weight:bold;height:30;display:table-cell;vertical-align:bottom;text-align:center;border:0px">
       <div id="hardware_name"><s:property value="%{hardware}"/>&nbsp;</div>
       <div id="connection_state"></div>
   </div>

   <table class="ConfigDataTable" align="center" width="30%">
      <thead>
         <tr><th align="center">Select Matrix</th></tr>
      </thead>
      <tbody>
         <tr><td align="center"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
         <tr><td align="center"><s:select onchange="viewmatrix();" label="Matrix Hardware" headerKey="-1" headerValue="-- select matrix --" list="hardwares" name="hardware"/></td></tr>
         <tr><td align="center"><img src="images/spacer.gif" width="1" height="5"/></td></tr>
      </tbody>
   </table>

    <table class="matrix_fullsize" id="matrix_view" cellpadding="0" cellspacing="0">
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
            <td align="center" style="background: #005566; white-space:nowrap"></td>
            <s:iterator value="outputAttenuation" status="attenrow">
            <td align="center" style='background: <s:property value="%{bgcolor}"/>' onclick="changeOutputAttn(this);"><s:property value="%{name}"/></td>
            </s:iterator>
        </tr>

        <s:iterator value="matrix" status="rowsStatus" var="row">
        <tr>
            <td align="center" style="width:120px; max-width:120px; background: #8ca9cf; white-space:nowrap">
                <div title='<s:property value="%{#row[0].description}"/>'><s:property value="%{#row[0].label}"/></div>
            </td>
            <s:iterator value="#row" status="colStatus">
            <s:if test="%{name==1}">
            <td align="center" style="background: green; cursor: pointer;" onclick="changeAttn(this);">ON</td>
            </s:if>
            <s:else>
            <td align="center" style="background: #C0C0C0; cursor: pointer;" onclick="changeAttn(this);">OFF</td>
            </s:else>
            </s:iterator>
        </tr>
        </s:iterator>
    </table>
    <div id="progressbar"><img src="images/progressbar.gif"></div>
   </s:else>

   <s:hidden name="action" value=""/>
   <s:textfield style="visibility: hidden;" id="connected_server_name" name="server"></s:textfield>

   <div class="blockbkg" id="bkg" style="visibility: hidden;">
   <div class="cont" id="dlg" style="visibility: hidden; height:150px;">
       <table >
           <tr>
               <td><img src="images/spacer.gif" width="100" height="1"></td>
               <td><img src="images/spacer.gif" width="80" height="1"></td>
               <td><img src="images/spacer.gif" width="100" height="1"></td>
               <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
           </tr>
       </table>

       <table>
            <thead>
            <tr>
              <th><div id="outputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th>
              <th>&nbsp;</th>
              <th><div id="inputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th>
            </thead>
            </tr>
            <tr>
                <td align="center"><s:textfield id="id_outputs" name="output" size="6"></s:textfield></td>
                <td align="center" style="visibility: hidden"><s:textfield id="attenuation" name="value" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs" name="input" size="6"></s:textfield></td>
            </tr>
            <tr>
                <td colspan="3" align="center"><img src="images/spacer.gif" width="1" height="5"></td>
            </tr>
            <tr><td colspan="3"><img src="images/spacer.gif" width="1" height="5"></td></tr>
            <tr>
                <td colspan="3" align="center">
                    <input type="button" class="button" style="height:26px; width:150px" name="setatten" id="id_commit" value="Commit" onclick="turn_on_switch (1);"/>
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
                <td align="right"><strong>Set Attenuation</strong></td>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><div class="closebtn" title="Close" id="closebtn1"></div></td>
            </tr>
          </table>

        <table cellspacing="3" cellpadding="0">
            <thead>
            <tr><th><div id="outputs_label1"></div><img src="images/spacer.gif" width="5" height="1"></th><th><div id="inputs_label1"></div><img src="images/spacer.gif" width="5" height="1"></th><th nowrap>Attenuation(db)</th>&nbsp;</th></tr>
            </thead>
            <tr>
                <td align="center"><s:textfield id="id_outputs1" name="output" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="id_inputs1" name="input" size="6"></s:textfield></td>
                <td align="center"><s:textfield id="attenuation1" name="value" size="6" ></s:textfield></td>
                <td align="center">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="4" align="center">
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
            <tr><td colspan="4"><div id="slider1"></div></td></tr>
            <tr>
                <td align="left" valign="top">0</td>
                <td colspan="2" align="center">
                    <input type="button" class="button" onclick="decrment_and_send(2);" value="1 <<"><img src="images/spacer.gif" height="1" width="3"><input type="button" class="button" onclick="increment_and_send(2);" value=">> 1">
                </td>
                <td align="right" valign="top">31</td>
            </tr>
            <tr style="visibility:hidden" id="attenuation_field1"><td colspan="4" align="center"><input type="button" name="setatten" value="Set Attenuation" onclick="turn_on_switch (2);"/></td></tr>
        </table>
    </div>
    </div>
</s:form>
