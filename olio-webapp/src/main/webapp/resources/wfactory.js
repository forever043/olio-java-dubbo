/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
/**
 * jMaki Widget Loader 1.8.1 for Plain HTML
 * 
 * For full source see : https://jmaki-ext.dev.java.net
 *
 */
eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('5.1O("5.1p.1q");5.1p.1q.1P=6(z){4 A=U;A.1a=C;5.O("1Q 1r 1R 1.8.1");6 1b(a,b){4 c={};4 d=V.1S(a);P(4 i=0;i<d.D;i++){3(d[i][b])c[d[i][b]]=C}W c}A.X=1b("1T","1U");A.I=1b("1V","1W");U.1s=6(b){4 c=1c;5.1d({1e:b+"Y.1X",1t:K,L:6(a){3(a.1f!=\'\'){1g{c=Q("("+a.1f+")")}1h(e){5.O("1i 1Y Y"+e)}}}});W c};U.Z=6(g,h){4 k=K;3(E h!="11")k=h;4 l=1Z.V;4 m=g.Y;4 n=g.1u;4 o=g.20;4 p=m.12;3(!p){4 q=m.1j.R(\'.\');p=5.21+"/"+q.S("/")+"/";m.12=p}4 r=g.L;3(!m.F)m.F=5.22();3(!n.13)n.13=m.F+"23";3(!o){o=A.1s(p)}3(m.9){m.9=5.24(m.9)}6 14(a){3(/../.G(a)){4 b=a.R("/");3(b.D>0){4 c=0;4 d=b.D-1;25(d>=0){3(b[d]==\'..\'){c++}B{3(c>0){4 e=d-(c-1);4 f=c*2;3(d-c<0){e=0}b.26(e,f);c=0}}d--}}W b.S("/")}B{W a}}6 T(c){5.1d({1e:p+"H.27",1t:K,L:6(a){4 b=a.1f.R(\'${F}\');J=b.S(m.F);b=J.R(\'${12}\');J=b.S(m.12);3(m.9&&/\\${9}/i.G(J)){b=J.R(\'${9}\');J=b.S(m.9+\'\')}n.28=J+"<1v 13=\'15"+m.F+"15\'></1v>"}});4 d=29(6(){3(l.1w("15"+m.F+"15")!=1c){2a(d);1x()}},10)}6 1x(){3((!A.I[p+"H.16"]||k)&&!(o&&E o.1y=="1z"&&o.1y==K)){5.1d({1e:p+"H.16",L:6(a){5.1k(p+"H.16");A.I[p+"H.16"]=C}})}3(5.2b("5.2c."+m.1j+".1r")==1c){5.O("2d="+p+"H.1l");5.1A({M:[p+"H.1l"],L:6(){A.X[p+"H.1l"]=C;4 a=5.Z(m);3(a&&E r=="6"){r(a,m,n)}n.17.1B="1C"},2e:11,1D:K})}B{4 b=5.Z(m);3(b&&E r=="6"){r(b,m,n)}n.17.1B="1C"}}3(o&&o.1m){4 s=o.1m.2f;3(s.N){4 t;P(4 i=0;i<s.N.D;i++){3(s.N[i][\'2g\']==C){t=s.N[i].17}3(s.N[i].13==5.1m.2h){t=s.N[i].17;2i}}4 u=14(p+t);3(!A.I[u]){5.1k(u);A.I[u]=C}}3(s.18){P(4 j=0;j<s.18.D;j++){4 v=14(((/^1E/i.G(s.18[j]))?\'\':p)+s.18[j]);3(!A.I[v]){5.1k(v);A.I[v]=C}}}3(s.1F){2j.Q(s.1F)}3(s.M){4 w=[];P(4 x=s.M.D-1;x>=0;x--){4 y=14(((/^1E/i.G(s.M[x]))?\'\':p)+s.M[x]);3(!A.X[y]){A.X[y]=C;w.2k(y)}}3(w.D>0){5.1A({M:w,L:T,1D:K})}B{T()}}B{T()}}B{T()}};U.1n=6(a){4 b=V.2l;3(E a!="11")b=V.1w(a);4 c=5.2m(b,[]);P(4 i=0;c&&i<c.D;i++){3(c[i]&&c[i].7(\'1G\')){4 d={};d.1j=c[i].7(\'1G\');3(c[i].7(\'1H\')){1g{d.19=Q("("+c[i].7(\'1H\')+")")}1h(e){5.O("1i 1I 2n:"+e)}}4 f;4 g=c[i].7(\'2o\');3(E g=="2p"&&!(/@\\{/.G(g))&&(/\\[/.G(g)||/\\{/.G(g))){1g{d.9=Q("("+g+")")}1h(e){5.O("1i 1I 2q :"+e)}}B 3(E g!=\'11\'){d.9=g}3(c[i].7(\'1J\'))d.2r=c[i].7(\'1J\');3(c[i].7(\'1K\'))d.2s=c[i].7(\'1K\');3(c[i].7(\'1L\')){4 h=c[i].7(\'1L\');3(/\\[/.G(h)){d.1o=Q("("+h+")")}B{d.1o=h}}3(c[i].7(\'1M\'))d.F=c[i].7(\'1M\');4 j=c[i];A.Z({Y:d,1u:j})}}};3(z.19&&E z.19.1N=="1z"){A.2t=z.19.1N}3(A.1a){3(5.2u)A.1n();B{5.1o("/5/2v/2w",6(){3(A.1a)A.1n()})}}};',62,157,'|||if|var|jmaki|function|getAttribute||value||||||||||||||||||||||||||||else|true|length|typeof|uuid|test|component|gstyles|template|false|callback|libs|themes|log|for|eval|split|join|loadTemplate|this|document|return|gscripts|widget|loadWidget||undefined|widgetDir|id|normalizeURL|_|css|style|styles|args|autoLoad|createElementMap|null|doAjax|url|responseText|try|catch|Error|name|loadStyle|js|config|findAndAdd|subscribe|extensions|widgetFactory|Widget|loadWidgetJson|asynchronous|container|div|getElementById|initWidget|hasCSS|boolean|addLibraries|visibility|visible|cleanup|http|preload|jmakiName|jmakiArgs|Parsing|jmakiService|jmakiPublish|jmakiSubscribe|jmakiId|parseOnLoad|namespace|Extension|Loaded|Factory|getElementsByTagName|script|src|link|href|json|loading|window|wjson|resourcesRoot|genId|_wrapper_|clone|while|splice|htm|innerHTML|setInterval|clearInterval|findObject|widgets|lib|inprocess|type|default|globalTheme|break|_globalScope|push|body|getAllChildren|Args|jmakiValue|string|Value|service|publish|autoload|loaded|runtime|loadComplete'.split('|'),0,{}))

jmaki.loadExtension({ name : "widgetFactory", args : {parseOnLoad : true}});
