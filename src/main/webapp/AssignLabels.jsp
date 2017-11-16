<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<STYLE>  
.nbb{border:none;background:none;padding-left:5px;margin:0}.bb{border:normal;background:#FFC;padding-left:5px}table.altrowstable{font-family:verdana,arial,sans-serif;font-size:12px;color:#333;text-align:center;border-collapse:collapse;border-color:#506080;border-width:1px}table.altrowstable tr:nth-child(odd){background-color:#DADADA}table.altrowstable tr:nth-child(even){background-color:#A8A8A8}table.altrowstable th,table.altrowstable td{border-color:#506080;border-style:solid;border-width:1px}.button{display:block;height:25px;width:80px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;background:linear-gradient(top,#E0E0E0,gray);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}
</STYLE>

<SCRIPT language="javascript">  
function editthis(obj){obj.className='bb'}function resetstyle(obj){obj.className='nbb'}function commitselection(){var inputlabels=document.getElementById('inputs_labels');var outputlabels=document.getElementById('outputs_labels');var hardwareSelection=document.getElementById('labels_hardware');var inputs=new Array();var inputs_desc=new Array();var outputs=new Array();var outputs_desc=new Array();for(var i=1;i<inputlabels.rows.length;i++){inputs[i-1]=inputlabels.rows[i].cells[1].childNodes[0].value;inputs_desc[i-1]=inputlabels.rows[i].cells[2].childNodes[0].value}for(var i=1;i<outputlabels.rows.length;i++){outputs[i-1]=outputlabels.rows[i].cells[1].childNodes[0].value;outputs_desc[i-1]=outputlabels.rows[i].cells[2].childNodes[0].value}document.getElementById('labels_inputLabels').value=inputs.join("%2C");document.getElementById('labels_inputDesc').value=inputs_desc.join("%2C");document.getElementById('labels_outputLabels').value=outputs.join("%2C");document.getElementById('labels_outputDesc').value=outputs_desc.join("%2C");document.getElementById('labels_action').value="commit "+hardwareSelection.options[hardwareSelection.selectedIndex].value;document.getElementById('labels').submit()}function resetselection(){var hardwareSelection=document.getElementById('labels_hardware');document.getElementById('labels_action').value="reset "+hardwareSelection.options[hardwareSelection.selectedIndex].value;document.getElementById('labels').submit()}function hardwareassignment(obj){if(obj.selectedIndex<1){alert('hardware name is invalid!');return}document.getElementById('labels_action').value="edit "+obj.options[obj.selectedIndex].value;document.getElementById('labels').submit()}function createdefault(obj){var hardwareSelection=document.getElementById('labels_hardware');document.getElementById('labels_action').value="insert_default "+hardwareSelection.options[hardwareSelection.selectedIndex].value;document.getElementById('labels').submit()}function label_import(){var hardwareSelection=document.getElementById('labels_hardware');if(hardwareSelection.selectedIndex<1){alert('hardware name is invalid!');return}document.getElementById('labels_action').value="import "+hardwareSelection.options[hardwareSelection.selectedIndex].value;document.getElementById('labels').submit()}function label_export(){var hardwareSelection=document.getElementById('labels_hardware');if(hardwareSelection.selectedIndex<1){alert('hardware name is invalid!');return}document.getElementById('labels_action').value="export "+hardwareSelection.options[hardwareSelection.selectedIndex].value;document.getElementById('labels').submit()}var label_download="hidden";$(document).ready(function(){$("#btn_downloads").click(function(){if(label_download=="hidden"){document.getElementById("label_download").style.display="";document.getElementById("label_edit").style.display="none";label_download="show"}else{document.getElementById("label_download").style.display="none";document.getElementById("label_edit").style.display="";label_download="hidden"}});$("#closebtn").click(function(){isSetAttenuationActive=false;$("#dlg").hide("500","swing",function(){$("#bkg").fadeOut("300")})});$("#label_import").click(function(){if(document.getElementById("bkg").style.visibility=="hidden"){document.getElementById("bkg").style.visibility="";$("#bkg").hide()}if(document.getElementById("dlg").style.visibility=="hidden"){document.getElementById("dlg").style.visibility="";$("#dlg").hide()}$("#bkg").fadeIn(300,"linear",function(){$("#dlg").show(500,"swing")})});$("#label_import_bottom").click(function(){if(document.getElementById("bkg").style.visibility=="hidden"){document.getElementById("bkg").style.visibility="";$("#bkg").hide()}if(document.getElementById("dlg").style.visibility=="hidden"){document.getElementById("dlg").style.visibility="";$("#dlg").hide()}$("#bkg").fadeIn(300,"linear",function(){$("#dlg").show(500,"swing")})});$("#btn_downloads_bottom").click(function(){if(label_download=="hidden"){document.getElementById("label_download").style.display="";document.getElementById("label_edit").style.display="none";label_download="show"}else{document.getElementById("label_download").style.display="none";document.getElementById("label_edit").style.display="";label_download="hidden"}})});function do_import(){document.getElementById('labels').submit()}
</SCRIPT>
   
<s:form theme="simple" action="labels.action" method="post" enctype="multipart/form-data">

   <s:if test="%{errorMessage != null}">
       <img src="images/warn.png">&nbsp;<div class="error_message"><s:property value="errorMessage"/></div>
   </s:if>

   <table class="ConfigDataTable" width="60%" align="center">
   <thead><tr><th colspan="2">Matrix Input Output Label Assignment</th></tr></thead>
      <tbody>
      	 <tr>
            <td colspan="2">
               <img src="images/spacer.gif" width="1" height="5"/>
            </td>
         </tr>
         <tr>
            <td align="right" width="40%">Select hardware:</td>
            <td align="left" width="60%">
               <s:select cssStyle="width:260px;" label="Hardware" headerKey="-1" headerValue="-- select hardware --" list="hardwarelist" name="hardware" onchange="hardwareassignment(this);"/>
            </td>
         </tr>
         <tr>
            <td colspan="2">
               <img src="images/spacer.gif" width="1" height="10"/>
            </td>
         </tr>
      </tbody>
   </table>
   
   <s:if test="%{showcontent=='yes'}">
   <div class="menubar" align="left">
     <table>
	 <tr>
     <s:if test='%{createDefault=="yes"}'>
     <td><input type="button" class="button" value="Create" onclick="createdefault();"/></td>
     </s:if>
     <td><input type="button" class="button" value="Save" onclick="commitselection();"/></td>
     <td><input type="button" class="button" value="Reset" onclick="resetselection();"/></td>
	 <td><input type="button" class="button" value="Import" id="label_import"/></td>
	 <td><input type="button" class="button" value="Export" onclick="label_export();"/></td>
	 <td><input type="button" class="button" value="Download" id="btn_downloads"/></td>
	 </tr>
	 </table>
   </div>
   <s:if test="%{warningMessage != null}">
      <div class="warn_message"><s:property value="warningMessage" /></div>
   </s:if>
   <s:else>

	  <table class="altrowstable" id="label_download" style="display: none;" width="60%">
	    <thead>
	      <tr>
	        <th>Name</th>
			<th>Size</th>
			<th>Last Modified</th>
			<th>Download</th>
		  </tr>
		  </thead>
		  <s:iterator value="filelist">
	      <tr>	     
	      	<td><s:property value="name"/></td> 	
			<td><s:property value="size"/></td>
			<td><s:property value="date"/></td> 	
	      	<td><a href='<s:property value="url"/>'><img class="download" src="images/download.png" onmouseover="this.src='images/download1.png'" onmouseout="this.src='images/download.png'"/></a></td>
	      </tr>
	    </s:iterator>
	  </table>
      <table width="80%" class="labels_table" id="label_edit" style="border: 0px">
         <tr>
            <td align="right" valign="top">
               <table id="inputs_labels" width="250px" style="border: 1px solid green; border-collapse:collapse; padding:2px; margin:0px;">
                  <thead>
                     <tr>
                        <th style="background: #005588; color:white; font-weight:bold;" align="left">Input</th>
                        <th style="background: #005588; color:white; font-weight:bold;" align="left">&nbsp;&nbsp;Label</th>
                        <th style="background: #005588; color:white; font-weight:bold;" align="left">Description</th>
                     </tr>
                  </thead>
                  <tbody>
                     <s:iterator value="inputs" status="sdtatus">
                        <s:if test="#sdtatus.even == true">
	                    <tr style="background: #C0C0C0">
	                       <td align="center"><s:property value="id"/></td>
	                       <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{label}"/>'></td>
	                       <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{description}"/>'></td>
                        </tr>
	                    </s:if>
	                    <s:else>
	                    <tr style="background: #EFEFEF">
	                       <td align="center"><s:property value="id"/></td>
	                       <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{label}"/>'></td>
	                       <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{description}"/>'></td>
                        </tr>
	                    </s:else>
	                 </s:iterator>
	              </tbody>
               </table>
            </td>
            <td><img src="images/spacer.gif" width="10" height="1"/></td>
            <td align="left" valign="top">
               <table  id="outputs_labels" width="250px" style="border: 1px solid green; border-collapse:collapse; padding:2px; margin:0px;">
                  <thead>
                     <tr>
                        <th style="background: #005588; color:white; font-weight:bold;" align="left">Output</th>
                        <th style="background: #005588; color:white; font-weight:bold;" align="left">&nbsp;&nbsp;Label</th>
                        <th style="background: #005588; color:white; font-weight:bold;" align="left">Description</th>
                     </tr>
                  </thead>
                  <tbody>
                     <s:iterator value="outputs" status="sdtatus">
                     <s:if test="#sdtatus.even == true">	   
	                 <tr style="background: #C0C0C0">
	                    <td align="center"><s:property value="id"/></td>
	                    <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{label}"/>'></td>
	                    <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{description}"/>'></td>
	                 </tr>
	                 </s:if>
	                 <s:else>
	                 <tr style="background: #EFEFEF">
	                    <td align="center"><s:property value="id"/></td>
	                    <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{label}"/>'></td>
	                    <td align="center" nowrap><input onfocus="editthis(this);" onblur="resetstyle(this);" onmouseleave="resetstyle(this);" class="nbb" value='<s:property value="%{description}"/>'></td>
	                 </tr>
	                 </s:else>
	                 </s:iterator>	
	              </tbody>
	           </table>
            </td>
         </tr>
      </table>
	  
	  <div class="blockbkg" id="bkg" style="visibility: hidden;">
      <div class="cont" id="dlg" style="visibility: hidden;">
        <table >
            <tr>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><strong>Label Import</strong></td>
                <td><img src="images/spacer.gif" width="90" height="1"></td>
                <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
            </tr>
        </table>
        <table>
            <thead>
            <tr><td><img src="images/spacer.gif" width="1" height="5"></td></tr>
            <tr>
                <td align="center"><s:file name="labelImport" label="Choose file to import" /></td>
            </tr>
			<tr><td><img src="images/spacer.gif" width="1" height="5"></td></tr>
			<tr>
                <td align="center"><input type="button" name="Import" value="Import" id="doimport" onclick="do_import();"></td>
            </tr>  
        </table>
      </div>
      </div>
   </s:else>
   
   <div class="menubar" align="left">
      <table>
	  <tr>
      <s:if test='%{createDefault=="yes"}'>
      <td><input type="button" class="button" value="Create" onclick="createdefault();"/></td>
      </s:if>   
      <td><input type="button" class="button" value="Save" onclick="commitselection();"/></td>
      <td><input type="button" class="button" value="Reset" onclick="resetselection();"/></td>
	  <td><input type="button" class="button" value="Import" id="label_import_bottom"/></td>
	  <td><input type="button" class="button" value="Export" onclick="label_export();"/></td>
	  <td><input type="button" class="button" value="Download" id="btn_downloads_bottom"/></td>
	  </tr>
	  </table>
   </div>
   </s:if>
  
  <s:hidden name="action" value=""/>
  <s:hidden name="inputLabels" value=""/>
  <s:hidden name="inputDesc" value=""/>
  <s:hidden name="outputLabels" value=""/>
  <s:hidden name="outputDesc" value=""/>
</s:form>
