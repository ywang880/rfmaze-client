<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/jquery_style.css">
<link rel="stylesheet" href="css/jquery-ui.css">
<link rel="stylesheet" href="css/matrix.css">
<link href="css/960.css" rel="stylesheet" media="screen" />
<link href="css/defaultTheme.css" rel="stylesheet" media="screen" />

<style>
#block_container{text-align:center}#connection_state,#hardware_name{display:inline}.divider{margin-top:20px;}.height380{height:380px;overflow-x:auto;overflow-y:auto}.matrixscrollabe{font-size:12px;color:#000;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif}.matrixscrollabe td,.matrixscrollabe th{border:1px solid #789;padding:5px}.matrixscrollabe tbody tr td{background-color:#eef2f9;background-image:-moz-linear-gradient(top,rgba(255,255,255,0.4) 0%,rgba(255,255,255,0.0) 100%);background-image:-webkit-gradient(linear,left top,left bottom,color-stop(0%,rgba(255,255,255,0.4)),color-stop(100%,rgba(255,255,255,0.0)))}.matrixscrollabe tbody tr.odd td{background-color:#d6e0ef;background-image:-moz-linear-gradient(top,rgba(255,255,255,0.4) 0%,rgba(255,255,255,0.0) 100%);background-image:-webkit-gradient(linear,left top,left bottom,color-stop(0%,rgba(255,255,255,0.4)),color-stop(100%,rgba(255,255,255,0.0)))}.matrixscrollabe thead tr th,.matrixscrollabe thead tr td,.matrixscrollabe tfoot tr th,.matrixscrollabe tfoot tr td{background-color:#8ca9cf;background-image:-moz-linear-gradient(top,rgba(255,255,255,0.4) 0%,rgba(255,255,255,0.0) 100%);background-image:-webkit-gradient(linear,left top,left bottom,color-stop(0%,rgba(255,255,255,0.4)),color-stop(100%,rgba(255,255,255,0.0)));font-weight:700}label{display:inline-block;background:#D0D0D0;width:5em;}
</style>

