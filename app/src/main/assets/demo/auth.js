(window.webpackJsonp=window.webpackJsonp||[]).push([[1],{145:function(e,t,n){"use strict";n.d(t,"c",(function(){return v})),n.d(t,"a",(function(){return w})),n.d(t,"d",(function(){return E})),n.d(t,"b",(function(){return x})),n.d(t,"e",(function(){return k}));n(140),n(155),n(141),n(156),n(84),n(142),n(85),n(143),n(86),n(48),n(157);var r=n(0),a=n.n(r),i=n(256),o=n(215),c=n(258),u=n(261),l=n(282),s=n(276),f=n(275);function m(e,t){return function(e){if(Array.isArray(e))return e}(e)||function(e,t){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(e)))return;var n=[],r=!0,a=!1,i=void 0;try{for(var o,c=e[Symbol.iterator]();!(r=(o=c.next()).done)&&(n.push(o.value),!t||n.length!==t);r=!0);}catch(e){a=!0,i=e}finally{try{r||null==c.return||c.return()}finally{if(a)throw i}}return n}(e,t)||function(e,t){if(!e)return;if("string"==typeof e)return d(e,t);var n=Object.prototype.toString.call(e).slice(8,-1);"Object"===n&&e.constructor&&(n=e.constructor.name);if("Map"===n||"Set"===n)return Array.from(e);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return d(e,t)}(e,t)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function d(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,r=new Array(t);n<t;n++)r[n]=e[n];return r}function p(e,t,n,r,a,i,o){try{var c=e[i](o),u=c.value}catch(e){return void n(e)}c.done?t(u):Promise.resolve(u).then(r,a)}function g(e){return function(){var t=this,n=arguments;return new Promise((function(r,a){var i=e.apply(t,n);function o(e){p(i,r,a,o,c,"next",e)}function c(e){p(i,r,a,o,c,"throw",e)}o(void 0)}))}}function h(){return(h=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e}).apply(this,arguments)}function y(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}const b=Object(i.a)(()=>({root:{textAlign:"center",padding:10,overflow:"scroll",display:"block","&::-webkit-scrollbar":{display:"none"}},btn:{width:"60%",margin:10,textTransform:"none"},card:{width:"90%",marginLeft:"auto",marginRight:"auto",marginTop:15,marginBottom:15,padding:10,backgroundColor:"#fdfdfd"},field:{margin:10,width:"40%",minWidth:200,"-ms-user-select":"text","-moz-user-select":"-moz-text","-webkit-user-select":"text","-khtml-user-select":"text","user-select":"text"},titleRoot:{padding:5},image:{margin:15,maxHeight:"50%"}})),v=function(e){const t=b(),n=e.src,r=y(e,["src"]);return a.a.createElement(f.a,h({className:t.image,component:"img",src:n},r))},w=function(e){var t=this;const n=b(),r=e.funName,i=e.text,c=(e.position,e.args),u=e.dialog,l=e.clickAfter,s=y(e,["funName","text","position","args","dialog","clickAfter"]);return a.a.createElement(o.a,h({variant:"contained",color:"primary",onClick:g(regeneratorRuntime.mark((function e(){var n,a;return regeneratorRuntime.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,$flex[r].apply(t,c);case 2:n=e.sent,l&&l(n),u&&(a="","object"==typeof n?Object.keys(n).forEach(e=>{a.concat(e).concat(" : ").concat(n[e]).concat("/n")}):a=String(n),$flex.Dialog(i,a,{basic:"확인"},!0,!0));case 5:case"end":return e.stop()}}),e)}))),className:n.btn},s),i)},E=function(e){const t=b(),n=Object.assign({},e);return a.a.createElement(l.a,h({variant:"outlined",size:"small",className:t.field},n))},x=function(e){const t=b(),n=e.children,r=e.title,i=y(e,["children","title"]);return a.a.createElement(c.a,h({className:t.card},i),a.a.createElement(u.a,{classes:{root:t.titleRoot},titleTypographyProps:{variant:"h6"},title:r}),a.a.createElement(s.a,{style:{margin:5}}),n)},k=function(e){const t=b(),n=e.children,r=y(e,["children"]),i=m(a.a.useState(window.outerHeight),2),o=i[0],c=i[1];return a.a.useEffect(()=>{window.addEventListener("resize",()=>{c(window.outerHeight)})}),a.a.createElement("div",h({className:t.root,style:{height:o-76}},r),n)}},279:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return c}));n(149);var r=n(0),a=n.n(r),i=n(145);const o=[{name:"Rooting(JailBreak) Check",fun:"RootingCheck"},{name:"Authentication",fun:"Authentication"},{name:"Get UniqueAppID",fun:"UniqueAppID"},{name:"Get UniqueDeviceID",fun:"UniqueDeviceID"}];function c(){const e=e=>{const t=e.position,n=o[t];switch(t){case 0:return a.a.createElement(i.b,{title:"Rooting(JailBreak) Check Test"},a.a.createElement(i.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));case 0:return a.a.createElement(i.b,{title:"Authentication Test"},a.a.createElement(i.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));case 2:return a.a.createElement(i.b,{title:"Get Unique App ID Test"},a.a.createElement(i.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));case 3:return a.a.createElement(i.b,{title:"Get Unique Device ID Test"},a.a.createElement(i.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));default:return a.a.createElement("div",null)}};return a.a.createElement(i.e,null,o.map((t,n)=>a.a.createElement(e,{position:n,key:n})))}}}]);