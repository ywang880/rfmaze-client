<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css?version=1.0.0">

<style>
#block_container {
    text-align:center;
}
#connection_state, #hardware_name {
    display:inline;
}
.spinner {
    position: fixed;
    top: 50%;
    left: 50%;
    margin-left: -50px; /* half width of the spinner gif */
    margin-top: -50px; /* half height of the spinner gif */
    text-align:center;
    z-index:1234;
    overflow: auto;
    width: 100px; /* width of the spinner gif */
    height: 102px; /*hight of the spinner gif +2px to fix IE8 issue */
}
</style>

<SCRIPT type="text/javascript" language="javascript" src="js/rfmaze.js?001"></SCRIPT>
<SCRIPT type="text/javascript" src="js/jscolor.js"></SCRIPT>
<SCRIPT language="javascript">
function viewmatrix() {
    if (document.getElementById('rfmaze_hardware').selectedIndex == 0) {
        alert("Invalid selection!");
        return;
    }
    stopRefresh();
    document.getElementById('rfmaze').submit();
}

function onInit() {
    $("#spinner").bind("ajaxSend", function() {
        $(this).show();
    }).bind("ajaxStop", function() {
        $(this).hide();
    }).bind("ajaxError", function() {
        $(this).hide();
    }); 
    ping();
}

function ping() {
    pingConnection();
    setTimeout(function() {
        ping()
    }, 5000);
}

var processid="PID";
function pingConnection() {
    var jqxhr = $.get( "/rfmaze/mazeServlet?command=hello", function(responseData) {
        document.getElementById('connection_state').innerHTML= "<img src='images/connected.png'>";
        $('#spinner').hide();
        if (processid=="PID") {
            processid=responseData;
        } else if (processid!=responseData) {
            $("#dialog_alert").dialog();
        }
    }).fail(function() {
        document.getElementById('connection_state').innerHTML= "<img src='images/disconnected.png'>";
        $('#spinner').show();
    });
}
</SCRIPT>
<SCRIPT language="javascript">
function changeAttn(a) {
    var b = document.getElementById("matrix_view").rows[0].cells[a.cellIndex], c = document.getElementById("matrix_view").rows[a.parentNode.rowIndex].cells[0], d = b.childNodes[1];
    headerText = d.childNodes[0].nodeValue, d = c.childNodes[1], labelText = d.childNodes[0].nodeValue;
    var e = a.parentNode.rowIndex, f = a.cellIndex - 1;
    if (0 != f) {
        if (isSetAttenuationActive) return void changeAttn2(a);
        document.getElementById("attenuation").value = a.innerHTML, $("#slider").slider("value", a.innerHTML), 
        "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "", 
        $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "", 
        $("#dlg").hide()), $("#bkg").fadeIn(500, "linear", function() {
            isSetAttenuationActive = !0, $("#dlg").css({top:300, left:300, position:'absolute'}).show(500, "swing"), $("#dlg").draggable();
        }), document.getElementById("id_inputs").value = e, document.getElementById("id_outputs").value = f, 
        document.getElementById("id_inputs").disabled = !0, document.getElementById("id_outputs").disabled = !0, 
        document.getElementById("mimo").disabled = !0, document.getElementById("inputs_label").innerHTML = labelText, 
        document.getElementById("outputs_label").innerHTML = headerText, $("#mimo").attr("checked", !1), 
        $("#mimo1").attr("checked", !1);
    }
}

