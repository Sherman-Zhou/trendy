(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-4d8a2c4a","chunk-047a65e1"],{"09f4":function(e,t,n){"use strict";n.d(t,"a",(function(){return l})),Math.easeInOutQuad=function(e,t,n,a){return e/=a/2,e<1?n/2*e*e+t:(e--,-n/2*(e*(e-2)-1)+t)};var a=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(e){window.setTimeout(e,1e3/60)}}();function i(e){document.documentElement.scrollTop=e,document.body.parentNode.scrollTop=e,document.body.scrollTop=e}function r(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function l(e,t,n){var l=r(),s=e-l,o=20,c=0;t="undefined"===typeof t?500:t;var u=function e(){c+=o;var r=Math.easeInOutQuad(c,l,s,t);i(r),c<t?a(e):n&&"function"===typeof n&&n()};u()}},1832:function(e,t,n){"use strict";n.d(t,"a",(function(){return i})),n.d(t,"b",(function(){return r})),n.d(t,"d",(function(){return l})),n.d(t,"c",(function(){return s})),n.d(t,"e",(function(){return o}));var a=n("b775");function i(e){return Object(a["a"])({url:"/dict-types/VEHICLE-BRAND/".concat(e),method:"get"})}function r(e){return Object(a["a"])({url:"/vehicles",method:"get",params:e})}function l(e){return Object(a["a"])({url:"/vehicles/sync",method:"get",params:e})}function s(e){return Object(a["a"])({url:"/vehicles/".concat(e),method:"get"})}function o(e){return Object(a["a"])({url:"/vehicles",method:"put",data:e})}},2605:function(e,t,n){},"635f":function(e,t,n){"use strict";var a=n("6c9c"),i=n.n(a);i.a},"6c9c":function(e,t,n){},"6f4b":function(e,t,n){"use strict";var a=n("2605"),i=n.n(a);i.a},"96af":function(e,t,n){"use strict";n.r(t);var a=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"main"},[n("div",{staticClass:"menu-tree"},[n("el-tree",{attrs:{"default-expand-all":"",data:e.data,props:e.defaultProps},on:{"node-click":e.handleNodeClick}})],1),e._v(" "),n("div",{staticClass:"app-container"},[n("div",{staticClass:"filter-container"},[n("div",{staticClass:"filter-left"},[n("div",{staticClass:"btns"},[n("el-button",{attrs:{type:"primary",plain:""},on:{click:e.search}},[e._v(e._s(e.$t("table.UpdateCars")))]),e._v(" "),n("el-upload",{staticClass:"upload-demo",attrs:{action:e.url,"on-success":e.uploadSuccess,"on-error":e.uploadError,"on-remove":e.handleRemove,headers:e.headers,"show-file-list":e.flag}},[n("el-button",{attrs:{type:"primary",plain:""},on:{click:e.search}},[e._v(e._s(e.$t("table.SettingsInformation")))])],1)],1)])]),e._v(" "),n("div",{staticClass:"filter-container"},[n("div",{staticClass:"filter-left first_left"},[n("div",[n("span",[e._v(e._s(e.$t("table.Makes")))]),e._v(" "),n("el-select",{attrs:{placeholder:e.$t("table.All"),clearable:!0},model:{value:e.query.brand,callback:function(t){e.$set(e.query,"brand",t)},expression:"query.brand"}},e._l(e.carsList,(function(e,t){return n("el-option",{key:t,attrs:{label:e.label,value:e.value}})})),1)],1),e._v(" "),n("div",[n("span",[e._v(e._s(e.$t("table.Licenseplate")))]),e._v(" "),n("el-input",{nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.search(t)}},model:{value:e.query.licensePlateNumber,callback:function(t){e.$set(e.query,"licensePlateNumber",t)},expression:"query.licensePlateNumber"}})],1),e._v(" "),n("div",[n("span",[e._v(e._s(e.$t("table.OnlineStatus")))]),e._v(" "),n("el-select",{attrs:{placeholder:"",clearable:!0},model:{value:e.query.isOnline,callback:function(t){e.$set(e.query,"isOnline",t)},expression:"query.isOnline"}},e._l(e.OnlineStatus,(function(e,t){return n("el-option",{key:t,attrs:{label:e.name,value:e.id}})})),1)],1),e._v(" "),n("div",[n("span",[e._v(e._s(e.$t("table.SettingsStatus")))]),e._v(" "),n("el-select",{attrs:{placeholder:"",clearable:!0},model:{value:e.query.isBounded,callback:function(t){e.$set(e.query,"isBounded",t)},expression:"query.isBounded"}},e._l(e.SettingsStatus,(function(e,t){return n("el-option",{key:t,attrs:{label:e.name,value:e.id}})})),1)],1)])]),e._v(" "),n("div",{staticClass:"filter-container"},[n("div",{staticClass:"filter-left second_left"},[n("div",[n("span",[e._v(e._s(e.$t("table.Nickname")))]),e._v(" "),n("el-input",{nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.search(t)}},model:{value:e.query.name,callback:function(t){e.$set(e.query,"name",t)},expression:"query.name"}})],1),e._v(" "),n("div",[n("span",[e._v(e._s(e.$t("table.FacilityID")))]),e._v(" "),n("el-input",{nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.search(t)}},model:{value:e.query.identifyNumber,callback:function(t){e.$set(e.query,"identifyNumber",t)},expression:"query.identifyNumber"}})],1),e._v(" "),n("div",[n("el-button",{attrs:{type:"primary"},on:{click:e.search}},[e._v(e._s(e.$t("table.search")))]),e._v(" "),n("el-button",{attrs:{type:"primary"},on:{click:e.getVehiclesSync}},[e._v(e._s(e.$t("table.Update")))])],1)])]),e._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticStyle:{width:"100%"},attrs:{data:e.list,stripe:"",border:""}},[n("el-table-column",{attrs:{type:"index",label:"#",width:"100",align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"divName",label:e.$t("login.City"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"orgName",label:e.$t("login.Station"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"name",label:e.$t("table.Nickname"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"prodYear",label:e.$t("table.Year"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"brand",label:e.$t("table.Makes"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"licensePlateNumber",label:e.$t("table.Licenseplate"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"isOnline",label:e.$t("table.OnlineStatus"),align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.isOnline?n("span",[e._v(e._s(e.$t("login.Online")))]):n("span",[e._v(e._s(e.$t("login.Offline")))])]}}])}),e._v(" "),n("el-table-column",{attrs:{prop:"bounded",label:e.$t("table.SettingsStatus"),align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.bounded?n("span",[e._v(e._s(e.$t("table.Settings")))]):n("span",[e._v(e._s(e.$t("table.Release")))])]}}])}),e._v(" "),n("el-table-column",{attrs:{prop:"identifyNumber",label:e.$t("table.FacilityID"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{label:e.$t("table.Operation"),align:"center",width:"300"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-button",{attrs:{plain:"",size:"mini",type:"primary"},on:{click:function(n){return e.bind(t.row)}}},[n("span",[e._v(e._s(e.$t("table.FacilitySettings")))])]),e._v(" "),n("el-button",{attrs:{plain:"",size:"mini",type:"primary"},on:{click:function(n){return e.connTesting(t.row)}}},[n("span",[e._v(e._s(e.$t("table.ConnectingTest")))])])]}}])})],1),e._v(" "),n("pagination",{attrs:{hidden:0===e.list.length,total:e.pages.total,page:e.pages.page,limit:e.pages.limit},on:{pagination:e.changeSize}}),e._v(" "),n("el-dialog",{attrs:{title:e.$t("table.FacilityID"),visible:e.dialogVisible,"close-on-click-modal":!1,"destroy-on-close":!0,width:"30%"},on:{close:e.close}},[n("el-form",{ref:"form",staticClass:"dialog-form-container",attrs:{model:e.form,"label-width":"100px"}},[n("el-form-item",{attrs:{label:e.$t("table.FacilityID")}},[n("el-select",{attrs:{filterable:"",placeholder:e.$t("table.FacilityID")},model:{value:e.form.equipmentId,callback:function(t){e.$set(e.form,"equipmentId",t)},expression:"form.equipmentId"}},e._l(e.allBound,(function(e){return n("el-option",{key:e.id,attrs:{label:e.identifyNumber,value:e.id}})})),1)],1)],1),e._v(" "),n("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{attrs:{plain:""},on:{click:e.close}},[e._v(e._s(e.$t("permission.cancel")))]),e._v(" "),n("el-button",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],attrs:{type:"primary",plain:""},on:{click:e.save}},[e._v(e._s(e.$t("permission.confirm")))])],1)],1),e._v(" "),n("error",{attrs:{visible:e.visible,errorForm:e.errorForm},on:{close:e.close}})],1)])},i=[],r=(n("28a5"),n("96cf"),n("3b8d")),l=n("db72"),s=n("2f62"),o=n("333d"),c=n("ed55"),u=n("b775");function d(e){return Object(u["a"])({url:"/binding/vehicle/search",method:"get",params:e})}function p(){return Object(u["a"])({url:"/binding/equipment/to-bound",method:"get"})}function f(e){return Object(u["a"])({url:"/binding",method:"post",data:e})}function m(){return Object(u["a"])({url:"/binding/user/divisions",method:"get"})}function b(e){return Object(u["a"])({url:"/binding/".concat(e,"/conn-testing"),method:"get"})}var v=n("1832"),h=n("4360"),g={name:"Role",components:{Pagination:o["a"],Error:c["default"]},data:function(){return{url:"http://localhost:8080/api/binding/upload",headers:{Authorization:"Bearer "+h["a"].getters.token},flag:!1,query:{divisionId:"",brand:"",licensePlateNumber:"",isOnline:"",isBounded:"",name:"",identifyNumber:"",prodYear:"",sort:"",page:0,size:20},carsList:[],querySync:{page:0,size:20,sort:""},pages:{total:0,page:0,limit:20},OnlineStatus:[{id:!0,name:this.$t("login.Online")},{id:!1,name:this.$t("login.Offline")}],SettingsStatus:[{id:!0,name:this.$t("table.Settings")},{id:!1,name:this.$t("table.Release")}],list:[],loading:!1,dialogData:{visible:!1,status:0,formData:{}},data:[],defaultProps:{children:"children",label:"name"},dialogVisible:!1,form:{vehicleId:"",equipmentId:""},allBound:[],errorForm:[],visible:!1}},computed:Object(l["a"])({},Object(s["b"])([]),{},Object(s["c"])({getters:function(e){return e.app.language}})),created:function(){this.fetchData(),this.getDivisions(),this.getCarsVehicles()},methods:{search:function(){this.query.page=0,this.fetchData()},changeSize:function(e){e.page>0&&(this.query.page=e.page-1),this.query.size=e.limit,this.fetchData()},handleNodeClick:function(e){console.log(e.id,"1111"),this.query.divisionId=e.id,this.fetchData()},handleRemove:function(e,t){console.log(e,t)},uploadSuccess:function(e,t){var n=this;e.successRowNum==e.totalRowsNum?this.$message.success("success"):(n.visible=!0,n.errorForm=e.errors),this.fetchData()},uploadError:function(e,t){this.$message.error("error")},fetchData:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(){var t=this;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:this.loading=!0,d(this.query).then((function(e){t.pages.total=e.data.total,t.list=e.data.items,t.loading=!1})).catch((function(e){t.loading=!1}));case 2:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),getCarsVehicles:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(){var t,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,Object(v["a"])(this.getters);case 2:t=e.sent,n=t.data,this.carsList=n;case 5:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),getDivisions:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(){var t=this;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:this.loading=!0,m().then((function(e){t.data=e.data,t.loading=!1})).catch((function(e){t.loading=!1}));case 2:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),getVehiclesSync:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(){var t=this;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:this.loading=!0,Object(v["d"])(this.querySync).then((function(e){t.pages.total=e.data.total,t.list=e.data.items,t.loading=!1})).catch((function(e){t.loading=!1}));case 2:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),edit:function(e){this.dialogData.visible=!0,this.dialogData.status=1;var t=e;t.menuIds=e.menuIds.split(","),this.dialogData.formData=t},bind:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(t){var n,a;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return this.form.vehicleId=t.id,this.dialogVisible=!0,e.next=4,p();case 4:n=e.sent,a=n.data,console.log(a,"aaaaaaaa"),this.allBound=a;case 8:case"end":return e.stop()}}),e,this)})));function t(t){return e.apply(this,arguments)}return t}(),close:function(){this.dialogVisible=!1,this.visible=!1},save:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(){var t,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:if(this.form.equipmentId){e.next=2;break}return e.abrupt("return");case 2:return e.next=4,f(this.form);case 4:t=e.sent,n=t.data,console.log(n,"asasasasa"),this.dialogVisible=!1;case 8:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),connTesting:function(){var e=Object(r["a"])(regeneratorRuntime.mark((function e(t){var n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,b(t.id);case 2:n=e.sent,this.$message.success(n.data),this.fetchData();case 5:case"end":return e.stop()}}),e,this)})));function t(t){return e.apply(this,arguments)}return t}()}},y=g,_=(n("6f4b"),n("635f"),n("2877")),k=Object(_["a"])(y,a,i,!1,null,"25249d45",null);t["default"]=k.exports},ed55:function(e,t,n){"use strict";n.r(t);var a=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-dialog",{attrs:{title:e.dialogTitle,visible:e.dialogVisible,"close-on-click-modal":!1,"destroy-on-close":!0},on:{close:e.close}},[n("el-table",{staticStyle:{width:"100%"},attrs:{data:e.errorForm,stripe:"",border:""}},[n("el-table-column",{attrs:{type:"index",label:"#",width:"100",align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"rowNum",label:e.$t("table.rowNum"),align:"center"}}),e._v(" "),n("el-table-column",{attrs:{prop:"msg",label:e.$t("route.errorLog"),align:"center"}})],1)],1)},i=[],r=(n("ed08"),n("333d")),l={name:"Dialog",components:{Pagination:r["a"]},props:{errorForm:{default:function(){return[]},type:Array},visible:{default:function(){return!1},type:Boolean}},data:function(){return{loading:!1}},computed:{dialogVisible:function(){var e=!1;return this.visible&&(e=!0),e},dialogTitle:function(){var e=this.$t("route.errorLog");return e}},created:function(){console.log(this.errorForm,"asas")},methods:{close:function(){this.$emit("close","close")}}},s=l,o=n("2877"),c=Object(o["a"])(s,a,i,!1,null,"44cc3661",null);t["default"]=c.exports}}]);
