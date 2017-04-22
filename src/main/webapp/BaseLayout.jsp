<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>RFMaze</title>    

<LINK REL="SHORTCUT ICON" HREF="images/favicon.png">
<link rel="stylesheet" type="text/css" href="css/stdtheme.css"/>
<link rel="stylesheet" href="css/style.css?version=1.0.0.0" type="text/css" media="screen, projection"/>
<link rel="stylesheet" href="css/menu.css" type="text/css"/>
    <!--[if lte IE 7]>
        <link rel="stylesheet" type="text/css" href="css/ie.css" media="screen" />
    <![endif]-->
            
    <script type="text/javascript" src="js/jquery-1.11.0.js"></script>
    <script type="text/javascript" src="js/jquery-ui.js"></script>
    <script type="text/javascript" src="js/jquery.leanModal.min.js"></script>    
    <script type="text/javascript" language="javascript" src="js/jquery.dropdownPlain.js"></script>
    <style>#footer{position:fixed;height:35px;bottom:0px;left:0px;right:0px;margin-bottom:0px;z-index:1;}#maintable {width:100%;border-spacing:0px;padding:0px;z-index:10;position: relative;}</style>
</head>

<body style="margin-bottom:0px;" background="images/rfmaze_background.png?1.0.0.0-1000">
    <table id="maintable">
        <tr>
            <td height="35" align="center" ><tiles:insertAttribute name="header"/></td>
        </tr>
        <tr>
            <td valign="top" align="center" width="100%"><tiles:insertAttribute name="body"/></td>
        </tr>
    </table>
    <div id="footer"><tiles:insertAttribute name="footer"/></div>
</body>
</html>
