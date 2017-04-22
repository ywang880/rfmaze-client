<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">

<script type="text/javascript" src="js/jscolor.js"></script>

<s:form theme="simple" action="colorscheme.action" method="post">
<table>
    <tr>
        <td align="right">Range1:</td>
        <td align="left"><s:textfield theme="simple" name="range1"></s:textfield></td>
        <td align="right">Assign Color:</td>
        <td><s:textfield name="color1" theme="simple" cssClass="color"></s:textfield></td>
    </tr>
    <tr>
        <td align="right">Range2:</td>
        <td align="left"><s:textfield name="range2" theme="simple" ></s:textfield></td>
        <td align="right">Assign Color:</td>
        <td><s:textfield name="color2" cssClass="color" theme="simple"></s:textfield></td>
    </tr>
        <tr>
        <td align="right">Range3:</td>
        <td align="left"><s:textfield name="range3" theme="simple"></s:textfield></td>
        <td align="right">Assign Color:</td>
        <td><s:textfield name="color3" cssClass="color" theme="simple"></s:textfield></td>
    </tr>
    <tr><td colspan="4"><img src="images/spacer.gif" width="1" height="10"></td></tr>
    <tr><td colspan="4" align="center"><s:submit value="Submit"></s:submit></td></tr>
</table>
</s:form>