function changeAttn2(a) {
    var b = a.parentNode.rowIndex, c = a.cellIndex - 1;
    0 != c && (document.getElementById("attenuation1").value = a.innerHTML, $("#slider1").slider("value", a.innerHTML), 
    "hidden" == document.getElementById("bkg1").style.visibility && (document.getElementById("bkg1").style.visibility = "", 
    $("#bkg1").hide()), "hidden" == document.getElementById("dlg1").style.visibility && (document.getElementById("dlg1").style.visibility = "", 
    $("#dlg1").hide()), $("#bkg1").fadeIn(500, "linear", function() {
        $("#dlg1").css({top:320, left:350, position:'absolute'}).show(500, "swing"), $("#dlg1").draggable();
    }), document.getElementById("id_inputs1").value = b, document.getElementById("id_outputs1").value = c, 
    document.getElementById("id_inputs1").disabled = !0, document.getElementById("id_outputs1").disabled = !0, 
    document.getElementById("mimo1").disabled = !0, document.getElementById("inputs_label1").innerHTML = labelText, 
    document.getElementById("outputs_label1").innerHTML = headerText, $("#mimo").attr("checked", !1), 
    $("#mimo1").attr("checked", !1));
}

function set_attenuation_btn(a) {
    if (1 == a) {
        var b = document.getElementById("attenuation").value, c = document.getElementById("id_inputs").value, d = document.getElementById("id_outputs").value;
        $("#slider").slider("value", b), set_attenuation(c, d, b);
    } else if (2 == a) {
        var b = document.getElementById("attenuation1").value, c = document.getElementById("id_inputs1").value, d = document.getElementById("id_outputs1").value;
        $("#slider1").slider("value", b), set_attenuation(c, d, b);
    }
}

function set_atten(a, b) {
    if (1 == a) {
        document.getElementById("attenuation").value = b;
        var c = document.getElementById("id_inputs").value, d = document.getElementById("id_outputs").value;
        $("#slider").slider("value", b), set_attenuation(c, d, b);
    } else if (2 == a) {
        document.getElementById("attenuation1").value = b;
        var c = document.getElementById("id_inputs1").value, d = document.getElementById("id_outputs1").value;
        $("#slider1").slider("value", b), set_attenuation(c, d, b);
    }
}

function increment_and_send(a) {
    if (!in_progress1) if (in_progress1 = !0, setTimeout(function() {
        in_progress1 = !1;
    }, 300), 1 == a) {
        var b = document.getElementById("id_inputs").value, c = document.getElementById("id_outputs").value, d = document.getElementById("attenuation").value;
        if (63 == d) return void alert("The new value exceeds the limit. Maximum value is 63");
        var e = ++d;
        document.getElementById("attenuation").value = e, $("#slider").slider("value", e), 
        set_attenuation(b, c, e);
    } else if (2 == a) {
        var b = document.getElementById("id_inputs1").value, c = document.getElementById("id_outputs1").value, d = document.getElementById("attenuation1").value;
        if (63 == d) return void alert("The new value exceeds the limit. Maximum value is 63");
        var e = ++d;
        document.getElementById("attenuation1").value = e, $("#slider1").slider("value", e), 
        set_attenuation(b, c, e);
    }
}

function decrment_and_send(a) {
    if (!in_progress2) if (in_progress2 = !0, setTimeout(function() {
        in_progress2 = !1;
    }, 300), 1 == a) {
        var b = document.getElementById("id_inputs").value, c = document.getElementById("id_outputs").value, d = document.getElementById("attenuation").value;
        if (0 == d) return void alert("The value cannot be decremented as the current value already reached the low boundary!");
        var e = --d;
        document.getElementById("attenuation").value = e, $("#slider").slider("value", e), 
        set_attenuation(b, c, e);
    } else if (2 == a) {
        var b = document.getElementById("id_inputs1").value, c = document.getElementById("id_outputs1").value, d = document.getElementById("attenuation1").value;
        if (0 == d) return void alert("The value cannot be decremented as the current value already reached the low boundary!");
        var e = --d;
        document.getElementById("attenuation1").value = e, $("#slider1").slider("value", e), 
        set_attenuation(b, c, e);
    }
}

