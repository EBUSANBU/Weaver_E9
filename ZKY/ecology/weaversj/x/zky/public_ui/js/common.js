<!-- westvalley dev start -->
function GlobalReger(f){
	var w = window;
	while (w != top) {
		f(w);
		w = w.parent;
	}
}
/**
Crivia Work Flow Java Script Function Group.
**/
top.CriviaWorkFlowJavaScriptFunctions = 
	undefined!=top.CriviaWorkFlowJavaScriptFunctions
	?top.CriviaWorkFlowJavaScriptFunctions:
//var _C =
{
		ev : '8'
		,UEF : '.c'/*UEF*/
		,ui : {
			_ : 'UserInterface'
			,isPC : function(){return '0' == this._;}
			,isMobile : function(){return '2' == this._;}
		}
		,rsImg : function(){
			return '8' == this.ev
				? '<img src="/images/BacoError_wev8.gif" align="absmiddle">'
				: '<img src="/images/BacoError.gif" align="absmiddle">';
		}
		,_rsSetting : []
/**
{
	Desc : 'Requird Switcher | 必填状态变更'
	,Params : {
		Field ID : String
		,Is Requird : Boolean
	} 
}
 */
		,rs : function (f,isRequird){
			var _c = this;
			if (! f){
				return;
			}
			_c._rsSetting[f] = {r:isRequird};
			var img = _c.rsImg();
			
			var coA = jQuery('input[name="inputcheck"]');
			var coB = jQuery('input[name="needcheck"]');
			if (coA.length&&coB.length){
				var v = _c.v(f);
				var checkerA = coA.val();
				var checkerB = coB.val();
				var oldChange = jQuery('#'+f).attr('onchange');
				if (isRequird) {
					checkerA=checkerA.replace(','+f,'');
					checkerB=checkerB.replace(','+f,'');
					checkerA +=','+f;
					checkerB +=','+f;
					if ('' == v){
						var jf = jQuery('#field_'+f.substring(5)+'span');
						if (jf.length){
							jf.html(img+'(必填)');
							jf.show();
						} else if ('8' == _c.ev
						&& jQuery('#'+f+'spanimg').length){
							_c.t(f+'spanimg',img,true);
						} else {
							_c.t(f,img);
						}
					}
					if ('8' == _c.ev) {
						jQuery('#'+f).attr('viewtype','1');
						if (jQuery('#'+f+'spanimg').length){
							jQuery('#'+f).attr('onchange',oldChange
								+';checkinput2('+'\''+f+'\' , \''+f+'spanimg\' '
								+', this.getAttribute(\'viewtype\'));');
						} else {
							jQuery('#'+f).attr('onchange',oldChange
								+';checkinput2('+'\''+f+'\' , \''+f+'span\' '
								+', this.getAttribute(\'viewtype\'));');
						}
					}
				}else{
					checkerA=checkerA.replace(','+f,'');
					checkerB=checkerB.replace(','+f,'');
					jQuery('#field_'+f.substring(5)+'span').hide();
					if ('8' == _c.ev){
						jQuery('#'+f).attr('viewtype','0');
						if (jQuery('#'+f+'spanimg').length){
							if (_c.t(f+'spanimg',undefined,true).indexOf('src="/images/BacoError_wev8.gif"') > -1){
								_c.t(f+'spanimg','',true);
							}
						} else if (jQuery('#'+f+'span').length) {
							if (_c.t(f).indexOf('src="/images/BacoError_wev8.gif"') > -1){
								_c.t(f,'');
							}
						}
					} else {
						if (_c.t(f).indexOf('src="/images/BacoError.gif"') > -1){
							_c.t(f,'');
						}
					}
				}
				
				coA.val(checkerA);
				coB.val(checkerB);
			}

			var  coC = jQuery('#'+f+'_ismandfield');
			var  coD = jQuery('#'+f+'_d_ismandfield');
			if (coC.length){
				if (isRequird){
					jQuery('#'+f+'_ismandfield').attr('name','mustInput');
					jQuery('#'+f+'_ismandspan').html(img);
					jQuery('#'+f+'_ismandspan').show();
				} else {
					jQuery('#'+f+'_ismandfield').attr('name','notmandfield');
					jQuery('#'+f+'_ismandspan').html('');
				}
			} else if (coD.length) {
				if (isRequird){
					jQuery('#'+f+'_d_ismandfield').attr('name','mustInput');
					jQuery('#'+f+'_d_ismandspan').html(img);
					jQuery('#'+f+'_ismandspan').show();
				} else {
					jQuery('#'+f+'_d_ismandfield').attr('name','notmandfield');
					jQuery('#'+f+'_d_ismandspan').html('');
				}
			}
		}
/**
{
	Desc : 'Sub Table Each | 子表遍历'
	,Params : {
		Each Field ID : String
		,Each Function : Function
	} 
	,Return : {
		Count : Sub Table Size (Int)
		,Sum : Sub Table Data Sum (Double)
		,Values : Sub Table Values Map
		,Concat : Values Concat (Function , Param : Concater Default ',')
	}
}
 */
		,stEach : function (id , f , mobileReadOnly){
			if ('string' != typeof id){
				return;
			}
			var _c = this;
			var count = 0;
			var sum = 0;
			var values = {};
			var each = function(fs){
				var x = false;
				if (!fs.length){
					return x;
				}
				for (var k = 0; k < fs.length; k++){
					var kr = jQuery(fs[k]).attr('id');
					if (false
					|| 'string' != typeof kr
					|| kr.match(/field\d+_?\d?__/)
					|| kr.match(/field\d+_?\d?_d/) 
					|| kr.match(/field\d+_\d+_ismandfield/)
					|| kr.indexOf('_ismandfield')==id.length){
						continue;
					}
					x = true;
					var rn = kr.indexOf('_d')==id.length
						? kr.substring(id.length,kr.length-2)
						: kr.substring(id.length);
					var v = _c.v(id + rn);
					count++;
					sum = sum + _c.n(v, 0);
					values[rn] = v;
					if (! f){
						continue;
					}
					if (mobileReadOnly){
						f(rn);
					} else {
						_c.emst(_c.f2stI(id+rn), rn, function(){
							f(rn);
						});
					}
				}
				return x;
			};
			if(each(jQuery('input[id^="' + id + '"]'))){
			} else if (each(jQuery('select[id^="' + id + '"]'))){
			} else if (each(jQuery('textarea[id^="' + id + '"]'))){
			}
			return {
				count : count
				,sum : sum
				,values : values
				,concat : function(concater){
					if (undefined == concater){
						concater = ',';
					}
					var m = this.values;
					var s = '';
					for (var k in m){
						if ('' != s){
							s = s + concater;
						}
						s = s + m[k];
					}
					return s;
				}
			};
		}
/**
{
	Desc : 'Value Keeper | 值变更储存'
	,Params : {
		Map Key : String
		,New Value : String
		,Map : Java Script Object
	} 
	,Return : {
		Type : Boolean
		,Desc : 'Old Value == New Value ? True : False'
	}
}
 */
		,vK : function (k , v , m){
			if (k in m){
				if (m[k] == v){
					return true;
				}
			}
			m[k] = v;
			return false;
		}
/**
{
	Desc : 'Value | 值操作'
	,Params : {
		Field ID : String
		,Input Value : String (M Get Or Set)
	} 
}
 */
		,_vom : {}
		,v : function(id , v){
			var o;
			if (id in this._vom){
				o = this._vom[id];
			} else {
				o = document.getElementById(id);
				if (! o){
					o = document.getElementById('dis'+id);
					if (! o){
						return;
					}
				}
				if ('string'==typeof o.tagName
				&& 'input'==o.tagName.toLowerCase()){
					this._vom[id] = o;
				}
			}
//			var o = document.getElementById(id);
//			if (! o){
//				o = document.getElementById('dis'+id);
//				if (! o){
//					return;
//				}
//			}
			if (undefined == v){
				return o.value;
			} else {
				o.value = v;
				if ('hidden' == o.getAttribute('type')){
					this.t(id, v);
				}
				jQuery(o).change();
				jQuery(o).blur();
				if ('8' == this.ev
				&& jQuery('#'+id+'spanimg').length){
					this.t(id+'spanimg',(!v?this.rsImg():''),true);
				}
				return o;
			}
		}
/**
{
	Desc : 'Text | 文本操作'
	,Params : {
		Field ID : String
		,Input Text : String (M Get Or Set)
		,Un Complete End : Boolean (M Default : False)
	} 
}
 */
		,t : function(id , t , uce){
			var end = 'span';
			if (uce){
				end = '';
			}
			var o = document.getElementById(id + end);
			if (! o){
				return;
			}
			if (undefined == t){
				return o.innerHTML;
			} else {
				o.innerHTML = t;
			}
		}
/**
{
	Desc : 'Value & Text | 值&文本赋予'
	,Params : {
		Field ID : String
		,Input Value : String
		,Input Text : String (M Default Value)
		,Text ID : String (M Default Field ID)
	} 
}
 */
		,vt : function(f , v , t , s){
			var _c = this;
			if (f == undefined || v == undefined){
				return;
			}
			if (t == undefined){
				t = v;
			}
			if (s == undefined){
				s = f;
			}
			_c.v(f, v);
			_c.t(s, t);
		}
/**
{
	Desc : 'Value & Show | 值&文本赋予'
	,Params : {
		Field ID : String
		,Input Value : String
		,Input Text : String (M Default Value)
	} 
}
 */
		,vs : function(f , v , t){
			var o = document.getElementById(f);
			if (! o){
				return;
			}
			this.v(f , v);
			if (false
			|| o.tagName == 'hidden' 
			|| (true
			&& o.tagName == 'input'
			&& o.getAttribute('type') == 'hidden')){
				if (! t){
					t = v;
				}
				this.t(f , t);
			}
		}
/**
{
	Desc : 'A Label Text | 各种链接中的文本'
	,Params : {
		Field ID : String
	} 
	,Return : Text
}
*/
		,at : function(f , ue){
			if (! f){
				return;
			}
			var e = 'span';
			if (ue){
				e = '';
			}
			return jQuery('#'+f+e).find('a').text();
		}
/**
{
	Desc : 'Add Function | 追加函数'
	,Params : {
		New Function : Function
		,Old Function Var : String (M Default : window.doSubmit)
	} 
}
 */
		,af : function(n , o){
			if (! n){
				return;
			}
			if (! o){
				o = window.doSubmit;
			}
			var f = o;
			o = function(p){
				n(f , p);
			};
		}
/**
{
	Desc : 'To Number | 数字转换'
	,Params : {
		String : String
		,Default Value : Target
		,Is Int : Boolean (M Default : False)
	} 
}
 */
		,n : function(s , dv , isInt){
			if ('number' == typeof s){
				return s;
			} else if ('string' != typeof s) {
				return dv;
			}
			var n;
			if (isInt){
				n = parseInt(s);
			} else {
				n = parseFloat(s.replace(/\,/g,''));
			}
			return isNaN(n) ? dv : n;
		}
/**
{
	Desc : 'Value To Number | 取值数字转换'
	,Params : {
		String : Field ID
		,Default Value : Target
		,Is Int : Boolean (M Default : False)
	} 
}
 */
		,vn : function(id , dv , isInt){
			return this.n(this.v(id), dv, isInt);
		}
/**
{
	Desc : 'Listener Runner | 启动监视器'
	,Params : {
		Option : Object
		,Example : {
			t : 'Time , Number , (M Default : 100)'
			,f : [
				{
					k : 'Example Main Table Field ID , String'
					,f : function(ov , nv){
						alert(
							'Old Value : ' + ov
							+ '\n' + 'New Value : ' + nv
						);
					}
					,f2 : function(nv , ov , k){
						alert(
							'New Value : ' + nv
							+ '\n' + 'Old Value : ' + ov
							+ '\n' + 'Field Key : ' + k
						);
					}
				},{
					k : 'Example Sub Table Field ID , String'
					,d : 'Sub Table Flag , Default : False'
					,f : function(ov , nv , r){
						alert(
							'Old Value : ' + ov
							+ '\n' + 'New Value : ' + nv
							+ '\n' + 'Row Index : ' + r
						);
					}
					,f2 : function(r , nv , ov , k){
						alert(
							'Row Index : ' + r
							+ '\n' + 'New Value : ' + nv
							+ '\n' + 'Old Value : ' + ov
							+ '\n' + 'Field Key : ' + k
						);
					}
					,f3 : function(p){
						alert(
							'Row Index : ' + p.r
							+ '\n' + 'New Value : ' + p.v.n
							+ '\n' + 'Old Value : ' + p.v.o
							+ '\n' + 'Field Key : ' + p.k
						);
					}
				}
			]
		}
	} 
}
 */
		,run : function(o){
			var _c = this;
			//Init Param.
			if (! o){
				o = {};
			}
			//Run Timer When Option Is Undefined.
			if (! _c._listenerOption){
				window.setInterval(
						'CriviaWorkFlowJavaScriptFunctions._cwjsListener()'
						, _c.n(o.t, 100, true));
			}
			//Old Param.
			if (! o.k){
				//Setting Options.
				_c._listenerOption = o;
			} 
			//New Param.
			else {
				//First Setting.
				if (! _c._listenerOption || ! _c._listenerOption.f){
					//Init Param.
					_c._listenerOption = { f : [] };
				}
				//Fields Size.
				var s = _c._listenerOption.f.length;
				//Each Check.
				for (var k = 0; k < s; k++){
					//Temp Field.
					var f = _c._listenerOption.f[k];
					//Repeat Key.
					if (f.k == o.k){
						//Replace Type.
						var t = _c.n(f.t, 0, true);
						//
						switch (t) {
						//Replace.
						case -1:
							f._fs = [o.f];
							f._f2s = [o.f2];
							f._f3s = [o.f3];
							return;
						//Add To Begin.
						case 1:
							f._fs.splice(0,0,o.f);
							f._f2s.splice(0,0,o.f2);
							f._f3s.splice(0,0,o.f3);
							return;
						//Add To End.
						default:
							f._fs[f._fs.length] = o.f;
							f._f2s[f._f2s.length] = o.f2;
							f._f3s[f._f3s.length] = o.f3;
							return;
						}
					}
				}//End Of Each.
				//Init Functions.
				o._fs = [o.f];
				o._f2s = [o.f2];
				o._f3s = [o.f3];
				//Regedit Field.
				_c._listenerOption.f[s] = o;
			}
		}
		,run2 : function(k,f){
			if (!k || !f){
				return;
			}
			var _c = this;
			jQuery(document).ready(function(){
				_c.run({k:k,f3:f});
			});
		}
		,_listenerOption : undefined
		,_cwjsListener : function(){
			var _c = this;
			//Requird Switch Setting.
			for (var rsf in _c._rsSetting){
				var o = _c._rsSetting[rsf];
				var v = _c.v(rsf);
				if (v == o.v) continue;
				o.v = v;
				if (!!o.r&&!v){
					var jf = jQuery('#field_'+rsf.substring(5)+'span');
					if (jf.length){
						jf.html(_c.rsImg()+'(必填)');
						jf.show();
					} else if ('8' == _c.ev
					&& jQuery('#'+rsf+'spanimg').length){
						_c.t(rsf+'spanimg',_c.rsImg(),true);
					} else {
						_c.t(rsf,_c.rsImg());
					}
				} else if (!o.r||!!v) {
					if ('8' == _c.ev){
						if (jQuery('#'+rsf+'spanimg').length){
							if (_c.t(rsf+'spanimg',undefined,true)
									.indexOf('src="/images/BacoError_wev8.gif"')
										> -1){
								_c.t(rsf+'spanimg','',true);
							}
						} else if (jQuery('#'+rsf+'span').length) {
							if (_c.t(rsf)
									.indexOf('src="/images/BacoError_wev8.gif"')
										> -1){
								_c.t(rsf,'');
							}
						}
					} else {
						if (_c.t(f).indexOf('src="/images/BacoError.gif"') > -1){
							_c.t(f,'');
						}
					}
				}
			}
			//Check Option.
			if (! _c._listenerOption){
				return;
			}
			//Fields.
			var fs = _c._listenerOption.f;
			//Fields Check.
			if (! fs){
				return;
			}
			//Check Value And Run Functions.
			for (var n = 0; n < fs.length; n++){
				//Value Map.
				var m = _c._cwjsListenerValueMap;
				//Field Option.
				var f = fs[n];
				//Check Key.
				if (! f.k){
					continue;
				}
				//Function : Check & Run.
				//Param : Temp Field , Field Key , Row Number.
				var cr = function(t , fk , r){
					//Old Value.
					var ov = m[fk];
					//New Value.
					var nv = _c.v(fk);
					//Check & Value Keep.
					if (_c.vK(fk, nv, m)){
						return;
					}
					//Function : Sequnce Runner.
					//Param : Sequnce , Runner , Bak Function.
					var sr = function(s , r , b){
						//Check Runner.
						if (! r){
							return;
						}
						//Check Sequnce.
						if (s){
							//Check Sequnce.
							for (var k = 0; k < s.length; k++){
								//Run.
								r(s[k]);
							}
						} else {
							//Run Bak Function.
							r(b);
						}
					};
					//Run Function Sequece.
					var rfs = function(){
						//F1.
						sr(t._fs, function(f){
							//Check Function.
							if (f){
								//Run.
								f(ov , nv , r);
							}
						}, t.f);
						//F2.
						sr(t._f2s, function(f){
							//Check Function.
							if (f){
								//Run.
								f(r , nv , ov , t.k);
							}
						}, t.f2);
						//F3.
						sr(t._f3s, function(f){
							//Check Function.
							if (! f){
								return;
							}
							//F3 Param.
							var p = {
								//Key.
								k : t.k
								//Detail.
								,d : t.d
								//Values.
								,v : {
									//Old.
									o : ov
									//New.
									,n : nv
								}
								//Row Num.
								,r : r
								//Sub Table Index.
								,stI : _c.f2stI(t.k)
							};
							//Run.
							f(p);
						}, t.f3);
					};
					//Check Row Number.
					if (r){
						//Edit Mobile Sub Table.
						_c.emst(_c.f2stI(t.k), r, rfs);
					} else {
						//Run Main Table.
						rfs();
					}
				};
				//All Each.
				_c.stEach(f.k, function(r){
					//Check & Run.
					cr(f , f.k + r , r);
				} , true);
			}
		}
		,_cwjsListenerValueMap : {}
/**
{
	Desc : 'Sub Table Button | 子表按钮附加'
	,Params : {
		Option : Object
		,Example : {
			stIndex : 'Sub Table Index , Int'
			,text : 'Button Text , String (M Default : 导入)' 
			,key : 'Button Key , String (M Default : T)'
			,name : 'Button Name , String (M Default : autoCreate)' 
			,onclick : 'On Click Act , String'
		}
	} 
}
 */
		,stButton : function(o){
			if (o.stIndex == undefined || ! o.onclick){
				return;
			}
			if ('string'==typeof o.stIndex){
				o.stIndex = this.f2stI(o.stIndex);
				if (o.stIndex < 0){
					return;
				}
			}
			if (! o.text){
				o.text = '导入';
			}
			if (! o.key){
				o.key = 'T';
			}
			if (! o.name){
				o.name = 'autoCreate';
			}
			if (! o.bg || ! o.img){
				o.img = 'copy';
			}
			var bn = o.name + o.stIndex;
			var buttonBox = jQuery('#div' + o.stIndex + 'button');
			var button = '8' == this.ev
			    ? ('<button class="addbtn_p" type="button"'
			    	+ ' style="background: url('+(o.bg ? o.bg
			    		: '/wui/theme/ecology8/jquery/js/button/'+o.img+'_wev8.png')+') no-repeat"'
				    + ' id="$'+bn+'$" name="'+bn+'" onclick="'
				    + o.onclick+'" title="'+o.text+'"></button>')
				: ('<button name="' + bn + '" class="BtnFlow"'
					+ ' id="$' + bn + '$" accessKey="' + o.key + '"'
					+ ' onclick="' + o.onclick + '" type="button">'
					+ '<u>' + o.key + '</u>'
					+ '-' + o.text
					+ '</button>')
				;
			buttonBox.html(button + buttonBox.html());
		}
/**
{
	Desc : 'Open Browser | 打开对话浏览框'
	,Params : {
		Option : Object
		,Example : {
			page : 'Browser Page URL , String'
			,height : 'Browser Height , String' (M Default : 550px)
			,width : 'Browser Width , String' (M Default : 550px)
			,f : 'Browser Field , String' (M)
		}
		,Value Operation : Function
	} 
	,Return : Page Return Value(Ecology 7 Only)
}
 */
		,ob : function(o , f){
			if (! o.page){
				return;
			}
			if (! o.height){
				o.height = '550px';
			}
			if (! o.width){
				o.width = '550px';
			}
			if (true
			&& '8' == this.ev 
			&& top.Dialog ){
				var d = new top.Dialog();
//				d.Title = '';
				d.Width = this.n(o.width, 550, true);
				d.Height = this.n(o.height, 550, true);
				d.Drag = true;
				d.maxiumnable = true;
				d.URL = o.page;
				d.callbackfun = function(p1,r,p3){
					if (o.f && r){
						var rv = function(k){
							if (! k in r){
								return;
							}
							var s = r[k];
							return s.indexOf(",") == 0 ? s.substring(1) : s;
						};
						_C.v(o.f , rv('id'));
						_C.t(o.f , rv('name'));
					}
					if (!!r 
					&& 'function' == typeof f){
						f(r);
					}
				};
				d.show();
			} else {
				var r = window.showModalDialog(o.page , undefined
					,'dialogHeight:' + o.height + ';'
					+ 'dialogWidth:' + o.width + ';'
					+ 'center:yes;menubar:no');
				if (o.f && r){
					var rv = function(k){
						if (! k in r){
							return;
						}
						var s = r[k];
						return s.indexOf(",") == 0 ? s.substring(1) : s;
					};
					_C.v(o.f , rv('id'));
					_C.t(o.f , rv('name'));
				}
				if (!!r 
				&& 'function' == typeof f){
					f(r);
				}
				return r;
			}
		}
/**
{
	Desc : 'Browser Button | 浏览按钮图标'
	,Params : {
		Field ID : String
		,Button Click Function : Function (M Override)
	} 
	,Return : Browser Button (JQuery Object)
}
 */
		,browserButton : function(id , f){
			var b = jQuery('#' + id).siblings('button:first');
			if ('function' == typeof f){
				for (var k = 0; k < b.length; k++){
					b[k].onclick = f;
				}
			}
			return b;
		}
/**
{
	Desc : 'Delete Row | 删除行'
	,Params : {
		Sub Table Index : Int
		,Row Index : Int Or String('_?') (M Delete All)
	} 
}
 */
		,deleteRow : function(stI , rI){
			if ('string'==typeof stI){
				stI = this.f2stI(stI);
				if (stI < 0){
					return;
				}
			}
			var _c = this;
			var cs = document.getElementsByName('check_node_' + stI);
			if (cs.length < 1){
				cs = document.getElementsByName('check_mode_' + stI);
				if (cs.length < 1){
					return;
				}
			}
			var n = -1;
			if (rI){
				n = 
					_c.n(rI
					, _c.n(rI.substring(1) 
						, -1
						, true), true);				
			}
			for (var k = 0; k < cs.length; k++){
				if (rI 
						&& n != _c.n(cs[k].value, -2, true)
						&& n != _c.n(jQuery(cs[k]).attr('_rowindex'), -2, true)){
					continue;
				}
				cs[k].checked = 'checked';
			}
			var oldDialog = window.isdel;
			window.isdel = function(){return true;};
			var oldConfirm = window.confirm;
			window.confirm = function(){return true;};
			eval('deleteRow' + stI + '(' + stI + ')');
			window.isdel = oldDialog;
			window.confirm = oldConfirm;
		}
/**
{
	Desc : 'Add Row | 增加行'
	,Params : {
		Sub Table Index : Int
		,Data Action : Function (Param : Row Number)
	} 
}
 */
		,addRow : function(stI , f){
			if (undefined == stI){
				return;
			}
			if ('string'==typeof stI){
				stI = this.f2stI(stI);
				if (stI < 0){
					return;
				}
			}
			var jo = jQuery('#indexnum'+stI);
			if (jo.length==0) jo = jQuery('#nodenum'+stI);
			var n = '_' + jo.val();
			try {
				eval('addRow' + stI + '(' + stI + ');');
			}catch(e){
				if(!!window.console&&'function'==typeof window.console.log)
					window.console.log('ADD ROW FAIL > '+e);
			}
			if (f){
				this.emst(stI, n, function(){
					f(n);
				});
			}
		}
/**
{
	Desc : 'Top Menu | 顶部菜单附加'
	,Params : {
		Menu Action : Function
		,Text : String (M Default '菜单X')
		,Img : String (M Default 'btn_list')
	} 
}
 */
		,topMenu : function(f , text , img){
			if(! window.parent.bar){
				return;
			}
			if (! f){
				return;
			}
			var mBox = jQuery('#toolbarmenu', window.parent.document);
			var id = mBox.children('td').length+1;
			if (! text){
				text = '菜单' + id;
			}
			if (! img){
				img = 'btn_list';
			}
			var key = 'menuItemDivId' + id;
			var bar_ = window.parent.bar;
			var m = {
				id: key
				, text: text
				, iconCls: img
				, handler: f
			};
			window.parent.bar = [m];
			window.parent.setToolBarMenu();
			bar_ = bar_.push(m);
			var t = jQuery('button:contains("'+text+'")'
					, mBox).parents('table:first').onclick = f;
		}
/**
{
	Desc : 'More Menu | 右键菜单附加'
	,Params : {
		Menu Action : Function
		,Text : String (M Default '菜单X')
	} 
}
 */
		,moreMenu : function(f , text){
			if (! f){
				return;
			}
			var mBox = jQuery('div[id="menuTable"]'
				, window.frames['rightMenuIframe'].document);
			var n = mBox.children('.b-m-item').length+1;
			if (! text){
				text = '菜单' + n;
			}
			var theme = window.parent.GLOBAL_CURRENT_THEME || 'ecology7';
			var folder = window.parent.GLOBAL_SKINS_FOLDER || 'default';
			var id = 'menuItemDivId' + n;
			var m = '<div id="' + id + '"'
				+ ' class="b-m-item"'
				+ ' onmouseover="this.className=\'b-m-ifocus\'"'
				+ ' onmouseout="this.className=\'b-m-item\'"'
				+ ' unselectable="on" >'
				+ '<div class="b-m-ibody" unselectable="on" >'
				+ '<nobr unselectable="on" >'
				+ '<img align="absMiddle"'
				+ ' src="/wui/theme/'
				+ theme
				+ '/skins/'
				+ folder
				+ '/contextmenu/default/11.png"'
				+ ' width="16" >'
				+ '<button id="'
				+ id
				+ '_btn" style="width: 120px" >'
				+ text
				+ '</button>'
				+ '</nobr></div></div>';

			mBox.append(jQuery(m));
			jQuery('#rightMenuIframe')
				.height(jQuery('#rightMenuIframe').height()+24);
			jQuery('#'+id+'_btn', mBox).bind('click', f);
		}
/**
{
	Desc : 'Post Ajax | Post Ajax'
	,Params : {
		URL : String
		,Function : Result Param
	} 
}
 */
		,post : function(url , f){
			jQuery.ajax({
				type : 'post'
				, url : url
				, success : f
			});
		}
/**
{
	Desc : 'To JSON | 字符串转JSON'
	,Params : {
		JSON String : String
		,Function : Error Function
	} 
}
 */
		,json : function(s , f){
			try {
				return eval('(' + s + ')');
			} catch (e) {
				if (f){
					f(e);
				}
			}
		}
/**
{
	Desc : 'URL Parameter | 获取地址栏参数'
	,Params : {
		Parameter Key : String (Return Parameter Of Key)
		,Default Value : String (M)
	} 
	,Return : Parameters
}
 */
		,uP : function(key , dv){
			var url = window.location.href;
			if (url.indexOf('?') < 0){
				return key ? dv : {};
			}
					
			var s = url.substring(url.indexOf('?') + 1).split('&');
			var p = {};
			for (var n = 0; n < s.length; n++){
				var t = s[n].split('=');
				var k = t[0];
				var v = t[1];
				if (k in p){
					if (p[k] instanceof String){
						var ks = p[k];
						var a = [];
						a[0] = ks;
						a[1] = v;
						p[k] = a;
					} else {
						var a = p[k];
						a[a.length] = v;
						p[k] = a;
					}
				} else {
					p[k] = v;
				}
			}
			if (key){
				return p[key];
			} else {
				return p;
			}
		}
/**
{
	Desc : 'Is Read Only | 流程是否只读'
	,Return : {
		True : Is Read Only
		,False : Editing
	}
}
 */
		,isRead : function(p){
			if (! p){
				p = this.uP();
			}
			if ('reEdit' in p){
				return false;
			}
			if ('message' in p){
				return false;
			}
			if ('requestid' in p){
				return true;
			}
			return false;
		}
/**
{
	Desc : 'Map Show | 遍历集合字符'
	,Params : {
		Map : Object
		,Title : String (M)
	} 
	,Return : Map Key And Values
}
 */
		,mapShow : function(o , t){
			if (! o){
				return;
			}
			if (! t){
				t = 'Map';
			}
			var s = t + ' : \n{';
			var f = true;
			for (var k in o){
				s = s + '\n   ' + (f ? ',' : ' ') + k + ' : ' + o[k];
				f = false;
			}
			s = s + '\n}';
			return s;
		}
/**
{
	Desc : 'Only One Check | 有且只有其一'
	,Params : {
		Target Field : String
		,All Field : String[]
		,Row Index : Int Or String('_?') (M Delete All)
	} 
}
 */
		,onlyOne : function(t , mf , r){
			var _c = this;
			if (_c._onlyOneRunning){
				return;
			}
			_c._onlyOneRunning = true;
			try {
				if (! t || ! mf){
					return;
				}
				if (! r){
					r = '';
				} else if (_c.n(r, -1, true) > -1){
					r = '_' + r;
				}
				if ('' == _c.v(t+r)){
					var nv = true;
					for (var k = 0; k < mf.length; k++){
						if (mf[k] == t){
							continue;
						}
						var fk = mf[k]+r;
						if ('' != _c.v(fk)){
							if (nv){
								nv = false;
							} else {
								_c.vt(fk, '');
							}
						}
					}
					if (nv){
						for (var k = 0; k < mf.length; k++){
							var fk = mf[k]+r;
							_c.rs(fk, true);
						}
					}
				} else {
					for (var k = 0; k < mf.length; k++){
						if (mf[k] == t){
							continue;
						}
						var fk = mf[k]+r;
						_c.rs(fk, false);
						_c.vt(fk, '');
					}
				}
			} catch (e) {}
			_c._onlyOneRunning = false;
		}
		,_onlyOneRunning : false
/**
{
	Desc : 'A Label For Hrm | 人力资源链接'
	,Params : {
		Hrm Resource ID Value : String
		,Hrm Resource Last Name Text : String
	} 
	,Return : A Label HTML
}
*/
		,aHrm : function(v , t){
			return '<a onclick="pointerXY(event);"'
				+ ' href="javascript:openhrm(' + v + ');">'
				+ t + '</a>';
		}
/**
{
	Desc : 'Editer Display | 编辑区显示切换(E8)'
	,Params : {
		Field ID : String
		,Is Display : Boolean
	}
}
*/
		,editerDisplay : function(id , isDisplay){
			var _c = this;
			if (jQuery('button#'+id+'browser').length){
				if (isDisplay){
					jQuery('button#'+id+'browser').show();
				} else {
					jQuery('button#'+id+'browser').hide();
				}
			} else if (jQuery('#'+ id + '_browserbtn').length){
				window.setTimeout(function(){
					var _jp = jQuery('#'+ id + 'span').parents('div.e8_os');
					if (isDisplay){
			            jQuery('#'+ id + 'span_crivia').hide();
			            _jp.show();
			            //jQuery('#'+ id + '_browserbtn').show();
			            //jQuery('#'+ id + 'span').parent().parent().show();
					} else {
			            if (! jQuery('#'+ id + 'span_crivia').length){
			                _jp.parent().append(
'<span id="'+id+'span_crivia" style="float: left;" ></span>');
			            }
			            //jQuery('#'+ id + '_browserbtn').hide();
			            //jQuery('#'+ id + 'span').parent().parent().hide();
			            _jp.hide();
			            jQuery('#'+ id + 'span_crivia').html(_c.t(id));
			            jQuery('#'+ id + 'span_crivia').show();
			            jQuery('#'+ id + 'span_crivia').find('span.e8_delClass').hide();
					}
				} , 370);
			} else {
				var o = jQuery('#'+ id);
				if (jQuery(o).attr('type') == 'hidden'){
					return;
				}
				var s = o.find('option:selected');
				if (isDisplay){
		            jQuery('#'+ id + 'span_crivia').hide();
					if (_c.ui.isMobile() && s.length){
						jQuery('input[id$="' + id +'"]').show();
					} else {
						o.show();
					}
					if (! s.length){
						if (_c.v(id)){
							_c.t(id , '');
						}
					}
				} else {
					if (_c.ui.isMobile() && s.length){
						jQuery('input[id$="' + id +'"]').hide();
					} else {
						o.hide();
					}
					if (s.length){
						var x = s.text();
						var t = jQuery('#'+ id + 'span_crivia');
			            if (! t.length){
			                var p = o.parent();
			                p.prepend( '<span id="'+id+'span_crivia" style="float: none;" ></span>');
			            }
						t = jQuery('#'+ id + 'span_crivia');
			            t.html(o.val()?x:'');
			            t.show();
					} else {
						if (_c.v(id)){
							var t = document.getElementById(id + 'span');
							if (!t){
				                var p = o.parent();
				                p.append('<span id="'+id+'span" style="float: none;" ></span>');
							}
							_c.t(id , _c.v(id));
						}
					}
				}
			}
		}
/**
{
	Desc : 'Edit Mobile Sub Table  | 编辑移动端子表'
	,Params : {
		Sub Table Index : Int Or String(Field)
		,Row Index : Int Or String('_?') 
		,Function : Edit Action
	}
}
*/
		,emst : function(stI , rI , f){
			if ('function'!=typeof f){
				return;
			}
			if (stI < 0){
				f();
				return;
			}
			if ('string'==typeof stI){
				this.emst(this.f2stI(stI), rI, f);
				return;
			}
			if ('function'!=typeof window.detailTrClick){
				f();
				return;
			}
			var _c = this;
			var n = -1;
			if (rI){
				n = 
					_c.n(rI
					, _c.n(rI.substring(1) 
						, -1
						, true), true);				
			}
			if (n < 0){
				return;
			}
			if ('function'!= typeof this._v){
				this._v = this.v;
				this.v = function(id , v){
					var o = this._v(id, v);
					if ('object'!=typeof o){
						return o;
					}
					var e = jQuery('#'+id+'_d');
					if (!e.length
					|| 'string'!= typeof e.attr('onchange')
					|| e.attr('onchange').indexOf('dynamicModify')<0){
						return;
					}
					e.val(v);
					e.change();
				};
			}
			var ea = jQuery('#trEdit_'+stI+'_'+n);
			if (ea.length > 0){
				f();
				var fo = document.activeElement;
				var tagName = fo.tagName;
				fo = jQuery(document.activeElement);
				var id = fo.attr('id');
				var name = fo.attr('name');
				var value = fo.val();
				try {
					window.detailTrClick(stI , n);
					window.detailTrClick(stI , n);
				} catch (e) {}
				var fo = id ? jQuery(tagName+'[id="'+id+'"]')
					: jQuery(tagName+'[name="'+id+'"]');
				fo.focus();
				fo.val(value);
				return;
			}
			try {
				window.detailTrClick(stI , n);
			} catch (e) {}
			f();
			try {
				window.detailTrClick(stI , n);
			} catch (e) {}
		}
/**
{
	Desc : 'Field To Sub Table Index  | 字段子表索引'
	,Params : Field(String)
	,Return : Sub Table Index(Int)
}
*/
		,f2stI : function(f){
			if (f in this._m4f2stI){
				return this._m4f2stI[f];
			}
			var o;
			var each = function(jo){
				if (jo.length < 1) return false;
				o = jQuery(jo[0]).parents('table[id^="oTable"]');
				return o.length > 0;
			};
			if(each(jQuery('input[id^="' + f + '"]'))){
			} else if (each(jQuery('select[id^="' + f + '"]'))){
			} else if (each(jQuery('textarea[id^="' + f + '"]'))){
			}
			if (!!o && o.length > 0){
				o = o.attr('id');
				if ('string'==typeof o){
					o = _C.n(o.substring(6),-1,true);
					this._m4f2stI[f] = o;
					if (o > -1){
						return;
					}
				}
			}
			this._m4f2stI[f] = -1;
			return this.f2stI(f);
		}
		,_m4f2stI : {}
/**
{
	Desc : 'Reset Browser Page | 重写浏览按钮打开页面(E8)'
	,Params : {
		Field ID : String
		,Page URL : String
		,Parameter Loader : Function
	}
}
*/
		,rbp : function(id , url , parameterLoader){
			var jo = jQuery('#'+id+'_browserbtn');
			if (! jo.length){
				return;
			}
			var btn = jo[0];
			var onclicks = btn.getAttribute('onclick').split(',');
			btn.onclick = function(){
				var s = '';
				for (var k = 0; k < onclicks.length; k++){
					if (0 < k){
						s += ',';
					}
					if (1 == k){
						var p = '';
						if ('function'==typeof parameterLoader){
							var o = parameterLoader();
							if ('string'==typeof o){
								p = o;
							}
						}
						s += '\''+url+p+'&__f=\'';
					} else if (4 == k) {
						var b = onclicks[k].substring(0,onclicks[k].indexOf('.'));
						var e = onclicks[k].substring(onclicks[k].indexOf('.'));
						if (b.match(/field\d+_?\d?/)){
							s += 'document.getElementById(\''
								+ b + '\')' + e;
						} else {
							s += onclicks[k];
						}
					} else {
						s += onclicks[k];
					}
				}
				eval(s);
			};
		}
/**
{
	Desc : 'Editer Status Switch | 控件状态切换'
	,Params : {
		Field ID : String
		,Editable & Requird : Boolean
	} 
}
*/
		,ess : function(f , ss){
			if (! ss){
				this.editerDisplay(f, true);
				this.v(f, '');
			}
			this.rs(f, !!ss);
			this.editerDisplay(f, !!ss);
		}
		,_fkReady : []
		,_fkReadyRun : function(){
			if (!!top._CriviaDymanicFields
			&& !!top._CriviaDymanicFields.length){
				for (var k = 0; k < this._fkReady.length; k++){
					this._fkReady[k]();
				}
				return;
			}
			window.setTimeout('_C._fkReadyRun()',100);
		}
/**
{
	Desc : 'Field Key Ready | 字段信息就绪'
	,Params : {
		Function : Do Something When Fields Ready
	} 
}
*/
		,fkReady : function(f){
			if ('function'!=typeof f) return;
			if (!!top._CriviaDymanicFields
			&& !!top._CriviaDymanicFields.length) {
				f(); return;
			}
			var n = this._fkReady.length;
			if (0 == n){
				this._fkReadyRun();
			}
			this._fkReady[n] = f;
		}
/**
{
	Desc : 'Detail Table Scroll Column | 设定锁列滚动表格'
	,Params : {
		Detail Table Index : Integer
		,All Width : Width Setting (like: 2000px)
		,Fix Column Count : Ingeter
	} 
}
*/
		, dtsc : function(dtI,aw,fcc){
			var jt = jQuery('table#oTable'+dtI);
			var jtBox = jt.parent('div#detailDiv_'+dtI);
			var w = _C.n(jtBox.css('width'),0,true);
			jtBox
				.css('width',w+'px')
				.css('overflow-x','scroll');
			jt.css('width',_C.n(''+aw,0,true)+'px');
			jtBox.scroll(function(){
				var x = jtBox.scrollLeft();
				jt.find('tr').each(function(){
					var jds = jQuery(this).find('td');
					for (var k = 0; k < jds.length; k++){
						if ('number'==typeof fcc && fcc>k)
							jQuery(jds[k])
								.css('position','relative')
								.css('top','0px')
								.css('left',x);
						else if ('function'==typeof fcc && fcc(jQuery(jds[k])))
							jQuery(jds[k])
								.css('position','relative')
								.css('top','0px')
								.css('left',x);
					}
				});
			});
		}
/**
{
	Desc : 'Value 4 Select | 根据文本设定下拉框的值'
	,Params : {
		Key : Select Key
		,Function : Is Select
	} 
}
*/		
		, v4select : function(k,f){
			if ('function'!=typeof f)return;
			var _c = this;
			jQuery('#'+k+' option').each(function(){
				var jo = jQuery(this);
				if(f(jo.text(),jo)) _c.v(k,jo.val());
			});
		}
};
GlobalReger(function(w){
	w.CriviaWorkFlowJavaScriptFunctions = top.CriviaWorkFlowJavaScriptFunctions;
});

