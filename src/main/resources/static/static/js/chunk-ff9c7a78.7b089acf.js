(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-ff9c7a78","chunk-2d0df483"],{"09f4":function(e,t,a){"use strict";a.d(t,"a",(function(){return s})),Math.easeInOutQuad=function(e,t,a,n){return e/=n/2,e<1?a/2*e*e+t:(e--,-a/2*(e*(e-2)-1)+t)};var n=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(e){window.setTimeout(e,1e3/60)}}();function r(e){document.documentElement.scrollTop=e,document.body.parentNode.scrollTop=e,document.body.scrollTop=e}function o(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function s(e,t,a){var s=o(),i=e-s,l=20,c=0;t="undefined"===typeof t?500:t;var u=function e(){c+=l;var o=Math.easeInOutQuad(c,s,i,t);r(o),c<t?n(e):a&&"function"===typeof a&&a()};u()}},"1f34":function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"app-container"},[a("div",{staticClass:"filter-container"},[a("div",{staticClass:"filter-left"},[a("el-button",{attrs:{type:"primary",plain:"",icon:"el-icon-circle-plus-outline"},on:{click:e.add}},[e._v(e._s(e.$t("table.add")))])],1),e._v(" "),a("div",{staticClass:"filter-right"},[a("el-input",{attrs:{placeholder:e.$t("table.input")},model:{value:e.query.name,callback:function(t){e.$set(e.query,"name",t)},expression:"query.name"}}),e._v(" "),a("el-button",{attrs:{type:"primary",plain:""},on:{click:e.search}},[e._v(e._s(e.$t("table.search")))])],1)]),e._v(" "),a("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticStyle:{width:"100%"},attrs:{data:e.userList,stripe:"",border:"",fit:""}},[a("el-table-column",{attrs:{align:"center",type:"index",width:"50",label:"#"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"login",label:e.$t("table.account")}}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"name",label:e.$t("table.Name")}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:e.$t("login.Gender")},scopedSlots:e._u([{key:"default",fn:function(t){return["M"==t.row.sex?a("span",[e._v(e._s(e.$t("login.Male")))]):e._e(),e._v(" "),"F"==t.row.sex?a("span",[e._v(e._s(e.$t("login.Female")))]):e._e(),e._v(" "),"U"==t.row.sex?a("span",[e._v(e._s(e.$t("login.Unknown")))]):e._e()]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"roleName",label:e.$t("table.Role"),width:"100px"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"mobileNo",label:e.$t("table.Phone")}}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"email",label:"E-mail"}}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"address",label:e.$t("table.Address")}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:e.$t("table.status")},scopedSlots:e._u([{key:"default",fn:function(t){return["A"==t.row.status?a("span",[e._v(e._s(e.$t("table.Enable")))]):e._e(),e._v(" "),"I"==t.row.status?a("span",[e._v(e._s(e.$t("table.Outservice")))]):e._e(),e._v(" "),"D"==t.row.status?a("span",[e._v(e._s(e.$t("table.delete")))]):e._e()]}}])}),e._v(" "),a("el-table-column",{attrs:{align:"center",prop:"remark",label:e.$t("table.remark")}}),e._v(" "),a("el-table-column",{attrs:{align:"center",label:e.$t("table.Operation"),width:"500"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{plain:"",size:"mini",type:"primary"},on:{click:function(a){return e.lookOrganization(t.row)}}},[a("span",[e._v(e._s(e.$t("table.Organization")))])]),e._v(" "),a("el-button",{attrs:{plain:"",size:"mini",type:"primary"},on:{click:function(a){return e.edit(t.row)}}},[a("span",[e._v(e._s(e.$t("table.edit")))])]),e._v(" "),a("el-button",{attrs:{plain:"",size:"mini",type:"danger"},on:{click:function(a){return e.del(t.row)}}},[a("span",[e._v(e._s(e.$t("table.delete")))])]),e._v(" "),"I"==t.row.status?a("el-button",{attrs:{plain:"",size:"mini",type:"warning"},on:{click:function(a){return e.editStatus(t.row)}}},[a("span",[e._v(e._s(e.$t("table.Enable")))])]):e._e(),e._v(" "),"A"==t.row.status?a("el-button",{attrs:{plain:"",size:"mini",type:"warning"},on:{click:function(a){return e.editStatus(t.row)}}},[a("span",[e._v(e._s(e.$t("table.Outservice")))])]):e._e(),e._v(" "),a("el-button",{attrs:{plain:"",size:"mini",type:"danger"},on:{click:function(a){return e.resetPassword(t.row)}}},[a("span",[e._v(e._s(e.$t("table.PasswordReset")))])])]}}])})],1),e._v(" "),a("pagination",{attrs:{hidden:0===e.userList.length,total:e.pages.total,page:e.pages.page,limit:e.pages.limit},on:{pagination:e.changeSize}}),e._v(" "),e.dialogData.visible?a("account-dialog",{attrs:{visible:e.dialogData.visible,status:e.dialogData.status,"form-data":e.dialogData.formData},on:{cb:e.dialogCallback}}):e._e(),e._v(" "),a("el-dialog",{attrs:{width:"30%",visible:e.dialogVisible,"close-on-click-modal":!1,"destroy-on-close":!0},on:{close:e.dialogCallback}},[a("el-tree",{ref:"tree",attrs:{data:e.roles,"show-checkbox":"","node-key":"id","default-checked-keys":e.rolesChecked,"default-expanded-keys":e.rolesExpanded,props:e.defaultProps}}),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{plain:""},on:{click:e.dialogCallback}},[e._v(e._s(e.$t("permission.cancel")))]),e._v(" "),a("el-button",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],attrs:{type:"primary",plain:""},on:{click:e.saveOrganization}},[e._v(e._s(e.$t("permission.confirm")))])],1)],1)],1)},r=[],o=(a("96cf"),a("3b8d")),s=a("db72"),i=a("2f62"),l=a("333d"),c=a("88f3"),u=a("c24f"),d={name:"Account",components:{Pagination:l["a"],AccountDialog:c["default"]},data:function(){return{query:{address:"",email:"",langKey:"",login:"",name:"",sort:"",status:"",page:0,size:20},pages:{total:0,page:0,limit:20},userList:[],loading:!1,dialogData:{visible:!1,status:0,formData:{}},dialogVisible:!1,roles:[],rolesChecked:[],rolesExpanded:[],defaultProps:{children:"children",label:"description"},userId:""}},computed:Object(s["a"])({},Object(i["b"])([])),created:function(){this.fetchData(),this.getDivision()},methods:{search:function(){this.query.page=0,this.fetchData()},changeSize:function(e){e.page>0&&(this.query.page=e.page-1),this.query.size=e.limit,this.fetchData()},fetchData:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(){var t=this;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:this.loading=!0,Object(u["f"])(this.query).then((function(e){t.userList=e.data.items,t.pages.total=e.data.total,t.loading=!1})).catch((function(e){t.loading=!1}));case 2:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),getDivision:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(){var t,a;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,Object(u["d"])();case 2:t=e.sent,a=t.data,this.roles=a,a.length>0&&this.rolesExpanded.push(a[0].id);case 6:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),lookOrganization:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(t){var a,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return this.dialogVisible=!0,this.userId=t.id,e.next=4,Object(u["g"])(t.id);case 4:a=e.sent,n=a.data,this.rolesChecked=n;case 7:case"end":return e.stop()}}),e,this)})));function t(t){return e.apply(this,arguments)}return t}(),add:function(){this.dialogData.visible=!0,this.dialogData.status=0,this.dialogData.formData={}},edit:function(e){this.dialogData.visible=!0,this.dialogData.status=1,this.dialogData.formData=e},del:function(e){var t=this;this.$confirm(this.$t("permission.delete"),{confirmButtonText:this.$t("permission.confirm"),cancelButtonText:this.$t("permission.cancel"),type:"warning"}).then(Object(o["a"])(regeneratorRuntime.mark((function a(){var n,r;return regeneratorRuntime.wrap((function(a){while(1)switch(a.prev=a.next){case 0:return a.next=2,Object(u["c"])(e.id);case 2:n=a.sent,r=n.data,console.log(r,"aaaaa"),t.$message({message:"success",type:"success"}),t.fetchData();case 7:case"end":return a.stop()}}),a)})))).catch((function(){t.$message({type:"info",message:t.$t("permission.cancel")})}))},resetPassword:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(t){var a;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,Object(u["k"])(t.id);case 2:a=e.sent,a.data,this.$message.success("success");case 5:case"end":return e.stop()}}),e,this)})));function t(t){return e.apply(this,arguments)}return t}(),editStatus:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(t){var a,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return a="enable","A"==t.status?a="disable":"I"==t.status&&(a="enable"),e.next=4,Object(u["b"])(t.id,a);case 4:n=e.sent,n.data,this.fetchData();case 7:case"end":return e.stop()}}),e,this)})));function t(t){return e.apply(this,arguments)}return t}(),saveOrganization:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(){var t,a,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return t=this.$refs.tree.getCheckedKeys(),e.next=3,Object(u["n"])(this.userId,t);case 3:a=e.sent,n=a.data,console.log(n,"aaaas"),this.$message.success("success"),this.fetchData(),this.dialogVisible=!1;case 9:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),dialogCallback:function(e){"refresh"===e&&this.fetchData(),this.dialogData.visible=!1,this.dialogVisible=!1}}},m=d,f=a("2877"),p=Object(f["a"])(m,n,r,!1,null,null,null);t["default"]=p.exports},"88f3":function(e,t,a){"use strict";a.r(t);var n=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("el-dialog",{attrs:{title:e.dialogTitle,visible:e.dialogVisible,"close-on-click-modal":!1,"destroy-on-close":!0},on:{close:e.close}},[a("el-form",{ref:"form",staticClass:"dialog-form-container",attrs:{width:"40%",model:e.form,"label-width":"130px",rules:e.rules}},[a("el-form-item",{attrs:{label:e.$t("table.account"),prop:"login"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.login,callback:function(t){e.$set(e.form,"login",t)},expression:"form.login"}})],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("login.password"),prop:"password"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.password,callback:function(t){e.$set(e.form,"password",t)},expression:"form.password"}})],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("table.Name"),prop:"name"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.name,callback:function(t){e.$set(e.form,"name",t)},expression:"form.name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("table.Sex"),prop:"sex"}},[a("el-select",{model:{value:e.form.sex,callback:function(t){e.$set(e.form,"sex",t)},expression:"form.sex"}},e._l(e.options,(function(e,t){return a("el-option",{key:t,attrs:{label:e.label,value:e.value}})})),1)],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("table.Role"),prop:"roleId"}},[a("el-select",{on:{change:e.changeRoles},model:{value:e.form.roleName,callback:function(t){e.$set(e.form,"roleName",t)},expression:"form.roleName"}},e._l(e.roles,(function(e,t){return a("el-option",{key:t,attrs:{label:e.name,value:e.id}})})),1)],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("table.Phone"),prop:"phone"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.mobileNo,callback:function(t){e.$set(e.form,"mobileNo",t)},expression:"form.mobileNo"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"E-mail",prop:"email"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.email,callback:function(t){e.$set(e.form,"email",t)},expression:"form.email"}})],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("table.Address"),prop:"name"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.address,callback:function(t){e.$set(e.form,"address",t)},expression:"form.address"}})],1),e._v(" "),a("el-form-item",{attrs:{label:e.$t("table.remark"),prop:"remark"}},[a("el-input",{attrs:{autocomplete:"off"},model:{value:e.form.remark,callback:function(t){e.$set(e.form,"remark",t)},expression:"form.remark"}})],1)],1),e._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{attrs:{plain:""},on:{click:e.close}},[e._v(e._s(e.$t("permission.cancel")))]),e._v(" "),a("el-button",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],attrs:{type:"primary",plain:""},on:{click:e.save}},[e._v(e._s(e.$t("permission.confirm")))])],1)],1)},r=[],o=(a("7f7f"),a("ac6a"),a("96cf"),a("3b8d")),s=a("db72"),i=(a("c5f6"),a("ed08")),l=(a("61f7"),a("c24f")),c=a("2f62"),u={name:"Dialog",props:{id:{default:function(){return""},type:String},status:{default:function(){return 0},type:Number},formData:{default:function(){return{}},type:Object},visible:{default:function(){return!1},type:Boolean}},data:function(){return{loading:!1,form:{id:"",login:"",name:"",sex:"",roleName:"",mobileNo:"",email:"",address:"",remark:"",avatar:"",divisionIds:[0],langKey:"",password:"",roleIds:[0],status:"A"},roles:[],options:[{value:"M",label:"男"},{value:"F",label:"女"},{value:"U",label:"未知"}],rules:{login:[{required:!0,message:this.$t("table.account"),trigger:["blur","change"]}]}}},computed:Object(s["a"])({dialogVisible:function(){var e=!1;return this.visible&&(e=!0),e},dialogTitle:function(){var e=this.$t("table.add");return 1===this.status&&(e=this.$t("table.edit")),e}},Object(c["c"])({getters:function(e){return e.app.language}})),created:function(){this.getUserRoles(),this.form.langKey=this.getters},methods:{getUserRoles:function(){var e=Object(o["a"])(regeneratorRuntime.mark((function e(){var t,a,n;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return t=this,1===this.status&&(this.form=Object(i["c"])(this.formData)),e.next=4,Object(l["h"])();case 4:a=e.sent,n=a.data,t.form.roleName&&(t.form.roleIds=[],n.forEach((function(e){e.name==t.form.roleName&&t.form.roleIds.push(e.id)}))),this.roles=n;case 8:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),changeRoles:function(e){var t=this;t.form.roleIds=[],this.roles.forEach((function(a){a.id==e&&(t.form.roleIds.push(e),t.form.roleName=a.name)}))},save:function(){var e=this,t=this;this.$refs.form.validate(function(){var a=Object(o["a"])(regeneratorRuntime.mark((function a(n){var r;return regeneratorRuntime.wrap((function(a){while(1)switch(a.prev=a.next){case 0:if(!n){a.next=17;break}if(t.loading=!0,0!==t.status){a.next=11;break}return a.next=5,Object(l["a"])(t.form);case 5:a.sent,e.$message.success("success"),e.$emit("cb","refresh"),e.loading=!1,a.next=17;break;case 11:return a.next=13,Object(l["m"])(t.form);case 13:r=a.sent,console.log(r,"aaaaaaaaaaa"),e.$emit("cb","refresh"),e.loading=!1;case 17:case"end":return a.stop()}}),a)})));return function(e){return a.apply(this,arguments)}}())},close:function(){this.$emit("cb","close")}}},d=u,m=a("2877"),f=Object(m["a"])(d,n,r,!1,null,"51707fff",null);t["default"]=f.exports}}]);