(window.webpackJsonp=window.webpackJsonp||[]).push([[9],{145:function(e,t,n){"use strict";n.d(t,"c",(function(){return v})),n.d(t,"a",(function(){return w})),n.d(t,"d",(function(){return E})),n.d(t,"b",(function(){return x})),n.d(t,"e",(function(){return D}));n(140),n(141),n(155),n(84),n(142),n(85),n(143),n(48),n(156);var r=n(0),a=n.n(r),o=n(253),i=n(214),l=n(255),u=n(258),c=n(279),s=n(273),f=n(272);function m(e,t){return function(e){if(Array.isArray(e))return e}(e)||function(e,t){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(e)))return;var n=[],r=!0,a=!1,o=void 0;try{for(var i,l=e[Symbol.iterator]();!(r=(i=l.next()).done)&&(n.push(i.value),!t||n.length!==t);r=!0);}catch(e){a=!0,o=e}finally{try{r||null==l.return||l.return()}finally{if(a)throw o}}return n}(e,t)||function(e,t){if(!e)return;if("string"==typeof e)return d(e,t);var n=Object.prototype.toString.call(e).slice(8,-1);"Object"===n&&e.constructor&&(n=e.constructor.name);if("Map"===n||"Set"===n)return Array.from(e);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return d(e,t)}(e,t)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function d(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,r=new Array(t);n<t;n++)r[n]=e[n];return r}function y(e,t,n,r,a,o,i){try{var l=e[o](i),u=l.value}catch(e){return void n(e)}l.done?t(u):Promise.resolve(u).then(r,a)}function p(e){return function(){var t=this,n=arguments;return new Promise((function(r,a){var o=e.apply(t,n);function i(e){y(o,r,a,i,l,"next",e)}function l(e){y(o,r,a,i,l,"throw",e)}i(void 0)}))}}function g(){return(g=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e}).apply(this,arguments)}function h(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}const b=Object(o.a)(e=>({root:{textAlign:"center",padding:10,overflow:"scroll",display:"block","&::-webkit-scrollbar":{display:"none"}},btn:{width:"60%",margin:10,textTransform:"none"},card:{width:"90%",marginLeft:"auto",marginRight:"auto",marginTop:15,marginBottom:15,padding:10,backgroundColor:"#fdfdfd"},field:{margin:10,width:"40%",minWidth:200,"-ms-user-select":"text","-moz-user-select":"-moz-text","-webkit-user-select":"text","-khtml-user-select":"text","user-select":"text"},titleRoot:{padding:5,color:e.overrides.MuiButton.contained.backgroundColor},image:{maxWidth:"90%",maxHeight:"50%",margin:"auto",width:"auto"}})),v=function(e){const t=b(),n=e.src,r=h(e,["src"]);return a.a.createElement(f.a,g({className:t.image,component:"img",src:n},r))},w=function(e){var t=this;const n=b(),r=e.funName,o=e.text,l=(e.position,e.args),u=e.dialog,c=e.clickAfter,s=h(e,["funName","text","position","args","dialog","clickAfter"]);return a.a.createElement(i.a,g({variant:"contained",color:"primary",onClick:p(regeneratorRuntime.mark((function e(){var n,a;return regeneratorRuntime.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,$flex[r].apply(t,l);case 2:n=e.sent,console.log(n),c&&null!=n&&c(n),u&&(a="",a="object"==typeof n||"array"==typeof n?JSON.stringify(n):String(n),$flex.Dialog(o,a,{basic:"확인"},!0,!0));case 6:case"end":return e.stop()}}),e)}))),className:n.btn},s),o)},E=function(e){const t=b(),n=Object.assign({},e);return a.a.createElement(c.a,g({variant:"outlined",size:"small",className:t.field},n))},x=function(e){const t=b(),n=e.children,r=e.title,o=h(e,["children","title"]);return a.a.createElement(l.a,g({className:t.card},o),a.a.createElement(u.a,{classes:{root:t.titleRoot},titleTypographyProps:{variant:"h5"},title:r}),a.a.createElement(s.a,{style:{margin:5}}),n)},D=function(e){const t=b(),n=e.children,r=h(e,["children"]),o=m(a.a.useState(window.outerHeight),2),i=o[0],l=o[1];return a.a.useEffect(()=>{window.addEventListener("resize",()=>{l(window.outerHeight)})}),a.a.createElement("div",g({className:t.root,style:{height:i-76}},r),n)}},277:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return c}));n(140),n(141),n(84),n(149),n(142),n(143),n(48);var r=n(0),a=n.n(r),o=n(145);function i(e,t){return function(e){if(Array.isArray(e))return e}(e)||function(e,t){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(e)))return;var n=[],r=!0,a=!1,o=void 0;try{for(var i,l=e[Symbol.iterator]();!(r=(i=l.next()).done)&&(n.push(i.value),!t||n.length!==t);r=!0);}catch(e){a=!0,o=e}finally{try{r||null==l.return||l.return()}finally{if(a)throw o}}return n}(e,t)||function(e,t){if(!e)return;if("string"==typeof e)return l(e,t);var n=Object.prototype.toString.call(e).slice(8,-1);"Object"===n&&e.constructor&&(n=e.constructor.name);if("Map"===n||"Set"===n)return Array.from(e);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return l(e,t)}(e,t)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function l(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,r=new Array(t);n<t;n++)r[n]=e[n];return r}const u=[{name:"Set Data In Local",fun:"LocalRepository"},{name:"Get Data In Local",fun:"LocalRepository"},{name:"Delete Data In Local",fun:"LocalRepository"},{name:"FileDownload From Web",fun:"FileDownload"}];function c(){const e=e=>{const t=e.position,n=u[t],r=i(a.a.useState([]),2),l=r[0],c=r[1],s=(e,t)=>{l[e]=t,c(l)};switch(t){case 0:return a.a.useEffect(()=>{s(0,0),s(1,"Data Key"),s(2,"Data Value")}),a.a.createElement(o.b,{title:"Local Data Set Test"},a.a.createElement(o.d,{label:"Data Key",defaultValue:"Data Key",onChange:e=>{s(1,e.target.value)}}),a.a.createElement(o.d,{label:"Data Value",defaultValue:"Data Value",onChange:e=>{s(2,e.target.value)}}),a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0,args:l}));case 1:return a.a.useEffect(()=>{s(0,1),s(1,"Data Key")}),a.a.createElement(o.b,{title:"Local Data Get Test"},a.a.createElement(o.d,{label:"Data Key",defaultValue:"Data Key",onChange:e=>{s(1,e.target.value)}}),a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0,args:l}));case 2:return a.a.useEffect(()=>{s(0,2),s(1,"Data Key")}),a.a.createElement(o.b,{title:"Local Data Delete Test"},a.a.createElement(o.d,{label:"Data Key",defaultValue:"Data Key",onChange:e=>{s(1,e.target.value)}}),a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0,args:l}));case 3:return a.a.useEffect(()=>{s(0,"http://www.africau.edu/images/default/sample.pdf")}),a.a.createElement(o.b,{title:"FileDownload Test"},a.a.createElement(o.d,{label:"FileUrl",defaultValue:"http://www.africau.edu/images/default/sample.pdf",onChange:e=>{s(0,e.target.value)}}),a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,args:l}));default:return a.a.createElement("div",null)}};return a.a.createElement(o.e,null,u.map((t,n)=>a.a.createElement(e,{position:n,key:n})))}}}]);