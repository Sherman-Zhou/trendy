(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-5cfb156a","chunk-75c01702","chunk-152f0e42"],{"09f4":function(t,e,a){"use strict";a.d(e,"a",(function(){return o})),Math.easeInOutQuad=function(t,e,a,n){return t/=n/2,t<1?a/2*t*t+e:(t--,-a/2*(t*(t-2)-1)+e)};var n=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(t){window.setTimeout(t,1e3/60)}}();function r(t){document.documentElement.scrollTop=t,document.body.parentNode.scrollTop=t,document.body.scrollTop=t}function i(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function o(t,e,a){var o=i(),l=t-o,s=20,c=0;e="undefined"===typeof e?500:e;var u=function t(){c+=s;var i=Math.easeInOutQuad(c,o,l,e);r(i),c<e?n(t):a&&"function"===typeof a&&a()};u()}},"54c8":function(t,e,a){"use strict";var n=a("ba8e"),r=a.n(n);r.a},6026:function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-dialog",{attrs:{title:t.dialogTitle,visible:t.visible,"close-on-click-modal":!1,"destroy-on-close":!0},on:{close:t.close}},[a("el-table",{staticStyle:{width:"100%"},attrs:{data:t.errorForm,stripe:"",border:""}},[a("el-table-column",{attrs:{type:"index",label:"#",width:"100",align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"rowNum",label:t.$t("table.rowNum"),align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"msg",label:t.$t("route.errorLog"),align:"center"}})],1)],1)},r=[],i=(a("ed08"),a("333d")),o={name:"Dialog",components:{Pagination:i["a"]},props:{errorForm:{default:function(){return[]},type:Array},dialogVisible:{default:function(){return!1},type:Boolean}},data:function(){return{loading:!1}},computed:{visible:function(){var t=!1;return this.dialogVisible&&(t=!0),t},dialogTitle:function(){var t=this.$t("route.errorLog");return t}},created:function(){console.log(this.errorForm,"asas")},methods:{close:function(){this.$emit("cb","close")}}},l=o,s=a("2877"),c=Object(s["a"])(l,n,r,!1,null,"c950e818",null);e["default"]=c.exports},9260:function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("div",{staticClass:"filter-container"},[a("div",{staticClass:"filter-left"},[a("el-button",{staticClass:"add",attrs:{type:"primary",plain:"",icon:"el-icon-circle-plus-outline"},on:{click:t.add}},[t._v(t._s(t.$t("table.add")))]),t._v(" "),a("el-upload",{staticClass:"upload-demo",attrs:{action:t.url,"on-success":t.uploadSuccess,"on-error":t.uploadError,"on-remove":t.handleRemove,headers:t.headers,"show-file-list":t.flag}},[a("el-button",{attrs:{type:"primary",plain:""}},[t._v(t._s(t.$t("table.ReadExcel")))])],1)],1),t._v(" "),a("div",{staticClass:"filter-right"},[a("el-input",{attrs:{placeholder:t.$t("table.EnterFacilityID")},nativeOn:{keyup:function(e){return!e.type.indexOf("key")&&t._k(e.keyCode,"enter",13,e.key,"Enter")?null:t.search(e)}},model:{value:t.query.identifyNumber,callback:function(e){t.$set(t.query,"identifyNumber",e)},expression:"query.identifyNumber"}}),t._v(" "),a("el-button",{attrs:{type:"primary",plain:""},on:{click:t.search}},[t._v(t._s(t.$t("table.search")))])],1)]),t._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],staticStyle:{width:"100%"},attrs:{data:t.list,stripe:"",border:""}},[a("el-table-column",{attrs:{type:"index",label:"#",width:"100",align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"identifyNumber",label:t.$t("table.FacilityID"),align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"imei",label:t.$t("table.FacilityIMEI"),align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"version",label:t.$t("table.FirmwareVersion"),align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"simCardNum",label:t.$t("table.SIMCardNumber"),align:"center"}}),t._v(" "),a("el-table-column",{attrs:{label:t.$t("table.FacilityStatus"),align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return["B"==e.row.status?a("span",[t._v(t._s(t.$t("table.Settings")))]):t._e(),t._v(" "),"U"==e.row.status?a("span",[t._v(t._s(t.$t("table.Unsetting")))]):t._e(),t._v(" "),"D"==e.row.status?a("span",[t._v(t._s(t.$t("table.delete")))]):t._e()]}}])}),t._v(" "),a("el-table-column",{attrs:{prop:"bluetoothName",label:"設備名稱",align:"center"}}),t._v(" "),a("el-table-column",{attrs:{prop:"remark",label:t.$t("table.remark"),align:"center"}}),t._v(" "),a("el-table-column",{attrs:{label:t.$t("table.Operation"),align:"center",width:"300"},scopedSlots:t._u([{key:"default",fn:function(e){return[a("el-button",{attrs:{plain:"",size:"mini",type:"primary"},on:{click:function(a){return t.edit(e.row)}}},[a("span",[t._v("設置藍牙展示名稱")])]),t._v(" "),a("el-button",{attrs:{plain:"",size:"mini",type:"primary"},on:{click:function(a){return t.edit(e.row)}}},[a("span",[t._v(t._s(t.$t("table.edit")))])]),t._v(" "),a("el-button",{attrs:{plain:"",size:"mini",type:"danger"},on:{click:function(a){return t.del(e.row)}}},[a("span",[t._v(t._s(t.$t("table.delete")))])])]}}])})],1),t._v(" "),a("pagination",{attrs:{hidden:0===t.list.length,total:t.pages.total,page:t.pages.page,limit:t.pages.limit},on:{pagination:t.changeSize}}),t._v(" "),t.dialogData.visible?a("account-dialog",{attrs:{visible:t.dialogData.visible,status:t.dialogData.status,"form-data":t.dialogData.formData},on:{cb:t.dialogCallback}}):t._e(),t._v(" "),a("error",{attrs:{dialogVisible:t.dialogVisible,errorForm:t.errorForm},on:{cb:t.dialogCallback}})],1)},r=[],i=(a("96cf"),a("3b8d")),o=a("db72"),l=a("2f62"),s=a("4360"),c=a("333d"),u=a("d0fc"),d=a("6026"),m=a("aa98"),f={name:"Role",components:{Pagination:c["a"],AccountDialog:u["default"],Error:d["default"]},data:function(){return{query:{identifyNumber:"",imei:"",isOnline:"",isBounded:"",simCardNum:"",sort:"",page:0,size:20},querySync:{page:0,size:20,sort:""},pages:{total:0,page:0,limit:20},list:[],loading:!1,dialogData:{visible:!1,status:0,formData:{}},url:"http://localhost:8080/api/equipment/upload",headers:{Authorization:"Bearer "+s["a"].getters.token},flag:!1,errorForm:[],dialogVisible:!1}},computed:Object(o["a"])({},Object(l["b"])([])),created:function(){this.fetchData()},methods:{search:function(){this.query.page=0,this.fetchData()},changeSize:function(t){t.page>0&&(this.query.page=t.page-1),this.query.size=t.limit,this.fetchData()},fetchData:function(){var t=Object(i["a"])(regeneratorRuntime.mark((function t(){var e=this;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:this.loading=!0,Object(m["c"])(this.query).then((function(t){e.pages.total=t.data.total,e.list=t.data.items,e.loading=!1})).catch((function(t){e.loading=!1}));case 2:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),add:function(){this.dialogData.visible=!0,this.dialogData.status=0,this.dialogData.formData={}},edit:function(t){this.dialogData.visible=!0,this.dialogData.status=1,this.dialogData.formData=t},del:function(t){var e=this;this.$confirm(this.$t("permission.delete"),{confirmButtonText:this.$t("permission.confirm"),cancelButtonText:this.$t("permission.cancel"),type:"warning"}).then(Object(i["a"])(regeneratorRuntime.mark((function a(){var n;return regeneratorRuntime.wrap((function(a){while(1)switch(a.prev=a.next){case 0:return a.next=2,Object(m["b"])(t.id);case 2:n=a.sent,n.data,e.$message({message:"success",type:"success"}),e.fetchData();case 6:case"end":return a.stop()}}),a)})))).catch((function(){e.$message({type:"info",message:e.$t("permission.cancel")})}))},handleRemove:function(t,e){console.log(t,e)},uploadSuccess:function(t,e){var a=this;t.successRowNum==t.totalRowsNum?this.$message.success("success"):(a.dialogVisible=!0,a.errorForm=t.errors),this.fetchData()},uploadError:function(t,e){this.$message.error("error")},dialogCallback:function(t){"refresh"===t&&this.fetchData(),this.dialogData.visible=!1,this.dialogVisible=!1}}},p=f,b=(a("54c8"),a("2877")),g=Object(b["a"])(p,n,r,!1,null,"3fd6fec2",null);e["default"]=g.exports},aa98:function(t,e,a){"use strict";a.d(e,"c",(function(){return r})),a.d(e,"a",(function(){return i})),a.d(e,"d",(function(){return o})),a.d(e,"e",(function(){return l})),a.d(e,"b",(function(){return s}));var n=a("b775");function r(t){return Object(n["a"])({url:"/equipment",method:"get",params:t})}function i(t){return Object(n["a"])({url:"/equipment",method:"post",data:t})}function o(t){return Object(n["a"])({url:"/equipment/".concat(t),method:"get"})}function l(t){return Object(n["a"])({url:"/equipment",method:"put",data:t})}function s(t){return Object(n["a"])({url:"/equipment/".concat(t),method:"delete"})}},ba8e:function(t,e,a){},d0fc:function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-dialog",{attrs:{title:t.dialogTitle,visible:t.dialogVisible,"close-on-click-modal":!1,"destroy-on-close":!0},on:{close:t.close}},[a("el-form",{ref:"form",staticClass:"dialog-form-container",attrs:{width:"30%",model:t.form,"label-width":"130px",rules:t.rules}},[a("el-form-item",{attrs:{label:t.$t("table.FacilityID")}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:t.form.identifyNumber,callback:function(e){t.$set(t.form,"identifyNumber",e)},expression:"form.identifyNumber"}})],1),t._v(" "),a("el-form-item",{attrs:{label:t.$t("table.FacilityIMEI")}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:t.form.imei,callback:function(e){t.$set(t.form,"imei",e)},expression:"form.imei"}})],1),t._v(" "),a("el-form-item",{attrs:{label:t.$t("table.SIMCardNumber")}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:t.form.simCardNum,callback:function(e){t.$set(t.form,"simCardNum",e)},expression:"form.simCardNum"}})],1),t._v(" "),a("el-form-item",{attrs:{label:t.$t("table.FirmwareVersion")}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:t.form.version,callback:function(e){t.$set(t.form,"version",e)},expression:"form.version"}})],1),t._v(" "),a("el-form-item",{attrs:{label:t.$t("table.remark")}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:t.form.remark,callback:function(e){t.$set(t.form,"remark",e)},expression:"form.remark"}})],1)],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{plain:""},on:{click:t.close}},[t._v(t._s(t.$t("permission.cancel")))]),t._v(" "),a("el-button",{directives:[{name:"loading",rawName:"v-loading",value:t.loading,expression:"loading"}],attrs:{type:"primary",plain:""},on:{click:t.save}},[t._v(t._s(t.$t("permission.confirm")))])],1)],1)},r=[],i=(a("96cf"),a("3b8d")),o=(a("c5f6"),a("ed08"),a("61f7"),a("aa98")),l={name:"Dialog",props:{id:{default:function(){return""},type:String},status:{default:function(){return 0},type:Number},formData:{default:function(){return{}},type:Object},visible:{default:function(){return!1},type:Boolean}},data:function(){return{loading:!1,form:{createdBy:"",createdDate:new Date,identifyNumber:"",imei:"",simCardNum:"",version:"",remark:""},rules:{}}},computed:{dialogVisible:function(){var t=!1;return this.visible&&(t=!0),t},dialogTitle:function(){var t=this.$t("table.add");return 1===this.status&&(t=this.$t("table.edit")),t}},created:function(){this.fetchData()},methods:{fetchData:function(){var t=Object(i["a"])(regeneratorRuntime.mark((function t(){var e,a,n;return regeneratorRuntime.wrap((function(t){while(1)switch(t.prev=t.next){case 0:if(1!==this.status){t.next=7;break}return e=this.formData.id,t.next=4,Object(o["d"])(e);case 4:a=t.sent,n=a.data,this.form=n;case 7:case"end":return t.stop()}}),t,this)})));function e(){return t.apply(this,arguments)}return e}(),save:function(){var t=this,e=this;this.$refs.form.validate(function(){var a=Object(i["a"])(regeneratorRuntime.mark((function a(n){return regeneratorRuntime.wrap((function(a){while(1)switch(a.prev=a.next){case 0:n&&(e.loading=!0,0===e.status?Object(o["a"])(e.form).then((function(e){t.$message.success("success"),t.$emit("cb","refresh"),t.loading=!1})).catch((function(e){t.$message.error("error"),t.loading=!1})):Object(o["e"])(e.form).then((function(e){t.$message.success("success"),t.$emit("cb","refresh"),t.loading=!1})).catch((function(e){t.$message.error("error"),t.loading=!1})));case 1:case"end":return a.stop()}}),a)})));return function(t){return a.apply(this,arguments)}}())},close:function(){this.$emit("cb","close")}}},s=l,c=a("2877"),u=Object(c["a"])(s,n,r,!1,null,"f998c604",null);e["default"]=u.exports}}]);
