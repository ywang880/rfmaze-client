<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<STYLE type="text/css">.logtable{width:620px;font:14px;helvetica,arial,sans-serif;border:0;background:#D0D0D0;border-collapse:collapse;margin:10px;-moz-border-radius:5px;-webkit-border-radius:5px;-khtml-border-radius:5px;border-radius:5px;border-style:solid;border-width:1px;border-color:#005566;padding:0px;border-spacing:0px;}.logtable thead{display:block;width:620px;overflow:auto;color:#fff;background:#005566}.logtable tbody{display:block;width:620px;height:380px;overflow:auto}.logtable th{text-align:center;height:30px;white-space:nowrap;vertical-align:middle;background:#005566;}.logtable td{white-space:nowrap;height:25px;vertical-align:top;border-left:1px solid #fff}.logtable tr:nth-child(even){background:#AAA}.width100{width:100px}.width100c{width:100px;text-align:center}.width200{width:200px}</STYLE>

<s:if test="%{errorMessage != null}">
    <div class="error_message"><font size="+1"><s:property value="errorMessage"/></font></div>
    <img src="images/spacer.gif" width="1" height="20"/>
</s:if>

<s:form theme="simple" action="download.action" method="post">
    <s:if test="%{warningMessage != null}">
        <div class="warn_message"><s:property value="warningMessage" /></div>
    </s:if>
    <s:else>    
    <img src="images/spacer.gif" width="1" height="20">
    <table class="logtable">
        <thead>
        <tr>
            <th class="width200">File name</th>
            <th class="width100">Size</th>
            <th class="width200">Last modified time</th>
            <th class="width100">Download</th>
        </tr>
        </thead>
        <s:iterator value="filelist">
        <tr>         
            <td class="width200"><s:property value="name"/></td>
            <td class="width100"><s:property value="size"/></td>
            <td class="width200"><s:property value="date"/></td>              
            <td class="width100c"><a href='<s:property value="url"/>'><img class="download" src="images/download.png" onmouseover="this.src='images/download1.png'"  onmouseout="this.src='images/download.png'"/></a></td>
        </tr>
        </s:iterator>
    </table>
    </s:else>    
</s:form>