top._C = CriviaWorkFlowJavaScriptFunctions==top._C
	?top._C:CriviaWorkFlowJavaScriptFunctions;
GlobalReger(function(w){
	w._C = top._C;
});
top._CRM = undefined!=top._CRM?top._CRM:_C._cwjsListenerValueMap;
GlobalReger(function(w){
	w._CRM = top._CRM;
});
<!-- westvalley Function dev end -->
//Money Format Show-千分位金额字段赋值
function mfs(k,n){
	var v = "number"==typeof n?""+n
		: _C.n(""+n,0)+"";
	_C.editerDisplay(k,true);
	if ("string" == typeof jQuery("#"+k)
		.attr("onblur")
		&&jQuery("#"+k)
		.attr("onblur")
		.indexOf("changeToThousands2")>-1){
		jQuery("#"+k).val(commafy(_C.n(v,0).toFixed(2)));
		return;
	}
	_C.v(k,_C.n(v,0));
	var nv = _C.v(k);
	if (!nv || nv.indexOf(",")>-1){
		return;
	}
	if ("function"== typeof window.changeToThousands2){
		changeToThousands2(k,2);
		var nv = _C.v(k);
		if (!nv || nv.indexOf(",")>-1){
			return;
		}
	}
	if ("function"== typeof window.commafy){
		_C.v(k,commafy(_C.n(v,0)));
	}
};
// Money Format Show (Read Only).千分位金额字段赋值(只读)
function mfsro(k,v){
	mfs(k,v);
	_C.editerDisplay(k,false);
	window.setTimeout(function(){
		if ("none"==jQuery("#"+k).css("display")){
			_C.t(k,_C.v(k));
		}
	} , 370);
}
/**
 * 文本字段只读
 * @param field
 * @returns
 */
