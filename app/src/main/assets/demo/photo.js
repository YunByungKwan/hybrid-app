(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{145:function(t,e,n){"use strict";n.d(e,"c",(function(){return v})),n.d(e,"a",(function(){return w})),n.d(e,"d",(function(){return S})),n.d(e,"b",(function(){return E})),n.d(e,"e",(function(){return x}));n(140),n(155),n(141),n(156),n(84),n(142),n(85),n(143),n(86),n(48),n(157);var r=n(0),a=n.n(r),o=n(256),i=n(215),c=n(258),u=n(261),l=n(282),s=n(276),f=n(275);function m(t,e){return function(t){if(Array.isArray(t))return t}(t)||function(t,e){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(t)))return;var n=[],r=!0,a=!1,o=void 0;try{for(var i,c=t[Symbol.iterator]();!(r=(i=c.next()).done)&&(n.push(i.value),!e||n.length!==e);r=!0);}catch(t){a=!0,o=t}finally{try{r||null==c.return||c.return()}finally{if(a)throw o}}return n}(t,e)||function(t,e){if(!t)return;if("string"==typeof t)return d(t,e);var n=Object.prototype.toString.call(t).slice(8,-1);"Object"===n&&t.constructor&&(n=t.constructor.name);if("Map"===n||"Set"===n)return Array.from(t);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return d(t,e)}(t,e)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function d(t,e){(null==e||e>t.length)&&(e=t.length);for(var n=0,r=new Array(e);n<e;n++)r[n]=t[n];return r}function p(t,e,n,r,a,o,i){try{var c=t[o](i),u=c.value}catch(t){return void n(t)}c.done?e(u):Promise.resolve(u).then(r,a)}function y(t){return function(){var e=this,n=arguments;return new Promise((function(r,a){var o=t.apply(e,n);function i(t){p(o,r,a,i,c,"next",t)}function c(t){p(o,r,a,i,c,"throw",t)}i(void 0)}))}}function g(){return(g=Object.assign||function(t){for(var e=1;e<arguments.length;e++){var n=arguments[e];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(t[r]=n[r])}return t}).apply(this,arguments)}function h(t,e){if(null==t)return{};var n,r,a=function(t,e){if(null==t)return{};var n,r,a={},o=Object.keys(t);for(r=0;r<o.length;r++)n=o[r],e.indexOf(n)>=0||(a[n]=t[n]);return a}(t,e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(t);for(r=0;r<o.length;r++)n=o[r],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(a[n]=t[n])}return a}const b=Object(o.a)(()=>({root:{textAlign:"center",padding:10,overflow:"scroll",display:"block","&::-webkit-scrollbar":{display:"none"}},btn:{width:"60%",margin:10,textTransform:"none"},card:{width:"90%",marginLeft:"auto",marginRight:"auto",marginTop:15,marginBottom:15,padding:10,backgroundColor:"#fdfdfd"},field:{margin:10,width:"40%",minWidth:200,"-ms-user-select":"text","-moz-user-select":"-moz-text","-webkit-user-select":"text","-khtml-user-select":"text","user-select":"text"},titleRoot:{padding:5},image:{margin:15,maxHeight:"50%"}})),v=function(t){const e=b(),n=t.src,r=h(t,["src"]);return a.a.createElement(f.a,g({className:e.image,component:"img",src:n},r))},w=function(t){var e=this;const n=b(),r=t.funName,o=t.text,c=(t.position,t.args),u=t.dialog,l=t.clickAfter,s=h(t,["funName","text","position","args","dialog","clickAfter"]);return a.a.createElement(i.a,g({variant:"contained",color:"primary",onClick:y(regeneratorRuntime.mark((function t(){var n,a;return regeneratorRuntime.wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,$flex[r].apply(e,c);case 2:n=t.sent,l&&l(n),u&&(a="","object"==typeof n?Object.keys(n).forEach(t=>{a.concat(t).concat(" : ").concat(n[t]).concat("/n")}):a=String(n),$flex.Dialog(o,a,{basic:"확인"},!0,!0));case 5:case"end":return t.stop()}}),t)}))),className:n.btn},s),o)},S=function(t){const e=b(),n=Object.assign({},t);return a.a.createElement(l.a,g({variant:"outlined",size:"small",className:e.field},n))},E=function(t){const e=b(),n=t.children,r=t.title,o=h(t,["children","title"]);return a.a.createElement(c.a,g({className:e.card},o),a.a.createElement(u.a,{classes:{root:e.titleRoot},titleTypographyProps:{variant:"h6"},title:r}),a.a.createElement(s.a,{style:{margin:5}}),n)},x=function(t){const e=b(),n=t.children,r=h(t,["children"]),o=m(a.a.useState(window.outerHeight),2),i=o[0],c=o[1];return a.a.useEffect(()=>{window.addEventListener("resize",()=>{c(window.outerHeight)})}),a.a.createElement("div",g({className:e.root,style:{height:i-76}},r),n)}},278:function(t,e,n){"use strict";n.r(e),n.d(e,"default",(function(){return l}));n(140),n(141),n(84),n(149),n(142),n(143),n(48);var r=n(0),a=n.n(r),o=n(145);function i(t,e){return function(t){if(Array.isArray(t))return t}(t)||function(t,e){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(t)))return;var n=[],r=!0,a=!1,o=void 0;try{for(var i,c=t[Symbol.iterator]();!(r=(i=c.next()).done)&&(n.push(i.value),!e||n.length!==e);r=!0);}catch(t){a=!0,o=t}finally{try{r||null==c.return||c.return()}finally{if(a)throw o}}return n}(t,e)||function(t,e){if(!t)return;if("string"==typeof t)return c(t,e);var n=Object.prototype.toString.call(t).slice(8,-1);"Object"===n&&t.constructor&&(n=t.constructor.name);if("Map"===n||"Set"===n)return Array.from(t);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return c(t,e)}(t,e)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function c(t,e){(null==e||e>t.length)&&(e=t.length);for(var n=0,r=new Array(e);n<e;n++)r[n]=t[n];return r}const u=[{name:"QRCode Scan",fun:"QRCodeScan"},{name:"Select Img from Photos",fun:"PhotoByDeviceRatio"}];function l(){const t=t=>{const e=t.position,n=u[e],r=i(a.a.useState([]),2),c=r[0],l=r[1],s=(t,e)=>{c[t]=e,l(c)};switch(e){case 0:return a.a.createElement(o.b,{title:"QRCodeScan Test"},a.a.createElement(o.a,{funName:n.fun,text:n.name,position:e,dialog:!0,args:c}));case 1:const t=i(a.a.useState(""),2),r=t[0],u=t[1];return a.a.useEffect(()=>{s(0,1),s(0,!0)}),a.a.createElement(o.b,{title:"Select Img from Photos Test"},a.a.createElement(o.c,{src:r}),a.a.createElement(o.a,{funName:n.fun,text:n.name,position:e,args:c,clickAfter:t=>{u(t)}}));default:return a.a.createElement("div",null)}};return a.a.createElement(o.e,null,u.map((e,n)=>a.a.createElement(t,{position:n,key:n})))}}}]);