function set_attenuation(a, b, c) {
    if (isBlank(a) || isBlank(b)) return void alert("Input is invalid. Row and column cannot not empty!");
    var d = /^[0-9,]*$/.test(a);
    if (!d) return void alert("Input is invalid. The inputs must be digits separated by comma.");
    if (d = /^[0-9,]*$/.test(b), !d) return void alert("Output is invalid. The outputs must be digits separated by comma.");
    for (var e = document.getElementById("matrix_view").rows.length - 1, f = document.getElementById("matrix_view").rows[0].cells.length - 2, g = a.split(","), h = b.split(","), i = 0; i < g.length; i++) if (g[i] > e) return void alert("Input is invalid. Maximum row number is " + e);
    for (var i = 0; i < h.length; i++) if (h[i] > f) return void alert("Output is invalid. Maximum column number is " + f);
    var j = document.getElementById("mimo").checked, k = "set_attenuation";
    j && (k = "set_mimo"), 0 > c ? c = 0 : c > 63 && (c = 63);
    var l;
    l = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP"), 
    l.cache = false;
    l.onreadystatechange = function() {
        if (4 == l.readyState && 200 == l.status) var a = l.responseXML.documentElement.getElementsByTagName("tr");
    }, l.open("POST", "/rfmaze/mazeServlet?command=" + k + "&outputs=" + b + "&inputs=" + a + "&value=" + c, !0), 
    l.send(null);
}

function updateMatrix(a, b) {
    var a, c, d, e, f, g = document.getElementById("matrix_view");
    for (c = 0; c < a.length; c++) try {
        for (tds = a[c].getElementsByTagName("td"), d = 0; d < tds.length; d++) (0 != d || b) && (e = tds[d].getElementsByTagName("v"), 
        f = tds[d].getElementsByTagName("c"), g.rows[c + 1].cells[d + 1].innerHTML = e[0].firstChild.nodeValue, 
        g.rows[c + 1].cells[d + 1].style.backgroundColor = f[0].firstChild.nodeValue);
    } catch (h) {
        console.log("Failed to update matrix");
    }
}

function updateMatrixOffset(a) {
    for (var b, c, d = document.getElementById("matrix_view"), e = d.rows.length, f = 0; f < a.length; f++) try {
        b = a[f].getElementsByTagName("td"), c = b[0].getElementsByTagName("v"), e > f + 1 && (d.rows[f + 1].cells[1].innerHTML = c[0].firstChild.nodeValue);
    } catch (g) {
        console.log("Failed to update matrix offset");
    }
}

function hand_off_start(cmd_string) {
    var a = document.getElementById("mimo_sele").selectedIndex, b = document.getElementById("mimo_sele").options[a].value, c = document.getElementById("handoff_out1").value;
    if ( !isInteger(c) ) return void alert("Output must be an integer!");
    var d = document.getElementById("handoff_out2").value;
    if ("NO" != b && !isInteger(d)) return void alert("Output must be an integer!");
    
    var e = document.getElementById("handoff_out3").value;
    var f = document.getElementById("handoff_out4").value;
    if ("4X4" == b && (!isInteger(e) || !isInteger(f))) return void alert("Output must be an integer!");
    
    var g = document.getElementById("handoff_in1").value;
    if (isBlank(g)) return void alert("Input is invalid!");
    
    var h = document.getElementById("handoff_in2").value;
    if ("NO" != b && isBlank(h)) return void alert("Input is invalid!");
    
    var i = document.getElementById("handoff_in3").value;    
    var j = document.getElementById("handoff_in4").value;
    if ("4X4" == b && ( isBlank(i) || isBlank(j) )) return void alert("Input is invalid!");
    
    var k = document.getElementById("handoff_step").value, l = document.getElementById("handoff_start").value, m = document.getElementById("handoff_speed").value, n = document.getElementById("handoff_target").value;
    if (isBlank(k) || isBlank(l) || isBlank(m) || isBlank(n)) return void alert("Input is invalid!");
    var o = document.getElementById("mm_pause").value, p = 0;
    var q = "command="+cmd_string+"&mimo=" + b;
    "NO" == b ? q = q + "&handoff_out1=" + c + "&handoff_in1=" + g : "2X2" == b ? q = q + "&handoff_out1=" + c + "&handoff_out2=" + d + "&handoff_in1=" + g + "&handoff_in2=" + h : "4X4" == b && (q = q + "&handoff_out1=" + c + "&handoff_out2=" + d + "&handoff_out3=" + e + "&handoff_out4=" + f + "&handoff_in1=" + g + "&handoff_in2=" + h + "&handoff_in3=" + i + "&handoff_in4=" + j), 
    q = q + "&handoff_step=" + k + "&handoff_start=" + l + "&handoff_speed=" + m + "&handoff_target=" + n + "&mm_pause=" + o + "&target_fadein=" + p, 
    
    stopRefresh();
    sendStartHandoverCommand(q);
    refreshInterval=800;
    startRefresh();
}