function setReadIT(field){
	jQuery("#"+field).attr({readonly:'true'});
}

function setReadIF(field){
	//jQuery("#"+field).attr({readonly:'false'});
	jQuery("#"+field).removeAttr("readonly");
}
/**
 * 选择框只读
 * @param field
 * @returns
 */
function setReadST(field){
	jQuery("#"+field).attr("disabled",true);
}
function setReadSF(field){
	jQuery("#"+field).attr("disabled",false);
}

/**
 * 返回获取指定元素
 * v id或者name
 */
function getID(v){
	var rtnEle = null;
	rtnEle = jQuery("*[id='" + v + "']");
	if (rtnEle == undefined || rtnEle == null||rtnEle.length==0) {
		rtnEle = jQuery("*[name='" + v + "']");
	}
	return rtnEle;
}

/**
 * 浏览按钮赋值
 */
function setButton(id,v,span){
	var sHtml = "";
	jQuery("#"+id).val(v);
	if (!isNull(v)) {
		sHtml = wrapshowhtml0(jQuery("#" + id).attr("viewtype"), "<a title='" + v + "'>" + span + "</a>&nbsp", v);
	}
	jQuery("#"+id+"span").html(sHtml);
}

/**
 *设置对象隐藏
 */
function setHide(v){
	if (isNull(v)) {
		return false;
	}
	getID(v).hide();
};

