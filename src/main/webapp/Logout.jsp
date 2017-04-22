<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>RFmaze</title>
   <LINK REL="SHORTCUT ICON" HREF="images/favicon.png">	

   <link rel="stylesheet" type="text/css" href="css/stdtheme.css"/>
   <link rel="stylesheet" type="text/css" href="css/style.css?1.0.0.0-1000"/>
	<!--[if lte IE 7]>
        <link rel="stylesheet" type="text/css" href="css/ie.css" media="screen" />
    <![endif]-->
			
    <script type="text/javascript" src="js/jquery-1.11.0.js"></script>
    <script type="text/javascript">
        function keyHandle(e) {
            if ( e.keyCode == 13 ) {
                $("form" ).submit();
            }
        };    
        window.addEventListener("keypress", keyHandle, true);    
    </script>
    <style>.LoginForm{padding:10px 40px;background:#ddd;width:300px;border-radius:10px;box-shadow: 10px 10px 5px #888888;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;font-size:14px;background:linear-gradient(top,#A0A0A0,gray);background:#8090AB;background:-moz-linear-gradient(top,#809DAB 0%,#DEDEDE 100%);background:-webkit-gradient(linear,left top,left bottom,color-stop(0%,#809DAB),color-stop(100%,#DEDEDE));background:-webkit-linear-gradient(top,#809DAB 0%,#DEDEDE 100%);background:-o-linear-gradient(top,#809DAB 0%,#DEDEDE 100%);background:-ms-linear-gradient(top,#809DAB 0%,#DEDEDE 100%);background:linear-gradient(to bottom,#809DAB 0%,#DEDEDE 100%);filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#809DAB',endColorstr='#DEDEDE',GradientType=0)}.button_form{height:30px;width:100px;border:1px solid rgba(200,200,200,0.59);color:rgba(0,0,0,0.8);text-align:center;font:bold "Helvetica Neue",Arial,Helvetica,Geneva,sans-serif;font-size:14px;background:linear-gradient(top,#A0A0A0,gray);background:#eee;background:-moz-linear-gradient(top,#eee 0%,#AAA 100%);background:-webkit-gradient(linear,left top,left bottom,color-stop(0%,#eee),color-stop(100%,#AAA));background:-webkit-linear-gradient(top,#eee 0%,#AAA 100%);background:-o-linear-gradient(top,#eee 0%,#AAA 100%);background:-ms-linear-gradient(top,#eee 0%,#AAA 100%);background:linear-gradient(to bottom,#eee 0%,#AAA 100%);filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#eeeeee',endColorstr='#AAAAAA',GradientType=0);-webkit-border-radius:5px;-khtml-border-radius:5px;-moz-border-radius:5px;border-radius:5px;text-shadow:0 2px 2px rgba(255,255,255,0.2)}#footer{position:fixed;height:35px;bottom:0px;left:0px;right:0px;margin-bottom:0px;}</style>
</head>

<body background="images/rfmaze_background.png?1.0.0.0-1000">
    <table border="0" cellspacing="0" cellpadding="0" width="100%" background="images/banner_bg.png">
        <tr>
            <td align="left" width="10%">
                <img src="images/acentury_logo.png" width="120" height="73"/>
            </td>
            <td align="center" width="90%">
                <img src="images/logo.png"/>
            </td>
        </tr>
        <tr><td colspan="2"><img src="images/spacer.gif" width="1" height="20"/></td></tr>
        <tr>
            <td align="right" colspan="2">
                <div class="greynavbar"><img src="images/spacer.gif" width="10" height="10"/></div>
            </td>
        </tr>
    </table>

    <div align="center">
    <table width="80%" align="center">
        <tr>
            <td align="center">
                <p style="font-family: Helvetica; color:#002233; font-size:16px;">Sign out successful!</p>
            </td>
        </tr>
    </table>
   
    <s:form theme="simple" action="login" method="post" namespace="/">
    <table class="LoginForm" width="300px" align="center" style="border-collapse:collapse;">
    <tbody>
        <tr><td><img src="images/spacer.gif" width="1" height="20"/></td><tr>
        <tr><td align="right">User ID:<img src="images/spacer.gif" width="10" height="1"/></td><td align="left"><input type="text" name="username" size="20" value="" id="login_username"/></td><tr>
        <tr><td><img src="images/spacer.gif" width="1" height="10"/> </td></tr>
        <tr><td align="right">Password:<img src="images/spacer.gif" width="10" height="1"/> </td><td align="left"><input type="password" name="password" size="20" id="login_password"/></td><tr>
        <tr><td><img src="images/spacer.gif" width="1" height="10"/> </td></tr>
        <tr><td align="center" colspan="2"><input type="button" id="login__execute" name="method:execute" value="Sign In" class="button_form" onclick="submit();" style="cursor: pointer"/></td><tr>
        <tr><td><img src="images/spacer.gif" width="1" height="20"/> </td></tr>
    </tbody>
    </table>
    </s:form>
    </div>
    <div id="footer">
        <table style="background-image: url(images/footer_bg.png); width: 100%; height:35px">
            <tr>
                <td style="text-align: center; vertical-align: middle;font-size:12px;">Copyright &copy;Acentury Inc. 2014-2016</td>
            </tr>
        </table>
    </div>
</body>
</html>
