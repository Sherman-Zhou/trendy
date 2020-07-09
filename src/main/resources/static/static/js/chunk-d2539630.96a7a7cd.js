(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-d2539630"],{"13a9":function(t,e,a){"use strict";a.d(e,"g",(function(){return n})),a.d(e,"k",(function(){return i})),a.d(e,"d",(function(){return c})),a.d(e,"h",(function(){return o})),a.d(e,"e",(function(){return s})),a.d(e,"a",(function(){return l})),a.d(e,"c",(function(){return u})),a.d(e,"f",(function(){return d})),a.d(e,"b",(function(){return h})),a.d(e,"j",(function(){return p})),a.d(e,"i",(function(){return v}));var r=a("b775");function n(t){return Object(r["a"])({url:"/monitor/vehicles/search",method:"get",params:t})}function i(t){return Object(r["a"])({url:"/internal/equipment/lock",method:"post",data:t})}function c(t){return Object(r["a"])({url:"/monitor/vehicle/current-state/".concat(t),method:"get"})}function o(t){return Object(r["a"])({url:"/monitor/trajectories",method:"get",params:t})}function s(t){return Object(r["a"])({url:"/vehicle-maintenances",method:"get",params:t})}function l(t){return Object(r["a"])({url:"/vehicle-maintenances",method:"post",data:t})}function u(t){return Object(r["a"])({url:"/vehicle-maintenances",method:"put",data:t})}function d(t,e){return Object(r["a"])({url:"/vehicle-maintenances/".concat(t),method:"get",params:e})}function h(t){return Object(r["a"])({url:"/vehicle-maintenances/".concat(t),method:"delete"})}function p(t,e){return Object(r["a"])({url:"monitor/trajectoryIds/".concat(t),method:"get",params:e})}function v(t){return Object(r["a"])({url:"/monitor/trajectory-details",method:"get",params:t})}},"67e1":function(t,e,a){"use strict";a.d(e,"a",(function(){return r}));a("f576");function r(t){var e=new Date(t),a=e.getFullYear(),r=(e.getMonth()+1+"").padStart(2,"0"),n=(e.getDate()+"").padStart(2,"0"),i=(e.getHours()+"").padStart(2,"0"),c=(e.getMinutes()+"").padStart(2,"0"),o=(e.getSeconds()+"").padStart(2,"0");return"".concat(a,"-").concat(r,"-").concat(n," ").concat(i,":").concat(c,":").concat(o)}},"7e63":function(t,e,a){"use strict";a.r(e);var r=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",[a("div",{staticClass:"status_title"},[t._v(t._s(t.$t("table.PlayRecording")))]),t._v(" "),a("div",{staticClass:"filter-container"},[a("div",{staticClass:"filter-left",attrs:{id:"recording-id"}},[a("el-radio",{attrs:{label:"1"},model:{value:t.radio1,callback:function(e){t.radio1=e},expression:"radio1"}},[t._v(t._s(t.$t("table.RecordingID"))+":")]),t._v(" "),a("el-select",{attrs:{filterable:"",clearable:"",placeholder:t.$t("table.RecordID")},model:{value:t.query.trajectoryId,callback:function(e){t.$set(t.query,"trajectoryId",e)},expression:"query.trajectoryId"}},t._l(t.trajectoryIds,(function(t,e){return a("el-option",{key:e,attrs:{label:t,value:t}})})),1)],1)]),t._v(" "),a("div",{staticClass:"filter-container"},[a("div",{staticClass:"filter-left accordingTime"},[a("div",[a("el-radio",{attrs:{label:"2"},model:{value:t.radio1,callback:function(e){t.radio1=e},expression:"radio1"}},[t._v(t._s(t.$t("table.AccordingTime"))+":")]),t._v(" "),a("el-date-picker",{attrs:{type:"datetimerange","range-separator":"-","start-placeholder":t.$t("table.StartingTime"),"end-placeholder":t.$t("table.EndingTime")},on:{change:t.changeDate},model:{value:t.value1,callback:function(e){t.value1=e},expression:"value1"}}),t._v(" "),a("el-button",{staticClass:"checkRecording",attrs:{type:"primary"},on:{click:t.search}},[t._v(t._s(t.$t("table.CheckRecording")))])],1),t._v(" "),a("el-checkbox",[t._v(t._s(t.$t("table.OverspeedPoint")))])],1)]),t._v(" "),a("div",{staticStyle:{width:"90%",height:"550px"},attrs:{id:"map"}}),t._v(" "),a("div",{staticClass:"filter-container"},[a("div",{staticClass:"filter-left speed"},[a("div",[t._v(t._s(t.$t("table.LimitedSpeed"))+":"+t._s(t.actualSpeed)+"Km/h")]),t._v(" "),a("div",[t._v(t._s(t.$t("table.ExactlySpeed"))+":"+t._s(t.actualSpeed)+"Km/h")]),t._v(" "),a("div",[t._v(t._s(t.$t("table.TotalMileageRecords"))+":"+t._s(t.totalMileage)+"Km")])])])])},n=[],i=(a("ac6a"),a("96cf"),a("3b8d")),c=a("67e1"),o=a("13a9"),s={props:{vehicleId:{default:function(){return""}}},data:function(){return{value1:"",radio1:"1",query:{startDate:"",endDate:"",trajectoryId:"",trajectoryIds:"",vehicleId:""},loading:!1,trajectoryIds:[],driverlist:[],center:{lat:35.667373,lng:139.89358},place:null,request:null,totalMileage:"",actualSpeed:""}},created:function(){this.vehicleId&&(this.query.vehicleId=this.vehicleId,this.getTrajectoryIds())},mounted:function(){this.initMap()},methods:{changeDate:function(t){t?(this.query.startDate=Object(c["a"])(t[0]),this.query.endDate=Object(c["a"])(t[1])):(this.query.startDate="",this.query.endDate="")},getTrajectoryIds:function(){var t=Object(i["a"])(regeneratorRuntime.mark((function t(){var e,a;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:return t.next=2,Object(o["j"])(this.vehicleId);case 2:e=t.sent,a=e.data,this.trajectoryIds=a;case 5:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),search:function(){var t=Object(i["a"])(regeneratorRuntime.mark((function t(){var e,a,r,n,i,c,s;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(e=this,this.vehicleId){t.next=3;break}return t.abrupt("return");case 3:return t.next=5,Object(o["i"])(this.query);case 5:a=t.sent,r=a.data,this.center=r[0],this.driverlist=r,n=0,r.forEach((function(t){n+=t.actualSpeed})),this.actualSpeed=(n/r.length).toFixed(2),i=new Date(r[0].receivedTime).getTime(),c=new Date(r[r.length-1].receivedTime).getTime(),s=(c-i)/1e3/60/60,this.totalMileage=(s*this.actualSpeed).toFixed(2),setTimeout((function(){e.initMap()}),10);case 17:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),initMap:function(){var t=this.center,e=new google.maps.Map(document.getElementById("map"),{center:t,zoom:15,mapTypeId:google.maps.MapTypeId.ROADMAP,mapTypeControl:!1,panControl:!1,zoomControl:!0,streetViewControl:!1});this.drawDriverRoute(e)},drawDriverRoute:function(t){for(var e=this.driverlist,a=new google.maps.Polyline({path:e,geodesic:!0,strokeColor:"#0000FF",strokeOpacity:.8,strokeWeight:8}),r=0;r<e.length;r++)new google.maps.Marker({position:e[r],map:t});a.setMap(t)}}},l=s,u=(a("87bb"),a("2877")),d=Object(u["a"])(l,r,n,!1,null,null,null);e["default"]=d.exports},"87bb":function(t,e,a){"use strict";var r=a("e13c"),n=a.n(r);n.a},e13c:function(t,e,a){}}]);