<script src="js/jquery-1.11.0.js"></script>
<script src="js/jquery-ui.js"></script>
<script type="text/javascript" language="javascript" src="js/rfmaze.js"></script>
<SCRIPT language="javascript">
function enableEditor(e){if(e){$(new_assignment).show();$(modify_assignment).hide()}else{$(new_assignment).hide();$(modify_assignment).show()}if(document.getElementById("bkg_editor").style.visibility=="hidden"){document.getElementById("bkg_editor").style.visibility="";$("#bkg_editor").hide()}if(document.getElementById("dlg_editor").style.visibility=="hidden"){document.getElementById("dlg_editor").style.visibility="";$("#dlg_editor").hide()}$("#bkg_editor").fadeIn(500,"linear",function(){$("#dlg_editor").show(500,"swing");$("#dlg_editor").draggable();$("#dlg_editor").zIndex(1)})}function getSelectValues(e){var t=[];var n=e&&e.options;var r;for(var i=0,s=n.length;i<s;i++){r=n[i];if(r.selected){t.push(r.value||r.text)}}return t.toString()}function getPdata(e){var t=document.getElementById("pForm");var n;var r=document.getElementById("assingn_user");var i=document.getElementById("share_user");var s=document.getElementById("reassign_from_user");var o=document.getElementById("reassign_to_user");var u=document.getElementById("free_user");var a=getSelectValues(r);var f=i.options[i.selectedIndex].value;var l=s.options[s.selectedIndex].value;var c=o.options[o.selectedIndex].value;var h=u.options[u.selectedIndex].value;if("Cancel"==e){return}else if("Share With"==e){if(i.selectedIndex<1){alert("Invalid selection. Please select user and try again!.");return}n="share "+currentdata+" with "+f}else if("Reassign"==e){if(s.selectedIndex<1||o.selectedIndex<1){alert("Invalid selection. Please select user and try again!.");return}if(l==c){alert("You cannot reassign to the same user. Please select users and try again!.");return}n="reassign "+currentdata+" from "+l+" to "+c}else if("Assign To"==e){if(isBlank(a)){alert("You must select at least one user to assign input!");return}n="assign "+currentdata+" to "+a}else if("Free From"==e){if(u.selectedIndex<1){alert("Invalid selection. Please select user and try again!.");return}n="free "+currentdata+" from "+h}else{alert("unsupported command, "+e+"!")}document.getElementById("inputs_action").value=n;document.getElementById("inputs").submit();t.style.display="none"}function buildmatrix(){document.getElementById("inputs_action").value="assign_inputs";document.getElementById("inputs").submit()}function hardwareassignment(e){if(e.selectedIndex>0){buildmatrix()}}function reassignselectedrow(e,t){currentdata=e.rowIndex;for(var n=1;n<e.cells.length;n++){from=e.cells[n].innerHTML;if(from!="-"){break}}document.getElementById("_id_row1").value=currentdata;document.getElementById("_id_row2").value=currentdata;unselectAll();disableAllOptions("reassign_from_user");disableAllOptions("free_user");var r=document.getElementById("matrix_assignment");var i=r.rows[currentdata];for(var n=1;n<i.cells.length;n++){var s=i.cells[n].innerHTML;if(s!="-"){findAndDisable(s)}}for(var n=1;n<i.cells.length;n++){var s=i.cells[n].innerHTML;if(s!="-"){findAndEnableOptions(s,"reassign_from_user");findAndEnableOptions(s,"free_user")}}enableEditor(t);return}function unselectAll(){unselectOption("reassign_to_user");unselectOption("share_user");unselectOption("assingn_user")}function unselectOption(e){var t=document.getElementById(e).options;for(var n=1;n<t.length;n++){t[n].selected=false}}function findAndDisable(e){getAndDisableOptions(e,"share_user");getAndDisableOptions(e,"reassign_to_user")}function getAndDisableOptions(e,t){var n=document.getElementById(t);var r=n.options.length;for(var i=1;i<r;i++){if(n.options[i].value==e){n.options[i].disabled=true}}}function findAndEnableOptions(e,t){var n=document.getElementById(t);var r=n.options.length;for(var i=1;i<r;i++){if(n.options[i].value==e){n.options[i].disabled=false}}}function disableAllOptions(e){var t=document.getElementById(e);var n=t.options.length;for(var r=1;r<n;r++){t.options[r].disabled=true}}function showHelp(e){$("#dialog").html(e);$("#dialog").dialog()}$(document).ready(function(){$("#closebtn").click(function(){$("#dlg_editor").hide("500","swing",function(){$("#bkg_editor").fadeOut("300")})})});var currentdata;var from;$(function(){$("#dialog").hide()})
</SCRIPT>
   