/**
 *设置对象显示
 */
function setShow(v){
	if (isNull(v)) {
		return false;
	}
	getID(v).show();
};

/**
 *设置按钮对象隐藏 -fieldid_browserbtn
 */
function setHideButton(v){
	if (isNull(v)) {
		return false;
	}
	jQuery("#" + v+"wrapspan").hide();
	//jQuery("#" + v+"span").hide();
	//jQuery("#" + v+"_browserbtn").hide();
};

/**
 * 判断是否为空
 */
function isNull(v){
	if (typeof(v) == "undefined" || v == "" || v == null) {
		return true;
	}
	return false;
};


/** 
 * 检查两个字符串是否相等
 */
function isEque(v1, v2){
	if (v1 == v2) {
		return true;
	}
	else {
		return false;
	}
}


/**
 * 转为正常的数字
 */
function toNum(v){
	if (isNull(v)) {
		return 0;
	}
	return parseFloat(String(v).replace(/,/g, ""));
};

/**
 * 保留2位小数
 */
function toFloat(v){
	if (isNull(v)) {
		return "0.00";
	}
	return Math.round(v * Math.pow(10, 2)) / Math.pow(10, 2);
}
/***
 * 获取下拉框选中的文本值
 * @param selectid
 * @returns
 */
function getSelectText(selectid,isread,val){
	var resu = "";
	if(val){
		selectid = isread ? "dis"+selectid : selectid; //只读下拉框id带dis
		resu = jQuery("#"+selectid).find("option[value="+val+"]").text();
	}else{
		//var selval = jQuery("#"+selectid).val();
		var selval = _C.v(selectid);
		if(selval){
			selectid = isread ? "dis"+selectid : selectid; //只读下拉框id带dis
			resu = jQuery("#"+selectid).find("option[value="+selval+"]").text();
		}
	}
	return resu;
}
/**
 * 日期加减天数操作-返回yyyy-mm-dd格式字符串
 * @param dd
 * @param dadd
 */
