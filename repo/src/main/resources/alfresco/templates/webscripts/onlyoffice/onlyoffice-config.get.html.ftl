<#include "/org/alfresco/repository/admin/admin-template.ftl" />

<!--
    Copyright (c) Ascensio System SIA 2021. All rights reserved.
    http://www.onlyoffice.com
-->

<@page title=msg("onlyoffice-config.title") readonly=true>

</form>
<div class="column-left">
   <@section label=msg("onlyoffice-config.doc-section") />

   <form id="docservcfg" action="${url.service}" method="POST" accept-charset="utf-8">
      <div class="description section">${msg("onlyoffice-config.description")}</div>
      <div class="control text">
         <label class="label" for="onlyurl">${msg("onlyoffice-config.doc-url")}</label>
         <span class="value">
            <input id="onlyurl" name="url" size="35" placeholder="http://docserver/" title="${msg('onlyoffice-config.doc-url-tooltip')}" pattern="(http(s)?://.*)|(/.*)" value="${docurl}" />
         </span>
      </div>
      <div class="control text">
         <label class="label" for="jwtsecret">${msg("onlyoffice-config.jwt-secret")}</label>
         <span class="value">
            <input class="value" id="jwtsecret" name="url" size="35" value="${jwtsecret}" />
         </span>
      </div>

      <@tsection label=msg("onlyoffice-config.advanced-section")>
         <div class="control text">
            <label class="label" for="onlyinnerurl">${msg("onlyoffice-config.doc-url-inner")}</label>
            <span class="value">
               <input class="value" id="onlyinnerurl" name="innerurl" size="35" placeholder="http://docserver/" title="${msg('onlyoffice-config.doc-url-inner-tooltip')}" pattern="http(s)?://.*" value="${docinnerurl}" />
            </span>
         </div>
         <div class="control text">
            <label class="label" for="alfurl">${msg("onlyoffice-config.alf-url")}</label>
            <span class="value">
               <input class="value" id="alfurl" name="alfurl" size="35" placeholder="http://alfresco/" title="${msg('onlyoffice-config.alf-url-tooltip')}" pattern="http(s)?://.*" value="${alfurl}" />
            </span>
         </div>
      </@tsection>

      <@section label=msg("onlyoffice-config.common-section") />
      <div class="control field">
         <input class="value" id="onlycert" name="cert" type="checkbox" ${cert} />
         <label class="label" for="onlycert">${msg("onlyoffice-config.ignore-ssl-cert")}</label>
      </div>
      <div class="control field">
         <input class="value" id="forcesave" name="forcesave" type="checkbox" ${forcesave} />
         <label class="label" for="forcesave">${msg("onlyoffice-config.forcesave")}</label>
      </div>
      <div class="control field">
         <input class="value" id="webpreview" name="cert" type="checkbox" ${webpreview} />
         <label class="label" for="webpreview">${msg("onlyoffice-config.webpreview")}</label>
      </div>
      <div class="control field section">
          <label class="label">${msg("onlyoffice-config.file-type")}</label>
          <div style="padding-top: 4px">
              <input class="value" id="csv" name="csv" type="checkbox" ${formatCSV} />
              <label class="label" style="margin-right: 21px" for="csv">csv</label>
              <input class="value" id="odp" name="odp" type="checkbox" ${formatODP} />
              <label class="label" style="margin-right: 21px" for="odp">odp</label>
              <input class="value" id="ods" name="ods" type="checkbox" ${formatODS} />
              <label class="label" style="margin-right: 21px" for="ods">ods</label>
              <input class="value" id="odt" name="odt" type="checkbox" ${formatODT} />
              <label class="label" style="margin-right: 21px" for="odt">odt</label>
              <input class="value" id="rtf" name="rtf" type="checkbox" ${formatRTF} />
              <label class="label" style="margin-right: 21px" for="rtf">rtf</label>
              <input class="value" id="txt" name="txt" type="checkbox" ${formatTXT} />
              <label class="label" style="margin-right: 21px" for="txt">txt</label>
          </div>
      </div>
      <br>
      <table>
          <tr style="vertical-align: top;">
              <td>
                  <input id="postonlycfg" type="button" value="${msg('onlyoffice-config.save-btn')}"/>
              </td>
              <td>
                  <div class="control field" style="margin-left: 20px;">
                      <input class="value" id="onlyofficeDemo" name="onlyofficeDemo" type="checkbox" ${demo} <#if !demoAvailable> disabled="disabled" </#if>/>
                      <label class="label" for="onlyofficeDemo">Connect to demo ONLYOFFICE Document Server</label>
                      </br>
                      <#if demoAvailable>
                          <div class="description">This is a public test server, please do not use it for private sensitive data. The server will be available during a 30-day period.</div>
                      <#else>
                          <div class="description">The 30-day test period is over, you can no longer connect to demo ONLYOFFICE Document Server.</div>
                      </#if>
                  </div>
              </td>
          </tr>
      </table>
   </form>
   <br>
   <span data-saved="${msg('onlyoffice-config.saved')}" data-error="${msg('onlyoffice-config.error')}" data-mixedcontent="${msg('onlyoffice-config.mixedcontent')}" data-jsonparse="${msg('onlyoffice-config.jsonparse')}" data-docservunreachable="${msg('onlyoffice-config.docservunreachable')}" data-docservcommand="${msg('onlyoffice-config.docservcommand')}" data-docservconvert="${msg('onlyoffice-config.docservconvert')}" data-jwterror="${msg('onlyoffice-config.jwterror')}" data-statuscode="${msg('onlyoffice-config.statuscode')}" id="onlyresponse" class="message hidden"></span>