<SCRIPT language="javascript">
(function(c){c.fn.fixedHeaderTable=function(m){var u={width:"100%",height:"100%",themeClass:"fht-default",borderCollapse:!0,fixedColumns:0,fixedColumn:!1,sortable:!1,autoShow:!0,footer:!1,cloneHeadToFoot:!1,autoResize:!1,create:null},b={},n={init:function(a){b=c.extend({},u,a);return this.each(function(){var a=c(this);h._isTable(a)?(n.setup.apply(this,Array.prototype.slice.call(arguments,1)),c.isFunction(b.create)&&b.create.call(this)):c.error("Invalid table mark-up")})},setup:function(){var a=c(this),d=a.find("thead"),e=a.find("tfoot"),g=0,f,k,p;b.originalTable=c(this).clone();b.includePadding=h._isPaddingIncludedWithWidth();b.scrollbarOffset=h._getScrollbarWidth();b.themeClassName=b.themeClass;f=-1<b.width.search("%")?a.parent().width()-b.scrollbarOffset:b.width-b.scrollbarOffset;a.css({width:f});a.closest(".fht-table-wrapper").length||(a.addClass("fht-table"),a.wrap('<div class="fht-table-wrapper"></div>'));f=a.closest(".fht-table-wrapper");!0==b.fixedColumn&&0>=b.fixedColumns&&(b.fixedColumns=1);0<b.fixedColumns&&0==f.find(".fht-fixed-column").length&&(a.wrap('<div class="fht-fixed-body"></div>'),c('<div class="fht-fixed-column"></div>').prependTo(f),k=f.find(".fht-fixed-body"));f.css({width:b.width,height:b.height}).addClass(b.themeClassName);a.hasClass("fht-table-init")||a.wrap('<div class="fht-tbody"></div>');p=a.closest(".fht-tbody");var l=h._getTableProps(a);h._setupClone(p,l.tbody);a.hasClass("fht-table-init")?k=f.find("div.fht-thead"):(k=0<b.fixedColumns?c('<div class="fht-thead"><table class="fht-table"></table></div>').prependTo(k):c('<div class="fht-thead"><table class="fht-table"></table></div>').prependTo(f),k.find("table.fht-table").addClass(b.originalTable.attr("class")).attr("style",b.originalTable.attr("style")),d.clone().appendTo(k.find("table")));h._setupClone(k,l.thead);a.css({"margin-top":-k.outerHeight(!0)});!0==b.footer&&(h._setupTableFooter(a,this,l),e.length||(e=f.find("div.fht-tfoot table")),g=e.outerHeight(!0));d=f.height()-d.outerHeight(!0)-g-l.border;p.css({height:d});a.addClass("fht-table-init");"undefined"!==typeof b.altClass&&n.altRows.apply(this);0<b.fixedColumns&&h._setupFixedColumn(a,this,l);b.autoShow||f.hide();h._bindScroll(p,l);return this},resize:function(){return this},altRows:function(a){var d=c(this);a="undefined"!==typeof a?a:b.altClass;d.closest(".fht-table-wrapper").find("tbody tr:odd:not(:hidden)").addClass(a)},show:function(a,d,b){var g=c(this),f=g.closest(".fht-table-wrapper");if("undefined"!==typeof a&&"number"===typeof a)return f.show(a,function(){c.isFunction(d)&&d.call(this)}),this;if("undefined"!==typeof a&&"string"===typeof a&&"undefined"!==typeof d&&"number"===typeof d)return f.show(a,d,function(){c.isFunction(b)&&b.call(this)}),this;g.closest(".fht-table-wrapper").show();c.isFunction(a)&&a.call(this);return this},hide:function(a,d,b){var g=c(this),f=g.closest(".fht-table-wrapper");if("undefined"!==typeof a&&"number"===typeof a)return f.hide(a,function(){c.isFunction(b)&&b.call(this)}),this;if("undefined"!==typeof a&&"string"===typeof a&&"undefined"!==typeof d&&"number"===typeof d)return f.hide(a,d,function(){c.isFunction(b)&&b.call(this)}),this;g.closest(".fht-table-wrapper").hide();c.isFunction(b)&&b.call(this);return this},destroy:function(){var a=c(this),d=a.closest(".fht-table-wrapper");a.insertBefore(d).removeAttr("style").append(d.find("tfoot")).removeClass("fht-table fht-table-init").find(".fht-cell").remove();d.remove();return this}},h={_isTable:function(a){var d=a.is("table"),b=0<a.find("thead").length;a=0<a.find("tbody").length;return d&&b&&a?!0:!1},_bindScroll:function(a){var d=a.closest(".fht-table-wrapper"),c=a.siblings(".fht-thead"),g=a.siblings(".fht-tfoot");a.bind("scroll",function(){0<b.fixedColumns&&d.find(".fht-fixed-column").find(".fht-tbody table").css({"margin-top":-a.scrollTop()});c.find("table").css({"margin-left":-this.scrollLeft});(b.footer||b.cloneHeadToFoot)&&g.find("table").css({"margin-left":-this.scrollLeft})})},_fixHeightWithCss:function(a,d){b.includePadding?a.css({height:a.height()+d.border}):a.css({height:a.parent().height()+d.border})},_fixWidthWithCss:function(a,d,e){b.includePadding?a.each(function(){c(this).css({width:void 0==e?c(this).width():e})}):a.each(function(){c(this).css({width:void 0==e?c(this).parent().width():e})})},_setupFixedColumn:function(a,d,e){var g=a.closest(".fht-table-wrapper"),f=g.find(".fht-fixed-body");d=g.find(".fht-fixed-column");var k=c('<div class="fht-thead"><table class="fht-table"><thead><tr></tr></thead></table></div>'),p=c('<div class="fht-tbody"><table class="fht-table"><tbody></tbody></table></div>');a=c('<div class="fht-tfoot"><table class="fht-table"><tfoot><tr></tr></tfoot></table></div>');var g=g.width(),l=f.find(".fht-tbody").height()-b.scrollbarOffset,q,t,r,s;k.find("table.fht-table").addClass(b.originalTable.attr("class"));p.find("table.fht-table").addClass(b.originalTable.attr("class"));a.find("table.fht-table").addClass(b.originalTable.attr("class"));q=f.find(".fht-thead thead tr > *:lt("+b.fixedColumns+")");r=b.fixedColumns*e.border;q.each(function(){r+=c(this).outerWidth(!0)});h._fixHeightWithCss(q,e);h._fixWidthWithCss(q,e);var m=[];q.each(function(){m.push(c(this).width())});t=f.find("tbody tr > *:not(:nth-child(n+"+(b.fixedColumns+1)+"))").each(function(a){h._fixHeightWithCss(c(this),e);h._fixWidthWithCss(c(this),e,m[a%b.fixedColumns])});k.appendTo(d).find("tr").append(q.clone());p.appendTo(d).css({"margin-top":-1,height:l+e.border});t.each(function(a){0==a%b.fixedColumns&&(s=c("<tr></tr>").appendTo(p.find("tbody")),b.altClass&&c(this).parent().hasClass(b.altClass)&&s.addClass(b.altClass));c(this).clone().appendTo(s)});d.css({height:0,width:r});var n=d.find(".fht-tbody .fht-table").height()-d.find(".fht-tbody").height();d.find(".fht-tbody .fht-table").bind("mousewheel",function(a,d,b,e){if(0!=e)return a=parseInt(c(this).css("marginTop"),10)+(0<e?120:-120),0<a&&(a=0),a<-n&&(a=-n),c(this).css("marginTop",a),f.find(".fht-tbody").scrollTop(-a).scroll(),!1});f.css({width:g});if(!0==b.footer||!0==b.cloneHeadToFoot)k=f.find(".fht-tfoot tr > *:lt("+b.fixedColumns+")"),h._fixHeightWithCss(k,e),a.appendTo(d).find("tr").append(k.clone()),d=a.find("table").innerWidth(),a.css({top:b.scrollbarOffset,width:d})},_setupTableFooter:function(a,d,e){d=a.closest(".fht-table-wrapper");var g=a.find("tfoot");a=d.find("div.fht-tfoot");a.length||(a=0<b.fixedColumns?c('<div class="fht-tfoot"><table class="fht-table"></table></div>').appendTo(d.find(".fht-fixed-body")):c('<div class="fht-tfoot"><table class="fht-table"></table></div>').appendTo(d));a.find("table.fht-table").addClass(b.originalTable.attr("class"));switch(!0){case!g.length&&!0==b.cloneHeadToFoot&&!0==b.footer:e=d.find("div.fht-thead");a.empty();e.find("table").clone().appendTo(a);break;case g.length&&!1==b.cloneHeadToFoot&&!0==b.footer:a.find("table").append(g).css({"margin-top":-e.border}),h._setupClone(a,e.tfoot)}},_getTableProps:function(a){var d={thead:{},tbody:{},tfoot:{},border:0},c=1;!0==b.borderCollapse&&(c=2);d.border=(a.find("th:first-child").outerWidth()-a.find("th:first-child").innerWidth())/c;d.thead=h._getColumnsWidth(a.find("thead tr"));d.tfoot=h._getColumnsWidth(a.find("tfoot tr"));d.tbody=h._getColumnsWidth(a.find("tbody tr"));return d},_getColumnsWidth:function(a){var d={},b={},g=0,f,k;f=h._getColumnsCount(a);for(k=0;k<f;k++)b[k]={rowspan:1,colspan:1};a.each(function(a){var l=0,k=0;c(this).children().each(function(a){for(var f=parseInt(c(this).attr("colspan"))||1,h=parseInt(c(this).attr("rowspan"))||1;1<b[a+k].rowspan;)b[a+k].rowspan--,k+=b[a].colspan;a+=l+k;l+=f-1;1<h&&(b[a]={rowspan:h,colspan:f});if("undefined"===typeof d[a]||1!=d[a].colspan)d[a]={width:c(this).width()+parseInt(c(this).css("border-left-width"))+parseInt(c(this).css("border-right-width")),colspan:f},1==f&&g++});if(g==f)return!1});return d},_getColumnsCount:function(a){var b=0;a.each(function(a){var g;c(this).children().each(function(a){a=parseInt(c(this).attr("colspan"))||1;g=parseInt(c(this).attr("rowspan"))||1;b+=a});if(1<b||1==g)return!1});return b},_setupClone:function(a,d){var e=a.find("thead").length?"thead tr":a.find("tfoot").length?"tfoot tr":"tbody tr",g={},e=a.find(e);columnsCount=h._getColumnsCount(e);for(i=0;i<columnsCount;i++)g[i]={rowspan:1,colspan:1};e.each(function(a){var e=0,h=0;c(this).children().each(function(a){for(var f=parseInt(c(this).attr("colspan"))||1,m=parseInt(c(this).attr("rowspan"))||1;1<g[a+h].rowspan;)g[a+h].rowspan--,h+=g[a].colspan;a+=e+h;e+=f-1;1<m&&(g[a]={rowspan:m,colspan:f});"undefined"!==typeof d[a]&&d[a].colspan==f&&((c(this).find("div.fht-cell").length?c(this).find("div.fht-cell"):c('<div class="fht-cell"></div>').appendTo(c(this))).css({width:parseInt(d[a].width,10)}),c(this).closest(".fht-tbody").length||!c(this).is(":last-child")||c(this).closest(".fht-fixed-column").length||(a=Math.max((c(this).innerWidth()-c(this).width())/2,b.scrollbarOffset),c(this).css({"padding-right":parseInt(c(this).css("padding-right"))+a+"px"})))})})},_isPaddingIncludedWithWidth:function(){var a=c('<table class="fht-table"><tr><td style="padding: 10px; font-size: 10px;">test</td></tr></table>'),d,e;a.addClass(b.originalTable.attr("class"));a.appendTo("body");d=a.find("td").height();a.find("td").css("height",a.find("tr").height());e=a.find("td").height();a.remove();return d!=e?!0:!1},_getScrollbarWidth:function(){var a=0;if(!a)if(/msie/.test(navigator.userAgent.toLowerCase())){var b=c('<textarea cols="10" rows="2"></textarea>').css({position:"absolute",top:-1E3,left:-1E3}).appendTo("body"),e=c('<textarea cols="10" rows="2" style="overflow: hidden;"></textarea>').css({position:"absolute",top:-1E3,left:-1E3}).appendTo("body"),a=b.width()-e.width()+2;b.add(e).remove()}else b=c("<div />").css({width:100,height:100,overflow:"auto",position:"absolute",top:-1E3,left:-1E3}).prependTo("body").append("<div />").find("div").css({width:"100%",height:200}),a=100-b.width(),b.parent().remove();return a}};if(n[m])return n[m].apply(this,Array.prototype.slice.call(arguments,1));if("object"!==typeof m&&m)c.error('Method "'+m+'" does not exist in fixedHeaderTable plugin!');else return n.init.apply(this,arguments)}})(jQuery);$(document).ready(function(){$('#matrix_assignment').fixedHeaderTable({footer:false,fixedColumns:1})});$(function(){$(document).tooltip();});
</SCRIPT>