function dateAddDay(dd,day){
	var a = new Date(dd);
	return formateDate(new Date(a.valueOf()+day * 24 * 60 * 60 * 1000));
};
/**
 * 格式化日期为yyyy-mm-dd
 * @param dd
 * @param dadd
 */
function formateDate(dd){
	var seperator1 = '-';
	var seperator2 = ':';
	var date = new Date();
	if(typeof dd=="date"){
		date = dd;
	}else if(dd){
		date = new Date(dd);
	}
	var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            //+ " " + date.getHours() + seperator2 + date.getMinutes()
            //+ seperator2 + date.getSeconds()
            ;
    return currentdate;
};
/**
 * 日期比较
 * @param dbegin date开始日期
 * @param dend date结束日期
 * return 0:相等,1:dend>dbegin,-1:dend<dbegin
 */
function dateCompare(dbegin,dend){
	var begin = new Date(dbegin).valueOf();
	var end = new Date(dend).valueOf();
	var a = 0 ;
	if(begin < end){
		a = 1;
	}else if(begin > end){
		a = -1;
	}
	return a;
};

/**
 * 解决IE7、8下日期NAN问题
 * @param dateStringInRange(yyyy-mm-dd格式日期)
 * @returns
 */
function parseISO8601(dateStringInRange) {  
	var isoExp = /^\s*(\d{4})-(\d\d)-(\d\d)\s*$/,  
	    date = new Date(NaN), month,  
	    parts = isoExp.exec(dateStringInRange);  
	if(parts) {  
	   month = +parts[2];  
	   date.setFullYear(parts[1], month - 1, parts[3]);  
	   if(month != date.getMonth() + 1) {  
	      date.setTime(NaN);  
	   }  
	}  
	return date;  
};
/**
 * 计算日期相差天数/Math.abs绝对值
 * @param strDateStart
 * @param strDateEnd
 * @returns
 */