function hand_off_stop(cmd_string) {
    var a = document.getElementById("mimo_sele").selectedIndex, b = document.getElementById("mimo_sele").options[a].value;
    var c = document.getElementById("handoff_out1").value;
    if (isBlank(c)) return void alert("Input is invalid!");
    var d = document.getElementById("handoff_out2").value;
    if ("NO" != b && isBlank(d)) return void alert("Input is invalid!");
    var e = document.getElementById("handoff_out3").value;
    if ("4X4" == b && isBlank(e)) return void alert("Input is invalid!");
    var f = document.getElementById("handoff_out4").value;
    if ("4X4" == b && isBlank(f)) return void alert("Input is invalid!");
    var g = "command="+cmd_string+"&mimo=" + b + "&handoff_out1=" + c + "&handoff_out2=" + d + "&handoff_out3=" + e + "&handoff_out4=" + f;
    
    stopRefresh();
    sendStopHandoverCommand(g);
    refreshInterval=2000;
    startRefresh();
}

function mimo_selected(a) {
    var b = a.selectedIndex, c = a.options[b].value;
    "NO" == c ? ($("#id_row_2").hide(), $("#id_row_3").hide(), $("#id_row_4").hide()) : "2X2" == c ? ($("#id_row_2").show(), 
    $("#id_row_3").hide(), $("#id_row_4").hide()) : "4X4" == c && ($("#id_row_2").show(), 
    $("#id_row_3").show(), $("#id_row_4").show(), $(".cont").css("height", "250px"));
}

function timedRefresh() {
    sendRefreshCommand();
    timerId = setTimeout(function() {
        timedRefresh();
    }, refreshInterval);
}

function startRefresh() {
    timer_is_on || (timer_is_on = 1, timedRefresh());
}

function stopRefresh() {
    clearTimeout(timerId);
    timer_is_on=0;
}

function sendStartHandoverCommand(a) {
    var b;
    b = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP"), 
    b.onreadystatechange = function() {
        4 == b.readyState && 200 == b.status && (document.getElementById("server_response").innerHTML = b.responseText);
    }, b.open("POST", "/rfmaze/mazeServlet?" + a, !0), b.send(null);
}

function sendStopHandoverCommand(a) {
    var b;
    b = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP"), 
    b.onreadystatechange = function() {
        4 == b.readyState && 200 == b.status && (document.getElementById("server_response").innerHTML = b.responseText);
    }, b.open("POST", "/rfmaze/mazeServlet?" + a, !0), b.send(null);
}

function change_scheme() {
    document.getElementById("rfmaze_action").value = "color_scheme", document.getElementById("rfmaze").submit();
}

function sendRefreshCommand() {
    var a;
    a = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    var b = document.getElementById("matrix_view"), c = b.rows;
    c.length;
    a.onreadystatechange = function() {
        if (4 == a.readyState && 200 == a.status) {
            var b = a.responseXML.documentElement.getElementsByTagName("tr");
            if (b.length > 0) {            
                updateMatrix(b, !0);
            }
        }
    }, a.open("POST", "/rfmaze/mazeServlet?command=refresh", !0), a.send();
}

