var videos = document.getElementsByTagName("video");
var i;
var divId = 1;
for (i = 0; i < videos.length; i++) {
	videos[i].onplaying = function() {
//		alert(this.duration);
		var rect = getAbsoluteLocationEx(this);
		var obj = document.createElement("div");
        obj.id="xwalkVideo" + divId;
        divId++;
        obj.style.width = rect.offsetWidth + "px"
        obj.style.height = rect.offsetHeight + "px"
        obj.style.position = "absolute"
        obj.style.zIndex = "999"
        obj.style.background = "darkblue"
//        obj.filter = "alpha(Opacity=80);-moz-opacity:0.8;opacity: 0.8;z-index:20000; background-color:#ffffff";
        obj.style.top = rect.absoluteTop + "px"
        obj.style.left = rect.absoluteLeft + "px"
        obj.innerText = "sdsdsdaf"
        var img = document.createElement("img");
        img.src = "file:///android_asset/img/player.png";
        obj.appendChild(img);
        document.body.appendChild(obj);
        document.getElementById(obj.id).onclick = function(){alert("click on video element");};
	};
}
function getAbsoluteLocationEx(element) {
    if(element==null) {
        return null;
    }
    var elmt = element;
    var offsetTop = elmt.offsetTop;
    var offsetLeft = elmt.offsetLeft;
    var offsetWidth = elmt.offsetWidth;
    var offsetHeight = elmt.offsetHeight;
    while (elmt = elmt.offsetParent) {
        // add this judge
        if(elmt.style.position == 'absolute'|| elmt.style.position == 'relative'
                ||(elmt.style.overflow != 'visible' && elmt.style.overflow != '')) {
            break;
        }
        offsetTop += elmt.offsetTop;
        offsetLeft += elmt.offsetLeft;
    }
    return {absoluteTop:offsetTop, absoluteLeft:offsetLeft,
            offsetWidth:offsetWidth, offsetHeight:offsetHeight};
}