function getBEDays(strDateStart,strDateEnd){ 
	var strSeparator = "-"; //日期分隔符 
	var oDate1; 
	var oDate2; 
	var iDays; 
	oDate1= strDateStart.split(strSeparator); 
	oDate2= strDateEnd.split(strSeparator); 
	var strDateS = new Date(oDate1[0], oDate1[1]-1, oDate1[2]); 
	var strDateE = new Date(oDate2[0], oDate2[1]-1, oDate2[2]); 
	iDays = parseInt((strDateE - strDateS ) / 1000 / 60 / 60 /24)//把相差的毫秒数转换为天数 
	return iDays ; 
};
/**
 * input框隐藏显示
 * @param inKey  : fieldid
 * @param isShow  显示：true 隐藏：false 
 */
function inputHAS(inKey,isShow){
	if(isShow){
		jQuery("#"+inKey).show();
	}else{
		jQuery("#"+inKey).hide();
	}
}
/**
 * 遍历明细赋值字段
 * @param o={field_from,field_to}
 * @returns
 */
function setDtValue(o){
	_C.stEach(o.field_from,function(r){
		_C.v(o.field_to+r,_C.v(o.field_from+r));
	});
};
/****费用类型浏览按钮-不同流程展现对应的费用类型数据*****/
function resetBrowserUnit(fieldid,wfid){
	var url = "/CostTypeBrowser.c?isSubOnly=1&wfid="+wfid;
	_C.rbp(fieldid,url);
};
/**刷新后绑定费用类型**/
function flashdtUnit(fdtid,wfid){
	_C.stEach(fdtid,function(r){
		resetBrowserUnit(fdtid+r,wfid);
	});
};
/***
 * 设置日期为当前日期-是否需要判断空值
 * @param field
 * @returns
 */