function heartbeat() {
    checkConnection(), setTimeout(function() {
        heartbeat();
    }, 5000);
}

function checkConnection() {
    var a;
    a = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP"), 
    a.onreadystatechange = function() {
        if (4 == a.readyState && 200 == a.status) {
            var b = a.responseXML.documentElement.getElementsByTagName("server_state"), c = b[0].firstChild.nodeValue;
            if ("reload" == c) return void (window.location = "rfmaze.action");
            var d = a.responseXML.documentElement.getElementsByTagName("state"), e = d[0].firstChild.nodeValue;
            document.getElementById("connection_state").innerHTML = '<img src="images/' + e + '">', 
            -1 == e.indexOf("disconnected") && $("#progressbar").hide();
            var f = a.responseXML.documentElement.getElementsByTagName("tr");
            updateMatrixOffset(f);
        }
    }, a.open("POST", "/rfmaze/mazeServlet?command=isconnected", !0), a.send();
}

var isSetAttenuationActive = !1, headerText = "", labelText = "", in_progress1 = !1, in_progress2 = !1;
var set
$(document).ready(function() {
    $("#closebtn").click(function() {
        isSetAttenuationActive = !1, $("#dlg").hide("500", "swing", function() {
            $("#bkg").fadeOut("300");
        });
    }), $("#closebtn1").click(function() {
        $("#dlg1").hide("500", "swing", function() {
            $("#bkg1").fadeOut("300");
        });
    }), $("#setattenuation").click(function() {        
        if (!isSetAttenuationActive) {
            isSetAttenuationActive = true;
            manual_input = !0, "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "", 
            $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "", 
            $("#dlg").hide()), $("#bkg").fadeIn(300, "linear", function() {
                $("#dlg").show(500, "swing"); $("#dlg").draggable();
            }), document.getElementById("inputs_label").innerHTML = "Inputs", document.getElementById("outputs_label").innerHTML = "Outputs", 
            document.getElementById("id_inputs").value = "", document.getElementById("id_outputs").value = "", 
            document.getElementById("id_inputs").disabled = !1, document.getElementById("id_outputs").disabled = !1, 
            document.getElementById("mimo").disabled = !1;
        } else {
            isSetAttenuationActive = false;
            manual_input = !0, "hidden" == document.getElementById("bkg1").style.visibility && (document.getElementById("bkg1").style.visibility = "", 
            $("#bkg1").hide()), "hidden" == document.getElementById("dlg1").style.visibility && (document.getElementById("dlg1").style.visibility = "", 
            $("#dlg1").hide()), $("#bkg1").fadeIn(300, "linear", function() {
                $("#dlg1").show(500, "swing"); $("#dlg1").draggable();
            }), document.getElementById("inputs_label1").innerHTML = "Inputs", document.getElementById("outputs_label1").innerHTML = "Outputs", 
            document.getElementById("id_inputs1").value = "", document.getElementById("id_outputs1").value = "", 
            document.getElementById("id_inputs1").disabled = !1, document.getElementById("id_outputs1").disabled = !1, 
            document.getElementById("mimo1").disabled = !1;
        }
    }), $("#dlg").draggable(), $("#closebtn2").click(function() {
        $("#dlg2").hide("500", "swing", function() {
            $("#bkg2").fadeOut("300");
        });
    }), $("#handoff").click(function() {
        "hidden" == document.getElementById("bkg2").style.visibility && (document.getElementById("bkg2").style.visibility = "", 
        $("#bkg2").hide()), "hidden" == document.getElementById("dlg2").style.visibility && (document.getElementById("dlg2").style.visibility = "", 
        $("#dlg2").hide()), $("#bkg2").fadeIn(300, "linear", function() {
            $("#dlg2").show(500, "swing"), $("#dlg2").draggable();
        });
    }), $("#tierroam").click(function() {
        "hidden" == document.getElementById("tr_bkg").style.visibility && (document.getElementById("tr_bkg").style.visibility = "", 
        $("#tr_bkg").hide()), "hidden" == document.getElementById("tr_dlg").style.visibility && (document.getElementById("tr_dlg").style.visibility = "", 
        $("#tr_dlg").hide()), $("#tr_bkg").fadeIn(300, "linear", function() {
            $("#tr_dlg").show(500, "swing"), $("#tr_dlg").draggable();
        });
    }),    $("#tier_roam_closebtn").click(function() {
        $("#tr_dlg").hide("500", "swing", function() {
            $("#tr_bkg").fadeOut("300");
        });
    }), $("#closebtn_cp").click(function() {
        $("#dlg_cp").hide("500", "swing", function() {
            $("#bkg_cp").fadeOut("300");
        });
    }), $("#customer_color_picker").click(function() {
        "hidden" == document.getElementById("bkg_cp").style.visibility && (document.getElementById("bkg_cp").style.visibility = "", 
        $("#bkg_cp").hide()), "hidden" == document.getElementById("dlg_cp").style.visibility && (document.getElementById("dlg_cp").style.visibility = "", 
        $("#dlg_cp").hide()), $("#bkg_cp").fadeIn(300, "linear", function() {
            $("#dlg_cp").show(500, "swing"), $("#dlg_cp").draggable();
        });
    }), $("#attenuation").keypress(function() {
        document.getElementById("attenuation_field").style.visibility = "";
    }), $("#attenuation1").keypress(function() {
        document.getElementById("attenuation_field1").style.visibility = "";
    }), 
    onInit(), heartbeat(), startRefresh();
}), $(function() {
    $("#slider").slider({
        value: 32,
        min: 0,
        max: 63,
        step: 1,
        slide: function(a, b) {
            $("#attenuation").val(b.value);
        }
    }), $("#attenuation").val($("#slider").slider("value")), $("#slider").mouseup(function() {
        $(this).after(function() {
            var a = document.getElementById("attenuation").value, b = document.getElementById("id_inputs").value, c = document.getElementById("id_outputs").value;
            set_attenuation(b, c, a);
        });
    }), $("#slider1").slider({
        value: 32,
        min: 0,
        max: 63,
        step: 1,
        slide: function(a, b) {
            $("#attenuation1").val(b.value);
        }
    }), $("#attenuation1").val($("#slider1").slider("value")), $("#slider1").mouseup(function() {
        $(this).after(function() {
            var a = document.getElementById("attenuation1").value, b = document.getElementById("id_inputs1").value, c = document.getElementById("id_outputs1").value;
            set_attenuation(b, c, a);
        });
    });
});
        
