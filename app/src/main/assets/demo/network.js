(window.webpackJsonp=window.webpackJsonp||[]).push([[7],{145:function(t,e,n){"use strict";n.d(e,"c",(function(){return v})),n.d(e,"a",(function(){return w})),n.d(e,"d",(function(){return x})),n.d(e,"b",(function(){return E})),n.d(e,"e",(function(){return k}));n(140),n(155),n(141),n(156),n(84),n(142),n(85),n(143),n(86),n(48),n(157);var r=n(0),o=n.n(r),a=n(255),i=n(215),c=n(257),u=n(260),l=n(281),s=n(275),f=n(274);function m(t,e){return function(t){if(Array.isArray(t))return t}(t)||function(t,e){if("undefined"==typeof Symbol||!(Symbol.iterator in Object(t)))return;var n=[],r=!0,o=!1,a=void 0;try{for(var i,c=t[Symbol.iterator]();!(r=(i=c.next()).done)&&(n.push(i.value),!e||n.length!==e);r=!0);}catch(t){o=!0,a=t}finally{try{r||null==c.return||c.return()}finally{if(o)throw a}}return n}(t,e)||function(t,e){if(!t)return;if("string"==typeof t)return d(t,e);var n=Object.prototype.toString.call(t).slice(8,-1);"Object"===n&&t.constructor&&(n=t.constructor.name);if("Map"===n||"Set"===n)return Array.from(t);if("Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n))return d(t,e)}(t,e)||function(){throw new TypeError("Invalid attempt to destructure non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function d(t,e){(null==e||e>t.length)&&(e=t.length);for(var n=0,r=new Array(e);n<e;n++)r[n]=t[n];return r}function p(t,e,n,r,o,a,i){try{var c=t[a](i),u=c.value}catch(t){return void n(t)}c.done?e(u):Promise.resolve(u).then(r,o)}function g(t){return function(){var e=this,n=arguments;return new Promise((function(r,o){var a=t.apply(e,n);function i(t){p(a,r,o,i,c,"next",t)}function c(t){p(a,r,o,i,c,"throw",t)}i(void 0)}))}}function h(){return(h=Object.assign||function(t){for(var e=1;e<arguments.length;e++){var n=arguments[e];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(t[r]=n[r])}return t}).apply(this,arguments)}function y(t,e){if(null==t)return{};var n,r,o=function(t,e){if(null==t)return{};var n,r,o={},a=Object.keys(t);for(r=0;r<a.length;r++)n=a[r],e.indexOf(n)>=0||(o[n]=t[n]);return o}(t,e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(t);for(r=0;r<a.length;r++)n=a[r],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(o[n]=t[n])}return o}const b=Object(a.a)(t=>({root:{textAlign:"center",padding:10,overflow:"scroll",display:"block","&::-webkit-scrollbar":{display:"none"}},btn:{width:"60%",margin:10,textTransform:"none"},card:{width:"90%",marginLeft:"auto",marginRight:"auto",marginTop:15,marginBottom:15,padding:10,backgroundColor:"#fdfdfd"},field:{margin:10,width:"40%",minWidth:200,"-ms-user-select":"text","-moz-user-select":"-moz-text","-webkit-user-select":"text","-khtml-user-select":"text","user-select":"text"},titleRoot:{padding:5,color:t.overrides.MuiButton.contained.backgroundColor},image:{maxWidth:"90%",maxHeight:"50%",margin:"auto",width:"auto"}})),v=function(t){const e=b(),n=t.src,r=y(t,["src"]);return o.a.createElement(f.a,h({className:e.image,component:"img",src:n},r))},w=function(t){var e=this;const n=b(),r=t.funName,a=t.text,c=(t.position,t.args),u=t.dialog,l=t.clickAfter,s=y(t,["funName","text","position","args","dialog","clickAfter"]);return o.a.createElement(i.a,h({variant:"contained",color:"primary",onClick:g(regeneratorRuntime.mark((function t(){var n,o;return regeneratorRuntime.wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return t.next=2,$flex[r].apply(e,c);case 2:n=t.sent,console.log(n),l&&null!=n&&l(n),u&&(o="","object"==typeof n?Object.keys(n).forEach(t=>{o.concat(t).concat(" : ").concat(n[t]).concat("/n")}):o=String(n),$flex.Dialog(a,o,{basic:"확인"},!0,!0));case 6:case"end":return t.stop()}}),t)}))),className:n.btn},s),a)},x=function(t){const e=b(),n=Object.assign({},t);return o.a.createElement(l.a,h({variant:"outlined",size:"small",className:e.field},n))},E=function(t){const e=b(),n=t.children,r=t.title,a=y(t,["children","title"]);return o.a.createElement(c.a,h({className:e.card},a),o.a.createElement(u.a,{classes:{root:e.titleRoot},titleTypographyProps:{variant:"h5"},title:r}),o.a.createElement(s.a,{style:{margin:5}}),n)},k=function(t){const e=b(),n=t.children,r=y(t,["children"]),a=m(o.a.useState(window.outerHeight),2),i=a[0],c=a[1];return o.a.useEffect(()=>{window.addEventListener("resize",()=>{c(window.outerHeight)})}),o.a.createElement("div",h({className:e.root,style:{height:i-76}},r),n)}},276:function(t,e,n){"use strict";n.r(e),n.d(e,"default",(function(){return c}));n(149);var r=n(0),o=n.n(r),a=n(145);const i=[{name:"Network",fun:"Network"},{name:"Location",fun:"Location"}];function c(){const t=t=>{const e=t.position,n=i[e];switch(e){case 0:return o.a.createElement(a.b,{title:"Network Test"},o.a.createElement(a.a,{funName:n.fun,text:n.name,position:e,dialog:!0}));case 1:return o.a.createElement(a.b,{title:"Location Test"},o.a.createElement(a.a,{funName:n.fun,text:n.name,position:e,dialog:!0}));default:return o.a.createElement("div",null)}};return o.a.createElement(a.e,null,i.map((e,n)=>o.a.createElement(t,{position:n,key:n})))}}}]);