function setNowDate(field1,field2){
	var seperator1 = '-';
	var date = new Date();
	var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            ;
    if(field1 && (_C.v(field1) == "" || _C.v(field1) == undefined)){
    	_C.v(field1,currentdate);
    }
    if(field2 && (_C.v(field2) == "" || _C.v(field2) == undefined)){
    	_C.v(field2,currentdate);
    }
};

/**
 * 日期转为年月
 * @param datestr
 * .substr(start,length)
 * @returns
 */
function date2YearMonth(datestr){
	var result = "";
	if(datestr){
		result = datestr.substring(0,4)+"年"+datestr.substring(5,7)+"月";
	}
	return result;
}
/**
 * 赋值明细字段到主表字段
 * @param dtField-明细字段
 * @param mField-主表字段
 * @param dpField-去重字段
 * @returns
 */
function writeDt2Main(dtField,mField,dpField){
	var dval = "";
	var dtext = "";
	var dpval = dpField ? _C.v(dpField) : "" ;
	var dptext = dpField ? _C.at(dpField) : "" ;
	var objval = [];
	_C.stEach(dtField,function(r){
		var val = _C.v(dtField+r);
		var text = _C.at(dtField+r);
		if(!(val in objval) && val != dpval){
			objval[val] = text;
		}
	});
	for(var key in objval){
		if(toNum(key) > 0){
			if(dval == ""){
				dval = key;
				dtext = objval[key];
			}else{
				dval += "," + key;
				dtext += " " + objval[key];
			}
		}
	}
	if(""!= dpval && dval == ""){
		dval = ("" == dval) ? dpval 
				: dpval;  // + "," + dval  //2018-01-29 - 去掉申请人
	}
	if(""!= dptext && dtext == ""){
		dtext = ("" == dtext) ? dptext 
				: dptext ;  //+ " " + dtext   //2018-01-29 - 去掉申请人
	}
	_C.vt(mField,dval,dtext);
};

