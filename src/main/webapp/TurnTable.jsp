<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css?version=1.0.1">

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
.myselect { 
    width:100px; 
} 
.myselect option { 
    width:80px; 
}
.container{width:90%;margin-left:0;margin-right:0;padding:5px}.container div{padding:5px;width:100%}.container .header{background-color:silver;border:1px solid silver;border-radius:5px;padding:2px;cursor:pointer;font-weight:700}.container .content{display:none;padding:5px}table.altrowstable{font-family:verdana,arial,sans-serif;font-size:12px;color:#333;text-align:center;border-collapse:collapse;border-color:#506080;border-width:1px}.button{height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}
</style>

<SCRIPT type="text/javascript" language="javascript" src="js/rfmaze.js"></SCRIPT>
<SCRIPT type="text/javascript" src="js/jscolor.js"></SCRIPT>
<SCRIPT language="javascript">

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
        }
    }, a.open("POST", "/rfmaze/mazeServlet?command=isconnected", !0), a.send();
}

function viewmatrix() {
    if (document.getElementById('rfmaze_hardware').selectedIndex == 0) {
        alert("Invalid selection!");
        return;
    }
    document.getElementById('rfmaze').submit();
}

function sendCommand(a) {
    var b;
    var cmd = "/rfmaze/mazeServlet?command=turntable." + a;
    b = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP"),
    b.onreadystatechange = function() {
         if (4 == b.readyState && 200 == b.status) {
             var c = this.responseText;
         }
    }, b.open("POST", cmd, !0), b.send(null);
}

$(document).ready(function() {
    $("#id_init").click(function() {
        sendCommand("init");
    }),

    $("#id_angle").click(function() {        
        "hidden" == document.getElementById("bkg").style.visibility && (document.getElementById("bkg").style.visibility = "", 
        $("#bkg").hide()), "hidden" == document.getElementById("dlg").style.visibility && (document.getElementById("dlg").style.visibility = "", 
        $("#dlg").hide()), 
        $("#bkg").fadeIn(300, "linear", function() {
            $("#dlg").show(500, "swing"); $("#dlg").draggable();
        });
    }),
    
    $("#id_home").click(function() {
        sendCommand("home");
    }),

    $("#id_power_on").click(function() {
        sendCommand("poweron");
    }),

    $("#id_power_off").click(function() {
        sendCommand("poweroff");
    }),
    
    $("#id_set_angle").click(function() {
        $("#dlg").hide("500", "swing", function() {
            $("#bkg").fadeOut("300");
        });
        
        var val = $("#id_angle_input").val();
        sendCommand("angle&param="+val);
    }),

    $("#set_rpm").on('change', function() {
        var val = $("#set_rpm").val();
        sendCommand("rpm&param="+val);
    }),
    
    onInit(), heartbeat();
});

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
            <tr><th align="center" colspan="2">Select Matrix/Table</th></tr>
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

    <table border="0" cellspacing="10" cellpadding="10" align="center">
        <tr>
            <td align="center"><input id="id_init" theme="simple" value="Initialize" class="button"><img src="images/spacer.gif" width="10" height="1"/></td>
            <td align="center"><input id="id_home" theme="simple" value="Home Table" class="button"><img src="images/spacer.gif" width="10" height="1"/>
            <td align="center"><input id="id_power_on" theme="simple" value="Power On" class="button"><img src="images/spacer.gif" width="10" height="1"/>
            <td align="center"><input id="id_power_off" theme="simple" value="Power Off" class="button"><img src="images/spacer.gif" width="10" height="1"/>
            <td align="center"><input id="id_angle" theme="simple" value="Set Angle" class="button"><img src="images/spacer.gif" width="10" height="1"/>
            <td align="center">Set RPM: <s:select label="Set RPM" id="set_rpm" list="rmpList" name="rpm" cssClass="myselect"/>
           </td>
       </tr>      
    </table>
    <div id="progressbar"><img src="images/progress_bar.gif"></div>
    </s:else>
    <s:hidden name="action" value=""/>    
    <div id="spinner" class="spinner" style="display:none;">
        <img id="img-spinner" src="images/spinner.gif" alt="Connecting"/>
    </div>

    <div class="blockbkg" id="bkg" style="visibility: hidden;">
    <div class="cont" id="dlg" style="visibility: hidden;">
       <table>
            <thead>
                <tr><th nowrap>Set Angle ( Degree )</th></tr>
            </thead>
            <tr><td align="center"><img src="spacer.gif" width="1" height="20"</td></tr>
            <tr>
                <td align="center"><s:textfield id="id_angle_input" name="angle" size="10"></s:textfield></td>              
            </tr>
            <tr>             
                <td align="center"><input id="id_set_angle" type="button" class="button" value="Submit"></td>                 
            </tr>          
        </table>
    </div>
    </div>
    
    </s:form>
    <div id="dialog_alert" title="Connection Recovered" style="display:none;">
        <p>Application Server Connection is Recovered. Please Login Again!</p>
    </div>
    <div id="debug"></div>