</div>

<script type="text/javascript">//<![CDATA[
   (function() {
      var url = document.getElementById("onlyurl");
      var innerurl = document.getElementById("onlyinnerurl");
      var alfurl = document.getElementById("alfurl");
      var cert = document.getElementById("onlycert");
      var fs = document.getElementById("forcesave");
      var webpreview = document.getElementById("webpreview");
      var jwts = document.getElementById("jwtsecret");
      var demo = document.getElementById("onlyofficeDemo");
      var odt = document.getElementById("odt");
      var ods = document.getElementById("ods");
      var odp = document.getElementById("odp");
      var csv = document.getElementById("csv");
      var txt = document.getElementById("txt");
      var rtf = document.getElementById("rtf");

      var form = document.getElementById("docservcfg");
      var btn = document.getElementById("postonlycfg");
      var msg = document.getElementById("onlyresponse");

      var doPost = function(obj) {
         var xhr = new XMLHttpRequest();
         xhr.open("POST", form.action, true);
         xhr.setRequestHeader("Content-type", "application/json");
         xhr.setRequestHeader("Accept", "application/json");
         xhr.overrideMimeType("application/json");

         xhr.onload = function () { callback(xhr); };

         xhr.send(JSON.stringify(obj));
      };

      var callback = function(xhr) {
         btn.disabled = false;

         if (xhr.status != 200) {
               showMessage(msg.dataset.error + " " + msg.dataset.statuscode + " " + xhr.status, true);
               return;
         }

         var response = JSON.parse(xhr.response);

         if (!response.success) {
               showMessage(msg.dataset.error + " " + msg.dataset[response.message], true);
               return;
         }

         showMessage(msg.dataset.saved);
      };

      var parseForm = function() {
         var obj = {};

         var reg = RegExp(url.pattern);
         if (!reg.test(url.value)) { return null; }

         obj.url = url.value.trim();
         obj.innerurl = innerurl.value.trim();
         obj.alfurl = alfurl.value.trim();
         obj.cert = cert.checked.toString();
         obj.forcesave = forcesave.checked.toString();
         obj.webpreview = webpreview.checked.toString();
         obj.jwtsecret = jwts.value.trim();
         obj.demo = demo.checked.toString();
         obj.formats = {
            odt: odt.checked.toString(),
            ods: ods.checked.toString(),
            odp: odp.checked.toString(),
            csv: csv.checked.toString(),
            txt: txt.checked.toString(),
            rtf: rtf.checked.toString()
         };

         return obj;
      };

      var hideMessage = function() {
         msg.classList.add("hidden");
         msg.classList.remove("error");
         msg.innerText = "";
      };

      var msgTimeout = null;
      var showMessage = function(message, error) {
         if (error) {
               msg.classList.add("error");
         }

         msg.innerText = message;
         msg.classList.remove("hidden");

         if (msgTimeout != null) {
            clearTimeout(msgTimeout);
         }
         msgTimeout = setTimeout(hideMessage, 3000);
      };

      btn.onclick = function() {
         hideMessage();
         if (btn.disabled) return;

         var obj = parseForm();
         if (!obj) return;

         btn.disabled = true;
         doPost(obj);
      };

      var demoToggle = function () {
          if (!demo.disabled) {
               url.disabled = demo.checked;
               jwts.disabled = demo.checked;
               innerurl.disabled = demo.checked;
          }
      };

      demo.onclick = demoToggle;
      demoToggle();
   })();
//]]></script>
</@page>