function getFous(fieldid){
	jQuery('#'+fieldid).focus();
}


//获取表单字段
function getModileFields(workflowid){
	var fieldDatas;
	jQuery.ajax({
		type:"POST",
		url: "/mobile/plugin/weaversj/workflow/publicUtil/getfieldid.jsp",
		data: 'wid='+workflowid,
		dataType:"json",
		async: false,
		success: function(data){	
			fieldDatas = data;
		}
	});
	return fieldDatas;
}

//获取表单字段
function getFields(workflowid){
	var fieldDatas;
	jQuery.ajax({
		type:"POST",
		url: "/weaversj/workflow/publicUtil/getfieldid.jsp",
		data: 'wid='+workflowid,
		dataType:"json",
		async: false,
		success: function(data){	
			fieldDatas = data;
		}
	});
	return fieldDatas;
}

//获取建模字段
function getFieldsToMode(formid){
	var fieldDatas;
	jQuery.ajax({
		type:"POST",
		url: "/weaversj/workflow/publicUtil/getfieldidToMode.jsp",
		data: 'wid='+formid,
		dataType:"json",
		async: false,
		success: function(data){	
			fieldDatas = data;
		}
	});
	return fieldDatas;
}

//获取当前用户系统语言
function getUserLanguage(){
	var userLanguage;
	jQuery.ajax({
		type:"POST",
		url: "/mobile/plugin/weaversj/workflow/publicUtil/getUserLanguage.jsp",
        contentType:'application/x-www-form-urlencoded',
		dataType:"text",
		async: false,
		success: function(data){	
			userLanguage = data.trim();
		}
	});
	return userLanguage;
}

function getEleHideFlag(ids){
	if(jQuery("#"+ids).attr("type") === "hidden"){
		return true;
	}
	return false;
}

function removeImg(fieldId){
	if(jQuery("#"+fieldId+"span").find("img").length>0){
		jQuery("#"+fieldId+"span").find("img").remove();
	}
}

function findImg(fieldId){
	return jQuery("#"+fieldId+"span").find("img").length;
}

function appendImg(fieldId){
	jQuery("#"+fieldId+"span").append("<img src='/images/BacoError_wev8.gif' align='absmiddle'>");
}