function start_tier_roam() {
    var a = document.getElementById("tierroam_out").value;
    if (isBlank(a)) return void alert("Output is invalid!");
    var b = document.getElementById("tierroam_in").value;    
    if (isBlank(b)) return void alert("Input is invalid!");
    var c = document.getElementById("tierroam_step").value;
    if (isBlank(c)) return void alert("step value is invalid!");
    var d = document.getElementById("tierroam_start").value;
    if (isBlank(d)) return void alert("start value is invalid!");
    var e = document.getElementById("tierroam_speed").value; 
    if (isBlank(e)) return void alert("speed value is invalid!");    
    var f = document.getElementById("tierroam_end").value;
    if (isBlank(f)) return void alert("end value is invalid!");    
    var g = document.getElementById("tierroam_mm_pause").value;
    if (isBlank(g)) return void alert("minimum/maximum pause value is invalid!");

    var cmd = "command=tierroam_start&tierroam_out=" + a + "&tierroam_in=" + b + 
            "&tierroam_step=" + c + "&tierroam_start=" + d + "&tierroam_speed=" + e + 
            "&tierroam_end=" + f + "&mm_pause=" + g;
            
    stopRefresh();
    sendTierRoamCommand(cmd);
    refreshInterval=800;
    startRefresh();
}

