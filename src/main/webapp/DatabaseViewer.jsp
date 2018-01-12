<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>RFmaze</title>
  <LINK REL="SHORTCUT ICON" HREF="images/favicon.png">
  <link type="text/css" href="css/Aristo.css" rel="stylesheet" />
  <STYLE>  
  #footer {
    position: fixed;
    height: 35px;
    bottom: 0px;
    left: 0px;
    right: 0px;
    margin-bottom: 0px;
  }
  </STYLE>
  <script src="js/jquery-1.11.0.js"></script> 
  <script src="js/jquery-ui.js"></script>

  <!--[if lte IE 7]>
    <link rel="stylesheet" type="text/css" href="css/ie.css" media="screen" />
  <![endif]-->
                
  <script type="text/javascript">
    function exec_cmd() {
      document.getElementById('databaseview').submit();
    }
    function query_cmd() {
      var selectedtable = document.getElementById('mtables');
      var tablename = selectedtable.options[selectedtable.selectedIndex].value;
      var command = "SELECT * FROM " + tablename;            
      document.getElementById('databaseview_command').value=command;
      document.getElementById('databaseview').submit();
    }        
    $(function() {
      $( "#tabs" ).tabs();
    });
  </script>
</head>

<body class="ui-form" background="images/rfmaze_background.png?1.0.0.0-1000">
<s:form theme="simple" action="databaseview.action" method="query"> 
  <table border="0" cellspacing="0" cellpadding="0" width="100%" background="images/banner_bg.png">
    <tr>
      <td align="left" width="10%">
        <img src="images/acentury_logo.png" width="120" height="73"/>
      </td>
      <td align="center" width="90%"><img src="images/logo.png"/></td>
    </tr>
    <tr><td colspan="2"><img src="images/spacer.gif" width="1" height="20"/></td></tr>
  </table>
  
  <div style="text-align: center; font-family: 'Helvetica', sans-serif; font-size: 16px; color='#336699';"> RFmaze Database Viewer </div>
  <div class="wrapper" align="center">
    <s:if test="%{errorMessage != null}">
      <div class="error_message"><font size="+1"><s:property value="errorMessage"/></font></div>
      <img src="images/spacer.gif" width="1" height="20"/>
    </s:if>
    <div id="tabs">
      <ul>
        <li><a href="#tabs-1">Execute SQL</a></li>
        <li><a href="#tabs-2">Users</a></li>
        <li><a href="#tabs-3">Servers</a></li>
        <li><a href="#tabs-4">Assignments</a></li>
      </ul>
      <div id="tabs-1">
        <table align="center" style="font-size: 12px;">
          <tr>
            <td align="right">SQL: </td>
            <td><s:textfield theme="simple" size="80" name="command"/></td>
            <td align="left"><input type="button" value="submit" onclick="exec_cmd();"/></td>
          </tr>
          <tr>
            <td align="right">Table: </td>
            <td>
              <select id="mtables">
                <option value="users">users</option>
                <option value="Servers">Servers</option>
                <option value="assignments">assignments</option>
                <option value="matrix_labels">matrix_labels</option>
                <option value="matrix_ports">matrix_ports</option>
              </select>                   
            </td><td align="left"><input type="button" value="Query" onclick="query_cmd();"/></td>
          </tr>      
          <tr>
            <td colspan="3"><s:textarea  theme="simple" name="result" cols="80" rows="10"/></td>
          </tr>  
        </table>
      </div>
      <div id="tabs-2" style="text-align:center;">
        <table align="center" class="labels_table" style="font-size: 12px;">
          <thead>
            <tr>
              <s:iterator value="userTableHeader" status="tstatus">
              <th align="center" style="width: 100px; min-width:100px;"><s:property/></th>
              </s:iterator>
            </tr>
          </thead>
          <s:iterator value="udata" status="rstatus" var="row">
            <s:if test="#rstatus.even == true">
	        <tr style="background: #C0C0C0">
	        </s:if>
	        <s:else>
            <tr style="background: #EFEFEF">
            </s:else>
              <s:iterator value="#row" status="cstatus">
              <td align="center"><s:property/></td>
          </s:iterator>
          </tr>
      </s:iterator>
     </table>
    </div>
    <div id="tabs-3">
      <table align="center" class="labels_table" style="font-size: 12px;">
        <thead>
          <tr>
            <s:iterator value="serverTableHeader" status="tstatus">
            <th align="center" style="width: 100px; min-width:100px;"><s:property/></th>
            </s:iterator>
          </tr>
        </thead>
        <s:iterator value="sdata" status="rstatus" var="row">
          <s:if test="#rstatus.even == true">
	        <tr style="background: #C0C0C0">
	        </s:if>
	        <s:else>
          <tr style="background: #EFEFEF">
          </s:else>
            <s:iterator value="#row" status="cstatus">
            <td align="center"><s:property/></td>
            </s:iterator>
          </tr>
        </s:iterator>
      </table>
    </div>
    <div id="tabs-4">
      <table align="center" class="labels_table" style="font-size: 12px;">
        <thead>
          <tr>
            <s:iterator value="assignmentsTableHeader" status="tstatus">
            <th align="center"><s:property/></th>
            </s:iterator>
          </tr>
       </thead>
    
       <s:iterator value="adata" status="rstatus" var="row">
         <s:if test="#rstatus.even == true">
	         <tr style="background: #C0C0C0">
	       </s:if>
	       <s:else>
           <tr style="background: #EFEFEF">
         </s:else>
           <s:iterator value="#row" status="cstatus">
             <td align="center"><s:property/></td>
           </s:iterator>
           </tr>
       </s:iterator>
     </table>
    </div>
  </div>
  </div><!-- container -->
</div>
</s:form>
  <div id="footer">
    <table style="background-image: url(images/footer_bg.png); width: 100%; height:35px">
      <tr>
        <td style="text-align: center; vertical-align: middle; font-size:12px;">Copyright &copy;Acentury Inc. 2014-2016</td>
      </tr>
    </table>
  </div>
</body>
</html>
