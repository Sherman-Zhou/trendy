(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-5a112539"],{"09f4":function(t,e,n){"use strict";n.d(e,"a",(function(){return o})),Math.easeInOutQuad=function(t,e,n,a){return t/=a/2,t<1?n/2*t*t+e:(t--,-n/2*(t*(t-2)-1)+e)};var a=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(t){window.setTimeout(t,1e3/60)}}();function r(t){document.documentElement.scrollTop=t,document.body.parentNode.scrollTop=t,document.body.scrollTop=t}function i(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function o(t,e,n){var o=i(),c=t-o,l=20,u=0;e="undefined"===typeof e?500:e;var s=function t(){u+=l;var i=Math.easeInOutQuad(u,o,c,e);r(i),u<e?a(t):n&&"function"===typeof n&&n()};s()}},"13a9":function(t,e,n){"use strict";n.d(e,"g",(function(){return r})),n.d(e,"k",(function(){return i})),n.d(e,"d",(function(){return o})),n.d(e,"h",(function(){return c})),n.d(e,"e",(function(){return l})),n.d(e,"a",(function(){return u})),n.d(e,"c",(function(){return s})),n.d(e,"f",(function(){return d})),n.d(e,"b",(function(){return p})),n.d(e,"j",(function(){return m})),n.d(e,"i",(function(){return h}));var a=n("b775");function r(t){return Object(a["a"])({url:"/monitor/vehicles/search",method:"get",params:t})}function i(t){return Object(a["a"])({url:"/internal/equipment/lock",method:"post",data:t})}function o(t){return Object(a["a"])({url:"/monitor/vehicle/current-state/".concat(t),method:"get"})}function c(t){return Object(a["a"])({url:"/monitor/trajectories",method:"get",params:t})}function l(t){return Object(a["a"])({url:"/vehicle-maintenances",method:"get",params:t})}function u(t){return Object(a["a"])({url:"/vehicle-maintenances",method:"post",data:t})}function s(t){return Object(a["a"])({url:"/vehicle-maintenances",method:"put",data:t})}function d(t,e){return Object(a["a"])({url:"/vehicle-maintenances/".concat(t),method:"get",params:e})}function p(t){return Object(a["a"])({url:"/vehicle-maintenances/".concat(t),method:"delete"})}function m(t,e){return Object(a["a"])({url:"monitor/trajectoryIds/".concat(t),method:"get",params:e})}function h(t){return Object(a["a"])({url:"/monitor/trajectory-details",method:"get",params:t})}},2486:function(t,e,n){"use strict";var a=n("70ef"),r=n.n(a);r.a},"61b4":function(t,e,n){"use strict";n.r(e);var a=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{attrs:{id:"trajectoryId"}},[n("div",{staticClass:"status_title"},[t._v(t._s(t.$t("table.OneWayRecording")))]),t._v(" "),n("div",{staticClass:"filter-container"},[n("div",{staticClass:"filter-left"},[n("el-date-picker",{attrs:{type:"datetimerange","range-separator":"-","start-placeholder":t.$t("table.StartingTime"),"end-placeholder":t.$t("table.EndingTime")},on:{change:t.changeDate},model:{value:t.value1,callback:function(e){t.value1=e},expression:"value1"}}),t._v(" "),n("el-input",{attrs:{placeholder:t.$t("table.EnterRecordingIDkeywords")},model:{value:t.query.trajectoryId,callback:function(e){t.$set(t.query,"trajectoryId",e)},expression:"query.trajectoryId"}}),t._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:t.search}},[t._v(t._s(t.$t("table.search")))]),t._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:t.search}},[t._v(t._s(t.$t("table.Refresh")))])],1)]),t._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticStyle:{width:"100%"},attrs:{data:t.list,stripe:"",border:""}},[n("el-table-column",{attrs:{type:"index",label:"#",align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"trajectoryId",label:t.$t("table.RecordID"),align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"startTime",label:t.$t("table.EngineStarting"),align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"endTime",label:t.$t("table.EngineEnding"),align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"mileage",label:t.$t("table.Mileage")+"(Km)",align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"overspeedNum",label:t.$t("table.OverspeedTimes"),align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"status",label:t.$t("table.status"),align:"center"}}),t._v(" "),n("el-table-column",{attrs:{prop:"remark",label:t.$t("table.Operation"),width:"130",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[n("el-button",{attrs:{size:"mini",type:"primary"},on:{click:function(n){return t.lookMap(e.row)}}},[n("span",[t._v(t._s(t.$t("table.CheckMap")))])])]}}])})],1),t._v(" "),n("el-dialog",{attrs:{visible:t.dialogVisible,"close-on-click-modal":!1,"destroy-on-close":!0,width:"50%"},on:{close:t.close}},[n("div",{staticStyle:{width:"100%",height:"550px"},attrs:{id:"map-record"}})])],1)},r=[],i=(n("ac6a"),n("96cf"),n("3b8d")),o=(n("333d"),n("67e1")),c=n("13a9"),l={components:{},props:{vehicleId:{default:function(){return""}}},data:function(){return{value1:"",query:{startDate:"",endDate:"",trajectoryId:"",trajectoryIds:"",vehicleId:""},loading:!1,list:[],pages:{total:0,page:0,limit:20},dialogVisible:!1,palce:null,driverlist:[],center:{}}},mounted:function(){},created:function(){this.vehicleId&&(this.query.vehicleId=this.vehicleId,this.fetchData())},methods:{search:function(){this.fetchData()},changeSize:function(t){this.query.page=t.page,this.query.size=t.limit},changeDate:function(t){t?(this.query.startDate=Object(o["a"])(t[0]),this.query.endDate=Object(o["a"])(t[1])):(this.query.startDate="",this.query.endDate="")},fetchData:function(){var t=Object(i["a"])(regeneratorRuntime.mark((function t(){var e,n;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return this.loading=!0,t.next=3,Object(c["h"])(this.query);case 3:e=t.sent,n=e.data,n.forEach((function(t){t.startTime=Object(o["a"])(t.startTime),t.endTime=Object(o["a"])(t.endTime)})),this.list=n,this.loading=!1;case 8:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),lookMap:function(){var t=Object(i["a"])(regeneratorRuntime.mark((function t(e){var n,a,r=this;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return this.dialogVisible=!0,this.query.trajectoryId=e.trajectoryId,t.next=4,Object(c["i"])(this.query);case 4:n=t.sent,a=n.data,this.driverlist=a,this.center=a[0],setTimeout((function(){r.initMaps()}),10);case 9:case"end":return t.stop()}}),t,this)})));function e(e){return t.apply(this,arguments)}return e}(),close:function(){this.dialogVisible=!1},initMaps:function(){var t=this.center,e=new google.maps.Map(document.getElementById("map-record"),{center:t,zoom:14,mapTypeId:google.maps.MapTypeId.ROADMAP,mapTypeControl:!1,panControl:!1,zoomControl:!0,streetViewControl:!1});this.drawDriverRoute(e)},drawDriverRoute:function(t){var e=this.driverlist,n=new google.maps.Polyline({path:e,geodesic:!0,strokeColor:"#0000FF",strokeOpacity:.8,strokeWeight:10});n.setMap(t)}}},u=l,s=(n("2486"),n("2877")),d=Object(s["a"])(u,a,r,!1,null,"1c847725",null);e["default"]=d.exports},"67e1":function(t,e,n){"use strict";n.d(e,"a",(function(){return a}));n("f576");function a(t){var e=new Date(t),n=e.getFullYear(),a=(e.getMonth()+1+"").padStart(2,"0"),r=(e.getDate()+"").padStart(2,"0"),i=(e.getHours()+"").padStart(2,"0"),o=(e.getMinutes()+"").padStart(2,"0"),c=(e.getSeconds()+"").padStart(2,"0");return"".concat(n,"-").concat(a,"-").concat(r," ").concat(i,":").concat(o,":").concat(c)}},"70ef":function(t,e,n){}}]);