<s:form theme="simple" action="inputs.action" method="post"> 
   <table class="ConfigDataTable" width="60%" align="center">
   <thead><tr><th colspan="2">Matrix Input Port Assignment</th></tr></thead>
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
            <td colspan="2" align="center"><img src="images/spacer.gif" width="1" height="10"></td>
         </tr>
         <tr>
            <td colspan="2">
               <img src="images/spacer.gif" width="1" height="10"/>
            </td>
         </tr>
      </tbody>
   </table>
 
   <s:if test="%{errorMessage != null}">
      <div class="error_message"><img src="images/error.png"/><s:property value="errorMessage" /></div>
   </s:if>
   
   <s:if test="%{warningMessage != null}">
      <div class="warn_message"><img src="images/warn.png"/><s:property value="warningMessage" /></div>
   </s:if>
   <s:else>
   
     <div class="container_12 divider">
     <div class="grid_10 height380">
      <table class="matrixscrollabe" id="matrix_assignment" align="center">
        <thead>
          <tr>
            <th><img src="images/label_admin.png"/></th>
            <s:iterator value="tableHeader" status="tableHeaderStatus">
            <th align="center">
			    <div title='<s:property value="%{description}"/>'><s:property value="%{label}"/></div>
            </th>
            </s:iterator>
          </tr>
        </thead>
        
        <s:iterator value="matrix" status="rowsStatus" var="row">       
          <s:if test="%{#row[0].rowstatus=='reserved'}">
          <tr onclick="reassignselectedrow(this, false);">
             <td align="center" style="background: #808080; width: 100px">
			 <div title='<s:property value="%{#row[0].description}"/>'><s:property value="%{#row[0].label}"/></div>
             </td>
          </s:if>
          <s:else>
           <tr onclick="reassignselectedrow(this, true);">
             <td align="center" style="background: #8ca9cf; white-space:nowrap; width: 100px">
			   <div title='<s:property value="%{#row[0].description}"/>'><s:property value="%{#row[0].label}"/></div>
             </td>
          </s:else>
          
          <s:iterator value="#row" status="rowsStatus">
              <s:if test='%{name=="-"}'>
                  <td align="center" style="background:green;"><s:property value="%{name}"/></td>
              </s:if>
              <s:else>
                  <td align="center" style="background: orange;"><s:property value="%{name}"/></td>
              </s:else>
          </s:iterator>
       </tr>
    </s:iterator>
  </table>
  </div>
  </div>
  </s:else>
  
  <div class="blockbkg" id="bkg_editor" style="visibility: hidden;">
  <div class="cont" id="dlg_editor" style="visibility: hidden;">
     <table >
           <tr>
               <td><img src="images/spacer.gif" width="90" height="1"></td>
               <td align="right" nowrap><strong>Inputs Assignment</strong></td>
               <td><img src="images/spacer.gif" width="50" height="1"></td>
               <td align="right"><div class="closebtn" title="Close" id="closebtn"></div></td>
           </tr>
       </table>
       <table id="new_assignment" width="90%" align="center">
           <tr>
               <td align="center">
                   <table>
                       <tr>
                           <td align="right" nowrap>Selected Input:</td>
                           <td align="left" colspan="2">
                               <img src="images/spacer.gif" width="10" height="1"/><input id="_id_row1" type="text" size="13" value="" readonly style="background: #D0D0D0">
                           </td>
                       </tr>
                       <tr><td colspan="2"><img src="images/spacer.gif" width="1" height="10"/></td></tr>
                       <tr>
                           <td align="center" valign="middle">
                               <input style="width: 90px; height: 22px" type="button" value="Assign To" onclick="getPdata(this.value)" width="20"/>                              
                           </td>
                           <td align="left"><img src="images/spacer.gif" width="10" height="1"/><s:select cssStyle="width:120px;" multiple="true" size="5" id="assingn_user" list="users" name="user"/></td>
                           <td align="left" valign="bottom"><img src="images/question_mark.png" width="16" height="16" onmouseover="this.style.cursor='pointer';" onclick="showHelp('Assign selected input to selected users. Hold the Ctrl key for multiple selections')"/></td>
                       </tr>
                   </table>
               </td>
           </tr>
       </table>
       <table id="modify_assignment" width="90%" align="center">
           <tr>
               <td align="center">
                   <table>
                       <tr>
                           <td align="right" nowrap>Selected Input:</td>
                           <td align="left" colspan="2">
                               <img src="images/spacer.gif" width="10" height="1"/><input id="_id_row2" type="text" size="13" value="" readonly style="background: #D0D0D0">
                           </td>
                       </tr>
                       <tr><td colspan="2"><img src="images/spacer.gif" width="1" height="10"/></td></tr>                    
                       <tr>
                           <td align="center" valign="top">
                               <input style="width: 90px; height: 22px" type="button" value="Reassign" onclick="getPdata(this.value)" width="20"/>                              
                           </td>
                           <td valign="top">
                               <table>
                                   <tr>
                                       <td align="right">From:</td><td align="left"><s:select cssStyle="width:120px;" headerKey="-1" headerValue="-- select user --" id="reassign_from_user" list="users" name="user"/></td>
                                   </tr>
                                   <tr>
                                       <td align="right">To:</td><td align="left"><s:select cssStyle="width:120px;" headerKey="-1" headerValue="-- select user --" id="reassign_to_user" list="users" name="user"/></td>
                                   </tr>
                               </table>
                           </td>
                           <td align="left" valign="bottom"><img src="images/question_mark.png" width="16" height="16" onmouseover="this.style.cursor='pointer';" onclick="showHelp('Reassign selected input to selected user. The attenuation value will be reset to default.')"/></td>
                       </tr>
                       <tr>
                           <td align="center">
                               <input style="width: 90px; height: 22px" type="button" value="Free From" onclick="getPdata(this.value)"/>                               
                           </td>
                           <td align="left"><img src="images/spacer.gif" width="35" height="1"/><s:select cssStyle="width:120px;" headerKey="-1" headerValue="-- select user --" id="free_user" list="users" name="user"/></td>
                           <td valign="bottom"><img src="images/question_mark.png" width="16" height="16" onmouseover="this.style.cursor='pointer';" onclick="showHelp('Free Input from selected user. The attenuation value will be reset to default.')"/></td>
                       </tr>
                       <tr>
                           <td align="center">
                               <input style="width: 90px; height: 22px" type="button" value="Share With" onclick="getPdata(this.value)"/>                               
                           </td>
                           <td align="left"><img src="images/spacer.gif" width="35" height="1"/><s:select cssStyle="width:120px;" headerKey="-1" headerValue="-- select user --" id="share_user" list="users" name="user"/></td>
                           <td valign="bottom"><img src="images/question_mark.png" width="16" height="16" onmouseover="this.style.cursor='pointer';" onclick="showHelp('Share Input with selected user.')"/></td>
                       </tr>
                       <tr>
                           <td align="center">
                               <input style="width: 90px; height: 22px" type="button" value="Cancel" onclick="getPdata(this.value)"/>
                           </td>
                           <td colspan="2"><img src="images/spacer.gif" width="1" height="1"/></td>
                       </tr>                       
                   </table>
               </td>
           </tr>
       </table>       
    </div>
    </div>
  <s:hidden name="action" value=""/>
</s:form>

<div id="dialog" title="Help"><p></p></div>
