(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d2306bf"],{ebe1:function(t,e,l){"use strict";l.r(e);var n=function(){var t=this,e=t.$createElement,l=t._self._c||e;return l("div",{staticStyle:{display:"inline-block"}},[l("label",{staticClass:"radio-label"},[t._v("Book Type: ")]),t._v(" "),l("el-select",{staticStyle:{width:"120px"},model:{value:t.bookType,callback:function(e){t.bookType=e},expression:"bookType"}},t._l(t.options,(function(t){return l("el-option",{key:t,attrs:{label:t,value:t}})})),1)],1)},o=[],i={props:{value:{type:String,default:"xlsx"}},data:function(){return{options:["xlsx","csv","txt"]}},computed:{bookType:{get:function(){return this.value},set:function(t){this.$emit("input",t)}}}},s=i,a=l("2877"),u=Object(a["a"])(s,n,o,!1,null,null,null);e["default"]=u.exports}}]);