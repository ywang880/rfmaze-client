<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link type="text/css" href="css/Aristo.css" rel="stylesheet" />
<link rel="stylesheet" href="css/matrix.css">

<STYLE>
.button{height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}
</STYLE>

<SCRIPT type="text/javascript" language="javascript" src="js/rfmaze.js"></SCRIPT>
<SCRIPT language="javascript">
function addRow(e) {
    var t = document.getElementById(e);
    var n = t.rows.length;
    var r = t.insertRow(n);
    var i = t.rows[0].cells.length;
    for (var s = 0; s < i; s++) {
        var o = r.insertCell(s);
        o.innerHTML = t.rows[0].cells[s].innerHTML;
        switch (o.childNodes[0].type) {
            case "text":
                o.childNodes[0].value = "";
                break;
            case "checkbox":
                o.childNodes[0].checked = false;
                break;
            case "select-one":
                o.childNodes[0].selectedIndex = 0;
                break
        }
    }
}

function deleteRow(e) {
    try {
        var t = document.getElementById(e);
        var n = t.rows.length;
        for (var r = 0; r < n; r++) {
            var i = t.rows[r];
            var s = i.cells[0].childNodes[0];
            if (null != s && true == s.checked) {
                if (n <= 1) {
                    alert("Cannot delete all the rows.");
                    break
                }
                t.deleteRow(r);
                n--;
                r--
            }
        }
    } catch (o) {
        alert(o)
    }
}

function enableInput(e) {
    var t = document.getElementById(e).style.display;
    if (t == "none") {
        document.getElementById(e).style.display = ""
        document.getElementById('submit_buttons').style.display = ""
    } else {
        document.getElementById(e).style.display = "none"
        document.getElementById('submit_buttons').style.display = "none"
    }
}

function disableInput(e,f) {
    document.getElementById(e).style.display = "none"
    document.getElementById(f).style.display = "none"
    document.getElementById('submit_buttons').style.display = "none"
}

function commit(e) {
    var x=document.getElementById("id_assigned");
    var hardwares='';
    for (var i = 0; i < x.options.length; i++) {
        if ( i == 0 ) {
             hardwares = x.options[i].value;
        } else {
          hardwares = hardwares + ',' + x.options[i].value;
       }
    }
    document.getElementById("_action").value = e+'?hardwares='+hardwares;
    document.getElementById("users").submit();
}

function deleteUser(e) {
    if (!confirm("Do you want to delete selected user?")) {
        return
    }
    document.getElementById("_action").value = "delete " + e;
    document.getElementById("users").submit()
}

function editUser(e, t, n) {
    var r = e.parentNode.parentNode.cells[0].innerHTML;
    var i = e.parentNode.parentNode.cells[1].innerHTML;

    document.getElementById('_user_assignment').style.display = "none"

    enableInput(t);
    document.getElementById("users_id").value = r;
    document.getElementById("user_passwd").value = i;
}

function assignMatrix(e, t) {
    var r = e.parentNode.parentNode.cells[0].innerHTML;
    getListAssignedHardwares(r);
    var i = e.parentNode.parentNode.cells[1].innerHTML;
    enableInput(t);

    document.getElementById('current_user').innerHTML = 'Hardware Assigned to ' + r;
    document.getElementById('_user_data').style.display = "none"
    document.getElementById("users_id").value = r;
    document.getElementById("user_passwd").value = i;
}

function show_hide_password() {
    var attr = $("#user_passwd").attr('type');
    if (attr == "text") {
        $("#user_passwd").attr('type', 'password');
    } else {
        $("#user_passwd").attr('type', 'text');
    }
}

function isAlreadyAssigned(a, b) {
    var jqxhr = $.get( "/rfmaze/mazeServlet?command=getassignedusers&hw=" + b, function(responseData) {
        if ( isBlank(responseData) ) {
            selectedHardwares.push( b );
            $("#id_assigned").append('<option value=' + a + '>' + b + '</option>');
        } else {
            var anwser = confirm("Hardware " + b + " is assigned to " + responseData.replace(/(\r\n|\n|\r)/gm, "") + ".\nAssign to a new user causes hardware is unassigned from previous assigned user.\nDo you want to proceed?");
            if ( anwser ) {
                selectedHardwares.push( b );
                $("#id_assigned").append('<option value=' + a + '>' + b + '</option>');
            }
        }
    });
    return false;
}

function getListAssignedHardwares(user) {
    var jqxhr = $.get( "/rfmaze/mazeServlet?command=gethardwares&user=" + user, function(responseData) {
        var i;
        var selectbox = document.getElementById('id_assigned');
        for(i = selectbox.options.length - 1 ; i >= 0 ; i--) {
            selectbox.remove(i);
        }

        if ( !isBlank( responseData ) ) {
            var res = responseData.split(",");
            for (a=0; a<res.length;a++) {
                $("#id_assigned").append('<option style="background: #FFFFFF" value=' + res[a] + '>' + res[a] + '</option>');
            }
        }
    });

    var jqxhr = $.get( "/rfmaze/mazeServlet?command=gethardwaresfullassignment&user=" + user, function(responseData) {
        if ( !isBlank( responseData ) ) {
            var res = responseData.split(",");
            var selectStatus = document.getElementById("id_assigned");
            for (var option in selectStatus.options){
                var v1 = selectStatus.options[option].value;
                for (a=0; a < res.length; a++) {
                    var v2 = res[a].replace(/(\r\n|\n|\r)/gm, "");
                    if ( v1 == v2 ) {
                        selectStatus.options[option].style.background = "#3c78b5";
                    }
                }
            }
        }
    });
}