function stop_tier_roam() {
    var a = document.getElementById("tierroam_out").value;
    if (isBlank(a)) return void alert("Output is invalid!");

    var cmd = "command=tierroam_stop&tierroam_out="+a;
    stopRefresh();
    sendTierRoamCommand(cmd);
    refreshInterval=2000;
    startRefresh();
}

function sendTierRoamCommand(cmd) {
    var b;
    b = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP"), 
    b.onreadystatechange = function() {
        4 == b.readyState && 200 == b.status && (document.getElementById("server_response").innerHTML = b.responseText);
    }, b.open("POST", "/rfmaze/mazeServlet?" + cmd, !0), b.send(null);
}

var timerId, timer_is_on = 0, refreshInterval=2000, connection_mon_timer;
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
               <th style="width:120px; max-width:120px;"><img src="images/label.png"/></th>
               <s:iterator value="tableHeader" status="tableHeaderStatus">
               <th align="center">
                    <s:if test="%{label=='Power'}">
                        <s:property value="%{label}"/>
                    </s:if>
                    <s:else>
                        <a class="tooltip" href="#" style="cursor:default"><s:property value="%{label}"/><span class="info"><s:property value="%{description}"/></span></a>
                    </s:else>
               </th>
               </s:iterator>
           </tr>
       </thead>

       <s:iterator value="matrix" status="rowsStatus" var="row">
       <tr>
           <td align="center" style="width:120px; max-width:120px; background: #005566; white-space:nowrap">
               <a class="tooltip" href="#" style="cursor:default"><s:property value="%{#row[0].label}"/><span class="info"><s:property value="%{#row[0].description}"/></span></a>
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
               <td align="right"><strong>Set Attenuation</strong></td>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
           </tr>
       </table>

       <table>
            <thead>
            <tr><th><div id="outputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th><th><div id="inputs_label"></div><img src="images/spacer.gif" width="5" height="1"></th><th nowrap>Attenuation(db)</th><th nowrap>MIMO</th></tr>
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

    <div class="blockbkg" id="bkg2" style="visibility: hidden;">
    <div class="cont" id="dlg2" style="visibility: hidden;">
    <table>
        <tr>
              <td align="left">MIMO:
                <select id="mimo_sele" onchange="mimo_selected(this);">
                    <option>NO</option>
                    <option>2X2</option>
                    <option>4X4</option>
                </select>
            </td>
            <td align="left"><strong>HandOver</strong></td>
            <td align="right"><div class="closebtn" title="Close" id="closebtn2"></div></td></tr>
        <tr>
            <td colspan="3">
                <table width="100%">
                    <thead><tr><th>Output</th><th>Inputs</th></tr><thead>
                   <tr><td align="center"><input type="text" size="12" id="handoff_out1" value=""></td><td align="center"><input type="text" size="12" id="handoff_in1"  value=""></td></tr>
                    <tr id="id_row_2" style="display: none;"><td align="center"><input type="text" size="12" id="handoff_out2" value=""></td><td align="center"><input type="text" size="12" id="handoff_in2" value=""></td></tr>
                    <tr id="id_row_3" style="display: none;"><td align="center"><input type="text" size="12" id="handoff_out3" value=""></td><td align="center"><input type="text" size="12" id="handoff_in3" value=""></td></tr>
                    <tr id="id_row_4" style="display: none;"><td align="center"><input type="text" size="12" id="handoff_out4" value=""></td><td align="center"><input type="text" size="12" id="handoff_in4" value=""></td></tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <table>
                    <tr>
                        <td align="right" nowrap>Step (dB)</td>
                        <td align="left"><input type="text" id="handoff_step" value="" size="6"></td>
                        <td align="right" nowrap>Start ATTN (dB)</td>
                        <td align="left"><input type="text" id="handoff_start" value="" size="6"></td>
                    </tr>
                     <tr>
                        <td align="right" nowrap>Speed (sec.)</td>
                        <td align="left"><input type="text" id="handoff_speed" value="" size="6"></td>
                        <td align="right" nowrap>End ATTN (dB)</td>
                        <td align="left"><input type="text" id="handoff_target" value="" size="6"></td>
                    </tr>
                    <tr>
                        <td align="right">Min/Max Pause(Sec.)</td>
                        <td align="left" valign="top"><input type="text" id="mm_pause" value="0" size="6"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td colspan="3"><img src="images/spacer.gif" width="1" height="10"></td></tr>
        <tr>
            <td align="center" colspan="3">
               <input type="button" value="Start" onClick="hand_off_start('handoff_start');"><img src="images/spacer.gif" width="5" height="1">
               <img src="images/spacer.gif" width="5" height="1"><input type="button" value="Stop" onClick="hand_off_stop('handoff_stop');">
            </td>
        </tr>
    </table>
    </div>
    </div>

    <div class="block_tr_bkg" id="tr_bkg" style="visibility: hidden;">
    <div class="cont" id="tr_dlg" style="visibility: hidden;">
    <table>
        <tr>
            <td><img src="images/spacer.gif" width="1" height="1"></td>
            <td align="center"><strong>Tier Roam</strong></td>
            <td align="right"><div class="closebtn" title="Close" id="tier_roam_closebtn"></div></td></tr>
        <tr>
            <td colspan="3">
                <table width="100%">
                    <thead><tr><th>Output</th><th>Input</th></tr><thead>
                    <tr>
                       <td align="center"><input type="text" size="12" id="tierroam_out" value=""></td>
                       <td align="center"><input type="text" size="12" id="tierroam_in"  value=""></td>
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
                        <td align="left" colspan="2" valign="top"><input type="text" id="tierroam_mm_pause" value="0" size="6"></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr><td colspan="3"><img src="images/spacer.gif" width="1" height="10"></td></tr>
        <tr>
            <td align="center" colspan="3">
               <input type="button" value="Start" onClick="start_tier_roam();">
               <img src="images/spacer.gif" width="10" height="1">
               <input type="button" value="Stop" onClick="stop_tier_roam();">
            </td>
        </tr>
    </table>
    </div>
    </div>

    <div class="blockbkg" id="bkg_cp" style="visibility: hidden;">
    <div class="cont" id="dlg_cp" style="visibility: hidden;">
        <table>
            <tr><td colspan="2">&nbsp;</td></tr>
            <tr>
                <td align="right" colspan="3"><strong>Color Scheme</strong></td>
                <td align="right"><div class="closebtn" title="Close" id="closebtn_cp"></div></td>
            </tr>
            <tr>
                <td align="right">Range1:</td>
                <td align="left"><s:textfield id="range1" theme="simple" name="range1" size="6"></s:textfield></td>
                <td align="right" nowrap>Assign Color:</td>
                <td><s:textfield name="color1" theme="simple" cssClass="color" size="6"></s:textfield></td>
            </tr>
            <tr>
                <td align="right">Range2:</td>
                <td align="left"><s:textfield id="range2" name="range2" theme="simple" size="6"></s:textfield></td>
                <td align="right" nowrap>Assign Color:</td>
                <td><s:textfield name="color2" cssClass="color" theme="simple" size="6"></s:textfield></td>
            </tr>
            <tr>
                <td align="right">Range3:</td>
                <td align="left"><s:textfield id="range3" name="range3" theme="simple" size="6"></s:textfield></td>
                <td align="right" nowrap>Assign Color:</td>
                <td><s:textfield name="color3" cssClass="color" theme="simple" size="6"></s:textfield></td>
            </tr>
            <tr><td colspan="4"><img src="images/spacer.gif" width="1" height="10"></td></tr>
            <tr><td colspan="4" align="center"><input type="button" value="Submit" onClick="change_scheme();"></td></tr>
        </table>
    </div>
    </div>
    </s:form>
    <div id="dialog_alert" title="Connection Recovered" style="display:none;">
        <p>Application Server Connection is Recovered. Please Login Again!</p>
    </div>
    <div id="debug"></div>
