(window.webpackJsonp=window.webpackJsonp||[]).push([[1],{145:function(e,t,n){"use strict";n.d(t,"c",(function(){return v})),n.d(t,"a",(function(){return w})),n.d(t,"d",(function(){return x})),n.d(t,"b",(function(){return E})),n.d(t,"e",(function(){return k}));n(140),n(155),n(141),n(156),n(84),n(142),n(85),n(143),n(86),n(48),n(157);var r=n(0),a=n.n(r),o=n(254),i=n(215),c=n(256),u=n(259),l=n(280),s=n(274),f=n(273);function m(e,t){return function(e){if(Array.isArray(e))return e}(e)||function(e,t){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(e)))return;var n=[],r=!0,a=!1,o=void 0;try{for(var i,c=e[Symbol.iterator]();!(r=(i=c.next()).done)&&(n.push(i.value),!t||n.length!==t);r=!0);}catch(e){a=!0,o=e}finally{try{r||null==c.return||c.return()}finally{if(a)throw o}}return n}(e,t)||function(e,t){if(!e)return;if("string"==typeof e)return d(e,t);var n=Object.prototype.toString.call(e).slice(8,-1);"Object"===n&&e.constructor&&(n=e.constructor.name);if("Map"===n||"Set"===n)return Array.from(e);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return d(e,t)}(e,t)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function d(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,r=new Array(t);n<t;n++)r[n]=e[n];return r}function p(e,t,n,r,a,o,i){try{var c=e[o](i),u=c.value}catch(e){return void n(e)}c.done?t(u):Promise.resolve(u).then(r,a)}function g(e){return function(){var t=this,n=arguments;return new Promise((function(r,a){var o=e.apply(t,n);function i(e){p(o,r,a,i,c,"next",e)}function c(e){p(o,r,a,i,c,"throw",e)}i(void 0)}))}}function h(){return(h=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e}).apply(this,arguments)}function y(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}const b=Object(o.a)(e=>({root:{textAlign:"center",padding:10,overflow:"scroll",display:"block","&::-webkit-scrollbar":{display:"none"}},btn:{width:"60%",margin:10,textTransform:"none"},card:{width:"90%",marginLeft:"auto",marginRight:"auto",marginTop:15,marginBottom:15,padding:10,backgroundColor:"#fdfdfd"},field:{margin:10,width:"40%",minWidth:200,"-ms-user-select":"text","-moz-user-select":"-moz-text","-webkit-user-select":"text","-khtml-user-select":"text","user-select":"text"},titleRoot:{padding:5,color:e.overrides.MuiButton.contained.backgroundColor},image:{maxWidth:"90%",maxHeight:"50%",margin:"auto",width:"auto"}})),v=function(e){const t=b(),n=e.src,r=y(e,["src"]);return a.a.createElement(f.a,h({className:t.image,component:"img",src:n},r))},w=function(e){var t=this;const n=b(),r=e.funName,o=e.text,c=(e.position,e.args),u=e.dialog,l=e.clickAfter,s=y(e,["funName","text","position","args","dialog","clickAfter"]);return a.a.createElement(i.a,h({variant:"contained",color:"primary",onClick:g(regeneratorRuntime.mark((function e(){var n,a;return regeneratorRuntime.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,$flex[r].apply(t,c);case 2:n=e.sent,console.log(n),l&&null!=n&&l(n),u&&(a="","object"==typeof n?Object.keys(n).forEach(e=>{a=a.concat(e).concat(" : ").concat(n[e]).concat("\n")}):a=String(n),$flex.Dialog(o,a,{basic:"확인"},!0,!0));case 6:case"end":return e.stop()}}),e)}))),className:n.btn},s),o)},x=function(e){const t=b(),n=Object.assign({},e);return a.a.createElement(l.a,h({variant:"outlined",size:"small",className:t.field},n))},E=function(e){const t=b(),n=e.children,r=e.title,o=y(e,["children","title"]);return a.a.createElement(c.a,h({className:t.card},o),a.a.createElement(u.a,{classes:{root:t.titleRoot},titleTypographyProps:{variant:"h5"},title:r}),a.a.createElement(s.a,{style:{margin:5}}),n)},k=function(e){const t=b(),n=e.children,r=y(e,["children"]),o=m(a.a.useState(window.outerHeight),2),i=o[0],c=o[1];return a.a.useEffect(()=>{window.addEventListener("resize",()=>{c(window.outerHeight)})}),a.a.createElement("div",h({className:t.root,style:{height:i-76}},r),n)}},277:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return c}));n(149);var r=n(0),a=n.n(r),o=n(145);const i=[{name:"Rooting(JailBreak) Check",fun:"RootingCheck"},{name:"Authentication",fun:"Authentication"},{name:"Get UniqueAppID",fun:"UniqueAppID"},{name:"Get UniqueDeviceID",fun:"UniqueDeviceID"}];function c(){const e=e=>{const t=e.position,n=i[t];switch(t){case 0:return a.a.createElement(o.b,{title:"Rooting(JailBreak) Check Test"},a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));case 0:return a.a.createElement(o.b,{title:"Authentication Test"},a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));case 2:return a.a.createElement(o.b,{title:"Get Unique App ID Test"},a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));case 3:return a.a.createElement(o.b,{title:"Get Unique Device ID Test"},a.a.createElement(o.a,{funName:n.fun,text:n.name,position:t,dialog:!0}));default:return a.a.createElement("div",null)}};return a.a.createElement(o.e,null,i.map((t,n)=>a.a.createElement(e,{position:n,key:n})))}}}]);