var selectedHardwares = new Array();
$(document).ready(function() {

    $("#addselected").click(function() {
        $('#id_hardwarelist :selected').each(function(i, selectedElement) {
            var a = $(selectedElement).val();
            var b = $(selectedElement).text();
            isAlreadyAssigned(a, b);
        });
        $( "#id_assignedHardware" ).val(selectedHardwares.join(","));
    });

    $("#removeselected").click(function() {
        $("#id_assigned option:selected").remove();
    });
});
</SCRIPT>

<s:form theme="simple" action="users.action" method="post">

<s:if test="%{warningMessage != null}">
    <img src="images/warn.png">&nbsp;<div class="warn_message"><s:property value="warningMessage"/></div>
</s:if>
<body class="ui-form">
<table class="ConfigDataTable" width="80%" align="center" style="border-collapse:collapse;">
   <thead>
      <tr>
         <th nowrap>User ID</th>
         <th nowrap>Password</th>
         <th nowrap colspan="3"><input type="button" class="button" value="Add New" align="center" onClick="enableInput('_user_data');"/></th>
      </tr>
   </thead>
   <tbody>
      <s:iterator value="users" status="usersStatus">
        <tr>
         <td align="center" style="border: 1px solid #A8A8A8;"><s:property value="%{id}"/></td>
         <td align="center" style="border: 1px solid #A8A8A8;">********</td>
         <td align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" style="width: 100px;" value=" Assign Matrix " onclick="assignMatrix(this, '_user_assignment');"/></td>
         <td align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" value=" Edit " onclick="editUser(this, '_user_data', '<s:property value="#usersStatus.count"/>');"/></td>
         <td align="center" style="border: 1px solid #A8A8A8;"><input type="button" class="button" value=" Delete " onclick="deleteUser('<s:property value="%{id}"/>');"/></td>
      </tr>
      </s:iterator>
   </table>
   <img src="images/spacer.gif" width="1" height="20"/>

   <table align="center" id="_user_data" class="ui-widget" style="display: none;">
        <tr>
            <td align="center">
                <table>
                    <tr>
                        <td align="right" nowrap>Name:&nbsp;</td>
                        <td align="left"><s:textfield name="id" key="label.username" size="20"/></td>
                        <td align="right" nowrap>Password:&nbsp;</td>
                        <td align="left"><s:password id="user_passwd" name="password" key="label.password" size="20"/>
                            <img id="show_password" src="images/eye.png" onClick="show_hide_password();"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <table align="center" id="_user_assignment" class="ui-widget" style="display: none;">
        <tr>
            <td style="color: #ffe680; font-size: 12 px; font-weight: bold; text-align: left">
                <strong>Note:</strong>
                <ul>
                    <li>All inputs and outputs of the selected matrix will be assigned to the selected user.</li>
                    <li>To unassign a matrix from the selected user move it from right to left.</li>
                </ul>
            </td>
        </tr>
        <tr>
            <td align="center">
                <table align="center">
                    <tr>
                        <td style=" color: white; font-size: 12 px; font-weight: bold; text-align: center">Available Hardware</td>
                        <td><img src="images/spacer.gif"></td>
                        <td id="current_user" style=" color: white; font-size: 12 px; font-weight: bold; text-align: center; white-space: nowrap;"></td>
                    </tr>
                    <tr>
                        <td align="right" valign="top" nowrap>
                           <s:select cssStyle="width:260px;" id="id_hardwarelist" label="AvailableHardware" multiple="true" size="5" headerKey="-1" list="hardwarelist" name="hardware"/>
                        </td>
                        <td  valign="middle" >
                            <table>
                                <tr>
                                    <td>
                                        <input type="button" value=" >> " class="button" id="addselected">
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <input type="button" value=" << " class="button" id="removeselected">
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td align="right" valign="top" nowrap>
                            <s:select cssStyle="width:260px;" id="id_assigned" label="Assigned" multiple="true" size="5" list="assignedHardwares" name="assignTo"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <table align="center" id="submit_buttons" style="display: none;">
        <tr>
            <td><img src="images/spacer.gif" width="1" height="10"/></td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="button" class="button" value="Commit" align="center" onclick="commit('commit');"/>
                <input type="button" class="button" value="Cancel" align="center" onClick="disableInput('_user_data', '_user_assignment' );"/>
            </td>
        </tr>
        <tr>
            <td><img src="images/spacer.gif" width="1" height="20"/></td>
        <tr>
   </table>
   <s:hidden name="action" id="_action" value="commit"/>
   <s:hidden name="assignedHardwareToUser" id="id_assignedHardware" value=""/>
</s:form>
</body>